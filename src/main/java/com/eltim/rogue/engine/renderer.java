package com.eltim.rogue.engine;

import com.eltim.rogue.world.map;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.summon.summon;
import com.eltim.rogue.system.InteractionSysteme;
import com.eltim.rogue.system.combatSysteme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

public class renderer extends JPanel {

    public static double CRT_CURVATURE_PRIMARY = 0.025;   // Courbure très subtile (effet léger)
    public static double CRT_CURVATURE_SECONDARY = 0.005; // Déformation minime des bords
    public static float CRT_CORNER_RADIUS_RATIO = 0.03f;  // Arrondi discret aux 4 coins (très léger)

    public void setCRTCurvature(double primary, double secondary, float cornerRatio) {
        CRT_CURVATURE_PRIMARY = primary;
        CRT_CURVATURE_SECONDARY = secondary;
        CRT_CORNER_RADIUS_RATIO = cornerRatio;
        cachedW = 0; 
    }

    private JFrame frame;
    private map currentMap;
    private BufferedImage crtOverlay;
    private BufferedImage sceneBuffer;
    private BufferedImage distortedBuffer;
    private int[] lutMap;
    private int cachedW = 0;
    private int cachedH = 0;
    private float scanlineOffset = 0;

    public renderer() {
        frame = new JFrame("RogueLike Swing");
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true); // Permet de redimensionner la fenêtre librement
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private game.GameState currentState = game.GameState.PLAYING;

    // Variables pour le calcul des clics de souris
    private int lastTileW = 1;
    private int lastTileH = 1;
    private int lastOffsetX = 0;
    private int lastOffsetY = 0;
    private int lastTextOffset = 0;

    public Point getGridCoordinates(int mouseX, int mouseY) {
        if (lastTileW <= 0 || lastTileH <= 0) return null;
        // On soustrait l'offset pour avoir la position relative à la grille (0,0)
        // Mais attention au décalage Y (textOffset est ajouté lors du drawString)
        int gridX = (mouseX - lastOffsetX) / lastTileW;
        int gridY = (mouseY - lastOffsetY) / lastTileH;
        return new Point(gridX, gridY);
    }

    public void updateMap(map m, game.GameState state) {
        this.currentMap = m;
        this.currentState = state;
        this.repaint();
    }

    public JFrame getFrame() {
        return frame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // 1. Initialiser/Redimensionner les tampons d'image hors écran si la taille change
        if (sceneBuffer == null || sceneBuffer.getWidth() != w || sceneBuffer.getHeight() != h) {
            sceneBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            distortedBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            updateBarrelLUT(w, h);
        }

        // 2. Dessiner L'INTÉGRALITÉ du jeu (Map, Menus, UI, HUD, Création) sur sceneBuffer
        Graphics2D g2dBuffer = sceneBuffer.createGraphics();
        g2dBuffer.setColor(Color.BLACK);
        g2dBuffer.fillRect(0, 0, w, h);

        float scale = Math.min((float) w / 800f, (float) h / 600f);
        renderGameScene(g2dBuffer, scale);
        g2dBuffer.dispose();

        // 3. Déformation géométrique globale (VRAIE Courbure de verre bombé CRT)
        applyCRTBarrelDistortion(w, h);

        // 4. Zoom-To-Fill : Zoome l'image déformée pour pousser 100% des trous des coins hors de l'écran (marge de sécurité totale)
        double zoomToFill = 1.0 + (CRT_CURVATURE_PRIMARY * 2.5) + (CRT_CURVATURE_SECONDARY * 3.5);
        int zoomW = (int) (w * zoomToFill);
        int zoomH = (int) (h * zoomToFill);
        int zoomX = (w - zoomW) / 2;
        int zoomY = (h - zoomH) / 2;

        Graphics2D g2dScreen = (Graphics2D) g;
        g2dScreen.drawImage(distortedBuffer, zoomX, zoomY, zoomW, zoomH, null);

        // 5. Superposition des scanlines droites
        drawCRTFilterOverlay(g2dScreen, w, h);
    }

    private void renderGameScene(Graphics2D g2d, float scale) {
        if (currentState == game.GameState.CHARACTER_CREATION) {
            drawCharacterCreationScreen(g2d, scale);
            return;
        }

        if (currentMap == null) return;

        int mapFontSize = Math.max(10, (int) (44 * scale));
        Font gameFont = new Font("Monospaced", Font.BOLD, mapFontSize); 
        g2d.setFont(gameFont);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g2d.getFontMetrics();
        int tileW = fm.charWidth('#'); 
        int tileH = (int)(fm.getAscent() * 0.9); 
        int textOffset = fm.getAscent(); 

        int offsetX = 0;
        int offsetY = 0;
        
        entity playerObj = null;
        for(entity e : currentMap.getEntities()) {
            if (e instanceof com.eltim.rogue.entity.player) {
                playerObj = e;
                break;
            }
        }
        
        if (playerObj != null) {
            offsetX = (getWidth() / 2) - (playerObj.getX() * tileW) - (tileW / 2);
            offsetY = (getHeight() / 2) - (playerObj.getY() * tileH) - (tileH / 2);
        } else {
            offsetX = (getWidth() - (currentMap.getWidth() * tileW)) / 2;
            offsetY = (getHeight() - (currentMap.getHeight() * tileH)) / 2;
        }

        this.lastTileW = tileW;
        this.lastTileH = tileH;
        this.lastOffsetX = offsetX;
        this.lastOffsetY = offsetY;
        this.lastTextOffset = textOffset;

        // Dessine la map
        for (int y = 0; y < currentMap.getHeight(); y++) {
            for (int x = 0; x < currentMap.getWidth(); x++) {
                com.eltim.rogue.world.tile t = currentMap.getTile(x, y);
                if (t != null && currentMap.getEntityAt(x, y) == null) {
                    char c = t.getSymbol();
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(String.valueOf(c), offsetX + x * tileW, offsetY + y * tileH + textOffset);
                }
            }
        }

        // Dessine les entités
        for (entity e : currentMap.getEntities()) {
            char c = e.getSymbol();
            
            if (e instanceof com.eltim.rogue.entity.player) g2d.setColor(new Color(100, 180, 255)); 
            else if (c == 'M') g2d.setColor(Color.RED);
            else if (e instanceof com.eltim.rogue.entity.environment.door) {
                com.eltim.rogue.entity.environment.door d = (com.eltim.rogue.entity.environment.door) e;
                if (d.isOpen()) {
                    g2d.setColor(new Color(50, 220, 50)); // Vert
                } else {
                    g2d.setColor(new Color(220, 50, 50)); // Rouge
                }
            }
            else if (e instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                g2d.setColor(Color.ORANGE);
            }
            else if (e instanceof com.eltim.rogue.entity.environment.InteractionTile) {
                g2d.setColor(new Color(255, 215, 0)); // Jaune doré pour les éléments d'interaction
            }
            else if (e instanceof com.eltim.rogue.entity.environment.chest) {
                com.eltim.rogue.entity.environment.chest ch = (com.eltim.rogue.entity.environment.chest) e;
                if (ch.isOpen()) {
                    g2d.setColor(Color.GRAY); // Gris classique si ouvert
                } else {
                    g2d.setColor(new Color(255, 215, 0)); // Or si fermé
                }
            }
            else g2d.setColor(Color.WHITE);
            
            int drawX = offsetX + e.getX() * tileW;
            int drawY = offsetY + e.getY() * tileH + textOffset;
            g2d.drawString(String.valueOf(c), drawX, drawY);
        }

        // Dessine l'UI d'interaction par-dessus si ouvert
        if (currentState == game.GameState.GAME_OVER) {
            drawGameOverScreen(g2d, scale);
        } else if (currentState == game.GameState.INVENTORY) {
            drawInventoryScreen(g2d, scale);
        } else if (currentState == game.GameState.SKILL_MENU) {
            drawSkillMenu(g2d, scale, playerObj);
        } else if (currentState == game.GameState.SUBCLASS_SELECTION) {
            drawSubclassSelectionScreen(g2d, scale);
        } else if (currentState == game.GameState.AUDIO_MENU) {
            drawHUD(g2d, scale, playerObj);
            drawAudioMenu(g2d, scale);
        } else if (currentState == game.GameState.DESCRIPTION) {
            drawHUD(g2d, scale, playerObj);
            drawDescriptionPopup(g2d, scale);
        } else if (InteractionSysteme.isMenuOpen()) {
            drawInteractionMenu(g2d, scale);
        } else if (combatSysteme.isCombatOpen()) {
            drawCombatMenu(g2d, scale);
        } else if (currentState == game.GameState.PLAYING) {
            drawHUD(g2d, scale, playerObj);
        }
    }

    private void drawCombatMenu(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.78);
        int boxH = (int) (h * 0.76);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond Noir uni
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);
        
        // Bordure blanche =
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke((int)Math.max(1, 2 * scale))); 
        g2d.drawRect(bx, by, boxW, boxH);           
        int inset = (int)(4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset*2, boxH - inset*2);

        // --- CALCULE DES POLICES DYNAMIQUES ---
        int fontTitleSize = Math.max(12, (int) (18 * scale));
        int fontBodySize = Math.max(10, (int) (15 * scale));
        int fontLogSize = Math.max(9, (int) (13 * scale));
        int lineSpacing = (int) (24 * scale);

        // --- PARTIE HAUTE : Entités ---
        int midY = by + (int)(220 * scale); 
        
        List<entity> allies = combatSysteme.getAllyGroup();
        List<summon> summons = combatSysteme.getSummonGroup();
        List<entity> enemies = combatSysteme.getEnemyGroup();
        entity activeActor = combatSysteme.getCurrentActor();
        boolean isCombatEnding = combatSysteme.isCombatEnding();
        boolean isVictory = combatSysteme.isVictory();

        // 1. Rendu du Groupe Allié (Gauche)
        int allyY = by + (int)(28 * scale);
        int allyX = bx + (int)(20 * scale);
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.setColor(new Color(100, 180, 255));
        g2d.drawString("ALLIÉS", allyX, allyY);
        allyY += (int)(20 * scale);

        for (int i = 0; i < allies.size(); i++) {
            entity ally = allies.get(i);
            boolean isActive = (ally == activeActor && !isCombatEnding);
            
            String prefix = isActive ? ">> " : "   ";
            g2d.setFont(new Font("Monospaced", isActive ? Font.BOLD : Font.PLAIN, fontBodySize));
            
            if (ally.isDead()) {
                g2d.setColor(Color.DARK_GRAY);
            } else if (isActive) {
                g2d.setColor(Color.YELLOW); 
            } else {
                g2d.setColor(i == 0 ? new Color(100, 180, 255) : Color.GREEN);
            }
            
            String status = ally.isDead() ? "[K.O.]" : "PV: " + ally.getLifePoint() + "/" + ally.getMaxLifePoint() + " MP: " + ally.getMana() + "/" + ally.getMaxMana();
            g2d.drawString(prefix + ally.getName() + " (" + status + ")", allyX, allyY);
            allyY += (int)(15 * scale);

            if (!ally.isDead()) {
                // Mini-barre PV & MP sous le personnage
                int barW = (int)(110 * scale);
                int barH = (int)(5 * scale);
                int barX = allyX + (int)(16 * scale);

                // Barre PV
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRect(barX, allyY, barW, barH);
                float hpRatio = Math.max(0f, Math.min(1f, (float)ally.getLifePoint() / Math.max(1, ally.getMaxLifePoint())));
                Color hpCol = (hpRatio > 0.5f) ? new Color(50, 205, 50) : (hpRatio > 0.25f ? Color.ORANGE : Color.RED);
                g2d.setColor(hpCol);
                g2d.fillRect(barX, allyY, (int)(barW * hpRatio), barH);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(barX, allyY, barW, barH);

                // Barre MP
                int mpBarX = barX + barW + (int)(8 * scale);
                int mpBarW = (int)(55 * scale);
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRect(mpBarX, allyY, mpBarW, barH);
                float mpRatio = Math.max(0f, Math.min(1f, (float)ally.getMana() / Math.max(1, ally.getMaxMana())));
                g2d.setColor(new Color(60, 160, 255));
                g2d.fillRect(mpBarX, allyY, (int)(mpBarW * mpRatio), barH);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(mpBarX, allyY, mpBarW, barH);

                allyY += (int)(11 * scale);

                // Bonus et Malus sous les barres
                java.util.List<com.eltim.rogue.alteration.alteration> buffs = ally.getBuffs();
                java.util.List<com.eltim.rogue.alteration.alteration> debuffs = ally.getDebuffs();
                boolean isStunned = ally.isStunned();

                g2d.setFont(new Font("Monospaced", Font.PLAIN, Math.max(8, (int)(10 * scale))));
                if (buffs.isEmpty() && debuffs.isEmpty() && !isStunned) {
                    g2d.setColor(new Color(100, 100, 100));
                    g2d.drawString("   Effets: aucun", barX, allyY);
                } else {
                    int curEffX = barX + (int)(6 * scale);

                    // Affichage des Bonus (Vert/Cyan)
                    for (com.eltim.rogue.alteration.alteration b : buffs) {
                        g2d.setColor(new Color(80, 230, 130));
                        String tag = b.getFormattedTag();
                        g2d.drawString(tag + " ", curEffX, allyY);
                        curEffX += g2d.getFontMetrics().stringWidth(tag + " ");
                    }

                    // Affichage des Malus (Rouge)
                    for (com.eltim.rogue.alteration.alteration d : debuffs) {
                        g2d.setColor(new Color(255, 95, 95));
                        String tag = d.getFormattedTag();
                        g2d.drawString(tag + " ", curEffX, allyY);
                        curEffX += g2d.getFontMetrics().stringWidth(tag + " ");
                    }

                    if (isStunned) {
                        g2d.setColor(new Color(255, 140, 50));
                        g2d.drawString("[- Étourdi] ", curEffX, allyY);
                    }
                }
                allyY += (int)(13 * scale);
            } else {
                allyY += (int)(6 * scale);
            }
        }

        // Rendu des Invocations
        for (int i = 0; i < summons.size(); i++) {
            summon s = summons.get(i);
            boolean isActive = (s == activeActor && !isCombatEnding);
            
            String prefix = isActive ? ">> " : "   ";
            g2d.setFont(new Font("Monospaced", isActive ? Font.BOLD : Font.PLAIN, fontBodySize));
            
            if (s.isDead()) {
                g2d.setColor(Color.DARK_GRAY);
            } else if (isActive) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.PINK);
            }
            
            String status = s.isDead() ? "[EXPIRED]" : "PV: " + s.getLifePoint() + " (" + s.getTurnsRemaining() + "t)";
            g2d.drawString(prefix + s.getName() + " (" + status + ")", allyX, allyY);
            allyY += (int)(16 * scale);
        }

        // VS (Centre de l'écran)
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.drawString("VS", bx + boxW / 2 - (int)(10*scale), by + (int)(80*scale));

        // 2. Rendu du Groupe Ennemi (Droite)
        int enemyY = by + (int)(28 * scale);
        int enemyX = bx + boxW / 2 + (int)(20 * scale); 
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.setColor(Color.RED);
        g2d.drawString("ENNEMIS", enemyX, enemyY);
        enemyY += (int)(20 * scale);

        int targetIdx = combatSysteme.getTargetEnemyIndex();
        for (int i = 0; i < enemies.size(); i++) {
            entity enemy = enemies.get(i);
            boolean isPlayerTurn = (activeActor != null && combatSysteme.getAllyGroup().contains(activeActor) && !isCombatEnding);
            boolean isTargeted = (isPlayerTurn && i == targetIdx);
            
            if (enemy.isDead()) {
                g2d.setColor(Color.DARK_GRAY);
            } else if (isTargeted) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(Color.RED);
            }
            
            g2d.setFont(new Font("Monospaced", isTargeted ? Font.BOLD : Font.PLAIN, fontBodySize));
            
            String prefix = isTargeted ? "◄ " : "  ";
            String suffix = isTargeted ? " ►" : "  ";
            String status = enemy.isDead() ? "[MORT]" : "PV: " + enemy.getLifePoint() + "/" + enemy.getMaxLifePoint();
            g2d.drawString(prefix + enemy.getName() + " (" + status + ")" + suffix, enemyX, enemyY);
            enemyY += (int)(15 * scale);

            // Si c'est l'ennemi ciblé (focus), afficher sa barre et ses bonus / malus
            if (!enemy.isDead() && isTargeted) {
                int barW = (int)(160 * scale);
                int barH = (int)(5 * scale);
                int barX = enemyX + (int)(16 * scale);

                // Barre de PV de l'ennemi
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRect(barX, enemyY, barW, barH);
                float hpRatio = Math.max(0f, Math.min(1f, (float)enemy.getLifePoint() / Math.max(1, enemy.getMaxLifePoint())));
                g2d.setColor(new Color(230, 60, 60));
                g2d.fillRect(barX, enemyY, (int)(barW * hpRatio), barH);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(barX, enemyY, barW, barH);

                enemyY += (int)(11 * scale);

                // Bonus et Malus sous la barre de l'ennemi ciblé
                java.util.List<com.eltim.rogue.alteration.alteration> buffs = enemy.getBuffs();
                java.util.List<com.eltim.rogue.alteration.alteration> debuffs = enemy.getDebuffs();
                boolean isStunned = enemy.isStunned();

                g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, (int)(10 * scale))));
                if (buffs.isEmpty() && debuffs.isEmpty() && !isStunned) {
                    g2d.setColor(new Color(110, 110, 110));
                    g2d.drawString("   Effets: aucun", barX, enemyY);
                } else {
                    int curEffX = barX + (int)(6 * scale);

                    // Affichage des Bonus de l'ennemi (Vert)
                    for (com.eltim.rogue.alteration.alteration b : buffs) {
                        g2d.setColor(new Color(80, 230, 130));
                        String tag = b.getFormattedTag();
                        g2d.drawString(tag + " ", curEffX, enemyY);
                        curEffX += g2d.getFontMetrics().stringWidth(tag + " ");
                    }

                    // Affichage des Malus de l'ennemi (Rouge)
                    for (com.eltim.rogue.alteration.alteration d : debuffs) {
                        g2d.setColor(new Color(255, 90, 90));
                        String tag = d.getFormattedTag();
                        g2d.drawString(tag + " ", curEffX, enemyY);
                        curEffX += g2d.getFontMetrics().stringWidth(tag + " ");
                    }

                    if (isStunned) {
                        g2d.setColor(new Color(255, 140, 50));
                        g2d.drawString("[- Étourdi] ", curEffX, enemyY);
                    }
                }
                enemyY += (int)(14 * scale);
            } else if (!enemy.isDead()) {
                enemyY += (int)(6 * scale);
            }
        }

        // Séparation (Milieu)
        g2d.setColor(Color.WHITE);
        g2d.drawLine(bx + (int)(10*scale), midY, bx + boxW - (int)(10*scale), midY);
        g2d.drawLine(bx + (int)(10*scale), midY + (int)(4*scale), bx + boxW - (int)(10*scale), midY + (int)(4*scale));

        // --- PARTIE BASSE : Actions & Log ---
        int actionX = bx + (int)(20 * scale);
        int headerY = midY + (int)(30 * scale);
        
        // En-tête du tour actif
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontBodySize));
        if (isCombatEnding) {
            if (isVictory) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("--- VICTOIRE ! ---", actionX, headerY);
            } else {
                g2d.setColor(Color.RED);
                g2d.drawString("--- GAME OVER ---", actionX, headerY);
            }
        } else if (activeActor != null) {
            if (activeActor == combatSysteme.getPlayer()) {
                g2d.setColor(Color.CYAN);
                g2d.drawString("--- À VOTRE TOUR ---", actionX, headerY);
            } else if (combatSysteme.getAllyGroup().contains(activeActor)) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("--- TOUR DE " + activeActor.getName().toUpperCase() + " ---", actionX, headerY);
            } else if (combatSysteme.getSummonGroup().contains(activeActor)) {
                g2d.setColor(Color.PINK);
                g2d.drawString("--- TOUR INVOCATION ---", actionX, headerY);
            } else if (combatSysteme.getEnemyGroup().contains(activeActor)) {
                g2d.setColor(Color.ORANGE);
                g2d.drawString("--- TOUR ENNEMIS ---", actionX, headerY);
            }
        }

        // Options d'action
        int actionY = headerY + (int)(30 * scale);
        java.util.List<String> options = combatSysteme.getOptions();
        int sel = combatSysteme.getSelection();

        g2d.setFont(new Font("Monospaced", Font.BOLD, fontBodySize));
        for (int i = 0; i < options.size(); i++) {
            if (i == sel) {
                g2d.setColor(Color.CYAN);
                g2d.drawString("=> [ " + options.get(i) + " ]", actionX, actionY + (i * (int)(25 * scale)));
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString("     " + options.get(i), actionX, actionY + (i * (int)(25 * scale)));
            }
        }

        // Séparateur vertical
        int logX = bx + boxW / 2;
        g2d.setColor(Color.WHITE);
        g2d.drawLine(logX - (int)(10*scale), midY + (int)(10*scale), logX - (int)(10*scale), by + boxH - (int)(10*scale));

        // Historique du combat coloré dynamiquement
        g2d.setFont(new Font("Monospaced", Font.PLAIN, fontLogSize));
        java.util.List<String> log = combatSysteme.getLog();
        for (int i = 0; i < log.size(); i++) {
            String line = log.get(i);
            
            if (line.contains("CRITIQUE")) {
                g2d.setColor(Color.YELLOW);
            } else if (line.contains("dégâts") || line.contains("touche") || line.contains("attaque") || line.contains("[Seconde Arme]")) {
                g2d.setColor(new Color(255, 120, 120)); 
            } else if (line.contains("rate") || line.contains("échouée") || line.contains("inatteignable")) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else if (line.contains("VICTOIRE")) {
                g2d.setColor(Color.GREEN);
            } else if (line.contains("DÉFAITE") || line.contains("GAME OVER")) {
                g2d.setColor(Color.RED);
            } else if (line.contains("Loot") || line.contains("Gain") || line.contains("XP")) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(new Color(200, 200, 200));
            }
            
            g2d.drawString(line, logX + (int)(10*scale), midY + (int)(30 * scale) + (i * (int)(20 * scale)));
        }
    }

    private void drawGameOverScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();

        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, w, h);

        String[] skull = {
            "    _____ ",
            "   /     \\",
            "  | () () |",
            "   \\  ^  /",
            "    ||||||",
            "    ||||||"
        };

        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(14, (int)(28 * scale))));
        g2d.setColor(new Color(180, 0, 0)); 
        FontMetrics fmSkull = g2d.getFontMetrics();
        int skullStartY = h / 2 - (int)(140 * scale);
        for (int i = 0; i < skull.length; i++) {
            int sw = fmSkull.stringWidth(skull[i]);
            g2d.drawString(skull[i], (w - sw) / 2, skullStartY + i * (int)(32 * scale));
        }

        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(24, (int)(52 * scale))));
        g2d.setColor(new Color(220, 30, 30)); 
        String gameOverText = "GAME OVER";
        FontMetrics fmTitle = g2d.getFontMetrics();
        int titleW = fmTitle.stringWidth(gameOverText);
        int titleY = h / 2 + (int)(60 * scale);
        g2d.drawString(gameOverText, (w - titleW) / 2, titleY);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, Math.max(10, (int)(18 * scale))));
        g2d.setColor(new Color(160, 160, 160)); 
        String subtitle = "Le Héros est tombé au combat...";
        FontMetrics fmSub = g2d.getFontMetrics();
        int subW = fmSub.stringWidth(subtitle);
        g2d.drawString(subtitle, (w - subW) / 2, titleY + (int)(40 * scale));

        float blinkAlpha = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 400.0));
        g2d.setColor(new Color(255, 255, 255, (int)(blinkAlpha * 255)));
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(12, (int)(20 * scale))));
        String restartText = "[ ENTRÉE ] Recommencer";
        FontMetrics fmRestart = g2d.getFontMetrics();
        int restartW = fmRestart.stringWidth(restartText);
        g2d.drawString(restartText, (w - restartW) / 2, titleY + (int)(100 * scale));

        g2d.setColor(new Color(150, 150, 150, (int)(blinkAlpha * 200)));
        g2d.setFont(new Font("Monospaced", Font.PLAIN, Math.max(10, (int)(16 * scale))));
        String quitText = "[ ÉCHAP ] Quitter";
        FontMetrics fmQuit = g2d.getFontMetrics();
        int quitW = fmQuit.stringWidth(quitText);
        g2d.drawString(quitText, (w - quitW) / 2, titleY + (int)(130 * scale));
    }

    private void drawInteractionMenu(Graphics2D g2d, float scale) {
        String targetName = InteractionSysteme.getTarget() != null && InteractionSysteme.getTarget().getName() != null 
                             ? InteractionSysteme.getTarget().getName() 
                             : "Inconnu";
        String title = " Rencontre : " + targetName + " ";

        int fontTitleSize = Math.max(12, (int)(18 * scale));
        int fontOptSize = Math.max(10, (int)(16 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        FontMetrics fmTitle = g2d.getFontMetrics();
        int maxTextWidth = fmTitle.stringWidth(title);

        g2d.setFont(new Font("Monospaced", Font.BOLD, fontOptSize));
        FontMetrics fmOpt = g2d.getFontMetrics();
        java.util.List<String> options = InteractionSysteme.getOptions();
        for (String opt : options) {
            int optW = fmOpt.stringWidth("=> [ " + opt + " ]");
            if (optW > maxTextWidth) maxTextWidth = optW;
        }

        int boxWidth = Math.max((int)(380 * scale), maxTextWidth + (int)(60 * scale));
        boxWidth = Math.min((int)(getWidth() * 0.78), boxWidth);
        int boxHeight = (int)(250 * scale);
        int bx = (getWidth() - boxWidth) / 2;
        int by = (getHeight() - boxHeight) / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxWidth, boxHeight);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke((int)Math.max(1, 2 * scale))); 
        g2d.drawRect(bx, by, boxWidth, boxHeight);           
        int inset = (int)(4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxWidth - inset*2, boxHeight - inset*2);

        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        int titleWidth = fmTitle.stringWidth(title);
        g2d.drawString(title, bx + (boxWidth - titleWidth) / 2, by + (int)(30 * scale));

        g2d.drawLine(bx + (int)(10*scale), by + (int)(40*scale), bx + boxWidth - (int)(10*scale), by + (int)(40*scale));
        g2d.drawLine(bx + (int)(10*scale), by + (int)(44*scale), bx + boxWidth - (int)(10*scale), by + (int)(44*scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, fontOptSize));
        int sel = InteractionSysteme.getSelection();

        for (int i = 0; i < options.size(); i++) {
            if (i == sel) {
                g2d.setColor(Color.WHITE);
                g2d.drawString("=> [ " + options.get(i) + " ]", bx + (int)(30 * scale), by + (int)(90 * scale) + (i * (int)(30 * scale)));
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString("     " + options.get(i), bx + (int)(30 * scale), by + (int)(90 * scale) + (i * (int)(30 * scale)));
            }
        }
    }

    // =============================================
    // ============= HUD EN JEU ====================
    // =============================================
    private void drawHUD(Graphics2D g2d, float scale, entity playerEntity) {
        if (playerEntity == null || !(playerEntity instanceof player)) return;
        player p = (player) playerEntity;
        int w = getWidth();
        int h = getHeight();

        // --- Polices ---
        int fontMain = Math.max(11, (int)(14 * scale));
        int fontSmall = Math.max(9, (int)(11 * scale));
        int fontTitle = Math.max(12, (int)(16 * scale));

        // ============================================
        // PARTIE GAUCHE-HAUTE : Barres du joueur
        // ============================================
        int hudX = (int)(42 * scale);
        int hudY = (int)(38 * scale); // En haut à gauche avec marge de sécurité ATH
        int barW = (int)(180 * scale);
        int barH = (int)(14 * scale);
        int spacing = (int)(22 * scale);
        int bgPad = (int)(8 * scale);

        // Fond semi-transparent derrière le HUD joueur
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(hudX - bgPad, hudY - bgPad,
                barW + bgPad * 2 + (int)(80 * scale), spacing * 3 + barH + bgPad * 2 + (int)(20 * scale),
                (int)(6 * scale), (int)(6 * scale));

        // Nom + Niveau du joueur
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontMain));
        g2d.setColor(new Color(100, 180, 255));
        g2d.drawString(p.getName() + "  Niv." + p.getLevel(), hudX, hudY + fontMain);

        int barStartY = hudY + fontMain + (int)(6 * scale);

        // Barre PV
        drawBar(g2d, scale, hudX, barStartY, barW, barH,
                p.getLifePoint(), p.getMaxLifePoint(),
                new Color(180, 40, 40), new Color(60, 180, 60),
                "PV", fontMain);

        // Barre Mana
        drawBar(g2d, scale, hudX, barStartY + spacing, barW, barH,
                p.getMana(), p.getMaxMana(),
                new Color(30, 30, 80), new Color(60, 100, 220),
                "MP", fontMain);

        // Barre XP
        int xpForNext = p.getLevel() * 100;
        int currentXp = p.getXp() % xpForNext;
        drawBar(g2d, scale, hudX, barStartY + spacing * 2, barW, (int)(barH * 0.65),
                currentXp, xpForNext,
                new Color(40, 30, 50), new Color(180, 120, 255),
                "XP", fontSmall);

        // ============================================
        // PARTIE GAUCHE-HAUTE : Barres des alliés (sous le joueur)
        // ============================================
        List<npc> party = p.getParty();
        if (!party.isEmpty()) {
            int allyBarW = (int)(barW * 0.65);
            int allyBarH = (int)(barH * 0.75);
            int allySpacing = (int)(17 * scale);
            int allyX = hudX;
            // Commence juste en dessous du bloc joueur
            int playerBlockBottom = barStartY + spacing * 2 + (int)(barH * 0.65) + (int)(8 * scale);

            // Calcul de la hauteur du bloc allié
            int allyBlockH = party.size() * (allySpacing * 2 + (int)(16 * scale)) + (int)(10 * scale);

            // Fond semi-transparent derrière le bloc allié
            g2d.setColor(new Color(0, 0, 0, 140));
            g2d.fillRoundRect(allyX - bgPad, playerBlockBottom - bgPad,
                    allyBarW + bgPad * 2 + (int)(60 * scale), allyBlockH + bgPad * 2,
                    (int)(6 * scale), (int)(6 * scale));

            int currentAllyY = playerBlockBottom + (int)(4 * scale);
            for (npc ally : party) {
                // Nom allié
                g2d.setFont(new Font("Monospaced", Font.BOLD, fontSmall));
                g2d.setColor(ally.isDead() ? Color.DARK_GRAY : Color.GREEN);
                String allyLabel = ally.getName() + (ally.isDead() ? " [K.O.]" : "");
                g2d.drawString(allyLabel, allyX, currentAllyY + (int)(10 * scale));
                currentAllyY += (int)(14 * scale);

                // PV allié
                drawBar(g2d, scale, allyX, currentAllyY, allyBarW, allyBarH,
                        ally.getLifePoint(), ally.getMaxLifePoint(),
                        new Color(140, 30, 30), new Color(50, 150, 50),
                        "PV", fontSmall);
                currentAllyY += allySpacing;

                // Mana allié
                drawBar(g2d, scale, allyX, currentAllyY, allyBarW, allyBarH,
                        ally.getMana(), ally.getMaxMana(),
                        new Color(25, 25, 70), new Color(50, 80, 190),
                        "MP", fontSmall);
                currentAllyY += allySpacing + (int)(6 * scale);
            }
        }

        // ============================================
        // HAUT-CENTRE : Nom du niveau
        // ============================================
        String lvlName = currentMap.getLevelName();
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitle));
        FontMetrics fmLvl = g2d.getFontMetrics();
        int lvlW = fmLvl.stringWidth(lvlName);
        int lvlX = (w - lvlW) / 2;
        int lvlY = (int)(42 * scale);

        // Fond derrière le nom du niveau
        g2d.setColor(new Color(0, 0, 0, 180));
        int lvlPadX = (int)(14 * scale);
        int lvlPadY = (int)(6 * scale);
        g2d.fillRoundRect(lvlX - lvlPadX, lvlY - fmLvl.getAscent() - lvlPadY,
                lvlW + lvlPadX * 2, fmLvl.getHeight() + lvlPadY * 2,
                (int)(8 * scale), (int)(8 * scale));

        // Bordure du bandeau
        g2d.setColor(new Color(180, 160, 100, 180));
        g2d.setStroke(new BasicStroke(Math.max(1, (int)(1.5f * scale))));
        g2d.drawRoundRect(lvlX - lvlPadX, lvlY - fmLvl.getAscent() - lvlPadY,
                lvlW + lvlPadX * 2, fmLvl.getHeight() + lvlPadY * 2,
                (int)(8 * scale), (int)(8 * scale));

        g2d.setColor(new Color(220, 200, 130));
        g2d.drawString(lvlName, lvlX, lvlY);

        // ============================================
        // BAS-DROITE : Raccourcis clavier
        // ============================================
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontSmall));
        g2d.setColor(new Color(180, 180, 180, 200));
        g2d.drawString("[E] Inventaire  [K] Compétences  [O] Options", w - (int)(390 * scale), h - (int)(40 * scale));

        // ============================================
        // BAS-GAUCHE : Terminal d'exploration
        // ============================================
        java.util.List<String> rawLog = com.eltim.rogue.system.ExplorationLog.getLog();
        if (!rawLog.isEmpty()) {
            int termFontSize = Math.max(8, (int)(11 * scale));
            int termLineH = Math.max(12, (int)(15 * scale));
            int maxChars = Math.max(30, (int)(45 * scale));
            java.util.List<String> exploLog = wrapLogLines(rawLog, maxChars);

            if (exploLog.size() > 8) {
                exploLog = exploLog.subList(exploLog.size() - 8, exploLog.size());
            }

            g2d.setFont(new Font("Monospaced", Font.PLAIN, termFontSize));
            FontMetrics fmTerm = g2d.getFontMetrics();
            int maxLineWidth = 0;
            for (String l : exploLog) {
                int lw = fmTerm.stringWidth(l);
                if (lw > maxLineWidth) maxLineWidth = lw;
            }

            int termW = Math.max((int)(360 * scale), maxLineWidth + (int)(24 * scale));
            termW = Math.min((int)(getWidth() * 0.70), termW);
            int termH = exploLog.size() * termLineH + (int)(14 * scale);
            int termX = (int)(42 * scale);
            int termY = h - termH - (int)(40 * scale);

            // Fond terminal
            g2d.setColor(new Color(0, 10, 0, 180));
            g2d.fillRoundRect(termX - (int)(4*scale), termY - (int)(4*scale),
                    termW + (int)(8*scale), termH + (int)(8*scale),
                    (int)(4*scale), (int)(4*scale));
            // Bordure verte fine
            g2d.setColor(new Color(0, 180, 0, 100));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(termX - (int)(4*scale), termY - (int)(4*scale),
                    termW + (int)(8*scale), termH + (int)(8*scale),
                    (int)(4*scale), (int)(4*scale));

            for (int i = 0; i < exploLog.size(); i++) {
                String line = exploLog.get(i);
                if (line.contains("Succès")) {
                    g2d.setColor(new Color(80, 220, 80));
                } else if (line.contains("Échec")) {
                    g2d.setColor(new Color(220, 80, 80));
                } else if (line.startsWith("[")) {
                    g2d.setColor(new Color(180, 220, 255));
                } else {
                    g2d.setColor(new Color(140, 220, 140));
                }
                g2d.drawString(line, termX, termY + (int)(12*scale) + i * termLineH);
            }
        }
    }

    private java.util.List<String> wrapLogLines(java.util.List<String> rawLines, int maxChars) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String line : rawLines) {
            if (line.length() <= maxChars) {
                result.add(line);
            } else {
                java.util.List<String> wrapped = wrapText(line, maxChars);
                result.addAll(wrapped);
            }
        }
        return result;
    }

    /**
     * Dessine une barre de stat (PV, Mana, XP) avec label et valeurs numériques.
     */
    private void drawBar(Graphics2D g2d, float scale, int x, int y, int barW, int barH,
                         int current, int max, Color bgColor, Color fillColor,
                         String label, int fontSize) {
        if (max <= 0) max = 1;
        float ratio = Math.max(0, Math.min(1, (float) current / max));
        int fillW = (int)(barW * ratio);

        // Fond de la barre
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y, barW, barH, (int)(4 * scale), (int)(4 * scale));

        // Remplissage
        if (fillW > 0) {
            // Dégradé subtil pour le remplissage
            Color brighter = new Color(
                    Math.min(255, fillColor.getRed() + 40),
                    Math.min(255, fillColor.getGreen() + 40),
                    Math.min(255, fillColor.getBlue() + 40));
            GradientPaint gp = new GradientPaint(x, y, brighter, x, y + barH, fillColor);
            g2d.setPaint(gp);
            g2d.fillRoundRect(x, y, fillW, barH, (int)(4 * scale), (int)(4 * scale));
        }

        // Bordure fine
        g2d.setColor(new Color(200, 200, 200, 120));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, barW, barH, (int)(4 * scale), (int)(4 * scale));

        // Texte : "PV 8/10"
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        g2d.setColor(Color.WHITE);
        String text = label + " " + current + "/" + max;
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (barW - fm.stringWidth(text)) / 2;
        int textY = y + (barH + fm.getAscent()) / 2 - (int)(2 * scale);
        g2d.drawString(text, textX, textY);
    }

    // =============================================
    // ============= DÉFORMATION CATHODIQUE 3D =====
    // =============================================
    private void updateBarrelLUT(int w, int h) {
        if (w == cachedW && h == cachedH && lutMap != null) return;
        cachedW = w;
        cachedH = h;
        lutMap = new int[w * h];

        double cx = w / 2.0;
        double cy = h / 2.0;
        double k1 = CRT_CURVATURE_PRIMARY;   // Courbure principale
        double k2 = CRT_CURVATURE_SECONDARY; // Courbure secondaire

        for (int y = 0; y < h; y++) {
            double dy = (y - cy) / cy;
            for (int x = 0; x < w; x++) {
                double dx = (x - cx) / cx;
                double r2 = dx * dx + dy * dy;

                double factor = 1.0 + k1 * r2 + k2 * r2 * r2;

                double sx = cx + dx * factor * cx;
                double sy = cy + dy * factor * cy;

                int ix = (int) Math.round(sx);
                int iy = (int) Math.round(sy);

                int index = y * w + x;
                if (ix >= 0 && ix < w && iy >= 0 && iy < h) {
                    lutMap[index] = iy * w + ix;
                } else {
                    lutMap[index] = -1; // Cadre du tube cathodique
                }
            }
        }
    }

    private void applyCRTBarrelDistortion(int w, int h) {
        if (lutMap == null || sceneBuffer == null || distortedBuffer == null) return;
        int[] srcPixels = ((DataBufferInt) sceneBuffer.getRaster().getDataBuffer()).getData();
        int[] dstPixels = ((DataBufferInt) distortedBuffer.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < dstPixels.length; i++) {
            int srcIndex = lutMap[i];
            if (srcIndex >= 0 && srcIndex < srcPixels.length) {
                dstPixels[i] = srcPixels[srcIndex];
            } else {
                dstPixels[i] = 0xFF0B0B10; // Remplissage noir/sombre profond de cadre de tube TV
            }
        }
    }

    private void drawCRTFilterOverlay(Graphics2D g2d, int w, int h) {
        if (w <= 0 || h <= 0) return;

        // 1. Scanlines droites & Balayage TV (en dehors de la courbure pour un effet vieil écran rétro pur)
        scanlineOffset += 0.12f;
        if (scanlineOffset >= 3) scanlineOffset = 0;

        g2d.setColor(new Color(0, 0, 0, 36));
        for (float y = scanlineOffset; y < h; y += 3) {
            g2d.fillRect(0, (int) y, w, 1);
        }

        // Micro-scintillement / Sauts de ligne aléatoires de rafraîchissement d'écran d'arcade
        int randomSkipLine = (int)(Math.random() * (h / 4)) * 4;
        g2d.setColor(new Color(255, 255, 255, 10));
        g2d.fillRect(0, randomSkipLine, w, 2);

        // 2. Vignetage et lueur de verre (mis en cache)
        if (crtOverlay == null || crtOverlay.getWidth() != w || crtOverlay.getHeight() != h) {
            createCRTOverlay(w, h);
        }

        float flickerAlpha = 0.98f + (float)(Math.random() * 0.02f);
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flickerAlpha));

        g2d.drawImage(crtOverlay, 0, 0, null);

        g2d.setComposite(oldComposite);
    }

    private void createCRTOverlay(int w, int h) {
        crtOverlay = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = crtOverlay.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // A. Reflet du verre bombé (Glass Glare)
        g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 10), w * 0.45f, h * 0.45f, new Color(255, 255, 255, 0)));
        g2d.fillRect(0, 0, w, h);

        // B. Ombrage de vignetage souple aux 4 coins (sans coins noirs bloquants)
        int cornerRadius = (int) (Math.min(w, h) * CRT_CORNER_RADIUS_RATIO);
        if (cornerRadius > 0) {
            g2d.setPaint(new RadialGradientPaint(
                    new Point2D.Float(0, 0), cornerRadius,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0, 0, 0, 100), new Color(0, 0, 0, 40), new Color(0, 0, 0, 0)}
            ));
            g2d.fillRect(0, 0, cornerRadius, cornerRadius);

            g2d.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w, 0), cornerRadius,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0, 0, 0, 100), new Color(0, 0, 0, 40), new Color(0, 0, 0, 0)}
            ));
            g2d.fillRect(w - cornerRadius, 0, cornerRadius, cornerRadius);

            g2d.setPaint(new RadialGradientPaint(
                    new Point2D.Float(0, h), cornerRadius,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0, 0, 0, 100), new Color(0, 0, 0, 40), new Color(0, 0, 0, 0)}
            ));
            g2d.fillRect(0, h - cornerRadius, cornerRadius, cornerRadius);

            g2d.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w, h), cornerRadius,
                    new float[]{0.0f, 0.6f, 1.0f},
                    new Color[]{new Color(0, 0, 0, 100), new Color(0, 0, 0, 40), new Color(0, 0, 0, 0)}
            ));
            g2d.fillRect(w - cornerRadius, h - cornerRadius, cornerRadius, cornerRadius);
        }

        g2d.dispose();
    }

    private void drawCharacterCreationScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.78);
        int boxH = (int) (h * 0.78);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond Noir uni avec double bordure
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke((int) Math.max(1, 2 * scale)));
        g2d.drawRect(bx, by, boxW, boxH);
        int inset = (int) (4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset * 2, boxH - inset * 2);

        int titleSize = Math.max(14, (int) (22 * scale));
        int subtitleSize = Math.max(12, (int) (15 * scale));
        int textSize = Math.max(10, (int) (13 * scale));
        int smallTextSize = Math.max(8, (int) (11 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(Color.WHITE);
        String title = "CRÉATION DE PERSONNAGE";
        FontMetrics fmTitle = g2d.getFontMetrics();
        g2d.drawString(title, bx + (boxW - fmTitle.stringWidth(title)) / 2, by + (int) (28 * scale));

        int startY = by + (int) (55 * scale);
        int startX = bx + (int) (50 * scale);
        int lineSpacing = Math.max(15, (int) (22 * scale));
        int labelWidth = (int) (150 * scale);

        com.eltim.rogue.system.CharacterCreationSystem.Field currentField = com.eltim.rogue.system.CharacterCreationSystem.getCurrentField();

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        int currentY = startY;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Nom:", com.eltim.rogue.system.CharacterCreationSystem.getName() + (com.eltim.rogue.system.CharacterCreationSystem.isEditingName() ? "_" : ""), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.NAME);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Classe:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getBaseClassChoice().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CLASS);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Race:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getRace().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.RACE);
        currentY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.ITALIC, smallTextSize));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("Compétence de race: " + com.eltim.rogue.system.CharacterCreationSystem.getRace().getSpecialSkillPlaceholder(),
                startX + labelWidth, currentY - 3);
        currentY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        drawCreationLine(g2d, startX, currentY, labelWidth, "Sexe:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getGender().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.GENDER);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Croyance:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getBelief().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.BELIEF);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Symbole:", com.eltim.rogue.system.CharacterCreationSystem.getSymbol() + (com.eltim.rogue.system.CharacterCreationSystem.isEditingSymbol() ? "_" : ""), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.SYMBOL);
        currentY += (int)(lineSpacing * 1.4);

        g2d.setFont(new Font("Monospaced", Font.BOLD, subtitleSize));
        g2d.setColor(Color.WHITE);
        g2d.drawString("STATISTIQUES (Points restants: " + com.eltim.rogue.system.CharacterCreationSystem.getAvailablePoints() + ")", startX, currentY);
        currentY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        int bF = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusForce();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Force:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getForce() + " > (" + (bF >= 0 ? "+" : "") + bF + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getForce() + bF), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.FORCE);
        currentY += lineSpacing;
        int bA = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusAgilite();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Agilité:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getAgilite() + " > (" + (bA >= 0 ? "+" : "") + bA + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getAgilite() + bA), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.AGILITE);
        currentY += lineSpacing;
        int bI = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusIntelligence();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Intelligence:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getIntelligence() + " > (" + (bI >= 0 ? "+" : "") + bI + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getIntelligence() + bI), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.INTELLIGENCE);
        currentY += lineSpacing;
        int bC = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusCharisme();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Charisme:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getCharisme() + " > (" + (bC >= 0 ? "+" : "") + bC + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getCharisme() + bC), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CHARISME);
        currentY += lineSpacing;
        int bCo = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusConstitution();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Constitution:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getConstitution() + " > (" + (bCo >= 0 ? "+" : "") + bCo + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getConstitution() + bCo), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CONSTITUTION);
        currentY += lineSpacing;
        int bS = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusSagesse();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Sagesse:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getSagesse() + " > (" + (bS >= 0 ? "+" : "") + bS + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getSagesse() + bS), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.SAGESSE);
        currentY += (int)(lineSpacing * 1.4);

        g2d.setFont(new Font("Monospaced", Font.BOLD, subtitleSize));
        if (currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CONFIRM) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("> COMMENCER L'AVENTURE <", startX, currentY);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("  COMMENCER L'AVENTURE", startX, currentY);
        }
    }

    private void drawCreationLine(Graphics2D g2d, int x, int y, int labelWidth, String label, String value, boolean isSelected) {
        if (isSelected) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("> " + label, x - 20, y);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.drawString("  " + label, x - 20, y);
        }
        g2d.setColor(isSelected ? Color.CYAN : Color.LIGHT_GRAY);
        g2d.drawString(value, x + labelWidth, y);
    }

    private void drawAudioMenu(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.78);
        int boxH = (int) (h * 0.72);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond Noir uni avec double bordure
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);

        g2d.setColor(new Color(255, 215, 0)); // Jaune d'or
        g2d.setStroke(new BasicStroke((int) Math.max(1, 2 * scale)));
        g2d.drawRect(bx, by, boxW, boxH);
        int inset = (int) (4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset * 2, boxH - inset * 2);

        int fontTitle = Math.max(14, (int) (22 * scale));
        int fontOpt = Math.max(11, (int) (15 * scale));
        int fontSub = Math.max(9, (int) (12 * scale));

        // Titre
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitle));
        FontMetrics fmTitle = g2d.getFontMetrics();
        String title = "=== OPTIONS AUDIO & REGULATEUR DE SON ===";
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString(title, bx + (boxW - fmTitle.stringWidth(title)) / 2, by + (int) (35 * scale));

        // Sous-titre explicatif
        g2d.setFont(new Font("Monospaced", Font.ITALIC, fontSub));
        g2d.setColor(Color.LIGHT_GRAY);
        String sub = "Utilisez [Z/S] pour naviguer, [Q/D] pour ajuster le volume";
        FontMetrics fmSub = g2d.getFontMetrics();
        g2d.drawString(sub, bx + (boxW - fmSub.stringWidth(sub)) / 2, by + (int) (62 * scale));

        // Séparateur
        g2d.setColor(Color.GRAY);
        g2d.drawLine(bx + (int) (20 * scale), by + (int) (75 * scale), bx + boxW - (int) (20 * scale), by + (int) (75 * scale));

        com.eltim.rogue.engine.sound.SoundManager soundMgr = com.eltim.rogue.engine.sound.SoundManager.getInstance();
        int sel = game.getAudioSelectedOption();

        int startY = by + (int) (110 * scale);
        int itemSpacing = (int) (40 * scale);

        drawAudioSliderLine(g2d, scale, bx + (int) (40 * scale), startY, "SON TOTAL (MASTER)", soundMgr.getMasterVolume(), sel == 0);
        drawAudioSliderLine(g2d, scale, bx + (int) (40 * scale), startY + itemSpacing, "MUSIQUE & AMBIANCE", soundMgr.getMusicVolume(), sel == 1);
        drawAudioSliderLine(g2d, scale, bx + (int) (40 * scale), startY + itemSpacing * 2, "EFFETS SONORES (VFX)", soundMgr.getSfxVolume(), sel == 2);

        // Bouton Retour
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontOpt));
        int retY = startY + itemSpacing * 3 + (int) (10 * scale);
        if (sel == 3) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("=> [ FERMER ET REVENIR AU JEU ]", bx + (int) (40 * scale), retY);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("   [ FERMER ET REVENIR AU JEU ]", bx + (int) (40 * scale), retY);
        }
    }

    private void drawAudioSliderLine(Graphics2D g2d, float scale, int x, int y, String label, float value, boolean isSelected) {
        int fontOpt = Math.max(11, (int) (15 * scale));
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontOpt));

        String prefix = isSelected ? "=> " : "   ";
        g2d.setColor(isSelected ? Color.YELLOW : Color.WHITE);
        g2d.drawString(prefix + String.format("%-22s", label) + " : ", x, y);

        // Dessin de la barre de volume slider [========  ]
        int pct = Math.round(value * 100);
        int barLength = 16;
        int filled = Math.round(value * barLength);

        StringBuilder barStr = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) barStr.append("=");
            else barStr.append(" ");
        }
        barStr.append("] ").append(String.format("%3d%%", pct));

        g2d.setColor(isSelected ? Color.CYAN : Color.GREEN);
        g2d.drawString(barStr.toString(), x + (int) (310 * scale), y);
    }

    private void drawInventoryScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.78);
        int boxH = (int) (h * 0.78);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke((int) Math.max(1, 2 * scale)));
        g2d.drawRect(bx, by, boxW, boxH);
        int inset = (int) (4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset * 2, boxH - inset * 2);

        int titleSize = Math.max(14, (int) (22 * scale));
        int textSize = Math.max(10, (int) (13 * scale));
        int smallSize = Math.max(8, (int) (11 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(Color.WHITE);
        String title = "INVENTAIRE ET ÉQUIPEMENT";
        FontMetrics fmTitle = g2d.getFontMetrics();
        g2d.drawString(title, bx + (boxW - fmTitle.stringWidth(title)) / 2, by + (int) (28 * scale));

        com.eltim.rogue.entity.base.entity activeChar = com.eltim.rogue.system.InventorySystem.getActiveCharacter();
        if (activeChar == null) return;

        int leftX = bx + (int) (40 * scale);
        int rightX = bx + (int) (boxW * 0.52);
        int startY = by + (int) (55 * scale);
        int lineSpacing = Math.max(16, (int) (20 * scale));

        // Draw Equipment Column (Left)
        g2d.setFont(new Font("Monospaced", Font.BOLD, textSize));
        g2d.setColor(Color.CYAN);
        g2d.drawString("ÉQUIPEMENT : " + activeChar.getName().toUpperCase() + " (TAB)", leftX, startY);
        
        String[] slotNames = {
            "Casque", "Armure", "Jambières", "Chaussures", "Gants",
            "Collier", "Anneau 1", "Anneau 2",
            "Main Droite", "Main Gauche", "Arme Secondaire"
        };
        com.eltim.rogue.item.base.item[] equippedItems = {
            activeChar.helmet, activeChar.armor, activeChar.leggings, activeChar.shoes, activeChar.gloves,
            activeChar.necklace, activeChar.ring1, activeChar.ring2,
            activeChar.rightHand, activeChar.leftHand, activeChar.secondaryWeapon
        };

        boolean isEquipCol = (com.eltim.rogue.system.InventorySystem.getCurrentColumn() == com.eltim.rogue.system.InventorySystem.Column.EQUIPMENT);
        int equipIdx = com.eltim.rogue.system.InventorySystem.getEquipmentIndex();

        for (int i = 0; i < slotNames.length; i++) {
            int y = startY + (i + 2) * lineSpacing;
            if (isEquipCol && i == equipIdx) {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("> " + slotNames[i] + ":", leftX - 15, y);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString("  " + slotNames[i] + ":", leftX - 15, y);
            }
            g2d.setColor(Color.CYAN);
            String itemName = (equippedItems[i] != null) ? equippedItems[i].getName() : "(Vide)";
            g2d.drawString(itemName, leftX + (int)(160 * scale), y);
        }

        // Draw Inventory Column (Right)
        g2d.setFont(new Font("Monospaced", Font.BOLD, textSize));
        g2d.setColor(Color.LIGHT_GRAY);
        
        // Draw Tabs
        com.eltim.rogue.system.InventorySystem.Tab activeTab = com.eltim.rogue.system.InventorySystem.getCurrentTab();
        
        String t1 = (activeTab == com.eltim.rogue.system.InventorySystem.Tab.EQUIPMENT ? "[1:ÉQUIP]" : " 1:ÉQUIP ");
        String t2 = (activeTab == com.eltim.rogue.system.InventorySystem.Tab.CONSUMABLES ? "[2:CONSOM]" : " 2:CONSOM ");
        String t3 = (activeTab == com.eltim.rogue.system.InventorySystem.Tab.OBJECTS ? "[3:OBJETS]" : " 3:OBJETS ");

        g2d.setColor(activeTab == com.eltim.rogue.system.InventorySystem.Tab.EQUIPMENT ? Color.YELLOW : Color.LIGHT_GRAY);
        g2d.drawString(t1, rightX, startY);
        g2d.setColor(activeTab == com.eltim.rogue.system.InventorySystem.Tab.CONSUMABLES ? Color.YELLOW : Color.LIGHT_GRAY);
        g2d.drawString(t2, rightX + (int)(100 * scale), startY);
        g2d.setColor(activeTab == com.eltim.rogue.system.InventorySystem.Tab.OBJECTS ? Color.YELLOW : Color.LIGHT_GRAY);
        g2d.drawString(t3, rightX + (int)(210 * scale), startY);


        boolean isInvCol = (com.eltim.rogue.system.InventorySystem.getCurrentColumn() == com.eltim.rogue.system.InventorySystem.Column.INVENTORY);
        int invIdx = com.eltim.rogue.system.InventorySystem.getInventoryIndex();
        java.util.List<com.eltim.rogue.item.base.item> inv = com.eltim.rogue.system.InventorySystem.getFilteredInventory();

        g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
        for (int i = 0; i < Math.min(inv.size(), 20); i++) {
            int y = startY + (i + 2) * lineSpacing;
            if (isInvCol && i == invIdx) {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("> " + inv.get(i).getName(), rightX - 15, y);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString("  " + inv.get(i).getName(), rightX - 15, y);
            }
        }
        if (inv.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("(Vide)", rightX, startY + 2 * lineSpacing);
        }

        // Draw Status Message
        String msg = com.eltim.rogue.system.InventorySystem.statusMessage;
        if (msg != null && !msg.isEmpty() && com.eltim.rogue.system.InventorySystem.statusMessageTimer > 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Monospaced", Font.BOLD, textSize));
            g2d.drawString(msg, w / 2 - g2d.getFontMetrics().stringWidth(msg) / 2, h - (int)(50 * scale));
        }

        // Draw Weapon Slot Prompt
        if (com.eltim.rogue.system.InventorySystem.isPromptingWeaponSlot()) {
            int promptW = (int)(300 * scale);
            int promptH = (int)(150 * scale);
            int promptX = (w - promptW) / 2;
            int promptY = (h - promptH) / 2;
            g2d.setColor(new Color(0, 0, 0, 240));
            g2d.fillRect(promptX, promptY, promptW, promptH);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(promptX, promptY, promptW, promptH);

            g2d.setFont(new Font("Monospaced", Font.BOLD, textSize));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Équiper où ?", promptX + (int)(20 * scale), promptY + (int)(30 * scale));

            String[] prompts = { "Main Droite", "Main Gauche", "Arme Secondaire" };
            int pIdx = com.eltim.rogue.system.InventorySystem.getWeaponPromptIndex();
            for (int i = 0; i < prompts.length; i++) {
                int py = promptY + (int)(70 * scale) + (i * (int)(25 * scale));
                if (i == pIdx) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("> " + prompts[i], promptX + (int)(40 * scale), py);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawString("  " + prompts[i], promptX + (int)(40 * scale), py);
                }
            }
        }
    }

    private void drawDescriptionPopup(Graphics2D g2d, float scale) {
        String desc = com.eltim.rogue.system.InteractionSysteme.getCurrentDescription();
        if (desc == null || desc.isEmpty()) return;

        int w = getWidth();
        int h = getHeight();
        int textSize = Math.max(11, (int)(15 * scale));

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));
        FontMetrics fm = g2d.getFontMetrics();

        java.util.List<String> lines = wrapText(desc, 52);
        int lineH = fm.getHeight();
        int boxW = Math.min((int)(500 * scale), (int)(w * 0.7));
        int boxH = lines.size() * lineH + (int)(60 * scale);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond
        g2d.setColor(new Color(5, 5, 20, 230));
        g2d.fillRoundRect(bx, by, boxW, boxH, (int)(8*scale), (int)(8*scale));

        // Double bordure dorée
        g2d.setColor(new Color(180, 160, 80));
        g2d.setStroke(new BasicStroke(Math.max(1, (int)(2*scale))));
        g2d.drawRoundRect(bx, by, boxW, boxH, (int)(8*scale), (int)(8*scale));
        int inset = (int)(4 * scale);
        g2d.setColor(new Color(120, 100, 40, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(bx + inset, by + inset, boxW - inset*2, boxH - inset*2, (int)(6*scale), (int)(6*scale));

        // Icône ?
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(14, (int)(20 * scale))));
        g2d.setColor(new Color(220, 200, 100));
        g2d.drawString("[ ? ]", bx + (int)(14*scale), by + (int)(26*scale));

        // Texte
        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));
        g2d.setColor(new Color(220, 220, 200));
        int textStartY = by + (int)(46 * scale);
        for (int i = 0; i < lines.size(); i++) {
            g2d.drawString(lines.get(i), bx + (int)(16*scale), textStartY + i * lineH);
        }

        // Instruction de fermeture (clignotante)
        float blink = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 500.0));
        g2d.setColor(new Color(180, 180, 180, (int)(blink * 200)));
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(9, (int)(12 * scale))));
        String closeHint = "[ ENTRÉE ] Fermer";
        FontMetrics fmHint = g2d.getFontMetrics();
        g2d.drawString(closeHint, bx + boxW - fmHint.stringWidth(closeHint) - (int)(14*scale), by + boxH - (int)(10*scale));
    }

    private java.util.List<String> wrapText(String text, int maxChars) {
        java.util.List<String> result = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (current.length() + word.length() + 1 > maxChars && current.length() > 0) {
                result.add(current.toString());
                current = new StringBuilder();
            }
            if (current.length() > 0) current.append(" ");
            current.append(word);
        }
        if (current.length() > 0) result.add(current.toString());
        return result;
    }

    private void drawSkillMenu(Graphics2D g2d, float scale, entity playerEntity) {
        if (playerEntity == null || !(playerEntity instanceof com.eltim.rogue.entity.player)) return;
        com.eltim.rogue.entity.player p = (com.eltim.rogue.entity.player) playerEntity;
        if (p.classe == null) return;

        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.84);
        int boxH = (int) (h * 0.84);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond Noir uni avec double bordure dorée
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);

        g2d.setColor(new Color(220, 180, 60));
        g2d.setStroke(new BasicStroke((int) Math.max(1, 2 * scale)));
        g2d.drawRect(bx, by, boxW, boxH);
        int inset = (int) (4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset * 2, boxH - inset * 2);

        int titleSize = Math.max(14, (int) (22 * scale));
        int bodySize = Math.max(10, (int) (13 * scale));
        int smallSize = Math.max(8, (int) (11 * scale));

        // 1. Titre & Points de compétence
        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(new Color(220, 180, 60));
        String title = "COMPÉTENCES — " + p.classe.name.toUpperCase() + (p.classe.hasSubclass() ? " / " + p.classe.subclass.toUpperCase() : "");
        FontMetrics fmT = g2d.getFontMetrics();
        g2d.drawString(title, bx + (boxW - fmT.stringWidth(title)) / 2, by + (int) (26 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
        g2d.setColor(new Color(100, 220, 255));
        String pts = "Points disponibles : " + p.classe.skillPoints + (p.classe.hasSubclass() ? " (Bi-classe: +1 pt/niv)" : " (Classe pure: +2 pts/niv)");
        FontMetrics fmPts = g2d.getFontMetrics();
        g2d.drawString(pts, bx + (boxW - fmPts.stringWidth(pts)) / 2, by + (int) (46 * scale));

        String statusMsg = com.eltim.rogue.system.SkillMenuSystem.getStatusMessage();
        if (statusMsg != null) {
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(Color.YELLOW);
            FontMetrics fmMsg = g2d.getFontMetrics();
            g2d.drawString(statusMsg, bx + (boxW - fmMsg.stringWidth(statusMsg)) / 2, by + (int) (62 * scale));
        }

        int selSlot = com.eltim.rogue.system.SkillMenuSystem.getSelectedSlot();
        int selTier = com.eltim.rogue.system.SkillMenuSystem.getSelectedTier();
        boolean selectingTree = com.eltim.rogue.system.SkillMenuSystem.isSelectingTree();

        // 2. Barres des 4 Emplacements d'Arbres (Tabs Horizontales en Haut)
        int tabsY = by + (int) (70 * scale);
        int tabW = (boxW - (int) (30 * scale)) / 4;
        int tabH = (int) (32 * scale);

        for (int t = 0; t < 4; t++) {
            com.eltim.rogue.entity.classe.SkillTree tree = p.classe.activeSlots[t];
            int tabX = bx + (int) (15 * scale) + t * tabW;
            boolean isFocusedSlot = (t == selSlot);

            if (isFocusedSlot) {
                g2d.setColor(new Color(60, 50, 20));
                g2d.fillRect(tabX, tabsY, tabW - (int) (5 * scale), tabH);
                g2d.setColor(new Color(255, 215, 0));
                g2d.drawRect(tabX, tabsY, tabW - (int) (5 * scale), tabH);
            } else {
                g2d.setColor(new Color(25, 25, 25));
                g2d.fillRect(tabX, tabsY, tabW - (int) (5 * scale), tabH);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(tabX, tabsY, tabW - (int) (5 * scale), tabH);
            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            String tabHeader;
            if (tree == null) {
                if (isFocusedSlot) {
                    g2d.setColor(Color.YELLOW);
                    tabHeader = "[ + CHOISIR ]";
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    tabHeader = "[ + Emplacement " + (t + 1) + " ]";
                }
            } else {
                g2d.setColor(isFocusedSlot ? Color.YELLOW : Color.LIGHT_GRAY);
                tabHeader = (isFocusedSlot ? "▼ " : "► ") + tree.name;
            }

            FontMetrics fmTab = g2d.getFontMetrics();
            int strX = tabX + (tabW - (int) (5 * scale) - fmTab.stringWidth(tabHeader)) / 2;
            g2d.drawString(tabHeader, Math.max(tabX + 4, strX), tabsY + (int) (20 * scale));
        }

        // 3. Panneau Principal
        int panelX = bx + (int) (15 * scale);
        int panelY = tabsY + tabH + (int) (8 * scale);
        int panelW = boxW - (int) (30 * scale);
        int panelH = (int) (195 * scale);

        g2d.setColor(new Color(15, 15, 20));
        g2d.fillRect(panelX, panelY, panelW, panelH);
        g2d.setColor(new Color(220, 180, 60));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(panelX, panelY, panelW, panelH);

        com.eltim.rogue.entity.classe.SkillTree activeTree = p.classe.activeSlots[selSlot];

        // CAS A : Modal de sélection d'arbre ouvert
        if (selectingTree) {
            java.util.List<com.eltim.rogue.entity.classe.SkillTree> unslotted = com.eltim.rogue.system.SkillMenuSystem.getUnslottedTrees();
            int pickIdx = com.eltim.rogue.system.SkillMenuSystem.getTreePickerIndex();

            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("CHOISIR UN ARBRE POUR L'EMPLACEMENT " + (selSlot + 1) + " (Coût: 1 point - Tier 1 inclus)", panelX + (int) (15 * scale), panelY + (int) (22 * scale));

            int listW = (int) (panelW * 0.38);
            int startItemY = panelY + (int) (36 * scale);
            int rowH = (int) (26 * scale);

            for (int i = 0; i < unslotted.size(); i++) {
                com.eltim.rogue.entity.classe.SkillTree st = unslotted.get(i);
                boolean isPicked = (i == pickIdx);
                int rowY = startItemY + i * rowH;

                if (isPicked) {
                    g2d.setColor(new Color(60, 60, 110));
                    g2d.fillRect(panelX + (int) (10 * scale), rowY - (int) (14 * scale), listW, rowH);
                    g2d.setColor(Color.CYAN);
                    g2d.drawRect(panelX + (int) (10 * scale), rowY - (int) (14 * scale), listW, rowH);
                }

                g2d.setFont(new Font("Monospaced", isPicked ? Font.BOLD : Font.PLAIN, smallSize));
                g2d.setColor(isPicked ? Color.WHITE : Color.LIGHT_GRAY);
                String prefix = isPicked ? "► " : "   ";
                g2d.drawString(prefix + st.name, panelX + (int) (14 * scale), rowY + (int) (4 * scale));
            }

            // Détails de l'arbre survolé (Droite)
            if (pickIdx >= 0 && pickIdx < unslotted.size()) {
                com.eltim.rogue.entity.classe.SkillTree previewTree = unslotted.get(pickIdx);
                int detX = panelX + listW + (int) (20 * scale);
                int detW = panelW - listW - (int) (30 * scale);
                int detY = panelY + (int) (36 * scale);

                g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
                g2d.setColor(Color.CYAN);
                g2d.drawString("Arbre : " + previewTree.name, detX, detY);

                g2d.setFont(new Font("Monospaced", Font.ITALIC, smallSize));
                g2d.setColor(Color.LIGHT_GRAY);
                if (previewTree.description != null && !previewTree.description.isEmpty()) {
                    g2d.drawString(previewTree.description, detX, detY + (int) (18 * scale));
                }

                // Aperçu du Tier 1
                if (!previewTree.skills.isEmpty()) {
                    com.eltim.rogue.entity.classe.Skill t1 = previewTree.skills.get(0);
                    g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
                    g2d.setColor(Color.GREEN);
                    g2d.drawString("⚡ Débloqué immédiatement : TIER 1 - " + t1.name, detX, detY + (int) (42 * scale));

                    g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
                    g2d.setColor(Color.WHITE);
                    java.util.List<String> wrappedT1 = wrapText(t1.description, 45);
                    for (int l = 0; l < Math.min(wrappedT1.size(), 3); l++) {
                        g2d.drawString(wrappedT1.get(l), detX, detY + (int) (58 * scale) + l * (int) (14 * scale));
                    }
                }

                // Aperçu des Tiers 2-5
                g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
                g2d.setColor(new Color(200, 180, 100));
                g2d.drawString("Tiers suivants :", detX, detY + (int) (110 * scale));
                g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
                g2d.setColor(Color.GRAY);
                for (int t = 1; t < previewTree.skills.size(); t++) {
                    com.eltim.rogue.entity.classe.Skill sk = previewTree.skills.get(t);
                    g2d.drawString("• Tier " + sk.tier + " : " + sk.name, detX, detY + (int) (124 * scale) + (t - 1) * (int) (14 * scale));
                }
            }

            // Instructions du picker modal en bas
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(Color.YELLOW);
            String pickerHint = "[↑/↓ ou Z/S] Parcourir  |  [ENTRÉE] Confirmer et équiper (1 pt)  |  [ÉCHAP] Annuler";
            FontMetrics fmPh = g2d.getFontMetrics();
            g2d.drawString(pickerHint, bx + (boxW - fmPh.stringWidth(pickerHint)) / 2, by + boxH - (int) (10 * scale));
            return;
        }

        // CAS B : Emplacement vide (sélectionné)
        if (activeTree == null) {
            int midX = panelX + panelW / 2;
            int cardY = panelY + (int) (35 * scale);

            g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(22, (int) (36 * scale))));
            g2d.setColor(new Color(255, 215, 0));
            String plusSym = "[  +  ]";
            FontMetrics fmPlus = g2d.getFontMetrics();
            g2d.drawString(plusSym, midX - fmPlus.stringWidth(plusSym) / 2, cardY + (int) (20 * scale));

            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            g2d.setColor(Color.WHITE);
            String emptyTitle = "EMPLACEMENT D'ARBRE LIBRE (SLOT " + (selSlot + 1) + "/4)";
            FontMetrics fmEt = g2d.getFontMetrics();
            g2d.drawString(emptyTitle, midX - fmEt.stringWidth(emptyTitle) / 2, cardY + (int) (60 * scale));

            g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
            g2d.setColor(Color.LIGHT_GRAY);
            String desc1 = "Vous pouvez choisir jusqu'à 4 arbres de talents pour votre personnage.";
            String desc2 = "Appuyez sur [ENTRÉE] pour choisir un arbre parmi vos compétences de classe.";
            String desc3 = "Coût : 1 point de compétence. Le Tier 1 sera immédiatement débloqué !";
            FontMetrics fmD = g2d.getFontMetrics();
            g2d.drawString(desc1, midX - fmD.stringWidth(desc1) / 2, cardY + (int) (85 * scale));
            g2d.drawString(desc2, midX - fmD.stringWidth(desc2) / 2, cardY + (int) (102 * scale));
            g2d.drawString(desc3, midX - fmD.stringWidth(desc3) / 2, cardY + (int) (119 * scale));

            boolean canBuyTree = (p.classe.skillPoints >= 1);
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(canBuyTree ? Color.GREEN : new Color(220, 100, 80));
            String actionPrompt = canBuyTree
                    ? "⚡ [ENTRÉE] Choisir un arbre maintenant (Points restants: " + p.classe.skillPoints + ")"
                    : "🔒 Points insuffisants (0 point). Gagnez un niveau pour débloquer un arbre !";
            FontMetrics fmA = g2d.getFontMetrics();
            g2d.drawString(actionPrompt, midX - fmA.stringWidth(actionPrompt) / 2, cardY + (int) (145 * scale));

            // Instruction bas de fenêtre
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(Color.LIGHT_GRAY);
            String hint = "[Q/D ou ←/→] Emplacement  |  [ENTRÉE] Choisir arbre  |  [K/ÉCHAP] Fermer";
            FontMetrics fmHint = g2d.getFontMetrics();
            g2d.drawString(hint, bx + (boxW - fmHint.stringWidth(hint)) / 2, by + boxH - (int) (10 * scale));
            return;
        }

        // CAS C : Emplacement actif avec un Arbre de talents
        int itemH = (int) (32 * scale);
        com.eltim.rogue.entity.classe.Skill focusedSkill = null;

        for (int tier = 0; tier < activeTree.skills.size(); tier++) {
            com.eltim.rogue.entity.classe.Skill skill = activeTree.skills.get(tier);
            int itemY = panelY + (int) (8 * scale) + tier * itemH;
            boolean isSelectedTier = (tier == selTier);
            if (isSelectedTier) focusedSkill = skill;

            boolean isUnlocked = skill.unlocked;
            boolean prevUnlocked = (tier == 0) || activeTree.skills.get(tier - 1).unlocked;
            int cost = p.classe.getSkillCost(skill);
            boolean canBuy = !isUnlocked && prevUnlocked && p.classe.skillPoints >= cost;

            if (isSelectedTier) {
                g2d.setColor(new Color(50, 50, 90));
                g2d.fillRect(panelX + (int) (5 * scale), itemY, panelW - (int) (10 * scale), itemH - (int) (4 * scale));
                g2d.setColor(Color.CYAN);
                g2d.drawRect(panelX + (int) (5 * scale), itemY, panelW - (int) (10 * scale), itemH - (int) (4 * scale));
            }

            String statusTag;
            Color statusColor;
            if (isUnlocked) {
                statusTag = "[✔ DÉBLOQUÉ]";
                statusColor = Color.GREEN;
            } else if (canBuy) {
                statusTag = "[⚡ DISPONIBLE - Coût: " + cost + " pt" + (cost > 1 ? "s" : "") + "]";
                statusColor = Color.YELLOW;
            } else if (prevUnlocked) {
                statusTag = "[🔒 VERROUILLÉ - Manque de points (" + cost + " pts)]";
                statusColor = new Color(220, 130, 40);
            } else {
                statusTag = "[🔒 VERROUILLÉ - Requis: Tier " + tier + "]";
                statusColor = Color.GRAY;
            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            String cursor = isSelectedTier ? "=> " : "   ";
            g2d.setColor(isSelectedTier ? Color.WHITE : Color.LIGHT_GRAY);
            g2d.drawString(cursor + "TIER " + (tier + 1) + " : " + skill.name, panelX + (int) (15 * scale), itemY + (int) (20 * scale));

            g2d.setColor(statusColor);
            FontMetrics fmTag = g2d.getFontMetrics();
            g2d.drawString(statusTag, panelX + panelW - fmTag.stringWidth(statusTag) - (int) (15 * scale), itemY + (int) (20 * scale));
        }

        // Cartouche de description
        if (focusedSkill != null) {
            int descX = panelX;
            int descY = panelY + panelH + (int) (8 * scale);
            int descW = panelW;
            int descH = by + boxH - descY - (int) (28 * scale);

            g2d.setColor(new Color(10, 15, 30));
            g2d.fillRect(descX, descY, descW, descH);
            g2d.setColor(new Color(100, 180, 255));
            g2d.drawRect(descX, descY, descW, descH);

            int cost = p.classe.getSkillCost(focusedSkill);
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(Color.CYAN);
            g2d.drawString("★ TALENT : " + focusedSkill.name.toUpperCase() + " (Arbre: " + activeTree.name + " - Tier " + focusedSkill.tier + " - Coût: " + cost + " pt" + (cost > 1 ? "s" : "") + ")",
                    descX + (int) (12 * scale), descY + (int) (18 * scale));

            g2d.setFont(new Font("Monospaced", Font.ITALIC, smallSize));
            g2d.setColor(Color.WHITE);

            java.util.List<String> wrapDesc = wrapText(focusedSkill.description, 75);
            for (int i = 0; i < Math.min(wrapDesc.size(), 3); i++) {
                g2d.drawString(wrapDesc.get(i), descX + (int) (12 * scale), descY + (int) (36 * scale) + i * (int) (15 * scale));
            }
        }

        // Instructions bas de fenêtre
        g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
        g2d.setColor(Color.LIGHT_GRAY);
        String hint = "[Q/D ou ←/→] Arbre  |  [Z/S ou ↑/↓] Talent  |  [ENTRÉE] Débloquer  |  [K/ÉCHAP] Fermer";
        FontMetrics fmHint = g2d.getFontMetrics();
        g2d.drawString(hint, bx + (boxW - fmHint.stringWidth(hint)) / 2, by + boxH - (int) (10 * scale));
    }

    /**
     * Menu plein écran de bi-classement au niveau 3.
     */
    private void drawSubclassSelectionScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.88);
        int boxH = (int) (h * 0.86);
        int bx = (w - boxW) / 2;
        int by = (h - boxH) / 2;

        // Fond Noir avec bordure dorée
        g2d.setColor(Color.BLACK);
        g2d.fillRect(bx, by, boxW, boxH);

        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke((int) Math.max(1, 2 * scale)));
        g2d.drawRect(bx, by, boxW, boxH);
        int inset = (int) (4 * scale);
        g2d.drawRect(bx + inset, by + inset, boxW - inset * 2, boxH - inset * 2);

        int titleSize = Math.max(14, (int) (22 * scale));
        int bodySize = Math.max(10, (int) (13 * scale));
        int smallSize = Math.max(8, (int) (11 * scale));

        // 1. Titre & Sous-titre
        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(new Color(255, 215, 0));
        String title = "★ MONTÉE AU NIVEAU 3 — CHOIX DE BI-CLASSEMENT ★";
        FontMetrics fmT = g2d.getFontMetrics();
        g2d.drawString(title, bx + (boxW - fmT.stringWidth(title)) / 2, by + (int) (28 * scale));

        g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
        g2d.setColor(Color.LIGHT_GRAY);
        String sub = "Choisissez d'associer votre classe à une seconde voie martiale/mystique, ou restez pur.";
        FontMetrics fmSub = g2d.getFontMetrics();
        g2d.drawString(sub, bx + (boxW - fmSub.stringWidth(sub)) / 2, by + (int) (46 * scale));

        // 2. Bannière de Règle
        int banX = bx + (int) (20 * scale);
        int banY = by + (int) (58 * scale);
        int banW = boxW - (int) (40 * scale);
        int banH = (int) (40 * scale);

        g2d.setColor(new Color(45, 30, 10));
        g2d.fillRect(banX, banY, banW, banH);
        g2d.setColor(new Color(255, 180, 50));
        g2d.drawRect(banX, banY, banW, banH);

        g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
        g2d.setColor(new Color(255, 215, 80));
        g2d.drawString("⚠ RÈGLE IMPORTANTE : Une bi-classe débloque 2 arbres exclusifs dans vos choix,", banX + (int) (12 * scale), banY + (int) (16 * scale));
        g2d.drawString("mais vous ne gagnerez plus qu'UN SEUL point de compétence par niveau (au lieu de 2).", banX + (int) (12 * scale), banY + (int) (32 * scale));

        // 3. Deux Colonnes : Liste des choix à Gauche, Détails à Droite
        int contentY = banY + banH + (int) (12 * scale);
        int colLeftW = (int) (boxW * 0.40);
        int colLeftX = bx + (int) (20 * scale);
        int colRightX = colLeftX + colLeftW + (int) (15 * scale);
        int colRightW = boxW - (int) (55 * scale) - colLeftW;
        int colH = by + boxH - contentY - (int) (35 * scale);

        // Colonne Gauche : Options
        g2d.setColor(new Color(15, 15, 25));
        g2d.fillRect(colLeftX, contentY, colLeftW, colH);
        g2d.setColor(new Color(100, 100, 150));
        g2d.drawRect(colLeftX, contentY, colLeftW, colH);

        g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
        g2d.setColor(Color.WHITE);
        g2d.drawString("VOIES PROPOSÉES :", colLeftX + (int) (12 * scale), contentY + (int) (22 * scale));

        java.util.List<com.eltim.rogue.entity.classe.Subclass> subs = com.eltim.rogue.system.SubclassSelectionSystem.getAvailableSubclasses();
        int selIdx = com.eltim.rogue.system.SubclassSelectionSystem.getSelectedIndex();
        int totalOpts = subs.size() + 1;
        int itemH = (int) (34 * scale);

        for (int i = 0; i < totalOpts; i++) {
            boolean isSelected = (i == selIdx);
            int optY = contentY + (int) (38 * scale) + i * itemH;

            if (isSelected) {
                g2d.setColor(new Color(60, 50, 110));
                g2d.fillRect(colLeftX + (int) (6 * scale), optY, colLeftW - (int) (12 * scale), itemH - (int) (4 * scale));
                g2d.setColor(Color.CYAN);
                g2d.drawRect(colLeftX + (int) (6 * scale), optY, colLeftW - (int) (12 * scale), itemH - (int) (4 * scale));
            }

            g2d.setFont(new Font("Monospaced", isSelected ? Font.BOLD : Font.PLAIN, smallSize));
            g2d.setColor(isSelected ? Color.WHITE : Color.LIGHT_GRAY);

            String label;
            if (i < subs.size()) {
                com.eltim.rogue.entity.classe.Subclass sc = subs.get(i);
                label = (isSelected ? "► " : "   ") + sc.name + " (" + sc.class1 + " + " + sc.class2 + ")";
            } else {
                com.eltim.rogue.entity.player curP = com.eltim.rogue.system.SubclassSelectionSystem.getCurrentPlayer();
                String baseName = (curP != null && curP.classe != null) ? curP.classe.name : "Base";
                label = (isSelected ? "► " : "   ") + "[ Rester Pur " + baseName + " ]";
            }
            g2d.drawString(label, colLeftX + (int) (10 * scale), optY + (int) (20 * scale));
        }

        // Colonne Droite : Détails
        g2d.setColor(new Color(15, 15, 25));
        g2d.fillRect(colRightX, contentY, colRightW, colH);
        g2d.setColor(new Color(100, 100, 150));
        g2d.drawRect(colRightX, contentY, colRightW, colH);

        if (selIdx < subs.size()) {
            com.eltim.rogue.entity.classe.Subclass focusedSub = subs.get(selIdx);
            int dY = contentY + (int) (22 * scale);
            int dX = colRightX + (int) (15 * scale);

            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString("SOUS-CLASSE : " + focusedSub.name.toUpperCase(), dX, dY);

            g2d.setFont(new Font("Monospaced", Font.ITALIC, smallSize));
            g2d.setColor(new Color(100, 200, 255));
            g2d.drawString("Alliance martiale : " + focusedSub.class1 + " & " + focusedSub.class2, dX, dY + (int) (18 * scale));

            g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
            g2d.setColor(Color.WHITE);
            java.util.List<String> wrapDesc = wrapText(focusedSub.description, 50);
            for (int l = 0; l < wrapDesc.size(); l++) {
                g2d.drawString(wrapDesc.get(l), dX, dY + (int) (38 * scale) + l * (int) (14 * scale));
            }

            int treesStartY = dY + (int) (75 * scale);
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(new Color(255, 200, 80));
            g2d.drawString("★ 2 ARBRES DE TALENTS EXCLUSIFS AJOUTÉS AUX CHOIX :", dX, treesStartY);

            for (int t = 0; t < focusedSub.trees.size(); t++) {
                com.eltim.rogue.entity.classe.SkillTree st = focusedSub.trees.get(t);
                int treeY = treesStartY + (int) (18 * scale) + t * (int) (48 * scale);

                g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
                g2d.setColor(Color.GREEN);
                g2d.drawString((t + 1) + ". Arbre [" + st.name + "]", dX + (int) (8 * scale), treeY);

                g2d.setFont(new Font("Monospaced", Font.ITALIC, smallSize));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(st.description, dX + (int) (8 * scale), treeY + (int) (14 * scale));

                if (!st.skills.isEmpty()) {
                    com.eltim.rogue.entity.classe.Skill t1 = st.skills.get(0);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
                    g2d.setColor(Color.CYAN);
                    g2d.drawString("   Tier 1 : " + t1.name + " - " + t1.description, dX + (int) (8 * scale), treeY + (int) (28 * scale));
                }
            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.setColor(new Color(255, 120, 80));
            g2d.drawString("Gain futur : 1 point de compétence par niveau.", dX, contentY + colH - (int) (15 * scale));

        } else {
            // Option Pure
            int dY = contentY + (int) (22 * scale);
            int dX = colRightX + (int) (15 * scale);

            com.eltim.rogue.entity.player curP = com.eltim.rogue.system.SubclassSelectionSystem.getCurrentPlayer();
            String baseName = (curP != null && curP.classe != null) ? curP.classe.name : "Base";

            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            g2d.setColor(new Color(100, 220, 255));
            g2d.drawString("VOIE PURE : " + baseName.toUpperCase() + " PUR", dX, dY);

            g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
            g2d.setColor(Color.WHITE);
            g2d.drawString("Vous choisissez de rester fidèle et dévoué à votre seule classe d'origine.", dX, dY + (int) (25 * scale));
            g2d.drawString("Aucun arbre de bi-classe n'est ajouté, mais vous conservez l'évolution maximale :", dX, dY + (int) (45 * scale));

            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
            g2d.drawString("✔ Vous continuerez à gagner 2 POINTS de compétence par niveau.", dX, dY + (int) (75 * scale));
            g2d.drawString("✔ Accès libre aux 4 arbres de spécialisation de votre classe.", dX, dY + (int) (95 * scale));
            g2d.drawString("✔ Progression beaucoup plus rapide dans les Tiers 3, 4 et 5.", dX, dY + (int) (115 * scale));
        }

        // Instructions
        g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
        g2d.setColor(Color.YELLOW);
        String hint = "[↑/↓ ou Z/S] Sélectionner une voie  |  [ENTRÉE] Confirmer définitivement votre choix";
        FontMetrics fmHint = g2d.getFontMetrics();
        g2d.drawString(hint, bx + (boxW - fmHint.stringWidth(hint)) / 2, by + boxH - (int) (10 * scale));
    }

}

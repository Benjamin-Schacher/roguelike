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
import java.awt.image.BufferedImage;
import java.util.List;

public class renderer extends JPanel {
    private JFrame frame;
    private map currentMap;
    private BufferedImage crtOverlay;
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
        
        Graphics2D g2d = (Graphics2D) g;
        float scale = Math.min((float) getWidth() / 800f, (float) getHeight() / 600f);

        if (currentState == game.GameState.CHARACTER_CREATION) {
            drawCharacterCreationScreen(g2d, scale);
            drawCRTFilter(g2d);
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

        // Ajoute le filtre CRT
        drawCRTFilter(g2d);
    }

    private void drawCombatMenu(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();
        int boxW = (int) (w * 0.85);
        int boxH = (int) (h * 0.8);
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
        int allyY = by + (int)(30 * scale);
        int allyX = bx + (int)(20 * scale);
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.setColor(new Color(100, 180, 255));
        g2d.drawString("ALLIÉS", allyX, allyY);
        allyY += lineSpacing;

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
            
            String status = ally.isDead() ? "[K.O.]" : "PV: " + ally.getLifePoint() + "/" + ally.getMaxLifePoint() + " MP: " + ally.getMana();
            g2d.drawString(prefix + ally.getName() + " (" + status + ")", allyX, allyY);
            allyY += lineSpacing;
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
            allyY += lineSpacing;
        }

        // VS (Centre de l'écran)
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.drawString("VS", bx + boxW / 2 - (int)(10*scale), by + (int)(80*scale));

        // 2. Rendu du Groupe Ennemi (Droite)
        int enemyY = by + (int)(30 * scale);
        int enemyX = bx + boxW / 2 + (int)(20 * scale); 
        g2d.setFont(new Font("Monospaced", Font.BOLD, fontTitleSize));
        g2d.setColor(Color.RED);
        g2d.drawString("ENNEMIS", enemyX, enemyY);
        enemyY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, fontBodySize));
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
            enemyY += lineSpacing;
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
        boxWidth = Math.min((int)(getWidth() * 0.92), boxWidth);
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
        int hudX = (int)(16 * scale);
        int hudY = (int)(16 * scale); // En haut à gauche
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
        int lvlY = (int)(28 * scale);

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
        g2d.drawString("[E] Inventaire  [K] Compétences", w - (int)(250 * scale), h - (int)(20 * scale));

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
            termW = Math.min((int)(getWidth() * 0.75), termW);
            int termH = exploLog.size() * termLineH + (int)(14 * scale);
            int termX = (int)(12 * scale);
            int termY = h - termH - (int)(10 * scale);

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

    private void drawCRTFilter(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();
        
        g2d.setColor(new Color(0, 0, 0, 100)); 
        scanlineOffset += 0.05f; 
        if (scanlineOffset >= 3) scanlineOffset = 0;

        for (float y = scanlineOffset; y < h; y += 3) {
            g2d.fillRect(0, (int)y, w, 1); 
        }
        
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

        Point center = new Point(w / 2, h / 2);
        float radius = Math.max(1, Math.max(w, h) * 0.7f); 
        float[] dist = {0.0f, 0.5f, 1.0f};
        Color[] colors = {
            new Color(0, 0, 0, 0),       
            new Color(0, 0, 0, 60),      
            new Color(0, 0, 0, 240)      
        };
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(p);
        g2d.fillRect(0, 0, w, h);
        
        g2d.dispose();
    }
    private void drawCharacterCreationScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, w, h);

        int titleSize = Math.max(16, (int) (32 * scale));
        int subtitleSize = Math.max(14, (int) (22 * scale));
        int textSize = Math.max(12, (int) (18 * scale));
        int smallTextSize = Math.max(10, (int) (14 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(Color.WHITE);
        String title = "CRÉATION DE PERSONNAGE";
        FontMetrics fmTitle = g2d.getFontMetrics();
        g2d.drawString(title, (w - fmTitle.stringWidth(title)) / 2, (int)(h * 0.1));

        int startY = (int)(h * 0.2);
        int startX = w / 4;
        int lineSpacing = Math.max(20, (int)(h * 0.05));
        int labelWidth = (int)(180 * scale);

        com.eltim.rogue.system.CharacterCreationSystem.Field currentField = com.eltim.rogue.system.CharacterCreationSystem.getCurrentField();

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        int currentY = startY;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Nom:", com.eltim.rogue.system.CharacterCreationSystem.getName() + (com.eltim.rogue.system.CharacterCreationSystem.isEditingName() ? "_" : ""), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.NAME);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Classe:", "Guerrier (Par défaut)", false);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Race:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getRace().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.RACE);
        currentY += lineSpacing;
        
        g2d.setFont(new Font("Monospaced", Font.ITALIC, smallTextSize));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("Compétence de race: " + com.eltim.rogue.system.CharacterCreationSystem.getRace().getSpecialSkillPlaceholder(), 
                       startX + labelWidth, currentY - 5);
        currentY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        drawCreationLine(g2d, startX, currentY, labelWidth, "Sexe:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getGender().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.GENDER);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Croyance:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getBelief().getDisplayName() + " >", currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.BELIEF);
        currentY += lineSpacing;

        drawCreationLine(g2d, startX, currentY, labelWidth, "Symbole:", com.eltim.rogue.system.CharacterCreationSystem.getSymbol() + (com.eltim.rogue.system.CharacterCreationSystem.isEditingSymbol() ? "_" : ""), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.SYMBOL);
        currentY += lineSpacing * 2;

        g2d.setFont(new Font("Monospaced", Font.BOLD, subtitleSize));
        g2d.setColor(Color.WHITE);
        g2d.drawString("STATISTIQUES (Points restants: " + com.eltim.rogue.system.CharacterCreationSystem.getAvailablePoints() + ")", startX, currentY);
        currentY += lineSpacing;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, textSize));

        int bF = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusForce();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Force:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getForce() + " > (" + (bF>=0?"+":"") + bF + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getForce() + bF), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.FORCE);
        currentY += lineSpacing;
        int bA = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusAgilite();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Agilité:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getAgilite() + " > (" + (bA>=0?"+":"") + bA + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getAgilite() + bA), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.AGILITE);
        currentY += lineSpacing;
        int bI = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusIntelligence();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Intelligence:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getIntelligence() + " > (" + (bI>=0?"+":"") + bI + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getIntelligence() + bI), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.INTELLIGENCE);
        currentY += lineSpacing;
        int bC = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusCharisme();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Charisme:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getCharisme() + " > (" + (bC>=0?"+":"") + bC + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getCharisme() + bC), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CHARISME);
        currentY += lineSpacing;
        int bCo = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusConstitution();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Constitution:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getConstitution() + " > (" + (bCo>=0?"+":"") + bCo + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getConstitution() + bCo), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.CONSTITUTION);
        currentY += lineSpacing;
        int bS = com.eltim.rogue.system.CharacterCreationSystem.getRace().getBonusSagesse();
        drawCreationLine(g2d, startX, currentY, labelWidth, "Sagesse:", "< " + com.eltim.rogue.system.CharacterCreationSystem.getSagesse() + " > (" + (bS>=0?"+":"") + bS + ") = " + (com.eltim.rogue.system.CharacterCreationSystem.getSagesse() + bS), currentField == com.eltim.rogue.system.CharacterCreationSystem.Field.SAGESSE);
        currentY += lineSpacing * 2;

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

    private void drawInventoryScreen(Graphics2D g2d, float scale) {
        int w = getWidth();
        int h = getHeight();

        // Background
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, w, h);

        int titleSize = Math.max(16, (int) (24 * scale));
        int textSize = Math.max(12, (int) (16 * scale));
        int smallSize = Math.max(10, (int) (14 * scale));

        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(Color.WHITE);
        String title = "INVENTAIRE ET ÉQUIPEMENT";
        FontMetrics fmTitle = g2d.getFontMetrics();
        g2d.drawString(title, (w - fmTitle.stringWidth(title)) / 2, (int)(40 * scale));

        com.eltim.rogue.entity.base.entity activeChar = com.eltim.rogue.system.InventorySystem.getActiveCharacter();
        if (activeChar == null) return;

        int leftX = (int)(w * 0.05);
        int rightX = (int)(w * 0.55);
        int startY = (int)(h * 0.15);
        int lineSpacing = Math.max(20, (int)(24 * scale));

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
            int boxW = (int)(300 * scale);
            int boxH = (int)(150 * scale);
            int bx = (w - boxW) / 2;
            int by = (h - boxH) / 2;
            g2d.setColor(new Color(0, 0, 0, 240));
            g2d.fillRect(bx, by, boxW, boxH);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(bx, by, boxW, boxH);

            g2d.setFont(new Font("Monospaced", Font.BOLD, textSize));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Équiper où ?", bx + (int)(20 * scale), by + (int)(30 * scale));

            String[] prompts = { "Main Droite", "Main Gauche", "Arme Secondaire" };
            int pIdx = com.eltim.rogue.system.InventorySystem.getWeaponPromptIndex();
            for (int i = 0; i < prompts.length; i++) {
                int py = by + (int)(70 * scale) + (i * (int)(25 * scale));
                if (i == pIdx) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("> " + prompts[i], bx + (int)(40 * scale), py);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawString("  " + prompts[i], bx + (int)(40 * scale), py);
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

        // Fond sombre
        g2d.setColor(new Color(0, 0, 0, 230));
        g2d.fillRect(0, 0, w, h);

        int titleSize = Math.max(14, (int)(22 * scale));
        int bodySize = Math.max(10, (int)(14 * scale));
        int smallSize = Math.max(9, (int)(12 * scale));
        int lineH = Math.max(18, (int)(22 * scale));

        // Titre
        g2d.setFont(new Font("Monospaced", Font.BOLD, titleSize));
        g2d.setColor(new Color(220, 180, 60));
        String title = "COMPÉTENCES — " + p.classe.name.toUpperCase();
        FontMetrics fmT = g2d.getFontMetrics();
        g2d.drawString(title, (w - fmT.stringWidth(title)) / 2, (int)(36 * scale));

        // Points disponibles
        g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
        g2d.setColor(new Color(100, 220, 255));
        String pts = "Points disponibles : " + p.classe.skillPoints;
        FontMetrics fmPts = g2d.getFontMetrics();
        g2d.drawString(pts, (w - fmPts.stringWidth(pts)) / 2, (int)(60 * scale));

        // Arbres
        java.util.List<com.eltim.rogue.entity.classe.SkillTree> trees = p.classe.trees;
        if (trees == null || trees.isEmpty()) return;

        int numTrees = trees.size();
        int colW = w / numTrees;
        int startY = (int)(80 * scale);

        int selTree = com.eltim.rogue.system.SkillMenuSystem.getSelectedTree();
        int selTier = com.eltim.rogue.system.SkillMenuSystem.getSelectedTier();

        for (int t = 0; t < numTrees; t++) {
            com.eltim.rogue.entity.classe.SkillTree tree = trees.get(t);
            int colX = t * colW + (int)(10 * scale);

            // En-tête d'arbre
            g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
            if (t == selTree) {
                g2d.setColor(new Color(220, 200, 80));
                g2d.drawString("▼ " + tree.name, colX, startY);
            } else {
                g2d.setColor(new Color(160, 160, 160));
                g2d.drawString("  " + tree.name, colX, startY);
            }

            // Séparateur
            g2d.setColor(new Color(80, 80, 80));
            g2d.drawLine(colX, startY + (int)(4*scale), colX + colW - (int)(20*scale), startY + (int)(4*scale));

            // Skills (5 tiers)
            for (int tier = 0; tier < tree.skills.size(); tier++) {
                com.eltim.rogue.entity.classe.Skill skill = tree.skills.get(tier);
                int skillY = startY + (int)(24 * scale) + tier * (lineH + (int)(26 * scale));

                boolean isSelected = (t == selTree && tier == selTier);
                boolean isUnlocked = skill.unlocked;
                boolean prevUnlocked = (tier == 0) || tree.skills.get(tier - 1).unlocked;
                int cost = p.classe.getSkillCost(skill);
                boolean canBuy = !isUnlocked && prevUnlocked && p.classe.skillPoints >= cost;

                // Fond de sélection
                if (isSelected) {
                    g2d.setColor(new Color(40, 40, 80, 180));
                    g2d.fillRoundRect(colX - (int)(4*scale), skillY - (int)(14*scale),
                            colW - (int)(12*scale), lineH + (int)(26*scale), (int)(4*scale), (int)(4*scale));
                }

                // Couleur selon état
                Color skillColor;
                if (isUnlocked) {
                    skillColor = new Color(60, 200, 60);
                } else if (canBuy) {
                    skillColor = new Color(220, 220, 60);
                } else if (prevUnlocked) {
                    skillColor = new Color(180, 100, 40);
                } else {
                    skillColor = new Color(80, 80, 80);
                }

                g2d.setFont(new Font("Monospaced", Font.BOLD, bodySize));
                g2d.setColor(skillColor);
                String prefix = isSelected ? "► " : (isUnlocked ? "✓ " : "  ");
                String tierLabel = "[T" + (tier + 1) + "]";
                g2d.drawString(prefix + tierLabel + " " + skill.name, colX, skillY);

                // Coût si non débloqué
                if (!isUnlocked) {
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, smallSize));
                    g2d.setColor(new Color(140, 140, 140));
                    String costStr = "(" + cost + "pt" + (cost > 1 ? "s" : "") + ")";
                    g2d.drawString(costStr, colX + (int)(6*scale), skillY + (int)(14*scale));
                }

                // Description si sélectionné
                if (isSelected) {
                    g2d.setFont(new Font("Monospaced", Font.ITALIC, smallSize));
                    g2d.setColor(new Color(180, 180, 220));
                    java.util.List<String> descLines = wrapText(skill.description, Math.max(20, (colW - (int)(20*scale)) / Math.max(1, smallSize / 2)));
                    for (int dl = 0; dl < Math.min(descLines.size(), 3); dl++) {
                        g2d.drawString(descLines.get(dl), colX + (int)(4*scale), skillY + (int)(28*scale) + dl * (int)(14*scale));
                    }
                }
            }
        }

        // Instructions bas d'écran
        g2d.setFont(new Font("Monospaced", Font.BOLD, smallSize));
        g2d.setColor(new Color(140, 140, 140));
        g2d.drawString("[G/D] Changer d'arbre  [H/B] Naviguer  [ENTRÉE] Acheter  [K] Fermer",
                (int)(20*scale), h - (int)(20*scale));
    }
}

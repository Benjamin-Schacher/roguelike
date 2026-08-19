package com.eltim.rogue.engine;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.level.level;
import com.eltim.rogue.level.tutoLevel;
import com.eltim.rogue.level.Level1;
import com.eltim.rogue.level.TextLevel;
import com.eltim.rogue.level.LevelLoader;
import com.eltim.rogue.system.mouvementSysteme;
import com.eltim.rogue.system.InteractionSysteme;
import com.eltim.rogue.system.combatSysteme;
import com.eltim.rogue.system.ExplorationLog;
import com.eltim.rogue.world.map;

import java.awt.event.KeyEvent;

public class game {
    
    public enum GameState {
        CHARACTER_CREATION,
        PLAYING,
        INVENTORY,
        SKILL_MENU,
        DESCRIPTION,
        GAME_OVER,
        AUDIO_MENU
    }

    public static int audioSelectedOption = 0; // 0 = Musique, 1 = Effets Sonores (SFX)

    public static int getAudioSelectedOption() {
        return audioSelectedOption;
    }

    private boolean isRunning;
    private renderer renderer;
    private inputHandler inputHandler;
    
    private level currentLevel;
    private map currentMap;
    private player player;
    private GameState state;
    private int frameCount = 0;
    private int lastDescX = -1;
    private int lastDescY = -1;

    public game() {
        renderer = new renderer();
        inputHandler = new inputHandler();
        renderer.getFrame().addKeyListener(inputHandler);
        // Désactiver la traversée focus par TAB pour que TAB soit capté par KeyListener
        renderer.getFrame().setFocusTraversalKeysEnabled(false);
        renderer.setFocusTraversalKeysEnabled(false);
        
        // Ajout du MouseListener pour gérer le clic sur la carte
        renderer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (state == GameState.PLAYING && currentMap != null) {
                    java.awt.Point gridPos = renderer.getGridCoordinates(e.getX(), e.getY());
                    if (gridPos != null) {
                        com.eltim.rogue.entity.base.entity target = currentMap.getEntityAt(gridPos.x, gridPos.y);
                        if (target instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                            com.eltim.rogue.system.InteractionSysteme.onEncounter(player, target, currentMap);
                            if (com.eltim.rogue.system.InteractionSysteme.isDescriptionOpen()) {
                                state = GameState.DESCRIPTION;
                            }
                        }
                    }
                } else if (state == GameState.DESCRIPTION) {
                    com.eltim.rogue.system.InteractionSysteme.closeDescription();
                    state = GameState.PLAYING;
                }
            }
        });

        initGame();
        isRunning = true;
    }

    private void initGame() {
        com.eltim.rogue.system.CharacterCreationSystem.init();
        state = GameState.CHARACTER_CREATION;
    }

    private void finalizeCharacterCreation() {
        player = new player(1, 1);
        player.setLevel(1);
        player.setName(com.eltim.rogue.system.CharacterCreationSystem.getName());
        player.setRace(com.eltim.rogue.system.CharacterCreationSystem.getRace());
        player.setGender(com.eltim.rogue.system.CharacterCreationSystem.getGender());
        player.setBelief(com.eltim.rogue.system.CharacterCreationSystem.getBelief());
        player.setSymbol(com.eltim.rogue.system.CharacterCreationSystem.getSymbol());
        player.setForce(com.eltim.rogue.system.CharacterCreationSystem.getForce() + player.getRace().getBonusForce());
        player.setAgilite(com.eltim.rogue.system.CharacterCreationSystem.getAgilite() + player.getRace().getBonusAgilite());
        player.setIntelligence(com.eltim.rogue.system.CharacterCreationSystem.getIntelligence() + player.getRace().getBonusIntelligence());
        player.setCharisme(com.eltim.rogue.system.CharacterCreationSystem.getCharisme() + player.getRace().getBonusCharisme());
        player.setConstitution(com.eltim.rogue.system.CharacterCreationSystem.getConstitution() + player.getRace().getBonusConstitution());
        player.setSagesse(com.eltim.rogue.system.CharacterCreationSystem.getSagesse() + player.getRace().getBonusSagesse());
        player.chooseClass(com.eltim.rogue.system.CharacterCreationSystem.getCharacterClass());

        tutoLevel firstLevel = new tutoLevel();
        currentLevel = firstLevel;
        currentMap = firstLevel.generate(player);
        
        state = GameState.PLAYING;
    }

    public void start() {
        while (isRunning) {
            renderer.updateMap(currentMap, state);

            KeyEvent key = inputHandler.getInput();
            if (key != null) {
                handleInput(key);
            }

            com.eltim.rogue.system.InventorySystem.update();

            // IA des ennemis (seulement hors combat et hors menu d'interaction, en mode PLAYING)
            if (state == GameState.PLAYING && currentMap != null && !combatSysteme.isCombatOpen() && !InteractionSysteme.isMenuOpen()) {
                com.eltim.rogue.system.EnemyAISystem.tick(currentMap, player, frameCount);
                frameCount++;
            }

            if (combatSysteme.isCombatOpen()) {
                combatSysteme.tick();
            }

            // Vérifie si le joueur est mort après chaque action (combat terminé)
            if (state == GameState.PLAYING && player.isDead()) {
                state = GameState.GAME_OVER;
            }

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.exit(0);
    }

    private void handleInput(KeyEvent key) {
        if (state == GameState.CHARACTER_CREATION) {
            com.eltim.rogue.system.CharacterCreationSystem.handleInput(key);
            if (com.eltim.rogue.system.CharacterCreationSystem.isDone()) {
                finalizeCharacterCreation();
            }
            return;
        }

        // --- État DESCRIPTION : Entrée pour fermer ---
        if (state == GameState.DESCRIPTION) {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_ESCAPE) {
                com.eltim.rogue.system.InteractionSysteme.closeDescription();
                state = GameState.PLAYING;
            }
            return;
        }

        // --- État SKILL_MENU : navigation dans les compétences ---
        if (state == GameState.SKILL_MENU) {
            if (key.getKeyCode() == KeyEvent.VK_K || key.getKeyCode() == KeyEvent.VK_ESCAPE) {
                state = GameState.PLAYING;
                return;
            }
            if (player != null && player.classe != null) {
                com.eltim.rogue.system.SkillMenuSystem.handleInput(key, player.classe);
            }
            return;
        }

        // --- État AUDIO_MENU : Réglages du son ---
        if (state == GameState.AUDIO_MENU) {
            com.eltim.rogue.engine.sound.SoundManager soundMgr = com.eltim.rogue.engine.sound.SoundManager.getInstance();
            int code = key.getKeyCode();
            if (code == KeyEvent.VK_O || code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_ENTER) {
                state = GameState.PLAYING;
                return;
            }
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_Z) {
                audioSelectedOption = 0; // Musique
            } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                audioSelectedOption = 1; // SFX
            } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_Q) {
                if (audioSelectedOption == 0) {
                    float newVol = Math.max(0.0f, soundMgr.getMusicVolume() - 0.05f);
                    soundMgr.setMusicVolume(newVol);
                } else {
                    float newVol = Math.max(0.0f, soundMgr.getSfxVolume() - 0.05f);
                    soundMgr.setSfxVolume(newVol);
                }
            } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                if (audioSelectedOption == 0) {
                    float newVol = Math.min(1.0f, soundMgr.getMusicVolume() + 0.05f);
                    soundMgr.setMusicVolume(newVol);
                } else {
                    float newVol = Math.min(1.0f, soundMgr.getSfxVolume() + 0.05f);
                    soundMgr.setSfxVolume(newVol);
                }
            } else if (code == KeyEvent.VK_M) {
                soundMgr.toggleMute();
            }
            return;
        }

        // --- État GAME OVER : Entrée pour recommencer, Échap pour quitter ---
        if (state == GameState.GAME_OVER) {
            if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                initGame();
            } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
                isRunning = false;
            }
            return;
        }

        // --- État PLAYING ---
        // Priorité 1 : Menu d'interaction (porte, coffre, npc...)
        if (InteractionSysteme.isMenuOpen()) {
            InteractionSysteme.handleMenuInput(key);
            return;
        }

        // Priorité 2 : Combat (lancé par l'interaction)
        if (combatSysteme.isCombatOpen()) {
            combatSysteme.handleInput(key);
            return;
        }

        // Vérifie si une popup de description vient d'être ouverte
        if (InteractionSysteme.isDescriptionOpen()) {
            state = GameState.DESCRIPTION;
            InteractionSysteme.closeDescription(); // reset le flag, on garde la description
            // Rouvrir la description via le renderer (currentDescription est déjà stocké)
            return;
        }

        if (state == GameState.INVENTORY) {
            com.eltim.rogue.system.InventorySystem.handleInput(key);
            return;
        }

        switch (key.getKeyCode()) {
            case KeyEvent.VK_E:
                if (state == GameState.PLAYING) {
                    state = GameState.INVENTORY;
                    inputHandler.clear();
                    com.eltim.rogue.system.InventorySystem.open(player, this);
                }
                break;
            case KeyEvent.VK_K:
                if (state == GameState.PLAYING && player != null && player.classe != null) {
                    state = GameState.SKILL_MENU;
                    inputHandler.clear();
                    com.eltim.rogue.system.SkillMenuSystem.open(player.classe);
                }
                break;
            case KeyEvent.VK_O:
                if (state == GameState.PLAYING) {
                    state = GameState.AUDIO_MENU;
                    inputHandler.clear();
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_W:
                mouvementSysteme.moveEntity(player, 0, -1, currentMap);
                if (InteractionSysteme.isMenuOpen() || combatSysteme.isCombatOpen()) {
                    inputHandler.clear();
                } else {
                    checkDescriptionTrigger();
                    checkLevelTransition();
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                mouvementSysteme.moveEntity(player, 0, 1, currentMap);
                if (InteractionSysteme.isMenuOpen() || combatSysteme.isCombatOpen()) {
                    inputHandler.clear();
                } else {
                    checkDescriptionTrigger();
                    checkLevelTransition();
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_Q:
            case KeyEvent.VK_A:
                mouvementSysteme.moveEntity(player, -1, 0, currentMap);
                if (InteractionSysteme.isMenuOpen() || combatSysteme.isCombatOpen()) {
                    inputHandler.clear();
                } else {
                    checkDescriptionTrigger();
                    checkLevelTransition();
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                mouvementSysteme.moveEntity(player, 1, 0, currentMap);
                if (InteractionSysteme.isMenuOpen() || combatSysteme.isCombatOpen()) {
                    inputHandler.clear();
                } else {
                    checkDescriptionTrigger();
                    checkLevelTransition();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                isRunning = false;
                break;
            default:
                break;
        }

        // Ouvrir la popup de description si déclenchée par mouvementSysteme
        // (mais seulement si on vient de bouger, pas après un menu)
        if (InteractionSysteme.isDescriptionOpen() && !InteractionSysteme.isMenuOpen()) {
            state = GameState.DESCRIPTION;
        }
    }

    /** Vérifie si le joueur se trouve sur un DescriptionMarker et l'affiche (une seule fois par case) */
    private void checkDescriptionTrigger() {
        if (currentMap == null || player == null) return;
        int px = player.getX();
        int py = player.getY();
        // Ne déclencher que si le joueur vient de changer de case
        if (px == lastDescX && py == lastDescY) return;
        for (com.eltim.rogue.entity.base.entity e : currentMap.getEntities()) {
            if (e.getX() == px && e.getY() == py && e instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                com.eltim.rogue.entity.environment.DescriptionMarker dm = (com.eltim.rogue.entity.environment.DescriptionMarker) e;
                InteractionSysteme.triggerDescription(dm.getDescription());
                lastDescX = px;
                lastDescY = py;
                break;
            }
        }
    }

    /** Vérifie si le joueur marche sur une tuile de changement de niveau */
    private void checkLevelTransition() {
        if (currentMap == null || player == null) return;
        int px = player.getX();
        int py = player.getY();

        if (currentLevel instanceof TextLevel) {
            TextLevel tl = (TextLevel) currentLevel;
            LevelLoader.LevelData data = tl.getData();

            com.eltim.rogue.world.tile currentTile = currentMap.getTile(px, py);
            String tileSymbol = (currentTile != null) ? String.valueOf(currentTile.getSymbol()) : "";

            String transitionRule = null;
            if (data.transitions.containsKey(tileSymbol)) {
                transitionRule = data.transitions.get(tileSymbol);
            } else if (py == 32 && px >= 9 && px <= 15 && data.transitions.containsKey("<tuto->")) {
                transitionRule = data.transitions.get("<tuto->");
            }

            if (transitionRule != null) {
                String[] parts = transitionRule.split("\\|");
                String targetFile = parts[0].trim();
                int nextX = data.spawnX;
                int nextY = data.spawnY;
                String msg = "";

                for (int i = 1; i < parts.length; i++) {
                    String p = parts[i].trim();
                    if (p.startsWith("spawn:")) {
                        String[] coords = p.substring(6).trim().split(",");
                        if (coords.length == 2) {
                            nextX = Integer.parseInt(coords[0].trim());
                            nextY = Integer.parseInt(coords[1].trim());
                        }
                    } else if (p.startsWith("msg:")) {
                        msg = p.substring(4).trim();
                    }
                }

                currentMap.removeEntity(player);
                TextLevel nextLevel = new TextLevel(targetFile);
                currentLevel = nextLevel;
                currentMap = nextLevel.generate(player);
                player.setX(nextX);
                player.setY(nextY);
                currentMap.addEntity(player);

                if (!msg.isEmpty()) {
                    ExplorationLog.add(msg);
                }
                return;
            }
        }
    }

    public void closeInventory() {
        if (state == GameState.INVENTORY) {
            state = GameState.PLAYING;
        }
    }
}

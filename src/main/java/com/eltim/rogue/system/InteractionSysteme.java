package com.eltim.rogue.system;

import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.base.Interactable;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.environment.DescriptionMarker;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.world.map;
import com.eltim.rogue.level.level;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InteractionSysteme {
    private static level currentLevel = null;
    private static boolean menuOpen = false;
    private static entity menuAttacker = null;
    private static entity menuTarget = null;
    private static map currentMap = null;
    private static List<String> options = new ArrayList<>();
    private static int selection = 0;
    private static long menuOpenTime = 0;
    // Description courante pour la popup ? (géré par le GameState DESCRIPTION)
    private static String currentDescription = null;

    public static void setCurrentLevel(level lvl) {
        currentLevel = lvl;
    }

    public static level getCurrentLevel() {
        return currentLevel;
    }

    public static void onEncounter(entity attacker, entity target, map gameMap) {
        // Cas spécial : marqueur de description
        if (target instanceof DescriptionMarker) {
            if (attacker instanceof player) {
                DescriptionMarker dm = (DescriptionMarker) target;
                currentDescription = dm.getDescription();
                dm.markRead();
                ExplorationLog.addDescription(dm.getDescription().length() > 40
                        ? dm.getDescription().substring(0, 40) + "..."
                        : dm.getDescription());
                // Demander au jeu de passer en état DESCRIPTION (via flag)
                menuOpen = false;
                descriptionOpen = true;
            }
            return;
        }

        // Si un monstre/NPC attaque le joueur, on inverse pour que le menu
        // soit toujours du point de vue du joueur.
        if (target instanceof player) {
            entity temp = attacker;
            attacker = target;
            target = temp;
        }

        // Si le menu est déjà ouvert sur la même cible, ne PAS réinitialiser selection à 0
        if (menuOpen && menuTarget == target) {
            return;
        }

        menuOpen = true;
        menuOpenTime = System.currentTimeMillis();
        menuAttacker = attacker;
        menuTarget = target;
        currentMap = gameMap;
        selection = 0;
        options.clear();

        com.eltim.rogue.engine.inputHandler.clearInput();

        // 1. Vérification si le niveau courant configure des options d'interaction personnalisées
        if (currentLevel != null && currentLevel.onConfigureInteractionOptions(attacker, target, options)) {
            selection = 0;
            return;
        }

        // 2. Éléments interactifs polymorphes (portes, coffres, tuiles d'interaction...)
        if (target instanceof Interactable && attacker instanceof player) {
            List<String> interactableOptions = ((Interactable) target).getInteractionOptions((player) attacker);
            if (interactableOptions == null || interactableOptions.isEmpty()) {
                menuOpen = false;
                return;
            }
            options.addAll(interactableOptions);
            selection = 0;
            return;
        }

        if (target instanceof monster) {
            options.add("Combattre");
            options.add("Utiliser un objet");
            options.add("Fuir");
        } else if (target instanceof npc) {
            if (attacker instanceof player && ((player) attacker).getParty().contains(target)) {
                menuOpen = false;
                return;
            }
            options.add("Discuter");
            if (attacker instanceof player) {
                player p = (player) attacker;
                if (p.getPartyNumber() < 2 && !p.getParty().contains(target)) {
                    options.add("Recruter");
                }
            }
            options.add("Combattre");
            options.add("Utiliser un objet");
            options.add("Quitter");
        } else {
            options.add("Fermer");
        }
    }

    // Flag séparé pour la popup de description (pas un menu interactif)
    private static boolean descriptionOpen = false;

    public static boolean isDescriptionOpen() { return descriptionOpen; }
    public static void closeDescription() { descriptionOpen = false; currentDescription = null; }
    public static String getCurrentDescription() { return currentDescription; }
    
    public static void triggerDescription(String desc) {
        currentDescription = desc;
        descriptionOpen = true;
    }


    public static void handleMenuInput(KeyEvent key) {
        long now = System.currentTimeMillis();
        long elapsed = now - menuOpenTime;

        if (key.getKeyCode() == KeyEvent.VK_UP) {
            selection--;
            if (selection < 0) selection = options.size() - 1;
        } else if (key.getKeyCode() == KeyEvent.VK_DOWN) {
            selection++;
            if (selection >= options.size()) selection = 0;
        } else if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            if (elapsed < 200) {
                return; // Ignorer l'appui sur Entrée s'il survient trop rapidement après l'ouverture du menu
            }
            String action = options.get(selection);
            executeAction(action);
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            menuOpen = false;
            menuTarget = null;
            menuAttacker = null;
        }
    }

    public static void executeAction(String action) {
        System.out.println("Action choisie : " + action);

        // 1. Délégation au niveau courant pour les interactions personnalisées (dialogues, autels...)
        if (currentLevel != null && currentLevel.onCustomInteraction(menuAttacker, menuTarget, action, selection, currentMap, options)) {
            if (!options.isEmpty() && com.eltim.rogue.system.dialogue.VarainDialogue.isDialogueActive()) {
                selection = 0;
                menuOpen = true;
                return;
            }
            menuOpen = false;
            return;
        }

        if (com.eltim.rogue.system.dialogue.VarainDialogue.getNpcSpeech() != null) {
            com.eltim.rogue.system.dialogue.VarainDialogue.closeDialogue();
            menuOpen = false;
            return;
        }

        menuOpen = false;

        if (action.equals("Combattre") || action.equals("Utiliser un objet")) {
                List<entity> enemies = findMonsterGroup(menuAttacker, menuTarget, currentMap);
                combatSysteme.startCombat(menuAttacker, enemies, currentMap);
            } else if (action.equals("Recruter")) {
                if (menuAttacker instanceof player && menuTarget instanceof npc) {
                    player p = (player) menuAttacker;
                    npc n = (npc) menuTarget;
                    p.addPartyMember(n);
                    if (currentMap != null) {
                        currentMap.removeEntity(n);
                    }
                    System.out.println(n.getName() + " a rejoint le groupe !");
                }
            } else if (menuTarget instanceof Interactable && menuAttacker instanceof player) {
                String examineText = (currentLevel != null) ? currentLevel.getCustomExamineText(menuTarget) : null;
                if (action.equalsIgnoreCase("Examiner") && examineText != null) {
                    ExplorationLog.addDescription(examineText);
                } else {
                    ((Interactable) menuTarget).handleInteraction((player) menuAttacker, action, currentMap);
                }
            } else if (menuTarget instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                com.eltim.rogue.entity.environment.DescriptionMarker dm = (com.eltim.rogue.entity.environment.DescriptionMarker) menuTarget;
                dm.markRead();
                ExplorationLog.addDescription(dm.getDescription());
            } else if (action.equals("Fuir")) {
                List<entity> enemies = findMonsterGroup(menuAttacker, menuTarget, currentMap);
                int playerDexMod = diceRollSysteme.getModifier(menuAttacker.getAgilite());
                int playerRoll = (int) (Math.random() * 20) + 1;
                int playerTotal = playerRoll + playerDexMod;

                int maxEnemyDex = 10;
                entity fastestEnemy = null;
                for (entity e : enemies) {
                    if (!e.isDead() && e.getAgilite() >= maxEnemyDex) {
                        maxEnemyDex = e.getAgilite();
                        fastestEnemy = e;
                    }
                }
                if (fastestEnemy == null && menuTarget != null) {
                    fastestEnemy = menuTarget;
                    maxEnemyDex = menuTarget.getAgilite();
                }

                int enemyDexMod = diceRollSysteme.getModifier(maxEnemyDex);
                int enemyRoll = (int) (Math.random() * 20) + 1;
                int enemyTotal = enemyRoll + enemyDexMod;
                String enemyName = (fastestEnemy != null && fastestEnemy.getName() != null) ? fastestEnemy.getName() : "Ennemi";

                boolean success = (playerTotal >= enemyTotal);

                ExplorationLog.add("« Fuite : Joueur " + playerTotal + " vs " + enemyName + " " + enemyTotal + " — " + (success ? "Succès" : "Échec") + " »");

                if (success) {
                    ExplorationLog.addDescription("FUITE RÉUSSIE ! Ennemis étourdis (5s).");
                    for (entity e : enemies) {
                        e.stunForMillis(5000);
                    }
                    menuOpen = false;
                } else {
                    ExplorationLog.addDescription("FUITE ÉCHOUÉE ! Combat engagé (1er tour perdu) !");
                    combatSysteme.startCombat(menuAttacker, enemies, currentMap, true);
                }
            } else if (action.equals("Fermer") || action.equals("Partir") || action.equals("Quitter")) {
                menuOpen = false;
            }
            
            menuTarget = null;
            menuAttacker = null;
    }

    public static boolean isMenuOpen() { return menuOpen; }
    public static entity getTarget() { return menuTarget; }
    public static List<String> getOptions() { return options; }
    public static int getSelection() { return selection; }

    /**
     * Recherche par propagation (BFS) les monstres à portée de poursuite du joueur (<= 3 cases)
     * et relayés de monstre en monstre à portée (<= 3 cases), jusqu'à un maximum de 6 ennemis.
     */
    public static List<entity> findMonsterGroup(entity playerEntity, entity initialTarget, map gameMap) {
        List<entity> enemies = new ArrayList<>();
        if (initialTarget instanceof monster) {
            enemies.add(initialTarget);
        }

        if (gameMap == null || !(playerEntity instanceof player)) {
            return enemies;
        }

        player p = (player) playerEntity;
        List<monster> candidates = new ArrayList<>();
        for (entity e : gameMap.getEntities()) {
            if (e instanceof monster && !enemies.contains(e)) {
                candidates.add((monster) e);
            }
        }

        Queue<entity> queue = new LinkedList<>();
        for (entity e : enemies) {
            queue.add(e);
        }

        // 1. Ajouter tout monstre directement à portée de poursuite du joueur (3 min + mod Sagesse si positif)
        for (monster m : new ArrayList<>(candidates)) {
            int sagMod = diceRollSysteme.getModifier(m.getSagesse());
            int pursuitRange = 3 + Math.max(0, sagMod);

            int distToPlayer = Math.abs(m.getX() - p.getX()) + Math.abs(m.getY() - p.getY());
            if (distToPlayer <= pursuitRange && !enemies.contains(m)) {
                enemies.add(m);
                queue.add(m);
                candidates.remove(m);
                if (enemies.size() >= 6) break;
            }
        }

        // 2. Relais de poursuite entre monstres : chaque monstre dans le groupe relaye la poursuite aux monstres à portée
        while (!queue.isEmpty() && enemies.size() < 6) {
            entity current = queue.poll();

            for (monster m : new ArrayList<>(candidates)) {
                int sagMod = diceRollSysteme.getModifier(m.getSagesse());
                int pursuitRange = 3 + Math.max(0, sagMod);

                int distToCurrent = Math.abs(m.getX() - current.getX()) + Math.abs(m.getY() - current.getY());
                if (distToCurrent <= pursuitRange) {
                    enemies.add(m);
                    queue.add(m);
                    candidates.remove(m);
                    if (enemies.size() >= 6) break;
                }
            }
        }

        return enemies;
    }
}

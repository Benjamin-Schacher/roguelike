package com.eltim.rogue.system;

import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.environment.DescriptionMarker;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.world.map;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class InteractionSysteme {
    private static boolean menuOpen = false;
    private static entity menuAttacker = null;
    private static entity menuTarget = null;
    private static map currentMap = null;
    private static List<String> options = new ArrayList<>();
    private static int selection = 0;
    // Description courante pour la popup ? (géré par le GameState DESCRIPTION)
    private static String currentDescription = null;

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

        menuOpen = true;
        menuAttacker = attacker;
        menuTarget = target;
        currentMap = gameMap;
        selection = 0;
        options.clear();

        
        String targetClass = target.getClass().getSimpleName().toLowerCase();
        
        if (target instanceof monster) {
            options.add("Combattre");
            options.add("Utiliser un objet");
            options.add("Fuir");
        } else if (target instanceof door) {
            door d = (door) target;
            if (d.getState() == doorStateEnum.OPEN) {
                // Porte déjà ouverte, on passe à travers
                menuOpen = false;
                return;
            }
            if (d.getState() == doorStateEnum.NORMAL) {
                options.add("Ouvrir");
            } else if (d.getState() == doorStateEnum.LOCKED) {
                options.add("Déverrouiller (Clé)");
            } else if (d.getState() == doorStateEnum.OLD) {
                options.add("Déverrouiller (Clé)");
                options.add("Forcer (Force)");
            }
            options.add("Partir");
        } else if (targetClass.equals("chest")) {
            options.add("Ouvrir");
            options.add("Partir");
        } else if (target instanceof npc) {
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
        if (key.getKeyCode() == KeyEvent.VK_UP) {
            selection--;
            if (selection < 0) selection = options.size() - 1;
        } else if (key.getKeyCode() == KeyEvent.VK_DOWN) {
            selection++;
            if (selection >= options.size()) selection = 0;
        } else if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            String action = options.get(selection);
            System.out.println("Action choisie : " + action);
            
            menuOpen = false;
            
            if (action.equals("Combattre")) {
                List<entity> enemies = new ArrayList<>();
                enemies.add(menuTarget);
                
                // Regroupe les monstres proches (distance de Manhattan <= 3)
                if (currentMap != null) {
                    for (entity e : currentMap.getEntities()) {
                        if (e instanceof monster && e != menuTarget && !enemies.contains(e)) {
                            int dist = Math.abs(e.getX() - menuTarget.getX()) + Math.abs(e.getY() - menuTarget.getY());
                            if (dist <= 3 && enemies.size() < 6) {
                                enemies.add(e);
                            }
                        }
                    }
                }
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
            } else if (action.equals("Ouvrir") && menuTarget instanceof door) {
                door d = (door) menuTarget;
                if (d.getState() == doorStateEnum.NORMAL || d.getState() == doorStateEnum.OLD) {
                    d.setState(doorStateEnum.OPEN);
                    d.setSymbol('D'); // reste 'D' mais sera vert
                    System.out.println("La porte s'ouvre.");
                } else {
                    System.out.println("La porte est verrouillée !");
                }
            } else if (action.equals("Déverrouiller (Clé)") && menuTarget instanceof door) {
                if (menuAttacker instanceof player) {
                    player p = (player) menuAttacker;
                    item foundKey = null;
                    for (item it : p.getInventory()) {
                        if (it instanceof key) {
                            foundKey = it;
                            break;
                        }
                    }
                    if (foundKey != null) {
                        door d = (door) menuTarget;
                        key k = (key) foundKey;
                        if (k.getKeyCode() == (d.getDoorCode())) {
                            p.getInventory().remove(foundKey);
                            d.setState(doorStateEnum.OPEN);
                            d.setSymbol('D');
                            System.out.println("Vous utilisez la clé. La porte s'ouvre !");
                        } else {
                            System.out.println("Vous n'avez pas la bonne clé !");
                        }
                    } else {
                        System.out.println("Vous n'avez pas de clé !");
                    }
                }
            } else if (action.equals("Forcer (Force)") && menuTarget instanceof door) {
                if (menuAttacker instanceof player) {
                    player p = (player) menuAttacker;
                    int forceMod = diceRollSysteme.getModifier(p.getForce());
                    int roll = (int)(Math.random() * 20) + 1;
                    boolean success = (roll + forceMod) >= 14;
                    ExplorationLog.addRoll("Forcer la porte", roll, forceMod, 14);
                    if (success) {
                        door d = (door) menuTarget;
                        d.setState(doorStateEnum.OPEN);
                        d.setSymbol('D');
                    } else {
                        // Échec : perte de 1d4 PV
                        int damage = (int)(Math.random() * 4) + 1;
                        p.setLifePoint(p.getLifePoint() - damage);
                        ExplorationLog.add("  ↳ Blessé de " + damage + " PV");
                    }
                }
            } else if (action.equals("Ouvrir") && menuTarget.getClass().getSimpleName().toLowerCase().equals("chest")) {
                if (menuAttacker instanceof player) {
                    player p = (player) menuAttacker;
                    com.eltim.rogue.entity.environment.chest c = (com.eltim.rogue.entity.environment.chest) menuTarget;
                    if (!c.isOpen()) {
                        c.setOpen(true);
                        List<item> loot = c.getLoot();
                        if (loot.isEmpty()) {
                            ExplorationLog.addDescription("Le coffre est vide.");
                        } else {
                            for (item it : loot) {
                                p.getInventory().add(it);
                                ExplorationLog.addDescription("Obtenu : " + it.getName());
                            }
                            c.getLoot().clear();
                        }
                    } else {
                        ExplorationLog.addDescription("Ce coffre a déjà été vidé.");
                    }
                }
            } else if (action.equals("Fermer") || action.equals("Partir") || action.equals("Quitter")) {
                menuOpen = false;
            }
            
            menuTarget = null;
            menuAttacker = null;
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            menuOpen = false;
            menuTarget = null;
            menuAttacker = null;
        }
    }

    public static boolean isMenuOpen() { return menuOpen; }
    public static entity getTarget() { return menuTarget; }
    public static List<String> getOptions() { return options; }
    public static int getSelection() { return selection; }
}

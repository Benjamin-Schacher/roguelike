package com.eltim.rogue.system;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.summon.summon;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.world.map;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class combatSysteme {
    private static boolean combatOpen = false;
    private static entity playerEntity;
    private static map currentMap;
    
    private static List<String> combatLog = new ArrayList<>();
    private static List<String> options = new ArrayList<>();
    private static int selection = 0;

    private static List<entity> allyGroup = new ArrayList<>();
    private static List<summon> summonGroup = new ArrayList<>();
    private static List<entity> enemyGroup = new ArrayList<>();
    
    private static List<entity> enemyFrontLine = new ArrayList<>();
    private static List<entity> enemyBackLine = new ArrayList<>();

    private static List<entity> turnQueue = new ArrayList<>();
    private static int currentTurnIndex = 0;
    private static entity currentActor;

    private static int targetEnemyIndex = 0;
    private static boolean isCombatEnding = false;
    private static boolean isVictory = false;
    private static int turnDelayCounter = 0;

    public static void startCombat(entity player, List<entity> enemies, map gameMap) {
        combatOpen = true;
        isCombatEnding = false;
        playerEntity = player;
        currentMap = gameMap;
        selection = 0;
        
        allyGroup.clear();
        allyGroup.add(player);
        if (player instanceof player) {
            player p = (player) player;
            allyGroup.addAll(p.getParty());
        }

        summonGroup.clear();
        if (player instanceof player) {
            player p = (player) player;
            summonGroup.addAll(p.getSummons());
        }

        enemyGroup.clear();
        enemyGroup.addAll(enemies);

        enemyFrontLine.clear();
        enemyBackLine.clear();
        for (int i = 0; i < enemies.size(); i++) {
            if (i < 3) {
                enemyFrontLine.add(enemies.get(i));
            } else if (i < 6) {
                enemyBackLine.add(enemies.get(i));
            }
        }

        buildTurnQueue();
        
        targetEnemyIndex = 0;
        findFirstLivingTarget();
        
        options.clear();
        options.add("Attaquer");
        options.add("Attaquer avec l'arme secondaire"); 
        options.add("Lancer un sort");
        options.add("Utiliser objet");
        options.add("Fuir");
        
        combatLog.clear();
        combatLog.add("Le combat commence !");
        combatLog.add("Ennemis - Ligne avant: " + enemyFrontLine.size() + " | Ligne arrière: " + enemyBackLine.size());
        
        playCurrentTurn();
    }

    private static void buildTurnQueue() {
        turnQueue.clear();
        for (entity e : allyGroup) if (!e.isDead()) turnQueue.add(e);
        for (summon s : summonGroup) if (!s.isDead()) turnQueue.add(s);
        for (entity e : enemyGroup) if (!e.isDead()) turnQueue.add(e);
        
        Collections.shuffle(turnQueue);
        
        turnQueue.sort(new Comparator<entity>() {
            @Override
            public int compare(entity e1, entity e2) {
                if (e1.getAgilite() != e2.getAgilite()) {
                    return Integer.compare(e2.getAgilite(), e1.getAgilite());
                }
                boolean e1IsAlly = allyGroup.contains(e1);
                boolean e2IsAlly = allyGroup.contains(e2);
                if (e1IsAlly && !e2IsAlly) return -1;
                if (!e1IsAlly && e2IsAlly) return 1;
                return 0;
            }
        });
        
        currentTurnIndex = 0;
    }

    private static void findFirstLivingTarget() {
        for (int i = 0; i < enemyGroup.size(); i++) {
            if (!enemyGroup.get(i).isDead()) {
                targetEnemyIndex = i;
                return;
            }
        }
        targetEnemyIndex = 0;
    }

    public static void handleInput(KeyEvent key) {
        if (!combatOpen) return;

        if (isCombatEnding) {
            if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                combatOpen = false;
            }
            return;
        }

        if (key.getKeyCode() == KeyEvent.VK_UP) {
            selection--;
            if (selection < 0) selection = options.size() - 1;
        } else if (key.getKeyCode() == KeyEvent.VK_DOWN) {
            selection++;
            if (selection >= options.size()) selection = 0;
        } else if (key.getKeyCode() == KeyEvent.VK_LEFT) {
            int start = targetEnemyIndex;
            do {
                targetEnemyIndex--;
                if (targetEnemyIndex < 0) targetEnemyIndex = enemyGroup.size() - 1;
            } while (enemyGroup.get(targetEnemyIndex).isDead() && targetEnemyIndex != start);
        } else if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
            int start = targetEnemyIndex;
            do {
                targetEnemyIndex++;
                if (targetEnemyIndex >= enemyGroup.size()) targetEnemyIndex = 0;
            } while (enemyGroup.get(targetEnemyIndex).isDead() && targetEnemyIndex != start);
        } else if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            executeAction();
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            combatOpen = false; 
        }
    }

    private static void playCurrentTurn() {
        if (isCombatEnding) return;
        
        if (currentTurnIndex >= turnQueue.size()) {
            buildTurnQueue();
            if (turnQueue.isEmpty()) return;
        }
        
        currentActor = turnQueue.get(currentTurnIndex);
        if (currentActor.isDead()) {
            nextTurn();
            return;
        }
        
        combatLog.add("--- Tour de " + currentActor.getName() + " ---");
        
        boolean isPlayerControlled = allyGroup.contains(currentActor);
        if (isPlayerControlled) {
            findFirstLivingTarget();
            selection = 0;
        } else {
            turnDelayCounter = 30; // Pause avant l'action de l'IA
        }
    }

    public static void tick() {
        if (!combatOpen || isCombatEnding || currentActor == null) return;

        boolean isPlayerControlled = allyGroup.contains(currentActor);
        if (!isPlayerControlled) {
            if (turnDelayCounter > 0) {
                turnDelayCounter--;
                return;
            }

            if (summonGroup.contains(currentActor)) {
                summon s = (summon) currentActor;
                s.performAction(enemyGroup);
            } else {
                enemyAiTurn(currentActor);
            }
            cleanCombatLog();
            nextTurn();
        }
    }

    private static void nextTurn() {
        if (checkVictory()) {
            victory();
            return;
        }
        if (playerEntity.isDead()) {
            defeat();
            return;
        }
        
        currentTurnIndex++;
        playCurrentTurn();
    }

    private static void executeAction() {
        if (isCombatEnding) return;

        String action = options.get(selection);
        boolean currentTurnUsed = true;
        
        entity targetEntity = enemyGroup.get(targetEnemyIndex);

        if (action.equals("Fuir")) {
            if (tryEscape(currentActor)) {
                return;
            }
        }
        else if (action.equals("Attaquer")) {
            weapon mainW = null;
            weapon secW = null;
            if (currentActor instanceof player) {
                player p = (player) currentActor;
                mainW = p.rightHand;
                secW = p.leftHand;
            }
            
            boolean isRanged = (mainW != null && mainW.getWeaponType() == weaponTypeEnum.DISTANCE);
            if (!isRanged && enemyBackLine.contains(targetEntity)) {
                boolean frontAlive = false;
                for (entity e : enemyFrontLine) {
                    if (!e.isDead()) { frontAlive = true; break; }
                }
                if (frontAlive) {
                    combatLog.add("> Cible inatteignable (Ligne avant en vie !)");
                    currentTurnUsed = false;
                }
            }
            
            if (currentTurnUsed) {
                boolean hasTwoWeapons = (mainW != null && secW != null && mainW.getWeaponItemType() != itemTypeEnum.SHIELD && secW.getWeaponItemType() != itemTypeEnum.SHIELD);
                boolean hasDagger = hasTwoWeapons && (mainW.getWeaponItemType() == itemTypeEnum.DAGGER || secW.getWeaponItemType() == itemTypeEnum.DAGGER);
                
                int attackDice = 20;
                if (hasTwoWeapons && !hasDagger) {
                    attackDice = 12;
                }
                
                if (attackSysteme.doAttackWithWeapon(currentActor, targetEntity, mainW, attackDice)) {
                    if (hasTwoWeapons && !targetEntity.isDead()) {
                        combatLog.add("  [Seconde Arme]");
                        attackSysteme.doAttackWithWeapon(currentActor, targetEntity, secW, 20);
                    }
                }
            }
        } 
        else if (action.equals("Attaquer avec l'arme secondaire")) {
            weapon secW = null;
            if (currentActor instanceof player) {
                player p = (player) currentActor;
                secW = p.leftHand;
            }
            if (secW != null && secW.getWeaponItemType() != itemTypeEnum.SHIELD) {
                boolean isRanged = (secW.getWeaponType() == weaponTypeEnum.DISTANCE);
                if (!isRanged && enemyBackLine.contains(targetEntity)) {
                    boolean frontAlive = false;
                    for (entity e : enemyFrontLine) {
                        if (!e.isDead()) { frontAlive = true; break; }
                    }
                    if (frontAlive) {
                        combatLog.add("> Cible inatteignable (Ligne avant en vie !)");
                        currentTurnUsed = false;
                    }
                }
                if (currentTurnUsed) {
                    attackSysteme.doAttackWithWeapon(currentActor, targetEntity, secW, 20);
                }
            } else {
                combatLog.add("> Action impossible : Pas d'arme secondaire !");
                currentTurnUsed = false;
            }
        }
        else if (action.equals("Lancer un sort")) {
            if (currentActor.getMana() < 2) {
                combatLog.add("> Action impossible : Pas assez de Mana ! (Requis: 2)");
                currentTurnUsed = false;
            } else {
                if (trySpell(currentActor, targetEntity)) {
                    currentActor.setMana(currentActor.getMana() - 2);
                    int degatSort = 4 + diceRollSysteme.getModifier(getMagicStatValue(currentActor));
                    if (degatSort < 1) degatSort = 1;
                    targetEntity.setLifePoint(targetEntity.getLifePoint() - degatSort);
                    combatLog.add("  -> " + currentActor.getName() + " inflige " + degatSort + " dégâts magiques !");
                } else {
                    currentActor.setMana(currentActor.getMana() - 2);
                }
            }
        } 
        else if (action.equals("Utiliser objet")) {
            if (currentActor instanceof player) {
                player p = (player) currentActor;
                com.eltim.rogue.item.base.item potionItem = null;
                for (com.eltim.rogue.item.base.item it : p.getInventory()) {
                    if (it instanceof com.eltim.rogue.item.potion) {
                        potionItem = it;
                        break;
                    }
                }
                if (potionItem != null) {
                    p.useItem(potionItem);
                    combatLog.add("> Utilise une potion.");
                } else {
                    combatLog.add("> Pas de potion dans l'inventaire !");
                    currentTurnUsed = false;
                }
            } else {
                currentTurnUsed = false;
            }
        }

        if (!currentTurnUsed) {
            return;
        }

        cleanCombatLog();
        nextTurn();
    }

    private static void enemyAiTurn(entity enemy) {
        List<entity> potentialTargets = new ArrayList<>();
        for (entity ally : allyGroup) {
            if (!ally.isDead()) potentialTargets.add(ally);
        }
        for (summon s : summonGroup) {
            if (!s.isDead()) potentialTargets.add(s);
        }

        if (potentialTargets.isEmpty() || playerEntity.isDead()) {
            return;
        }

        int randIdx = (int) (Math.random() * potentialTargets.size());
        entity target = potentialTargets.get(randIdx);

        combatLog.add("> " + enemy.getName() + " attaque " + target.getName() + ".");
        attackSysteme.doAttackWithWeapon(enemy, target, null, 20);
    }

    private static boolean checkVictory() {
        for (entity enemy : enemyGroup) {
            if (!enemy.isDead()) return false;
        }
        return true;
    }

    private static void victory() {
        isCombatEnding = true;
        isVictory = true;
        options.clear();
        options.add("[ Continuer ]");
        selection = 0;

        combatLog.add(">>> VICTOIRE !!! <<<");

        int totalXp = 0;
        for (entity e : enemyGroup) {
            if (e instanceof monster) {
                totalXp += ((monster) e).getXpReward();
            }
        }
        playerEntity.addXp(totalXp);
        combatLog.add("> Gain : " + totalXp + " XP.");

        for (entity e : enemyGroup) {
            if (e instanceof monster) {
                monster m = (monster) e;
                com.eltim.rogue.item.base.item loot = m.rollLoot();
                if (loot != null) {
                    if (playerEntity instanceof player) {
                        ((player) playerEntity).addLoot(loot);
                    }
                    combatLog.add("> Loot : " + loot.getName() + ".");
                }
            }
        }

        if (currentMap != null) {
            for (entity e : enemyGroup) {
                currentMap.removeEntity(e);
            }
        }

        if (playerEntity instanceof player) {
            ((player) playerEntity).getSummons().clear();
        }
    }

    private static void defeat() {
        isCombatEnding = true;
        isVictory = false;
        options.clear();
        options.add("[ Continuer ]");
        selection = 0;
        combatLog.add(">>> DÉFAITE ! GAME OVER <<<");
    }

    private static void cleanCombatLog() {
        while (combatLog.size() > 10) {
            combatLog.remove(0);
        }
    }

    public static boolean tryEscape(entity target) {
        int defenseFuite = 10 + diceRollSysteme.getModifier(target.getAgilite());
        if (diceRollSysteme.rollDice(20, diceRollSysteme.getModifier(playerEntity.getAgilite()), defenseFuite)) {
            combatLog.add("> Fuite réussie !");
            combatOpen = false;
            return true;
        } else {
            combatLog.add("> Fuite échouée.");
            return false;
        }
    }

    public static boolean trySpell(entity attacker, entity target) {
        int magicValue = getMagicStatValue(attacker);
        int targetDefense = 10 + diceRollSysteme.getModifier(target.getAgilite());

        if (diceRollSysteme.rollDice(20, diceRollSysteme.getModifier(magicValue), targetDefense)) {
            return true;
        } else {
            combatLog.add("> Incantation ratée.");
            return false;
        }
    }

    private static int getMagicStatValue(entity e) {
        if (e.getMagicDetermination() == entity.MagicStat.INT) {
            return e.getIntelligence();
        } else if (e.getMagicDetermination() == entity.MagicStat.SAG) {
            return e.getSagesse();
        } else if (e.getMagicDetermination() == entity.MagicStat.CHA) {
            return e.getCharisme();
        }
        return e.getIntelligence();
    }

    public static boolean isCombatOpen() { return combatOpen; }
    public static entity getPlayer() { return playerEntity; }
    public static entity getCurrentActor() { return currentActor; }
    public static boolean isCombatEnding() { return isCombatEnding; }
    public static boolean isVictory() { return isVictory; }
    public static List<entity> getAllyGroup() { return allyGroup; }
    public static List<summon> getSummonGroup() { return summonGroup; }
    public static List<entity> getEnemyGroup() { return enemyGroup; }
    public static int getTargetEnemyIndex() { return targetEnemyIndex; }
    public static List<String> getOptions() { return options; }
    public static int getSelection() { return selection; }
    public static List<String> getLog() { return combatLog; }
}

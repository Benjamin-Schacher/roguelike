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
    private static long combatOpenTime = 0;
    private static boolean itemUsedThisTurn = false;
    private static boolean inItemSubMenu = false;
    private static List<com.eltim.rogue.item.base.item> usableItemsSubMenu = new ArrayList<>();
    private static boolean inSkillSubMenu = false;
    private static List<com.eltim.rogue.entity.classe.Skill> usableSkillsSubMenu = new ArrayList<>();

    private static void resetMainMenuOptions() {
        inItemSubMenu = false;
        inSkillSubMenu = false;
        usableItemsSubMenu.clear();
        usableSkillsSubMenu.clear();
        options.clear();
        options.add("Attaquer");
        options.add("Attaquer avec l'arme secondaire"); 
        options.add("Utiliser une compétence");
        options.add("Utiliser objet");
        options.add("Fuir");
        selection = 0;
    }

    public static void startCombat(entity player, List<entity> enemies, map gameMap) {
        startCombat(player, enemies, gameMap, false);
    }

    public static void startCombat(entity player, List<entity> enemies, map gameMap, boolean skipFirstPlayerTurn) {
        com.eltim.rogue.engine.inputHandler.clearInput();
        com.eltim.rogue.engine.sound.SoundManager.getInstance().startCombatMusic();
        combatOpen = true;
        combatOpenTime = System.currentTimeMillis();
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
        resetMainMenuOptions();
        
        combatLog.clear();
        combatLog.add("Le combat commence !");
        combatLog.add("Ennemis - Ligne avant: " + enemyFrontLine.size() + " | Ligne arrière: " + enemyBackLine.size());

        if (skipFirstPlayerTurn) {
            combatLog.add("> Fuite pré-combat échouée : Vous perdez votre premier tour !");
            if (!turnQueue.isEmpty() && allyGroup.contains(turnQueue.get(0))) {
                currentTurnIndex++;
            }
        }
        
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

    private static final long DEBOUNCE_MS = 250;

    public static void handleInput(KeyEvent key) {
        if (!combatOpen) return;

        long now = System.currentTimeMillis();
        long elapsed = now - combatOpenTime;

        if (isCombatEnding) {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (elapsed >= DEBOUNCE_MS) {
                    combatOpen = false;
                    com.eltim.rogue.engine.inputHandler.clearInput();
                }
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
            if (elapsed < DEBOUNCE_MS) {
                return; // Ignorer la touche de confirmation si elle survient trop rapidement après le début du combat
            }
            executeAction();
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (inItemSubMenu || inSkillSubMenu) {
                boolean wasSkill = inSkillSubMenu;
                resetMainMenuOptions();
                selection = wasSkill ? 2 : 3;
                combatLog.add("> Action annulée.");
                return;
            }
            combatOpen = false; 
            com.eltim.rogue.engine.inputHandler.clearInput();
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

        // Traitement des altérations (Bonus / Malus) actives au début du tour
        if (currentActor.getAlterationList() != null && !currentActor.getAlterationList().isEmpty()) {
            List<com.eltim.rogue.alteration.alteration> toRemove = new ArrayList<>();
            for (com.eltim.rogue.alteration.alteration alt : currentActor.getAlterationList()) {
                if (alt.getType() == com.eltim.rogue.alteration.alteration.Type.MALUS && alt.getValue() > 0) {
                    currentActor.setLifePoint(Math.max(0, currentActor.getLifePoint() - alt.getValue()));
                    combatLog.add("  -> " + currentActor.getName() + " subit " + alt.getValue() + " dégâts de [" + alt.getName() + "] !");
                } else if (alt.getType() == com.eltim.rogue.alteration.alteration.Type.BUFF && alt.getValue() > 0 && alt.getName().toLowerCase().contains("régén")) {
                    int heal = alt.getValue();
                    currentActor.setLifePoint(Math.min(currentActor.getMaxLifePoint(), currentActor.getLifePoint() + heal));
                    combatLog.add("  -> " + currentActor.getName() + " régénère " + heal + " PV grâce à [" + alt.getName() + "] !");
                }
                alt.tickTurn();
                if (alt.isExpired()) {
                    toRemove.add(alt);
                }
            }
            for (com.eltim.rogue.alteration.alteration exp : toRemove) {
                currentActor.removeAlteration(exp);
                combatLog.add("  -> L'effet [" + exp.getName() + "] sur " + currentActor.getName() + " a pris fin.");
            }
        }

        if (currentActor.isDead()) {
            combatLog.add("> " + currentActor.getName() + " s'effondre sous les effets subis !");
            cleanCombatLog();
            nextTurn();
            return;
        }

        if (currentActor.isStunned()) {
            combatLog.add("> " + currentActor.getName() + " est étourdi et passe son tour !");
            cleanCombatLog();
            nextTurn();
            return;
        }
        
        boolean isPlayerControlled = allyGroup.contains(currentActor);
        if (isPlayerControlled) {
            itemUsedThisTurn = false;
            findFirstLivingTarget();
            resetMainMenuOptions();
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

        if (inSkillSubMenu) {
            if (selection >= usableSkillsSubMenu.size() || options.get(selection).equals("[ Annuler ]")) {
                resetMainMenuOptions();
                selection = 2; // Replacer le curseur sur "Utiliser une compétence"
                combatLog.add("> Utilisation de compétence annulée.");
                return;
            }

            com.eltim.rogue.entity.classe.Skill chosenSkill = usableSkillsSubMenu.get(selection);
            entity targetEntity = enemyGroup.get(targetEnemyIndex);

            executeSkillEffect(currentActor, targetEntity, chosenSkill);

            cleanCombatLog();
            resetMainMenuOptions();
            nextTurn();
            return;
        }

        if (inItemSubMenu) {
            if (selection >= usableItemsSubMenu.size() || options.get(selection).equals("[ Annuler ]")) {
                resetMainMenuOptions();
                selection = 3;
                combatLog.add("> Utilisation d'objet annulée.");
                return;
            }

            com.eltim.rogue.item.base.item chosenItem = usableItemsSubMenu.get(selection);
            entity targetEntity = enemyGroup.get(targetEnemyIndex);

            if (chosenItem != null && chosenItem.getSoundName() != null && !chosenItem.getSoundName().isEmpty()) {
                com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX(chosenItem.getSoundName());
            }

            if (chosenItem instanceof com.eltim.rogue.item.potion) {
                ((com.eltim.rogue.item.potion) chosenItem).applyEffect(currentActor);
                combatLog.add("  -> " + currentActor.getName() + " consomme " + chosenItem.getName() + ".");
            } else if (chosenItem instanceof com.eltim.rogue.item.objectItem) {
                com.eltim.rogue.item.objectItem obj = (com.eltim.rogue.item.objectItem) chosenItem;
                // A. Projectiles offensifs à dégâts (Dague, Pierre, Shuriken, Poudre noire, Grenade, Trait de feu)
                if (obj.getDamageDiceCount() > 0 && obj.getDamageDiceSides() > 0) {
                    int dmg = obj.rollDamage();
                    if (dmg < 1) dmg = 1;
                    targetEntity.setLifePoint(targetEntity.getLifePoint() - dmg);
                    combatLog.add("  -> " + chosenItem.getName() + " inflige " + dmg + " dégâts à " + targetEntity.getName() + " !");
                } 
                // B. Objet empoisonné (Fiole de poison)
                else if (obj.getTempDamageOverTime() > 0) {
                    obj.applyEffect(targetEntity);
                    combatLog.add("  -> " + chosenItem.getName() + " empoisonne " + targetEntity.getName() + " !");
                } 
                // C. Buff / Debuff ciblé (Sac de sable, Parchemins, Fumigène)
                else if (obj.getBuffEffect() != null && !obj.getBuffEffect().isEmpty()) {
                    obj.applyEffect(targetEntity);
                    combatLog.add("  -> " + chosenItem.getName() + " applique l'effet (" + obj.getBuffEffect() + ") sur " + targetEntity.getName() + " !");
                } 
                // D. Soin personnel (Bandage)
                else if (obj.getHealAmount() > 0) {
                    obj.applyEffect(currentActor);
                    combatLog.add("  -> " + chosenItem.getName() + " soigne " + currentActor.getName() + " de " + obj.getHealAmount() + " PV !");
                } 
                else {
                    obj.applyEffect(currentActor);
                    combatLog.add("  -> " + chosenItem.getName() + " utilisé par " + currentActor.getName() + ".");
                }
            }

            if (playerEntity instanceof player) {
                ((player) playerEntity).getInventory().remove(chosenItem);
            }

            itemUsedThisTurn = true;
            combatLog.add("> [Objet] " + currentActor.getName() + " utilise " + chosenItem.getName() + " !");
            cleanCombatLog();

            resetMainMenuOptions();
            return;
        }

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
        else if (action.equals("Utiliser une compétence")) {
            usableSkillsSubMenu.clear();
            if (currentActor instanceof player) {
                player p = (player) currentActor;
                if (p.classe != null && p.classe.trees != null) {
                    for (com.eltim.rogue.entity.classe.SkillTree tree : p.classe.trees) {
                        for (com.eltim.rogue.entity.classe.Skill sk : tree.skills) {
                            if (sk.unlocked) {
                                usableSkillsSubMenu.add(sk);
                            }
                        }
                    }
                }
            }

            if (usableSkillsSubMenu.isEmpty()) {
                combatLog.add("> Aucune compétence débloquée ! (Appuyez sur [K] hors-combat)");
                return;
            }

            inSkillSubMenu = true;
            options.clear();
            for (com.eltim.rogue.entity.classe.Skill sk : usableSkillsSubMenu) {
                options.add("★ " + sk.name);
            }
            options.add("[ Annuler ]");
            selection = 0;
            combatLog.add("--- Choisissez une compétence à utiliser ---");
            return;
        } 
        else if (action.equals("Utiliser objet")) {
            if (itemUsedThisTurn) {
                combatLog.add("> Vous avez déjà utilisé un objet durant ce tour !");
                return;
            }
            if (playerEntity instanceof player) {
                player p = (player) playerEntity;
                usableItemsSubMenu.clear();
                for (com.eltim.rogue.item.base.item it : p.getInventory()) {
                    if (it instanceof com.eltim.rogue.item.potion || it instanceof com.eltim.rogue.item.objectItem) {
                        usableItemsSubMenu.add(it);
                    }
                }
                if (usableItemsSubMenu.isEmpty()) {
                    combatLog.add("> Pas d'objet utilisable disponible dans l'inventaire !");
                    return;
                }

                inItemSubMenu = true;
                options.clear();
                for (com.eltim.rogue.item.base.item it : usableItemsSubMenu) {
                    options.add("⚡ " + it.getName());
                }
                options.add("[ Annuler ]");
                selection = 0;
                combatLog.add("--- Choisissez un objet à utiliser ---");
                return;
            } else {
                return;
            }
        }

        if (!currentTurnUsed) {
            return;
        }

        cleanCombatLog();
        nextTurn();
    }

    private static void enemyAiTurn(entity enemy) {
        if (enemy != null && enemy.getSoundName() != null && !enemy.getSoundName().isEmpty()) {
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX(enemy.getSoundName());
        }

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

        attackSysteme.doAttackWithWeapon(enemy, target, null, 20);
    }

    private static boolean checkVictory() {
        for (entity enemy : enemyGroup) {
            if (!enemy.isDead()) return false;
        }
        return true;
    }

    private static void victory() {
        combatOpenTime = System.currentTimeMillis();
        isCombatEnding = true;
        isVictory = true;
        options.clear();
        options.add("[ Continuer ]");
        selection = 0;

        com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("victory");
        com.eltim.rogue.engine.sound.SoundManager.getInstance().restorePreviousMusic();

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
                List<com.eltim.rogue.item.base.item> loots = m.rollLoots();
                for (com.eltim.rogue.item.base.item loot : loots) {
                    if (playerEntity instanceof player) {
                        ((player) playerEntity).addLoot(loot);
                    }
                    combatLog.add("> Loot : " + loot.getName() + ".");
                }

                int goldGained = m.rollGold();
                if (goldGained > 0) {
                    playerEntity.addGold(goldGained);
                    combatLog.add("> Or gagné : " + goldGained + " pièces d'or.");
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
        combatOpenTime = System.currentTimeMillis();
        isCombatEnding = true;
        isVictory = false;
        options.clear();
        options.add("[ Continuer ]");
        selection = 0;
        com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("defeat");
        combatLog.add(">>> DÉFAITE ! GAME OVER <<<");
    }

    private static void cleanCombatLog() {
        while (combatLog.size() > 10) {
            combatLog.remove(0);
        }
    }

    public static boolean tryEscape(entity target) {
        int playerDexMod = diceRollSysteme.getModifier(playerEntity.getAgilite());
        int playerRoll = (int)(Math.random() * 20) + 1;
        int playerTotal = playerRoll + playerDexMod;
        String playerModSign = (playerDexMod >= 0) ? ("+" + playerDexMod) : String.valueOf(playerDexMod);

        int maxEnemyDex = 10;
        entity fastestEnemy = null;
        for (entity e : enemyGroup) {
            if (!e.isDead() && e.getAgilite() >= maxEnemyDex) {
                maxEnemyDex = e.getAgilite();
                fastestEnemy = e;
            }
        }
        if (fastestEnemy == null && target != null) {
            fastestEnemy = target;
            maxEnemyDex = target.getAgilite();
        }

        int enemyDexMod = diceRollSysteme.getModifier(maxEnemyDex);
        int enemyRoll = (int)(Math.random() * 20) + 1;
        int enemyTotal = enemyRoll + enemyDexMod;
        String enemyModSign = (enemyDexMod >= 0) ? ("+" + enemyDexMod) : String.valueOf(enemyDexMod);
        String enemyName = (fastestEnemy != null && fastestEnemy.getName() != null) ? fastestEnemy.getName() : "Ennemi";

        boolean success = (playerTotal >= enemyTotal);

        ExplorationLog.add("« Fuite : Joueur " + playerTotal + " vs " + enemyName + " " + enemyTotal + " — " + (success ? "Succès" : "Échec") + " »");

        if (success) {
            combatLog.add("> Jet Opposé de Fuite (DEX) : Joueur (" + playerRoll + playerModSign + "=" + playerTotal + ") vs " + enemyName + " (" + enemyRoll + enemyModSign + "=" + enemyTotal + ") — SUCCÈS !");
            combatLog.add("  ↳ Ennemis étourdis pendant 5 secondes !");

            for (entity e : enemyGroup) {
                e.stunForMillis(5000);
            }

            combatOpen = false;
            com.eltim.rogue.engine.sound.SoundManager.getInstance().restorePreviousMusic();
            return true;
        } else {
            combatLog.add("> Jet Opposé de Fuite (DEX) : Joueur (" + playerRoll + playerModSign + "=" + playerTotal + ") vs " + enemyName + " (" + enemyRoll + enemyModSign + "=" + enemyTotal + ") — ÉCHEC !");
            combatLog.add("  ↳ Vous perdez votre tour !");
            return false;
        }
    }

    public static boolean trySpell(entity attacker, entity target) {
        int magicValue = getMagicStatValue(attacker);
        int magicMod = diceRollSysteme.getModifier(magicValue);
        int targetDefense = 10 + diceRollSysteme.getModifier(target.getAgilite());
        int roll = (int)(Math.random() * 20) + 1;
        int total = roll + magicMod;
        String modSign = (magicMod >= 0) ? ("+" + magicMod) : String.valueOf(magicMod);

        if (total >= targetDefense) {
            combatLog.add("> " + attacker.getName() + " lance un [Sort Magique] (Jet d20: " + roll + " " + modSign + " = " + total + " vs DEF " + targetDefense + ") -> INCANTATION RÉUSSIE !");
            return true;
        } else {
            combatLog.add("> " + attacker.getName() + " lance un [Sort Magique] (Jet d20: " + roll + " " + modSign + " = " + total + " vs DEF " + targetDefense + ") -> INCANTATION RATÉE !");
            return false;
        }
    }

    private static void executeSkillEffect(entity actor, entity target, com.eltim.rogue.entity.classe.Skill skill) {
        if (skill == null) return;

        if (skill.soundName != null && !skill.soundName.isEmpty()) {
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX(skill.soundName);
        }

        combatLog.add("> [Compétence] " + actor.getName() + " déclenche " + skill.name + " !");

        String id = skill.id != null ? skill.id : "";
        int strMod = diceRollSysteme.getModifier(actor.getForce());
        int dexMod = diceRollSysteme.getModifier(actor.getAgilite());
        int intMod = diceRollSysteme.getModifier(actor.getIntelligence());
        int sagMod = diceRollSysteme.getModifier(actor.getSagesse());

        switch (id) {
            case "jet_sable":
                target.addAlteration(new com.eltim.rogue.alteration.alteration("Aveuglé", com.eltim.rogue.alteration.alteration.Type.MALUS, 3, -2));
                combatLog.add("  -> " + target.getName() + " est aveuglé par le sable (-2 aux jets de toucher) !");
                break;
            case "posture_defensive":
            case "bouclier_foi":
                String buffDefName = id.equals("bouclier_foi") ? "Bouclier de Foi" : "Posture Défensive";
                actor.addAlteration(new com.eltim.rogue.alteration.alteration(buffDefName, com.eltim.rogue.alteration.alteration.Type.BUFF, 3, 3));
                combatLog.add("  -> " + actor.getName() + " adopte une posture défensive (Défense augmentée) !");
                break;
            case "attaque_ampleur":
            case "boule_de_feu":
            case "tempete_acier_magique":
                int cleaveCount = 0;
                int baseCleaveDmg = id.equals("boule_de_feu") ? (10 + intMod) : (6 + strMod);
                for (entity e : enemyFrontLine) {
                    if (!e.isDead()) {
                        int dmg = Math.max(1, baseCleaveDmg);
                        e.setLifePoint(e.getLifePoint() - dmg);
                        cleaveCount++;
                    }
                }
                if (cleaveCount == 0 && !target.isDead()) {
                    int dmg = Math.max(1, baseCleaveDmg);
                    target.setLifePoint(target.getLifePoint() - dmg);
                    cleaveCount = 1;
                }
                combatLog.add("  -> Frappe de zone touchant " + cleaveCount + " cible(s) adverse(s) !");
                break;
            case "etourdissement":
            case "grenade_flash":
            case "nova_glace":
                target.stunForMillis(2000);
                target.addAlteration(new com.eltim.rogue.alteration.alteration("Étourdi", com.eltim.rogue.alteration.alteration.Type.MALUS, 1));
                combatLog.add("  -> " + target.getName() + " est sous le choc et étourdi !");
                break;
            case "hurlement_guerrier":
            case "chant_courage":
                String warCryName = id.equals("chant_courage") ? "Chant de Courage" : "Cri de Guerre";
                for (entity a : allyGroup) {
                    a.addAlteration(new com.eltim.rogue.alteration.alteration(warCryName, com.eltim.rogue.alteration.alteration.Type.BUFF, 3, 2));
                }
                combatLog.add("  -> Cri de guerre et ferveur ! L'équipe gagne un bonus aux dégâts !");
                break;
            case "hurlement_provocation":
            case "totem_provoc":
                actor.addAlteration(new com.eltim.rogue.alteration.alteration("Provocation", com.eltim.rogue.alteration.alteration.Type.BUFF, 2));
                combatLog.add("  -> " + actor.getName() + " attire la haine des ennemis !");
                break;
            case "second_souffle":
            case "priere_soin":
            case "toucher_guerisseur":
            case "imposition_mains":
                int heal = 12 + Math.max(diceRollSysteme.getModifier(actor.getConstitution()), sagMod) * 2;
                if (id.equals("imposition_mains")) heal += 10;
                if (heal < 5) heal = 5;
                actor.setLifePoint(Math.min(actor.getMaxLifePoint(), actor.getLifePoint() + heal));
                actor.addAlteration(new com.eltim.rogue.alteration.alteration("Bénédiction de Vie", com.eltim.rogue.alteration.alteration.Type.BUFF, 2, 2));
                combatLog.add("  -> " + actor.getName() + " récupère " + heal + " PV !");
                break;
            case "lame_empoisonnee":
            case "poison_neuro":
                target.addAlteration(new com.eltim.rogue.alteration.alteration("Poison", com.eltim.rogue.alteration.alteration.Type.MALUS, 3, 3));
                int pDmg = 8 + dexMod;
                target.setLifePoint(target.getLifePoint() - pDmg);
                combatLog.add("  -> Lame empoisonnée infligeant " + pDmg + " dégâts et empoisonnant " + target.getName() + " !");
                break;
            case "brulure_persistante":
            case "fleche_enflammee":
                target.addAlteration(new com.eltim.rogue.alteration.alteration("Brûlure", com.eltim.rogue.alteration.alteration.Type.MALUS, 3, 4));
                int bDmg = 8 + intMod;
                target.setLifePoint(target.getLifePoint() - bDmg);
                combatLog.add("  -> Enflamme la cible infligeant " + bDmg + " dégâts et brûlure persistante !");
                break;
            case "regeneration_sacree":
            case "rosee_restauratrice":
                for (entity a : allyGroup) {
                    a.addAlteration(new com.eltim.rogue.alteration.alteration("Régénération", com.eltim.rogue.alteration.alteration.Type.BUFF, 3, 4));
                }
                combatLog.add("  -> Une aura de régénération sacrée enveloppe tous les alliés (+4 PV/tour) !");
                break;
            case "attaque_sournoise":
            case "execution":
            case "frappe_sournoise_g":
                int sneakDmg = 10 + dexMod * 2;
                if (id.equals("execution") && target.getLifePoint() < target.getMaxLifePoint() * 0.4) sneakDmg *= 2;
                target.setLifePoint(target.getLifePoint() - sneakDmg);
                combatLog.add("  -> Coup critique sournois infligeant " + sneakDmg + " dégâts à " + target.getName() + " !");
                break;
            case "double_fleche":
            case "cent_coups":
            case "danse_lames":
                int strikes = id.equals("cent_coups") ? 3 : (id.equals("danse_lames") ? 4 : 2);
                int strikeDmg = Math.max(2, 5 + Math.max(dexMod, strMod));
                for (int s = 0; s < strikes; s++) {
                    target.setLifePoint(target.getLifePoint() - strikeDmg);
                }
                combatLog.add("  -> Enchaîne " + strikes + " coups fulgurants infligeant " + (strikeDmg * strikes) + " dégâts au total !");
                break;
            case "toucher_vampirique":
                int vampDmg = 8 + intMod;
                target.setLifePoint(target.getLifePoint() - vampDmg);
                actor.setLifePoint(Math.min(actor.getMaxLifePoint(), actor.getLifePoint() + vampDmg));
                actor.addAlteration(new com.eltim.rogue.alteration.alteration("Siphon Vital", com.eltim.rogue.alteration.alteration.Type.BUFF, 2));
                combatLog.add("  -> Draine " + vampDmg + " PV à " + target.getName() + " pour régénérer le lanceur !");
                break;
            case "meteore":
            case "colere_dieux":
            case "fureur_elements":
            case "pluie_fleches":
                int cataclysmDmg = 14 + Math.max(intMod, sagMod) * 2;
                for (entity e : enemyGroup) {
                    if (!e.isDead()) {
                        e.setLifePoint(e.getLifePoint() - cataclysmDmg);
                    }
                }
                combatLog.add("  -> CATACLYSME DIVIN/MAGIQUE ! Inflige " + cataclysmDmg + " dégâts à TOUS les ennemis !");
                break;
            default:
                // Attaque / Sort magique générique de classe
                int dmg = 8 + Math.max(Math.max(strMod, dexMod), Math.max(intMod, sagMod));
                if (dmg < 2) dmg = 2;
                target.setLifePoint(target.getLifePoint() - dmg);
                combatLog.add("  -> Inflige " + dmg + " dégâts à " + target.getName() + " !");
                break;
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

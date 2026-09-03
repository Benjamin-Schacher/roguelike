package com.eltim.rogue.system;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;
import com.eltim.rogue.spell.spell;

public class attackSysteme {
    
    public static boolean doAttackWithWeapon(entity attacker, entity target, weapon w, int diceSides) {
        int statAttack = attacker.getForce();
        int targetDefense = 10 + diceRollSysteme.getModifier(target.getAgilite());

        if (w != null) {
            if (w.getWeaponType() == weaponTypeEnum.DISTANCE) {
                statAttack = attacker.getAgilite();
            }
            if (w.getDamageType() != damageTypeEnum.PHYSICAL) {
                statAttack = attacker.getIntelligence();
                targetDefense = target.getMagicalDefence();
            } else {
                targetDefense = target.getPhysicalDefence();
            }
        }

        int roll = (int) (Math.random() * diceSides) + 1;
        boolean isCrit = (roll == diceSides && diceSides == 20);
        int statMod = diceRollSysteme.getModifier(statAttack);
        int attackRoll = roll + statMod;

        String weaponName = (w != null && w.getName() != null && !w.getName().isEmpty()) ? w.getName() : "Mains nues";
        String modSign = (statMod >= 0) ? ("+" + statMod) : String.valueOf(statMod);

        if (isCrit) {
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("crit");
            combatSysteme.getLog().add("> " + attacker.getName() + " utilise [" + weaponName + "] (Jet d" + diceSides + ": " + roll + " - CRITIQUE !) vs DEF " + targetDefense);
            damageSysteme.doDamageWithWeapon(attacker, target, w, true);
            return true;
        }

        if (attackRoll >= targetDefense) {
            String soundToPlay = null;
            if (w != null && w.getSoundName() != null && !w.getSoundName().isEmpty()) {
                soundToPlay = w.getSoundName();
            } else if (attacker != null && attacker.getSoundName() != null && !attacker.getSoundName().isEmpty()) {
                soundToPlay = attacker.getSoundName();
            } else {
                soundToPlay = "hit";
            }
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX(soundToPlay);
            combatSysteme.getLog().add("> " + attacker.getName() + " utilise [" + weaponName + "] (Jet d" + diceSides + ": " + roll + " " + modSign + " = " + attackRoll + " vs DEF " + targetDefense + ") -> TOUCHE !");
            damageSysteme.doDamageWithWeapon(attacker, target, w, false);
            return true;
        } else {
            combatSysteme.getLog().add("> " + attacker.getName() + " utilise [" + weaponName + "] (Jet d" + diceSides + ": " + roll + " " + modSign + " = " + attackRoll + " vs DEF " + targetDefense + ") -> RATÉ !");
            return false;
        }
    }

    public static void doAttackWithSpell(entity attacker, entity target, spell s) {
        
    }
}

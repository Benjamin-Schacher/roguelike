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
        int attackRoll = roll + diceRollSysteme.getModifier(statAttack);

        if (isCrit) {
            combatSysteme.getLog().add("> [" + attacker.getName() + "] FAIT UN CRITIQUE sur " + target.getName() + " !");
            damageSysteme.doDamageWithWeapon(attacker, target, w, true);
            return true;
        }

        if (attackRoll >= targetDefense) {
            combatSysteme.getLog().add("> [" + attacker.getName() + "] touche " + target.getName() + ".");
            damageSysteme.doDamageWithWeapon(attacker, target, w, false);
            return true;
        } else {
            combatSysteme.getLog().add("> [" + attacker.getName() + "] rate " + target.getName() + ".");
            return false;
        }
    }

    public static void doAttackWithSpell(entity attacker, entity target, spell s) {
        
    }
}

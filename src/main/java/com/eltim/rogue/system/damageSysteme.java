package com.eltim.rogue.system;

import com.eltim.rogue.entity.weakness;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.spell.spell;

public class damageSysteme {
    
    public static void doDamageWithWeapon(entity attacker, entity target, weapon w, boolean isCrit) {
        int damage = 0;
        if (w != null) {
            damage = w.rollWeaponDamage();
            switch (w.getDamageType()) {
                case PHYSICAL:
                    if (w.getWeaponType() == weaponTypeEnum.MELEE) {
                        damage += diceRollSysteme.getModifier(attacker.getForce());
                    }
                    break;
                case MAGICAL:
                    damage += diceRollSysteme.getModifier(attacker.getIntelligence());
                    break;
            }
        } else {
            damage = 1 + diceRollSysteme.getModifier(attacker.getForce());
        }

        if (isCrit) {
            damage *= 2;
        }

        if (damage < 1) damage = 1;
        
        double modifier = 1.0;

        if (w != null && target.getWeaknessList() != null) {
            for (weakness wk : target.getWeaknessList()) {
                if (wk.getType() == w.getDamageType()) {
                    modifier += wk.getDamageMultiplier() - 1.0;
                }
            }
        }

        if (w != null && target.getResistanceList() != null) {
            for (weakness res : target.getResistanceList()) {
                if (res.getType() == w.getDamageType()) {
                    modifier -= res.getDamageMultiplier() - 1.0;
                }
            }
        }

        damage *= (int) modifier;
        if (damage < 1) damage = 1;
        
        target.setLifePoint(target.getLifePoint() - damage);
        
        if (isCrit) {
            combatSysteme.getLog().add("  -> CRITIQUE ! " + damage + " dégâts.");
        } else {
            combatSysteme.getLog().add("  -> " + damage + " dégâts.");
        }
    }

    public static void dodamageWithSpell(entity attacker, entity target, spell spell){
        
    }

}

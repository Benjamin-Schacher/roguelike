package com.eltim.rogue.system;

import com.eltim.rogue.entity.weakness;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.spell.spell;

public class damageSysteme {
    
    public static void doDamageWithWeapon(entity attacker, entity target, weapon w, boolean isCrit) {
        int baseRoll = 0;
        int statMod = 0;
        String diceFormula = "";
        String statName = "";

        if (w != null) {
            baseRoll = w.rollWeaponDamage();
            diceFormula = w.getNbDice() + "d" + w.getDiceFaces();
            switch (w.getDamageType()) {
                case PHYSICAL:
                    if (w.getWeaponType() == weaponTypeEnum.MELEE) {
                        statMod = diceRollSysteme.getModifier(attacker.getForce());
                        statName = "Mod. Force";
                    } else if (w.getWeaponType() == weaponTypeEnum.DISTANCE) {
                        statMod = diceRollSysteme.getModifier(attacker.getAgilite());
                        statName = "Mod. Agilité";
                    }
                    break;
                case MAGICAL:
                    statMod = diceRollSysteme.getModifier(attacker.getIntelligence());
                    statName = "Mod. Int";
                    break;
            }
        } else {
            baseRoll = 1;
            diceFormula = "Mains nues (1)";
            statMod = diceRollSysteme.getModifier(attacker.getForce());
            statName = "Mod. Force";
        }

        int subTotal = baseRoll + statMod;
        if (subTotal < 1) subTotal = 1;

        int finalDamage = subTotal;
        if (isCrit) {
            finalDamage *= 2;
        }

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

        finalDamage = (int) (finalDamage * modifier);
        if (finalDamage < 1) finalDamage = 1;
        
        target.setLifePoint(target.getLifePoint() - finalDamage);

        StringBuilder sb = new StringBuilder();
        sb.append("  ↳ ").append(finalDamage).append(" dégâts");
        if (isCrit) sb.append(" (CRITIQUE !)");
        
        sb.append(" [");
        if (w != null) {
            sb.append("Jet ").append(diceFormula).append(": ").append(baseRoll);
        } else {
            sb.append(diceFormula);
        }
        
        if (!statName.isEmpty()) {
            sb.append(" + ").append(statName).append(": ").append(statMod >= 0 ? "+" + statMod : statMod);
        }

        if (isCrit) {
            sb.append(" = ").append(subTotal).append(" x2 CRIT");
        }

        if (modifier != 1.0) {
            sb.append(" x").append(modifier).append(" Résist/Faiblesse");
        }

        sb.append("]");

        combatSysteme.getLog().add(sb.toString());
    }

    public static void dodamageWithSpell(entity attacker, entity target, spell spell){
        
    }

}

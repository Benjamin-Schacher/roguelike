package com.eltim.rogue.spell;

import com.eltim.rogue.entity.weakness;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.system.diceRollSysteme;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class fireBallSpell extends spell{


    public fireBallSpell() {
        super("Fire Ball", "Throws a ball of fire at the target.", 6, 1, 1, false, false, true);
    }


    public void cast(entity attacker, entity target) {
        int damage = diceRollSysteme.getModifier(attacker.getIntelligence());
        if (damage < 1) damage = 1;
        
        double modifier = 1.0;

        for (weakness wk : target.getWeaknessList()) {
            if (wk.getType() == damageTypeEnum.FIRE) {
                modifier += wk.getDamageMultiplier() - 1.0;
            }
        }

        for (weakness res : target.getResistanceList()) {
            if (res.getType() == damageTypeEnum.FIRE) {
                modifier -= res.getDamageMultiplier() - 1.0;
            }
        }

        damage *= (int) modifier;
        
        target.setLifePoint(target.getLifePoint() - damage);
    }

}

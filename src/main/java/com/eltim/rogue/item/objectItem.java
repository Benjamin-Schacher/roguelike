package com.eltim.rogue.item;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.itemWeightEnum;
import com.eltim.rogue.item.enumerateur.itemKnowledgeEnum;
import com.eltim.rogue.item.enumerateur.attackeTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class objectItem extends item {

    private int damageDiceCount = 0;
    private int damageDiceSides = 0;
    private int healAmount = 0;
    private String buffEffect = "";
    private int durationTurns = 0;
    private boolean affectsParty = false;
    private int tempDamageOverTime = 0;

    // Constructeur dégâts à distance (Dague de lancer, Pierre, Shuriken, Grenade, Fiole de poudre noire, Parchemin de trait de feu)
    public objectItem(String name, int damageDiceCount, int damageDiceSides, damageTypeEnum damageType, attackeTypeEnum attackType, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, itemTypeEnum.OBJECT, itemQualityTypeEnum.COMMON, itemWeightEnum.LEGER, knowledge);
        this.damageDiceCount = damageDiceCount;
        this.damageDiceSides = damageDiceSides;
        this.damageType = damageType;
        this.attackType = attackType;
    }

    public objectItem(String name, int damageDiceCount, int damageDiceSides, itemQualityTypeEnum quality, damageTypeEnum damageType, attackeTypeEnum attackType, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, itemTypeEnum.OBJECT, quality, itemWeightEnum.LEGER, knowledge);
        this.damageDiceCount = damageDiceCount;
        this.damageDiceSides = damageDiceSides;
        this.damageType = damageType;
        this.attackType = attackType;
    }

    // Constructeur effet / buff (Sac de sable, Bandage, Parchemins de résistance, Fumigène, Parchemin de défaiblissement)
    public objectItem(String name, String buffEffect, int durationTurns, itemTypeEnum type, itemQualityTypeEnum quality, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, type, quality, itemWeightEnum.LEGER, knowledge);
        this.buffEffect = buffEffect;
        this.durationTurns = durationTurns;
        this.damageType = null;
    }

    public objectItem(String name, int healAmount, itemTypeEnum type, itemQualityTypeEnum quality, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, type, quality, itemWeightEnum.LEGER, knowledge);
        this.healAmount = healAmount;
        this.damageType = null;
    }

    public objectItem(String name, int tempDamageOverTime, int durationTurns, attackeTypeEnum attackType, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, itemTypeEnum.OBJECT, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LEGER, knowledge);
        this.tempDamageOverTime = tempDamageOverTime;
        this.durationTurns = durationTurns;
        this.damageType = damageTypeEnum.POISON;
        this.attackType = attackType;
    }

    public int getDamageDiceCount() { return damageDiceCount; }
    public int getDamageDiceSides() { return damageDiceSides; }
    public int getHealAmount() { return healAmount; }
    public String getBuffEffect() { return buffEffect; }
    public int getDurationTurns() { return durationTurns; }
    public boolean isAffectsParty() { return affectsParty; }
    public void setAffectsParty(boolean affectsParty) { this.affectsParty = affectsParty; }
    public int getTempDamageOverTime() { return tempDamageOverTime; }

    public int rollDamage() {
        int total = 0;
        for (int i = 0; i < damageDiceCount; i++) {
            total += (int)(Math.random() * damageDiceSides) + 1;
        }
        return total;
    }

    @Override
    public void applyEffect(entity target) {
        if (target == null) return;

        // 1. Soin (ex: Bandage)
        if (healAmount > 0) {
            target.setLifePoint(Math.min(target.getMaxLifePoint(), target.getLifePoint() + healAmount));
            return;
        }

        // 2. Dégâts de poison / sur la durée (ex: Fiole de poison)
        if (tempDamageOverTime > 0) {
            int dmg = tempDamageOverTime * Math.max(1, durationTurns);
            target.setLifePoint(Math.max(0, target.getLifePoint() - dmg));
            return;
        }

        // 3. Buff / Debuff (ex: Sac de sable "attack_-2", Parchemin de défaiblissement "weaken_all")
        if (buffEffect != null && !buffEffect.isEmpty()) {
            if (buffEffect.startsWith("attack_")) {
                try {
                    int val = Integer.parseInt(buffEffect.substring(7).trim());
                    target.setForce(Math.max(1, target.getForce() + val));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (buffEffect.equals("weaken_all")) {
                target.setForce(Math.max(1, target.getForce() - 2));
                target.setAgilite(Math.max(1, target.getAgilite() - 2));
            } else if (buffEffect.startsWith("team_defense_")) {
                target.setConstitution(target.getConstitution() + 1);
            } else if (buffEffect.contains("resistance")) {
                target.setConstitution(target.getConstitution() + 2);
            }
        }
    }
}

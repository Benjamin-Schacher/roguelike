package com.eltim.rogue.item;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.potionTypeEnum;
import com.eltim.rogue.item.enumerateur.itemWeightEnum;
import com.eltim.rogue.item.enumerateur.itemKnowledgeEnum;

public class potion extends item {
    private potionTypeEnum potionType;
    private int diceCount = 0;
    private int diceSides = 0;
    private boolean isMaxEffect = false;

    private String statBuffName = "";
    private int statBuffValue = 0;
    private int durationTurns = 0;
    private boolean resurrection = false;

    public potion(String name, int cost, itemQualityTypeEnum quality, potionTypeEnum potionType) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.potionType = potionType;
    }

    public potion(String name, int diceCount, int diceSides, itemTypeEnum type, itemQualityTypeEnum quality, potionTypeEnum potionType, double cost) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.diceCount = diceCount;
        this.diceSides = diceSides;
        this.potionType = potionType;
    }

    public potion(String name, String statBuffName, int statBuffValue, int durationTurns, itemTypeEnum type, itemQualityTypeEnum quality, potionTypeEnum potionType, double cost) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.statBuffName = statBuffName;
        this.statBuffValue = statBuffValue;
        this.durationTurns = durationTurns;
        this.potionType = potionType;
    }

    public potion(String name, boolean isMaxEffect, itemTypeEnum type, itemQualityTypeEnum quality, potionTypeEnum potionType, double cost) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.isMaxEffect = isMaxEffect;
        this.potionType = potionType;
    }

    public potion(String name, boolean isMaxEffect, boolean resurrection, itemTypeEnum type, itemQualityTypeEnum quality, potionTypeEnum potionType, double cost) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.isMaxEffect = isMaxEffect;
        this.resurrection = resurrection;
        this.potionType = potionType;
    }

    public potionTypeEnum getPotionType() { return potionType; }
    public int getDiceCount() { return diceCount; }
    public int getDiceSides() { return diceSides; }
    public boolean isMaxEffect() { return isMaxEffect; }
    public String getStatBuffName() { return statBuffName; }
    public int getStatBuffValue() { return statBuffValue; }
    public int getDurationTurns() { return durationTurns; }
    public boolean isResurrection() { return resurrection; }

    @Override
    public void applyEffect(entity target) {
        if (target == null) return;
        if (potionType == potionTypeEnum.HEAL) {
            if (isMaxEffect) {
                target.setLifePoint(target.getMaxLifePoint());
            } else if (diceCount > 0 && diceSides > 0) {
                int heal = 0;
                for (int i = 0; i < diceCount; i++) heal += (int)(Math.random() * diceSides) + 1;
                target.setLifePoint(Math.min(target.getMaxLifePoint(), target.getLifePoint() + heal));
            }
        } else if (potionType == potionTypeEnum.MANA) {
            if (isMaxEffect) {
                target.setMana(target.getMaxMana());
            } else if (diceCount > 0 && diceSides > 0) {
                int manaGain = 0;
                for (int i = 0; i < diceCount; i++) manaGain += (int)(Math.random() * diceSides) + 1;
                target.setMana(Math.min(target.getMaxMana(), target.getMana() + manaGain));
            }
        } else if (potionType == potionTypeEnum.BUFF) {
            String bName = "Potion " + (statBuffName != null ? statBuffName : "Force");
            target.addAlteration(new com.eltim.rogue.alteration.alteration(bName, com.eltim.rogue.alteration.alteration.Type.BUFF, 4, statBuffValue));
            if ("force".equalsIgnoreCase(statBuffName)) {
                target.setForce(target.getForce() + statBuffValue);
            } else if ("agilite".equalsIgnoreCase(statBuffName) || "agiliter".equalsIgnoreCase(statBuffName)) {
                target.setAgilite(target.getAgilite() + statBuffValue);
            } else if ("intelligence".equalsIgnoreCase(statBuffName)) {
                target.setIntelligence(target.getIntelligence() + statBuffValue);
            } else if ("constitution".equalsIgnoreCase(statBuffName)) {
                target.setConstitution(target.getConstitution() + statBuffValue);
            } else if ("charisme".equalsIgnoreCase(statBuffName) || "charisma".equalsIgnoreCase(statBuffName)) {
                target.setCharisme(target.getCharisme() + statBuffValue);
            } else if ("sagesse".equalsIgnoreCase(statBuffName)) {
                target.setSagesse(target.getSagesse() + statBuffValue);
            }
        }
    }
}

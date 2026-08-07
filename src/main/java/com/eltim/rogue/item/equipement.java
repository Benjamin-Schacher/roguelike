package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.itemWeightEnum;
import com.eltim.rogue.item.enumerateur.itemKnowledgeEnum;

public class equipement extends item {
    private int defenseBonus;
    private int bonusForce = 0;
    private int bonusIntelligence = 0;
    private int bonusDistanceAttack = 0;
    private int manaRegenPerTurn = 0;
    private int damageReturn = 0;

    public equipement(String name, int cost, itemTypeEnum type, int defenseBonus) {
        super(name, cost, type, itemQualityTypeEnum.COMMON);
        this.defenseBonus = defenseBonus;
    }

    public equipement(String name, int defenseBonus, itemTypeEnum type, itemQualityTypeEnum quality, itemWeightEnum weight, double cost) {
        super(name, cost, type, quality, weight, itemKnowledgeEnum.COMMUN);
        this.defenseBonus = defenseBonus;
    }

    public equipement(String name, int defenseBonus, itemTypeEnum type, itemQualityTypeEnum quality, itemWeightEnum weight, itemKnowledgeEnum knowledge, double cost) {
        super(name, cost, type, quality, weight, knowledge);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() { return defenseBonus; }
    public void setDefenseBonus(int defenseBonus) { this.defenseBonus = defenseBonus; }

    public int getBonusForce() { return bonusForce; }
    public void setBonusForce(int bonusForce) { this.bonusForce = bonusForce; }

    public int getBonusIntelligence() { return bonusIntelligence; }
    public void setBonusIntelligence(int bonusIntelligence) { this.bonusIntelligence = bonusIntelligence; }

    public int getBonusDistanceAttack() { return bonusDistanceAttack; }
    public void setBonusDistanceAttack(int bonusDistanceAttack) { this.bonusDistanceAttack = bonusDistanceAttack; }

    public int getManaRegenPerTurn() { return manaRegenPerTurn; }
    public void setManaRegenPerTurn(int manaRegenPerTurn) { this.manaRegenPerTurn = manaRegenPerTurn; }

    public int getDamageReturn() { return damageReturn; }
    public void setDamageReturn(int damageReturn) { this.damageReturn = damageReturn; }
}

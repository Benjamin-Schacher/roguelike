package com.eltim.rogue.item.base;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.itemWeightEnum;
import com.eltim.rogue.item.enumerateur.itemKnowledgeEnum;
import com.eltim.rogue.item.enumerateur.attackeTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class item {
    protected String name;
    protected double cost;
    protected itemTypeEnum type;
    protected itemQualityTypeEnum quality;
    protected itemWeightEnum weight = itemWeightEnum.LEGER;
    protected itemKnowledgeEnum knowledge = itemKnowledgeEnum.COMMUN;
    protected attackeTypeEnum attackType = attackeTypeEnum.BLUNT;
    protected damageTypeEnum damageType = damageTypeEnum.PHYSICAL;

    public item(String name, double cost, itemTypeEnum type, itemQualityTypeEnum quality) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.quality = quality;
    }

    public item(String name, double cost, itemTypeEnum type, itemQualityTypeEnum quality, itemWeightEnum weight, itemKnowledgeEnum knowledge) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.quality = quality;
        this.weight = weight;
        this.knowledge = knowledge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return (int) Math.round(cost);
    }

    public double getCostDouble() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public itemTypeEnum getType() {
        return type;
    }

    public void setType(itemTypeEnum type) {
        this.type = type;
    }

    public itemQualityTypeEnum getQuality() {
        return quality;
    }

    public void setQuality(itemQualityTypeEnum quality) {
        this.quality = quality;
    }

    public itemWeightEnum getWeight() {
        return weight;
    }

    public void setWeight(itemWeightEnum weight) {
        this.weight = weight;
    }

    public itemKnowledgeEnum getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(itemKnowledgeEnum knowledge) {
        this.knowledge = knowledge;
    }

    public attackeTypeEnum getAttackType() {
        return attackType;
    }

    public void setAttackType(attackeTypeEnum attackType) {
        this.attackType = attackType;
    }

    public damageTypeEnum getDamageType() {
        return damageType;
    }

    public void setDamageType(damageTypeEnum damageType) {
        this.damageType = damageType;
    }

    /**
     * Règle de poids (carac d'équipement) :
     * - lourd : besoin de force supérieure à 16 (force >= 17)
     * - moyen : besoin de force supérieure à 12 (force >= 13)
     * - léger : pas besoin de force
     *
     * Règle de savoir (carac d'équipement) :
     * - mystique : besoin de sagesse 16 (sagesse >= 16 ou 17)
     * - technique : besoin de sagesse 12 (sagesse >= 12 ou 13)
     * - commun : pas besoin de sagesse
     */
    public boolean canBeEquippedBy(entity e) {
        if (e == null) return true;
        
        // Règle de poids
        if (weight == itemWeightEnum.MOYEN && e.getForce() <= 12) {
            return false;
        }
        if (weight == itemWeightEnum.LOURD && e.getForce() <= 16) {
            return false;
        }

        // Règle de savoir
        if (knowledge == itemKnowledgeEnum.TECHNIQUE && e.getSagesse() <= 12) {
            return false;
        }
        if (knowledge == itemKnowledgeEnum.MYSTIQUE && e.getSagesse() <= 16) {
            return false;
        }

        return true;
    }

    protected String soundName;

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    @SuppressWarnings("unchecked")
    public <T extends item> T withSound(String soundName) {
        this.soundName = soundName;
        return (T) this;
    }

    public void applyEffect(entity e) {
        // Effet de base (à redéfinir dans les sous-classes)
    }
}

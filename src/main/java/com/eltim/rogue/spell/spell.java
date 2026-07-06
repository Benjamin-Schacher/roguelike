package com.eltim.rogue.spell;

import com.eltim.rogue.entity.base.entity;

public abstract class spell {

    public String name;
    public int diceDamageSides;
    public int diceDamageCount;
    public String description;
    public int mana;
    public boolean isAoE;
    public boolean isBeneficial;
    public boolean isMaleficial;
    
    public spell(String name, String description, int diceDamageSides, int diceDamageCount, int mana, boolean isAoE, boolean isBeneficial, boolean isMaleficial) {
        this.name = name;
        this.description = description;
        this.diceDamageSides = diceDamageSides;
        this.diceDamageCount = diceDamageCount;
        this.mana = mana;
        this.isAoE = isAoE;
        this.isBeneficial = isBeneficial;
        this.isMaleficial = isMaleficial;
    }

    public abstract void cast(entity attacker, entity target);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    


}

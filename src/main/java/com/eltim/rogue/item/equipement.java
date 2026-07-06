package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;

public class equipement extends item {
    private int defenseBonus;

    public equipement(String name, int cost, itemTypeEnum type, int defenseBonus) {
        super(name, cost, type, itemQualityTypeEnum.COMMON);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() { return defenseBonus; }
    public void setDefenseBonus(int defenseBonus) { this.defenseBonus = defenseBonus; }
}

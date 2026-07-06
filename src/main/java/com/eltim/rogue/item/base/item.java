package com.eltim.rogue.item.base;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;

public class item {
    protected String name;
    protected int cost;
    protected itemTypeEnum type;
    protected itemQualityTypeEnum quality;
    

    public item(String name, int cost, itemTypeEnum type, itemQualityTypeEnum quality) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public itemTypeEnum getType() {
        return type;
    }

    public void applyEffect(entity e) {
        // Effet de base (à redéfinir dans les sous-classes)
    }
}

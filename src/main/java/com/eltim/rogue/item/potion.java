package com.eltim.rogue.item;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.potionTypeEnum;

public class potion extends item {
    private potionTypeEnum type;

    public potion(String name, int cost, itemQualityTypeEnum quality, potionTypeEnum type) {
        super(name, cost, itemTypeEnum.POTION, quality);
        this.type = type;
    }

    public void applyEffect(entity target, potionTypeEnum type) {

    }
}

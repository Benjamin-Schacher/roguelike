package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;

public class key extends item {
    int keyCode;
    public key(String name, int keyCode) {
        super(name, 0, itemTypeEnum.KEY, itemQualityTypeEnum.COMMON);
        this.keyCode = keyCode;
    }
    
    public int getKeyCode() {
        return keyCode;
    }
}

package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class weapon extends item {

    private int damageDiceCount; 
    private int damageDiceSides; 
    private itemTypeEnum type;
    private weaponTypeEnum weaponType;
    private boolean twoHanded;   
    private boolean forbidsSecondary;
    private damageTypeEnum damageType;

    public weapon(String name, int damageDiceCount, int damageDiceSides, itemTypeEnum type , itemQualityTypeEnum quality, weaponTypeEnum weaponType, boolean twoHanded, damageTypeEnum damageType) {
        this(name, damageDiceCount, damageDiceSides, type, quality, weaponType, twoHanded, false, damageTypeEnum.PHYSICAL);
    }

    public weapon(String name, int damageDiceCount, int damageDiceSides, itemTypeEnum type, itemQualityTypeEnum quality, weaponTypeEnum weaponType, boolean twoHanded, boolean forbidsSecondary, damageTypeEnum damageType) {
        super(name, 1, type, quality);
        this.damageDiceCount = damageDiceCount;
        this.damageDiceSides = damageDiceSides;
        this.weaponType = weaponType;
        this.type = type;
        this.twoHanded = twoHanded;
        this.forbidsSecondary = forbidsSecondary;
        this.damageType = damageType;
    }

    public int rollWeaponDamage() {
        int totalDamage = 0;
        for (int i = 0; i < damageDiceCount; i++) {
            totalDamage += (int) (Math.random() * damageDiceSides) + 1;
        }
        return totalDamage;
    }

    public String getWeaponDamageString() {
        return damageDiceCount + "d" + damageDiceSides;
    }

    public int getDamageDiceCount() { return damageDiceCount; }
    public void setDamageDiceCount(int damageDiceCount) { this.damageDiceCount = damageDiceCount; }

    public int getDamageDiceSides() { return damageDiceSides; }
    public void setDamageDiceSides(int damageDiceSides) { this.damageDiceSides = damageDiceSides; }

    public weaponTypeEnum getWeaponType() { return weaponType; }
    public void setWeaponType(weaponTypeEnum type) { this.weaponType = type; }

    public boolean isTwoHanded() { return twoHanded; }
    public void setTwoHanded(boolean twoHanded) { this.twoHanded = twoHanded; }

    public boolean isForbidsSecondary() { return forbidsSecondary; }
    public void setForbidsSecondary(boolean forbidsSecondary) { this.forbidsSecondary = forbidsSecondary; }

    public itemTypeEnum getWeaponItemType() { return type; }
    public void setWeaponItemType(itemTypeEnum type) { this.type = type; }

    public void setDamageType(damageTypeEnum damageType) { this.damageType = damageType; }
    public damageTypeEnum getDamageType() { return damageType; }
}

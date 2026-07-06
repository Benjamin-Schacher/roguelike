package com.eltim.rogue.item.itemImplementation;

import com.eltim.rogue.item.potion;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.potionTypeEnum;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class equipementImplementation {

    // basic weapon
    public weapon epee = new weapon("Épée a deux main", 1, 8, itemTypeEnum.SWORD, itemQualityTypeEnum.COMMON , weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon epeeCourte = new weapon("Épée courte", 1, 6, itemTypeEnum.SWORD, itemQualityTypeEnum.COMMON , weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL);
    public weapon arc = new weapon("Arc", 1, 6, itemTypeEnum.BOW, itemQualityTypeEnum.COMMON, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL);
    public weapon dague = new weapon("Dague", 1, 4, itemTypeEnum.DAGGER, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL);
    public weapon bouclier = new weapon("Bouclier", 0, 0, itemTypeEnum.SHIELD, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL);
    public weapon baton = new weapon("Baton", 1, 6, itemTypeEnum.STAFF, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon batonFerer = new weapon("Baton férer", 1, 6, itemTypeEnum.STAFF, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon grimoire = new weapon("Grimoire", 1, 4, itemTypeEnum.BOOK, itemQualityTypeEnum.RARE, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL);
    public weapon marteau = new weapon("Marteau a deux main", 1, 8, itemTypeEnum.HAMMER, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon hache = new weapon("Hache a deux main", 1, 8, itemTypeEnum.AXE, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon arbalete = new weapon("Arbalète", 1, 10, itemTypeEnum.CROSSBOW, itemQualityTypeEnum.RARE, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL);
    public weapon halebarde = new weapon("Halebarde", 1, 10, itemTypeEnum.POLEARM, itemQualityTypeEnum.RARE, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL);
    public weapon arcLong = new weapon("Arc long", 1, 8, itemTypeEnum.BOW, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL);

    // potion


    // armor




}

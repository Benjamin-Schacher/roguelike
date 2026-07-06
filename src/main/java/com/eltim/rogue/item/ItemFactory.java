package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.item.enumerateur.potionTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;
import java.util.Random;

public class ItemFactory {
    private static final Random random = new Random();

    public static item generateRandomItem(itemQualityTypeEnum quality) {
        int typeRand = random.nextInt(2); // 0 = Potion, 1 = Arme

        if (typeRand == 0) {
            return generateRandomPotion(quality);
        } else {
            return generateRandomWeapon(quality);
        }
    }

    private static potion generateRandomPotion(itemQualityTypeEnum quality) {
        potionTypeEnum[] types = potionTypeEnum.values();
        potionTypeEnum selectedType = types[random.nextInt(types.length)];
        
        String name;
        int cost;
        switch (quality) {
            case UNCOMMON: cost = 25; name = "Potion supérieure de " + getTranslatedName(selectedType); break;
            case RARE: cost = 50; name = "Potion rare de " + getTranslatedName(selectedType); break;
            case EPIC: cost = 100; name = "Élixir de " + getTranslatedName(selectedType); break;
            case LEGENDARY: cost = 250; name = "Larme divine de " + getTranslatedName(selectedType); break;
            case COMMON:
            default:
                cost = 10; name = "Potion de " + getTranslatedName(selectedType); break;
        }
        return new potion(name, cost, quality, selectedType);
    }

    private static weapon generateRandomWeapon(itemQualityTypeEnum quality) {
        int diceCount = 1;
        int diceSides = 4;
        String name = "Arme";

        switch (quality) {
            case UNCOMMON: diceCount = 1; diceSides = 6; name = "Arme peu commune"; break;
            case RARE: diceCount = 2; diceSides = 6; name = "Arme rare"; break;
            case EPIC: diceCount = 2; diceSides = 8; name = "Arme épique"; break;
            case LEGENDARY: diceCount = 3; diceSides = 8; name = "Arme légendaire"; break;
            case COMMON:
            default:
                diceCount = 1; diceSides = 4; name = "Arme commune"; break;
        }

        weaponTypeEnum[] wTypes = weaponTypeEnum.values();
        weaponTypeEnum selectedType = wTypes[random.nextInt(wTypes.length)];
        
        itemTypeEnum subType = itemTypeEnum.SWORD;
        damageTypeEnum dmgType = damageTypeEnum.PHYSICAL;

        if (selectedType == weaponTypeEnum.MELEE) {
            name = name.replace("Arme", "Épée");
            subType = itemTypeEnum.SWORD;
            dmgType = damageTypeEnum.PHYSICAL;
        } else if (selectedType == weaponTypeEnum.DISTANCE) {
            name = name.replace("Arme", "Arc");
            subType = itemTypeEnum.BOW;
            dmgType = damageTypeEnum.PHYSICAL;
        } else if (selectedType == weaponTypeEnum.MAGIC) {
            name = name.replace("Arme", "Bâton");
            subType = itemTypeEnum.STAFF;
            dmgType = damageTypeEnum.MAGICAL;
        }

        return new weapon(name, diceCount, diceSides, subType, quality, selectedType, false, dmgType);
    }

    private static String getTranslatedName(potionTypeEnum pType) {
        switch(pType) {
            case HEAL: return "Soins";
            case MANA: return "Mana";
            case STRENGTH: return "Force";
            case DEFENSE: return "Défense";
            case SPEED: return "Vitesse";
            case AGILITY: return "Agilité";
            default: return "Mystère";
        }
    }
}

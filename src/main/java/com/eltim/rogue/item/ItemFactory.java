package com.eltim.rogue.item;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.itemImplementation.equipementImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemFactory {
    private static final Random random = new Random();
    private static final equipementImplementation equipImpl = new equipementImplementation();

    public static item generateRandomItem(itemQualityTypeEnum quality) {
        List<item> candidates = getItemsByQuality(quality);
        if (candidates.isEmpty()) {
            candidates = equipImpl.getAllItems();
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    public static weapon generateRandomWeapon(itemQualityTypeEnum quality) {
        List<item> candidates = new ArrayList<>();
        for (item i : getItemsByQuality(quality)) {
            if (i instanceof weapon) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            for (item i : equipImpl.getAllItems()) {
                if (i instanceof weapon) {
                    candidates.add(i);
                }
            }
        }
        if (candidates.isEmpty()) return null;
        return (weapon) candidates.get(random.nextInt(candidates.size()));
    }

    public static potion generateRandomPotion(itemQualityTypeEnum quality) {
        List<item> candidates = new ArrayList<>();
        for (item i : getItemsByQuality(quality)) {
            if (i instanceof potion) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            for (item i : equipImpl.getAllItems()) {
                if (i instanceof potion) {
                    candidates.add(i);
                }
            }
        }
        if (candidates.isEmpty()) return null;
        return (potion) candidates.get(random.nextInt(candidates.size()));
    }

    private static List<item> getItemsByQuality(itemQualityTypeEnum quality) {
        List<item> matching = new ArrayList<>();
        itemQualityTypeEnum targetQuality = (quality != null) ? quality : itemQualityTypeEnum.COMMON;

        for (item i : equipImpl.getAllItems()) {
            if (i.getQuality() == targetQuality) {
                matching.add(i);
            }
        }

        // Si aucun objet de la rareté exacte n'est trouvé, se rabattre sur les objets disponibles
        if (matching.isEmpty()) {
            for (item i : equipImpl.getAllItems()) {
                if (i.getQuality() == itemQualityTypeEnum.RARE || i.getQuality() == itemQualityTypeEnum.UNCOMMON || i.getQuality() == itemQualityTypeEnum.COMMON) {
                    matching.add(i);
                }
            }
        }

        return matching;
    }
}

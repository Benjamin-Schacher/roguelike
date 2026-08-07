package com.eltim.rogue.entity.environment;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.chestTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.ItemFactory;

import java.util.ArrayList;
import java.util.List;

public class chest extends entity {
    private List<item> loot;
    private boolean isOpen;
    private boolean randomLoot;
    private chestTypeEnum quality;
    private boolean hasGeneratedLoot;

    public chest(int x, int y, boolean randomLoot, chestTypeEnum quality) {
        super(x, y, 'L');
        this.loot = new ArrayList<>();
        this.randomLoot = randomLoot;
        this.quality = quality;
        this.isOpen = false;
        this.hasGeneratedLoot = false;

        switch (quality) {
            case UNCOMMON: this.setName("Coffre Peu Commun"); break;
            case RARE: this.setName("Coffre Rare"); break;
            case EPIC: this.setName("Coffre Épique"); break;
            case LEGENDARY: this.setName("Coffre Légendaire"); break;
            case TRAPED: this.setName("Coffre Piégé"); break;
            case CREST: this.setName("Reliquaire Sacré"); break;
            case ARMORYCHEST: this.setName("Coffre d'Armurerie"); break;
            case ALCHEMIST: this.setName("Coffre d'Alchimiste"); break;
            case MERCHANT: this.setName("Coffre de Marchand"); break;
            case SPECIAL: this.setName("Coffre Spécial"); break;
            case COMMON:
            default: this.setName("Coffre Commun"); break;
        }
    }

    /** Constructeur pour coffre spécial avec loot fixe/précis */
    public chest(int x, int y, String customName, List<item> fixedLoot) {
        super(x, y, 'L');
        this.setName(customName != null ? customName : "Coffre Trésor");
        this.loot = fixedLoot != null ? new ArrayList<>(fixedLoot) : new ArrayList<>();
        this.randomLoot = false;
        this.quality = chestTypeEnum.SPECIAL;
        this.isOpen = false;
        this.hasGeneratedLoot = true;
    }

    public void addLoot(item i) {
        this.loot.add(i);
    }

    public chestTypeEnum getQuality() {
        return quality;
    }

    public boolean isTrapped() {
        return quality == chestTypeEnum.TRAPED;
    }

    public List<item> getLoot() {
        if (randomLoot && !hasGeneratedLoot) {
            hasGeneratedLoot = true;
            int nbItem = (int) (Math.random() * 3) + 1; // 1 à 3 objets

            switch (quality) {
                case COMMON:
                case UNCOMMON:
                case RARE:
                case EPIC:
                case LEGENDARY:
                    itemQualityTypeEnum matchQuality = itemQualityTypeEnum.valueOf(quality.name());
                    for (int i = 0; i < nbItem; i++) {
                        loot.add(ItemFactory.generateRandomItem(matchQuality));
                    }
                    break;
                case ARMORYCHEST:
                    for (int i = 0; i < nbItem; i++) {
                        itemQualityTypeEnum q = (Math.random() < 0.3) ? itemQualityTypeEnum.RARE : itemQualityTypeEnum.UNCOMMON;
                        loot.add(ItemFactory.generateRandomWeapon(q));
                    }
                    break;
                case ALCHEMIST:
                    int nbPotions = (int) (Math.random() * 2) + 2; // 2 à 3 potions
                    for (int i = 0; i < nbPotions; i++) {
                        itemQualityTypeEnum q = (Math.random() < 0.4) ? itemQualityTypeEnum.RARE : itemQualityTypeEnum.UNCOMMON;
                        loot.add(ItemFactory.generateRandomPotion(q));
                    }
                    break;
                case CREST:
                    loot.add(ItemFactory.generateRandomItem(itemQualityTypeEnum.RARE));
                    if (Math.random() < 0.5) {
                        loot.add(ItemFactory.generateRandomItem(itemQualityTypeEnum.EPIC));
                    }
                    break;
                case MERCHANT:
                    int nbLoot = (int) (Math.random() * 2) + 2;
                    for (int i = 0; i < nbLoot; i++) {
                        loot.add(ItemFactory.generateRandomItem(itemQualityTypeEnum.RARE));
                    }
                    break;
                case TRAPED:
                    loot.add(ItemFactory.generateRandomItem(itemQualityTypeEnum.COMMON));
                    break;
                default:
                    for (int i = 0; i < nbItem; i++) {
                        loot.add(ItemFactory.generateRandomItem(itemQualityTypeEnum.COMMON));
                    }
                    break;
            }
        }
        return loot;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }
}


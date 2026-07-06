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
        this.setName("Coffre de bois");
        this.loot = new ArrayList<>();
        this.randomLoot = randomLoot;
        this.quality = quality;
        this.isOpen = false;
        this.hasGeneratedLoot = false;
    }

    public void addLoot(item i) {
        this.loot.add(i);
    }

    public List<item> getLoot() {
        if (randomLoot && !hasGeneratedLoot) {
            hasGeneratedLoot = true;
            itemQualityTypeEnum itemQuality = itemQualityTypeEnum.COMMON;
            
            switch (quality) {
                case COMMON: itemQuality = itemQualityTypeEnum.COMMON; break;
                case UNCOMMON: itemQuality = itemQualityTypeEnum.UNCOMMON; break;
                case RARE: itemQuality = itemQualityTypeEnum.RARE; break;
                case EPIC: itemQuality = itemQualityTypeEnum.EPIC; break;
                case LEGENDARY: itemQuality = itemQualityTypeEnum.LEGENDARY; break;
                case TRAPED: itemQuality = itemQualityTypeEnum.COMMON; break;
                case CREST: itemQuality = itemQualityTypeEnum.COMMON; break;
                case ARMORYCHEST: itemQuality = itemQualityTypeEnum.UNCOMMON; break;
                case MERCHANT: itemQuality = itemQualityTypeEnum.RARE; break;
            }

            int nbItem = (int) (Math.random() * 3) + 1;
            for (int i = 0; i < nbItem; i++) {
                loot.add(ItemFactory.generateRandomItem(itemQuality));
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

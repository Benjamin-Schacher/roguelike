package com.eltim.rogue.item.enumerateur;

public enum potionEffectEnum {
    HEAL_SMALL(4, 6, 10, 1);

    private final int diceCount;
    private final int diceSides;
    private final int cost;
    private final int tier;

    potionEffectEnum(int diceCount, int diceSides, int cost, int tier) {
        this.diceCount = diceCount;
        this.diceSides = diceSides;
        this.cost = cost;
        this.tier = tier;
    }



    
}

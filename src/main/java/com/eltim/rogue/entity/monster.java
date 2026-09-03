package com.eltim.rogue.entity;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.summon.Iaction;
import com.eltim.rogue.item.base.item;

import java.util.ArrayList;
import java.util.List;

public class monster extends entity {

    private Iaction action;
    private boolean isSummoned;
    private int turnsRemaining;

    private int xpReward = 10; 
    private List<item> lootTable = new ArrayList<>(); 

    public monster(int x, int y, char symbol) {
        super(x, y, symbol);
        this.name = "Monstre";
        this.xpReward = 10;
        this.soundName = "rpg-battle-system/5";
    }

    public monster(int x, int y, char symbol, Iaction action) {
        super(x, y, symbol);
        this.action = action;
        this.soundName = "rpg-battle-system/5";
    }

    public monster(int x, int y, char symbol, String name, int xpReward, Iaction action) {
        super(x, y, symbol);
        this.name = name;
        this.xpReward = xpReward;
        this.action = action;
        this.soundName = "rpg-battle-system/5";
    }

    private int minGold = 0;
    private int maxGold = 0;

    public item rollLoot() {
        if (lootTable.isEmpty()) return null;
        int index = (int) (Math.random() * lootTable.size());
        return lootTable.get(index);
    }

    public List<item> rollLoots() {
        List<item> dropped = new ArrayList<>();
        if (lootTable.isEmpty()) return dropped;

        int count = 1 + (int)(Math.random() * 3);
        for (int i = 0; i < count; i++) {
            int index = (int) (Math.random() * lootTable.size());
            dropped.add(lootTable.get(index));
        }
        return dropped;
    }

    public int rollGold() {
        if (maxGold <= 0) return 0;
        if (maxGold <= minGold) return minGold;
        return minGold + (int)(Math.random() * (maxGold - minGold + 1));
    }

    // --- Getters & Setters ---
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public int getMinGold() { return minGold; }
    public void setMinGold(int minGold) { this.minGold = minGold; }

    public int getMaxGold() { return maxGold; }
    public void setMaxGold(int maxGold) { this.maxGold = maxGold; }

    public void setGoldReward(int min, int max) {
        this.minGold = min;
        this.maxGold = max;
    }

    public List<item> getLootTable() { return lootTable; }
    public void setLootTable(List<item> lootTable) { this.lootTable = lootTable; }

    public void addLoot(item i) { this.lootTable.add(i); }
}

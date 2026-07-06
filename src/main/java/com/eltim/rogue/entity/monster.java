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
    }

    public monster(int x, int y, char symbol, Iaction action) {
        super(x, y, symbol);
        this.action = action;
    }

    public monster(int x, int y, char symbol, String name, int xpReward, Iaction action) {
        super(x, y, symbol);
        this.name = name;
        this.xpReward = xpReward;
        this.action = action;
    }

    public item rollLoot() {
        if (lootTable.isEmpty()) return null;
        int index = (int) (Math.random() * lootTable.size());
        return lootTable.get(index);
    }

    // --- Getters & Setters ---
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public List<item> getLootTable() { return lootTable; }
    public void setLootTable(List<item> lootTable) { this.lootTable = lootTable; }

    public void addLoot(item i) { this.lootTable.add(i); }
}

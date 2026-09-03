package com.eltim.rogue.entity;

import com.eltim.rogue.entity.base.entity;

public class npc extends entity {
    public npc(int x, int y, char symbol) {
        super(x, y, symbol);
        this.soundName = "medieval-fantasy/5";
    }
}

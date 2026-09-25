package com.eltim.rogue.entity;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.classe.classe;
import com.eltim.rogue.entity.classe.warriorClasse;

public class npc extends entity {

    public classe classe;

    public npc(int x, int y, char symbol) {
        super(x, y, symbol);
        this.soundName = "medieval-fantasy/5";
        this.classe = new warriorClasse();
    }
}

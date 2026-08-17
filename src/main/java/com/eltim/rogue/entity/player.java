package com.eltim.rogue.entity;

import java.util.ArrayList;
import java.util.List;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.classe.classe;
import com.eltim.rogue.entity.summon.summon;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.equipement;
import com.eltim.rogue.item.base.item;


public class player extends entity {

    public classe classe;

    private List<item> inventory = new ArrayList<>();
    public List<item> getInventory() { return inventory; }
    public void setInventory(List<item> inventory) { this.inventory = inventory; }

    private List<npc> party = new ArrayList<>();
    private List<summon> summons = new ArrayList<>();

    public player(int x, int y) {
        super(x, y, '@');
        this.name = "Le Héros";
    }


    public void addLoot(item i) {
        this.inventory.add(i);
    }

    public int getPartyNumber() {
        return party.size();
    }
    public int getSummonsNumber() {
        return summons.size();
    }
    public List<npc> getParty() {
        return party;
    }
    public List<summon> getSummons() {
        return summons;
    }
    public void addPartyMember(npc n){
        if(party.size() < 2){
            party.add(n);
        }
    }
    public void removePartyMember(npc n) {
        party.remove(n);
    }
    public void addSummon(summon s){
        summons.add(s); // Invocations illimitées
    }
    public void removeSummon(summon s) {
        summons.remove(s);
    }

    public boolean isAllies(entity e) {
        if(e instanceof player) return true;
        if(party.contains(e)) return true;
        if(summons.contains(e)) return true;
        return false;
    }   

    public void useItem(item it){
        it.applyEffect(this);
        inventory.remove(it);
    }
    
    public void chooseClass(classe c){
        this.classe = c;
        if (this.classe != null && this.classe.skillPoints < 2) {
            this.classe.skillPoints = 2;
        }
    }
}

package com.eltim.rogue.entity;

import java.util.ArrayList;
import java.util.List;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.classe.classe;
import com.eltim.rogue.entity.summon.summon;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.system.ExplorationLog;

public class player extends entity {

    public classe classe;
    private boolean pendingSubclassChoice = false;

    private List<item> inventory = new ArrayList<>();
    public List<item> getInventory() { return inventory; }
    public void setInventory(List<item> inventory) { this.inventory = inventory; }

    private List<npc> party = new ArrayList<>();
    private List<summon> summons = new ArrayList<>();

    public player(int x, int y) {
        super(x, y, '@');
        this.name = "Le Héros";
        this.soundName = "medieval-fantasy/9";
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

    public boolean isPendingSubclassChoice() {
        return pendingSubclassChoice;
    }

    public void setPendingSubclassChoice(boolean pending) {
        this.pendingSubclassChoice = pending;
    }

    @Override
    public void addXp(int amount) {
        this.xp += amount;
        checkLevelUp();
    }

    public void checkLevelUp() {
        int xpForNext = this.level * 100;
        while (this.xp >= xpForNext) {
            this.xp -= xpForNext;
            this.level++;
            this.maxLifePoint += 5;
            this.lifePoint = this.maxLifePoint;
            this.maxMana += 3;
            this.mana = this.maxMana;

            if (this.classe != null) {
                this.classe.gainLevelPoints();
            }

            if (this.level >= 3 && this.classe != null && !this.classe.hasSubclass() && !this.classe.hasRefusedSubclass) {
                this.pendingSubclassChoice = true;
            }

            ExplorationLog.addLog("LEVEL UP ! Vous atteignez le niveau " + this.level + " !");
            xpForNext = this.level * 100;
        }
    }
}

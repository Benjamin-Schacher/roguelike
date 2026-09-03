package com.eltim.rogue.entity.base;

import com.eltim.rogue.alteration.alteration;
import com.eltim.rogue.entity.weakness;

import java.util.List;

public abstract class entity {
    protected int x;
    protected int y;
    protected char symbol;
    protected int lifePoint = 10;
    protected int mana = 5;
    protected List<alteration> alterationList;
    protected List<weakness> weaknessList;
    protected List<weakness> resistanceList;
    protected String name;
    protected int maxLifePoint = 10;
    protected int maxMana = 5;
    protected int xp = 0;
    protected int level = 1;
    protected int gold = 0;
    protected int physicalDefence = 10;
    protected int magicalDefence = 10;
    
    public enum MagicStat {
        INT, 
        SAG, 
        CHA  
    }
    protected MagicStat magicDetermination = MagicStat.INT;
    
    protected int force = 10;
    protected int agilite = 10;
    protected int intelligence = 10;
    protected int charisme = 10;
    protected int constitution = 10;
    protected int sagesse = 10;

    protected Gender gender = Gender.MASCULIN;
    protected Belief belief = Belief.SANS_RELIGION;
    protected Race race = Race.HUMAIN;

    public com.eltim.rogue.item.weapon rightHand;
    public com.eltim.rogue.item.weapon leftHand;
    public com.eltim.rogue.item.weapon secondaryWeapon;
    public com.eltim.rogue.item.equipement armor;
    public com.eltim.rogue.item.equipement helmet;
    public com.eltim.rogue.item.equipement leggings;
    public com.eltim.rogue.item.equipement shoes;
    public com.eltim.rogue.item.equipement gloves;
    public com.eltim.rogue.item.equipement necklace;
    public com.eltim.rogue.item.equipement ring1;
    public com.eltim.rogue.item.equipement ring2;


    public entity(int x, int y, char symbol) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }

    public entity(int maxMana, int maxLifePoint, String name, int y, int x) {
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.maxLifePoint = maxLifePoint;
        this.lifePoint = maxLifePoint;
        this.name = name;
        this.y = y;
        this.x = x;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public char getSymbol() { return symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getLifePoint() { return lifePoint; }
    public void setLifePoint(int hp) { this.lifePoint = hp; }

    public int getMaxLifePoint() { return maxLifePoint; }

    public int getMana() { return mana; }
    public void setMana(int mp) { this.mana = mp; }

    public int getMaxMana() { return maxMana; }
    
    // --- Stats RPG ---
    public int getForce() { return force; }
    public void setForce(int val) { this.force = val; }
    
    public int getAgilite() { return agilite; }
    public void setAgilite(int val) { this.agilite = val; }
    
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int val) { this.intelligence = val; }
    
    public int getCharisme() { return charisme; }
    public void setCharisme(int val) { this.charisme = val; }
    
    public int getConstitution() { return constitution; }
    public void setConstitution(int val) { this.constitution = val; }
    
    public int getSagesse() { return sagesse; }
    public void setSagesse(int val) { this.sagesse = val; }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public List<alteration> getAlterationList() {
        if (alterationList == null) alterationList = new java.util.ArrayList<>();
        return alterationList;
    }

    public void setAlterationList(List<alteration> alterationList) {
        this.alterationList = alterationList;
    }

    public void addAlteration(alteration a) {
        if (a == null) return;
        if (alterationList == null) alterationList = new java.util.ArrayList<>();
        for (alteration existing : alterationList) {
            if (existing.getName().equalsIgnoreCase(a.getName())) {
                existing.setDuration(Math.max(existing.getDuration(), a.getDuration()));
                return;
            }
        }
        alterationList.add(a);
    }

    public void removeAlteration(alteration a) {
        if (alterationList != null) alterationList.remove(a);
    }

    public void clearAlterations() {
        if (alterationList != null) alterationList.clear();
    }

    public List<alteration> getBuffs() {
        List<alteration> buffs = new java.util.ArrayList<>();
        for (alteration a : getAlterationList()) {
            if (a.getType() == alteration.Type.BUFF) buffs.add(a);
        }
        return buffs;
    }

    public List<alteration> getDebuffs() {
        List<alteration> debuffs = new java.util.ArrayList<>();
        for (alteration a : getAlterationList()) {
            if (a.getType() == alteration.Type.MALUS) debuffs.add(a);
        }
        return debuffs;
    }

    public void setMaxLifePoint(int maxLifePoint) {
        this.maxLifePoint = maxLifePoint;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public boolean isDead() {
        return lifePoint <= 0;
    }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public void addXp(int amount) {
        this.xp += amount;
    }

    public MagicStat getMagicDetermination() {
        return magicDetermination;
    }

    public void setMagicDetermination(MagicStat magicDetermination) {
        this.magicDetermination = magicDetermination;
    }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public Belief getBelief() { return belief; }
    public void setBelief(Belief belief) { this.belief = belief; }

    public Race getRace() { return race; }
    public void setRace(Race race) { this.race = race; }

    public List<weakness> getWeaknessList() {
        return weaknessList;
    }

    public void setWeaknessList(List<weakness> weaknessList) {
        this.weaknessList = weaknessList;
    }

    public int getPhysicalDefence() { return physicalDefence; }
    public void setPhysicalDefence(int physicalDefence) { this.physicalDefence = physicalDefence; }

    public int getMagicalDefence() { return magicalDefence; }
    public void setMagicalDefence(int magicalDefence) { this.magicalDefence = magicalDefence; }

    public List<weakness> getResistanceList() {
        return resistanceList;
    }

    public void setResistanceList(List<weakness> resistanceList) {
        this.resistanceList = resistanceList;
    }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public void addGold(int amount) { this.gold += amount; }

    public void addWeakness(weakness w) {
        if (this.weaknessList != null) {
            this.weaknessList.add(w);
        }
    }

    public void addResistance(weakness res) {
        if (this.resistanceList != null) {
            this.resistanceList.add(res);
        }
    }

    protected long stunnedUntilTime = 0;
    protected String soundName;

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    @SuppressWarnings("unchecked")
    public <T extends entity> T withSound(String soundName) {
        this.soundName = soundName;
        return (T) this;
    }

    public void stunForMillis(long millis) {
        this.stunnedUntilTime = System.currentTimeMillis() + millis;
    }

    public boolean isStunned() {
        return System.currentTimeMillis() < stunnedUntilTime;
    }
}

package com.eltim.rogue.entity.classe;

/**
 * Représente une compétence dans un arbre de talents.
 */
public class Skill {

    public final String id;
    public final String name;
    public final String description;
    public final int tier; // 1 à 5
    public final String tree; // Nom de l'arbre parent

    public boolean unlocked = false;
    public String soundName;

    public Skill(String id, String name, String description, int tier, String tree) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.tree = tree;
    }

    public Skill withSound(String soundName) {
        this.soundName = soundName;
        return this;
    }
}

package com.eltim.rogue.entity.classe;

import java.util.List;

public class Subclass {
    public final String name;
    public final String class1;
    public final String class2;
    public final String description;
    public final List<SkillTree> trees; // Toujours 2 arbres exclusifs

    public Subclass(String name, String class1, String class2, String description, List<SkillTree> trees) {
        this.name = name;
        this.class1 = class1;
        this.class2 = class2;
        this.description = description;
        this.trees = trees;
    }

    public boolean isCompatibleWith(String baseClassName) {
        if (baseClassName == null) return false;
        return class1.equalsIgnoreCase(baseClassName) || class2.equalsIgnoreCase(baseClassName);
    }
}

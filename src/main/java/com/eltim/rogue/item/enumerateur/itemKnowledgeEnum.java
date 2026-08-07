package com.eltim.rogue.item.enumerateur;

public enum itemKnowledgeEnum {
    COMMUN("Commun", 0),
    TECHNIQUE("Technique", 13), // Sagesse > 12
    MYSTIQUE("Mystique", 17);   // Sagesse > 16

    private final String displayName;
    private final int minSagesseRequired;

    itemKnowledgeEnum(String displayName, int minSagesseRequired) {
        this.displayName = displayName;
        this.minSagesseRequired = minSagesseRequired;
    }

    public String getDisplayName() { return displayName; }
    public int getMinSagesseRequired() { return minSagesseRequired; }
}

package com.eltim.rogue.entity.base;

public enum Gender {
    MASCULIN("Masculin"),
    FEMININ("Féminin");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

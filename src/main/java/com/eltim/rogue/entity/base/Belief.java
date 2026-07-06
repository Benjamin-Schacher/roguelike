package com.eltim.rogue.entity.base;

public enum Belief {
    GABEL("Gabel"),
    KARIN("Karin"),
    MAHRIE("Mâhrie"),
    SANS_RELIGION("Sans religion");

    private final String displayName;

    Belief(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

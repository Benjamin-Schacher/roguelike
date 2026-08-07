package com.eltim.rogue.item.enumerateur;

public enum itemWeightEnum {
    LEGER("Léger", 0),
    MOYEN("Moyen", 13), // Force > 12
    LOURD("Lourd", 17);  // Force > 16

    private final String displayName;
    private final int minForceRequired;

    itemWeightEnum(String displayName, int minForceRequired) {
        this.displayName = displayName;
        this.minForceRequired = minForceRequired;
    }

    public String getDisplayName() { return displayName; }
    public int getMinForceRequired() { return minForceRequired; }
}

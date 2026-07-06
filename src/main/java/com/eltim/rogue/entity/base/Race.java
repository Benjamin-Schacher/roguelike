package com.eltim.rogue.entity.base;

public enum Race {
    HUMAIN("Humain", 1, 1, 1, 1, 1, 1, "Polyvalence"),
    ELFE("Elfe", -1, 2, 2, 0, -1, 1, "Vision nocturne"),
    NAIN("Nain", 2, -1, 0, -1, 2, 1, "Résistance au poison"),
    ORC("Orc", 3, 0, -2, -2, 2, -1, "Rage sanguinaire");

    private final String displayName;
    private final int bonusForce;
    private final int bonusAgilite;
    private final int bonusIntelligence;
    private final int bonusCharisme;
    private final int bonusConstitution;
    private final int bonusSagesse;
    private final String specialSkillPlaceholder;

    Race(String displayName, int force, int agilite, int intel, int charisme, int cons, int sag, String skill) {
        this.displayName = displayName;
        this.bonusForce = force;
        this.bonusAgilite = agilite;
        this.bonusIntelligence = intel;
        this.bonusCharisme = charisme;
        this.bonusConstitution = cons;
        this.bonusSagesse = sag;
        this.specialSkillPlaceholder = skill;
    }

    public String getDisplayName() { return displayName; }
    public int getBonusForce() { return bonusForce; }
    public int getBonusAgilite() { return bonusAgilite; }
    public int getBonusIntelligence() { return bonusIntelligence; }
    public int getBonusCharisme() { return bonusCharisme; }
    public int getBonusConstitution() { return bonusConstitution; }
    public int getBonusSagesse() { return bonusSagesse; }
    public String getSpecialSkillPlaceholder() { return specialSkillPlaceholder; }
}

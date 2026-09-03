package com.eltim.rogue.alteration;

public class alteration {

    public enum Type {
        BUFF("Bonus"),
        MALUS("Malus");

        private final String label;
        Type(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private String name;
    private Type type;
    private int duration; // Nombre de tours restants
    private int value;    // Valeur de l'effet (+2 stat, 3 dégâts par tour, etc.)
    private String description;

    public alteration(String name, Type type, int duration) {
        this(name, type, duration, 0, "");
    }

    public alteration(String name, Type type, int duration, int value) {
        this(name, type, duration, value, "");
    }

    public alteration(String name, Type type, int duration, int value, String description) {
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.value = value;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isExpired() {
        return duration <= 0;
    }

    public void tickTurn() {
        if (duration > 0) {
            duration--;
        }
    }

    public String getFormattedTag() {
        String sign = (type == Type.BUFF) ? "+" : "-";
        String durStr = (duration > 0) ? " (" + duration + "t)" : "";
        String valStr = (value != 0) ? " " + (value > 0 && type == Type.BUFF ? "+" : "") + value : "";
        return "[" + sign + " " + name + valStr + durStr + "]";
    }

    @Override
    public String toString() {
        return getFormattedTag();
    }
}

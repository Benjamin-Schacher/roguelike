package com.eltim.rogue.world;

public class tile {
    private char symbol;
    private boolean walkable;

    public tile(char symbol, boolean walkable) {
        this.symbol = symbol;
        this.walkable = walkable;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isWalkable() {
        return walkable;
    }
}

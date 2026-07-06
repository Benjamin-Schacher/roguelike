package com.eltim.rogue.entity.environment;

import com.eltim.rogue.entity.base.entity;

public class door extends entity {

    private int doorCode;
    private doorStateEnum state;

    public door(int x, int y, char symbol, doorStateEnum initialState, int doorCode) {
        super(x, y, symbol);
        this.state = initialState;
        this.setName("Porte");
        this.setMaxLifePoint(10);
        this.setLifePoint(10);
        this.doorCode = doorCode;
    }

    public doorStateEnum getState() {
        return state;
    }

    public void setState(doorStateEnum state) {
        this.state = state;
    }

    public int getDoorCode() {
        return doorCode;
    }

    public void setDoorCode(int doorCode) {
        this.doorCode = doorCode;
    }

    public boolean isOpen() {
        return state == doorStateEnum.OPEN;
    }
}

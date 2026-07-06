package com.eltim.rogue.entity;

import com.eltim.rogue.system.enumarateur.damageTypeEnum;

public class weakness {

    private damageTypeEnum type;
    private double damageMultiplier;
    private int duration;
    private int trueDamage;


    public weakness(damageTypeEnum type, double damageMultiplier, int duration, int trueDamage) {
        this.type = type;
        this.damageMultiplier = damageMultiplier;
        this.duration = duration;
        this.trueDamage = trueDamage;
    }

    public damageTypeEnum getType() {
        return type;
    }

    public void setType(damageTypeEnum type) {
        this.type = type;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTrueDamage() {
        return trueDamage;
    }

    public void setTrueDamage(int trueDamage) {
        this.trueDamage = trueDamage;
    }
    
}

package com.eltim.rogue.entity.summon;

import java.util.List;

import com.eltim.rogue.entity.base.entity;

public interface Iaction {
    public void execute(entity attacker, List<entity> entities);
}

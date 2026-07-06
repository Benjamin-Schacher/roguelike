package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.entity.player;

public interface level {
    map generate(player p);
}

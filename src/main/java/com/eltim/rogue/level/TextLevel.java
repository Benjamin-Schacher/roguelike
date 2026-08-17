package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.entity.player;

public class TextLevel implements level {

    private String filePath;
    private LevelLoader.LevelData data;

    public TextLevel(String filePath) {
        this.filePath = filePath;
        this.data = LevelLoader.parseFile(filePath);
    }

    @Override
    public map generate(player p) {
        return LevelLoader.generateMap(data, p);
    }

    public String getFilePath() {
        return filePath;
    }

    public LevelLoader.LevelData getData() {
        return data;
    }
}

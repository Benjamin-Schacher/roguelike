package com.eltim.rogue.level;

public class tutoLevel extends TextLevel {

    public tutoLevel() {
        super("levels/tuto.txt");
        com.eltim.rogue.engine.sound.SoundManager.getInstance().playMusic("Dark Tomb");
    }
}

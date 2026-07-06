package com.eltim.rogue.world;
import java.util.List;

import com.eltim.rogue.entity.base.entity;

public class map {
    private int width;
    private int height;
    private tile[][] tiles;
    private List<entity> entities;
    private String levelName = "???";

    public map(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new tile[width][height];
        this.entities = new java.util.ArrayList<>();
    }

    public void setTile(int x, int y, tile t) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = t;
        }
    }

    public List<entity> getEntities() {
        return entities;
    }

    public tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return new tile(' ', false); // Hors limites
        }
        return tiles[x][y];
    }

    public void addEntity(entity e) {
        entities.add(e);
    }

    public void removeEntity(entity e) {
        entities.remove(e);
    }

    public entity getEntityAt(int x, int y) {
        for (entity e : entities) {
            if (e.getX() == x && e.getY() == y) {
                return e;
            }
        }
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}

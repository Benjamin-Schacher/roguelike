package com.eltim.rogue.system;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.world.map;

public class mouvementSysteme {
    public static void moveEntity(entity e, int dx, int dy, map gameMap) {
        int newX = e.getX() + dx;
        int newY = e.getY() + dy;

        entity targetEntity = gameMap.getEntityAt(newX, newY);
        if (targetEntity != null) {
            // Si c'est une porte ouverte, on la traverse sans la supprimer
            if (targetEntity instanceof door && ((door) targetEntity).isOpen()) {
                e.setX(newX);
                e.setY(newY);
            } else if (targetEntity instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                // Le joueur se place SUR la tuile, mais on NE lance PAS la description
                e.setX(newX);
                e.setY(newY);
            } else {
                // Lancer l'interaction uniquement si le menu n'est pas déjà ouvert
                if (!InteractionSysteme.isMenuOpen()) {
                    InteractionSysteme.onEncounter(e, targetEntity, gameMap);
                }
            }
        } else if (gameMap.getTile(newX, newY).isWalkable()) {
            // Se déplacer normalement si c'est vide
            e.setX(newX);
            e.setY(newY);
        }
    }
}

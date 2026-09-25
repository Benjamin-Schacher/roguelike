package com.eltim.rogue.system;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.world.map;

public class mouvementSysteme {
    public static void moveEntity(entity e, int dx, int dy, map gameMap) {
        int oldX = e.getX();
        int oldY = e.getY();
        int newX = oldX + dx;
        int newY = oldY + dy;

        boolean moved = false;
        entity targetEntity = gameMap.getEntityAt(newX, newY);
        if (targetEntity != null) {
            // Si c'est un compagnon du groupe du joueur, le joueur peut passer à travers sans bloquer !
            if (e instanceof player && targetEntity instanceof npc && ((player) e).getParty().contains(targetEntity)) {
                e.setX(newX);
                e.setY(newY);
                moved = true;
            }
            // Si c'est une porte ouverte, on la traverse sans la supprimer
            else if (targetEntity instanceof door && ((door) targetEntity).isOpen()) {
                e.setX(newX);
                e.setY(newY);
                moved = true;
            } else if (targetEntity instanceof com.eltim.rogue.entity.environment.DescriptionMarker) {
                ((com.eltim.rogue.entity.environment.DescriptionMarker) targetEntity).markRead();
                e.setX(newX);
                e.setY(newY);
                moved = true;
            } else if (targetEntity instanceof com.eltim.rogue.entity.environment.chest && ((com.eltim.rogue.entity.environment.chest) targetEntity).isOpen() && !((com.eltim.rogue.entity.environment.chest) targetEntity).isAllowReinteraction()) {
                // Coffre déjà ouvert/pillé : inerte, aucune interaction
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
            moved = true;
        }

        // Si le joueur a bougé, les compagnons du groupe le suivent directement derrière lui !
        if (moved && e instanceof player) {
            player p = (player) e;
            int prevX = oldX;
            int prevY = oldY;
            for (npc companion : p.getParty()) {
                int compOldX = companion.getX();
                int compOldY = companion.getY();
                companion.setX(prevX);
                companion.setY(prevY);
                prevX = compOldX;
                prevY = compOldY;
            }
        }
    }
}

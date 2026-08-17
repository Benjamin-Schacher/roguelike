package com.eltim.rogue.system;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.world.map;

public class EnemyAISystem {

    // 60 frames = 1 seconde
    private static final int BASE_TICKS_PER_MOVE = 60;

    public static void tick(map currentMap, player p, int frameCount) {
        if (currentMap == null || p == null) return;

        for (entity e : currentMap.getEntities()) {
            if (e instanceof monster) {
                monster m = (monster) e;
                
                // Portée de poursuite : 3 cases minimum + modificateur de Sagesse (si positif)
                int sagMod = diceRollSysteme.getModifier(m.getSagesse());
                int pursuitRange = 3 + Math.max(0, sagMod);

                int distance = Math.abs(m.getX() - p.getX()) + Math.abs(m.getY() - p.getY());
                if (distance <= pursuitRange) {
                    // Calculer la vitesse de déplacement en fonction de la Dextérité (Agilité)
                    // Plus l'agilité est élevée, moins de frames sont nécessaires pour bouger
                    int dex = m.getAgilite();
                    int ticksToMove = Math.max(10, BASE_TICKS_PER_MOVE - (dex * 2));

                    if (frameCount % ticksToMove == 0) {
                        moveTowardsPlayer(m, p, currentMap);
                    }
                }
            }
        }
    }

    private static void moveTowardsPlayer(monster m, player p, map currentMap) {
        int dx = 0;
        int dy = 0;

        if (m.getX() < p.getX()) dx = 1;
        else if (m.getX() > p.getX()) dx = -1;

        if (m.getY() < p.getY()) dy = 1;
        else if (m.getY() > p.getY()) dy = -1;

        // Si on doit bouger en diagonale, on priorise l'axe avec la plus grande distance, ou on prend un des deux
        if (dx != 0 && dy != 0) {
            if (Math.abs(m.getX() - p.getX()) > Math.abs(m.getY() - p.getY())) {
                if (canMove(m, dx, 0, currentMap)) dy = 0;
                else if (canMove(m, 0, dy, currentMap)) dx = 0;
                else return; // bloqué
            } else {
                if (canMove(m, 0, dy, currentMap)) dx = 0;
                else if (canMove(m, dx, 0, currentMap)) dy = 0;
                else return; // bloqué
            }
        } else if (dx != 0) {
            if (!canMove(m, dx, 0, currentMap)) return; // bloqué
        } else if (dy != 0) {
            if (!canMove(m, 0, dy, currentMap)) return; // bloqué
        } else {
            return; // Déjà sur le joueur (impossible en théorie)
        }

        mouvementSysteme.moveEntity(m, dx, dy, currentMap);
    }

    private static boolean canMove(monster m, int dx, int dy, map currentMap) {
        int targetX = m.getX() + dx;
        int targetY = m.getY() + dy;
        
        entity targetEntity = currentMap.getEntityAt(targetX, targetY);
        if (targetEntity != null && !(targetEntity instanceof com.eltim.rogue.entity.player)) {
            if (!(targetEntity instanceof com.eltim.rogue.entity.environment.DescriptionMarker)) {
                return false; // Bloqué par une autre entité qui n'est pas le joueur ni un marqueur
            }
        }
        return currentMap.getTile(targetX, targetY).isWalkable();
    }
}

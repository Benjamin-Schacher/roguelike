package com.eltim.rogue.entity.summon;

import java.util.List;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;

public class summon extends entity {

    private Iaction action;
    private entity summoner;
    private int turnsRemaining;

    public summon(Iaction action, entity summoner, int turnsRemaining) {
        super(summoner.getX(), summoner.getY(), 's');
        this.name = "Invocation";
        this.action = action;
        this.summoner = summoner;
        this.turnsRemaining = turnsRemaining;
    }

    public void performAction(List<entity> enemies) {
        if (turnsRemaining == 0) {
            if (summoner instanceof player) {
                ((player) summoner).removeSummon(this);
            }
            return;
        }
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
        if (action != null) {
            action.execute(this, enemies);
        } else {
            // Comportement par défaut : attaque l'ennemi au HP le plus bas
            entity lowest = null;
            for (entity enemy : enemies) {
                if (!enemy.isDead()) {
                    if (lowest == null || enemy.getLifePoint() < lowest.getLifePoint()) {
                        lowest = enemy;
                    }
                }
            }
            if (lowest != null) {
                //if (com.eltim.rogue.system.combatSysteme.tryAttackWeapon(this, lowest, null)) {
                //    com.eltim.rogue.system.combatSysteme.doDamageWeapon(this, lowest, null);
                //}
            }
        }
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public entity getSummoner() {
        return summoner;
    }
}

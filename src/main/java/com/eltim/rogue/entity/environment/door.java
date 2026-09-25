package com.eltim.rogue.entity.environment;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.base.Interactable;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.key;
import com.eltim.rogue.system.ExplorationLog;
import com.eltim.rogue.system.diceRollSysteme;
import com.eltim.rogue.world.map;

import java.util.ArrayList;
import java.util.List;

public class door extends entity implements Interactable {

    private int doorCode;
    private doorStateEnum state;

    public door(int x, int y, char symbol, doorStateEnum initialState, int doorCode) {
        super(x, y, symbol);
        this.state = initialState;
        this.setName("Porte");
        this.setMaxLifePoint(10);
        this.setLifePoint(10);
        this.doorCode = doorCode;
    }

    public doorStateEnum getState() {
        return state;
    }

    public void setState(doorStateEnum state) {
        this.state = state;
    }

    public int getDoorCode() {
        return doorCode;
    }

    public void setDoorCode(int doorCode) {
        this.doorCode = doorCode;
    }

    public boolean isOpen() {
        return state == doorStateEnum.OPEN;
    }

    @Override
    public List<String> getInteractionOptions(player p) {
        if (isOpen()) {
            return new ArrayList<>(); // Porte déjà ouverte, on passe à travers
        }
        List<String> options = new ArrayList<>();
        if (state == doorStateEnum.NORMAL) {
            options.add("Ouvrir");
        } else if (state == doorStateEnum.LOCKED) {
            options.add("Déverrouiller (Clé)");
        } else if (state == doorStateEnum.OLD) {
            options.add("Déverrouiller (Clé)");
            options.add("Forcer (Force)");
        }
        options.add("Partir");
        return options;
    }

    @Override
    public void handleInteraction(player p, String action, map currentMap) {
        if (action.equals("Ouvrir")) {
            if (state == doorStateEnum.NORMAL || state == doorStateEnum.OLD) {
                state = doorStateEnum.OPEN;
                setSymbol('D');
                com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("door_open");
                System.out.println("La porte s'ouvre.");
            } else {
                System.out.println("La porte est verrouillée !");
            }
        } else if (action.equals("Déverrouiller (Clé)")) {
            key matchingKey = null;
            boolean hasAnyKey = false;
            for (item it : p.getInventory()) {
                if (it instanceof key) {
                    hasAnyKey = true;
                    key k = (key) it;
                    if (k.getKeyCode() == doorCode) {
                        matchingKey = k;
                        break;
                    }
                }
            }
            if (matchingKey != null) {
                p.getInventory().remove(matchingKey);
                state = doorStateEnum.OPEN;
                setSymbol('D');
                com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("door_open");
                System.out.println("Vous utilisez la clé. La porte s'ouvre !");
            } else if (hasAnyKey) {
                System.out.println("Vous n'avez pas la bonne clé !");
            } else {
                System.out.println("Vous n'avez pas de clé !");
            }
        } else if (action.equals("Forcer (Force)")) {
            int forceMod = diceRollSysteme.getModifier(p.getForce());
            int roll = (int) (Math.random() * 20) + 1;
            boolean success = (roll + forceMod) >= 14;
            ExplorationLog.addRoll("Forcer la porte", roll, forceMod, 14);
            if (success) {
                state = doorStateEnum.OPEN;
                setSymbol('D');
                com.eltim.rogue.engine.sound.SoundManager.getInstance().playSFX("door_open");
            } else {
                int damage = (int) (Math.random() * 4) + 1;
                p.setLifePoint(p.getLifePoint() - damage);
                ExplorationLog.add("  ↳ Blessé de " + damage + " PV");
            }
        }
    }
}

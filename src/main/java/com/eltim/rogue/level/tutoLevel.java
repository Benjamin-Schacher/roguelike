package com.eltim.rogue.level;

import com.eltim.rogue.entity.base.Belief;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.environment.InteractionTile;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.system.ExplorationLog;
import com.eltim.rogue.system.dialogue.VarainDialogue;
import com.eltim.rogue.world.map;

import java.util.List;

public class tutoLevel extends TextLevel {

    public tutoLevel() {
        super("levels/tuto.txt");
        com.eltim.rogue.engine.sound.SoundManager.getInstance().playMusic("Dark Tomb");
    }

    @Override
    public boolean onConfigureInteractionOptions(entity attacker, entity target, List<String> options) {
        // Configuration spécifique du dialogue de Varain
        if (target instanceof npc && attacker instanceof player) {
            npc n = (npc) target;
            if (n.getName() != null && n.getName().toLowerCase().contains("varain")) {
                VarainDialogue.startDialogue((player) attacker, n);
                options.clear();
                options.addAll(VarainDialogue.getPlayerOptions());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCustomInteraction(entity attacker, entity target, String action, int selection, map currentMap, List<String> options) {
        // 1. Branche de dialogue interactive de Varain
        if (VarainDialogue.isDialogueActive()) {
            if (attacker instanceof player && target instanceof npc) {
                VarainDialogue.selectOption(selection, (player) attacker, (npc) target);
                options.clear();
                options.addAll(VarainDialogue.getPlayerOptions());
                return true; // Le menu reste ouvert pour poursuivre le dialogue
            }
        }

        // Fermeture automatique du dialogue après une réponse finale
        if (VarainDialogue.getNpcSpeech() != null) {
            VarainDialogue.closeDialogue();
            return false; // Permet de fermer le menu
        }

        // 2. Interaction avec l'Autel de Karin
        if (target instanceof InteractionTile && attacker instanceof player) {
            InteractionTile it = (InteractionTile) target;
            String text = (it.getActionName() + " " + it.getSecretEffectText()).toLowerCase();
            if (text.contains("karin")) {
                if (action.equalsIgnoreCase("Examiner")) {
                    ExplorationLog.addDescription("Un ancien autel à la gloire de Karin, dieu des voleurs.");
                    return true;
                } else if (!action.equalsIgnoreCase("Quitter") && !action.equalsIgnoreCase("Partir")) {
                    handleKarinPrayer((player) attacker, it, currentMap);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getCustomExamineText(entity target) {
        if (target instanceof InteractionTile) {
            InteractionTile it = (InteractionTile) target;
            String text = (it.getActionName() + " " + it.getSecretEffectText()).toLowerCase();
            if (text.contains("karin")) {
                return "Un ancien autel à la gloire de Karin, dieu des voleurs.";
            }
        }
        return null;
    }

    /**
     * Logique de prière à l'autel de Karin : déverrouille la porte de cellule du joueur si fidèle de Karin.
     */
    public static void handleKarinPrayer(player p, InteractionTile it, map currentMap) {
        p.hasPrayedAtAltar = true;
        if (p.getBelief() == Belief.KARIN) {
            if (currentMap != null) {
                door cellDoor = null;
                double minDistance = Double.MAX_VALUE;
                for (entity e : currentMap.getEntities()) {
                    if (e instanceof door) {
                        door d = (door) e;
                        if (d.getState() == doorStateEnum.OLD) {
                            cellDoor = d;
                            break;
                        }
                        double dist = Math.hypot(d.getX() - it.getX(), d.getY() - it.getY());
                        if (dist < minDistance) {
                            minDistance = dist;
                            cellDoor = d;
                        }
                    }
                }
                if (cellDoor != null) {
                    cellDoor.setState(doorStateEnum.OPEN);
                    cellDoor.setSymbol('D');
                }
            }
            ExplorationLog.addDescription("Succès : Le gond de la porte lâche, usé par la vieillesse ! La porte s'ouvre !");
        } else {
            ExplorationLog.addDescription("Échec : Vous priez Karin, mais vous n'êtes pas son fidèle. Rien ne se passe.");
        }
    }
}

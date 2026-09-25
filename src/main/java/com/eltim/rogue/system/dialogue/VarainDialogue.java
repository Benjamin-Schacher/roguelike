package com.eltim.rogue.system.dialogue;

import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.system.ExplorationLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'arbre de dialogue interactif de Varain selon niveauSchema.txt.
 */
public class VarainDialogue {

    public enum DialogueState {
        NOT_STARTED,
        ALTAR_BRANCH,       // Question si prié à l'autel de Karin
        NO_ALTAR_BRANCH_1,  // Question 1 si pas prié à l'autel
        NO_ALTAR_BRANCH_2,  // Question 2 après "Je ne te connais pas, qui es-tu ?"
        ENDED
    }

    private static DialogueState currentState = DialogueState.NOT_STARTED;
    private static String npcSpeech = null;
    private static List<String> playerOptions = new ArrayList<>();

    public static boolean isDialogueActive() {
        return currentState != DialogueState.NOT_STARTED && currentState != DialogueState.ENDED;
    }

    public static String getNpcSpeech() {
        return npcSpeech;
    }

    public static List<String> getPlayerOptions() {
        return playerOptions;
    }

    public static void startDialogue(player p, npc varain) {
        if (p.getParty().contains(varain)) {
            npcSpeech = "Varain : \"Allons-y, ne perdons pas de temps !\"";
            playerOptions.clear();
            playerOptions.add("Continuer");
            currentState = DialogueState.ENDED;
            return;
        }

        if (p.hasPrayedAtAltar) {
            currentState = DialogueState.ALTAR_BRANCH;
            npcSpeech = "Varain : \"J'ai vu que Karin est avec toi mon frère, tu dois avoir de l'importance à ses yeux. Je peux t'accompagner pour voir le destin qu'il te réserve ?\"";
            playerOptions.clear();
            playerOptions.add("Oui");
            playerOptions.add("Non");
        } else {
            currentState = DialogueState.NO_ALTAR_BRANCH_1;
            npcSpeech = "Varain : \"On est dans une sacré merde tous les deux, on peut peut-être s'entraider ?\"";
            playerOptions.clear();
            playerOptions.add("Je ne te connais pas, qui es-tu ?");
            playerOptions.add("À deux on aura plus de chance");
        }
    }

    public static void selectOption(int optionIndex, player p, npc varain) {
        if (currentState == DialogueState.ALTAR_BRANCH) {
            if (optionIndex == 0) { // "Oui"
                npcSpeech = "Varain : \"Parfait !\"";
                if (!p.getParty().contains(varain)) {
                    p.getParty().add(varain);
                }
                ExplorationLog.addDescription("Varain a rejoint votre groupe !");
            } else { // "Non"
                npcSpeech = "Varain : \"Qu'il en soit ainsi.\"";
                ExplorationLog.addDescription("Varain reste statique dans sa cellule.");
            }
            playerOptions.clear();
            playerOptions.add("Fermer");
            currentState = DialogueState.ENDED;
        } 
        else if (currentState == DialogueState.NO_ALTAR_BRANCH_1) {
            if (optionIndex == 0) { // "Je ne te connais pas, qui es-tu ?"
                currentState = DialogueState.NO_ALTAR_BRANCH_2;
                npcSpeech = "Varain : \"Je suis Varain, je suis ici car j'ai volé le Baron. Je peux t'aider si tu me promets de me faire évader.\"";
                playerOptions.clear();
                playerOptions.add("Je ne fréquente pas les voleurs !");
                playerOptions.add("Je veux bien t'aider si tu me jures de ne plus voler");
                playerOptions.add("À deux on aura plus de chance");
            } else { // "À deux on aura plus de chance"
                npcSpeech = "Varain : \"C'est l'esprit qui me plaît ! Allons-y !\"";
                if (!p.getParty().contains(varain)) {
                    p.getParty().add(varain);
                }
                ExplorationLog.addDescription("Varain a rejoint votre groupe !");
                playerOptions.clear();
                playerOptions.add("Fermer");
                currentState = DialogueState.ENDED;
            }
        }
        else if (currentState == DialogueState.NO_ALTAR_BRANCH_2) {
            if (optionIndex == 0) { // "Je ne fréquente pas les voleurs !"
                npcSpeech = "Varain : \"Qu'il en soit ainsi.\"";
                ExplorationLog.addDescription("Varain reste statique dans sa cellule.");
            } else if (optionIndex == 1) { // "Je veux bien t'aider si tu me jures de ne plus voler"
                npcSpeech = "Varain : \"Qu'il en soit ainsi, je le jure !\"";
                if (!p.getParty().contains(varain)) {
                    p.getParty().add(varain);
                }
                ExplorationLog.addDescription("Varain a rejoint votre groupe !");
            } else { // "À deux on aura plus de chance"
                npcSpeech = "Varain : \"C'est l'esprit qui me plaît ! Allons-y !\"";
                if (!p.getParty().contains(varain)) {
                    p.getParty().add(varain);
                }
                ExplorationLog.addDescription("Varain a rejoint votre groupe !");
            }
            playerOptions.clear();
            playerOptions.add("Fermer");
            currentState = DialogueState.ENDED;
        }
    }

    public static void closeDialogue() {
        currentState = DialogueState.NOT_STARTED;
        npcSpeech = null;
        playerOptions.clear();
    }
}

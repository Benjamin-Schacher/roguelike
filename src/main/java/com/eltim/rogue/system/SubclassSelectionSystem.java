package com.eltim.rogue.system;

import com.eltim.rogue.entity.classe.Subclass;
import com.eltim.rogue.entity.classe.SubclassCatalog;
import com.eltim.rogue.entity.player;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SubclassSelectionSystem {

    private static player currentPlayer;
    private static List<Subclass> availableSubclasses = new ArrayList<>();
    private static int selectedIndex = 0;
    private static boolean isDone = false;

    public static void open(player p) {
        currentPlayer = p;
        availableSubclasses = SubclassCatalog.getAvailableFor(p.classe);
        selectedIndex = 0;
        isDone = false;
    }

    public static void handleInput(KeyEvent key) {
        if (currentPlayer == null || isDone) return;

        int totalOptions = availableSubclasses.size() + 1; // +1 pour l'option "Rester classe pure"
        int code = key.getKeyCode();

        switch (code) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_W:
                selectedIndex = (selectedIndex - 1 + totalOptions) % totalOptions;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                selectedIndex = (selectedIndex + 1) % totalOptions;
                break;
            case KeyEvent.VK_ENTER:
                confirmSelection();
                break;
        }
    }

    private static void confirmSelection() {
        if (currentPlayer == null || currentPlayer.classe == null) {
            isDone = true;
            return;
        }

        if (selectedIndex < availableSubclasses.size()) {
            Subclass chosen = availableSubclasses.get(selectedIndex);
            currentPlayer.classe.setSubclass(chosen.name, chosen.trees);
            ExplorationLog.addLog("Vous vous êtes bi-classé en [" + chosen.name + "] ! (1 pt/niveau)");
        } else {
            currentPlayer.classe.hasRefusedSubclass = true;
            ExplorationLog.addLog("Vous restez fidèle à votre classe pure [" + currentPlayer.classe.name + "] ! (2 pts/niveau)");
        }
        currentPlayer.setPendingSubclassChoice(false);
        isDone = true;
    }

    public static List<Subclass> getAvailableSubclasses() {
        return availableSubclasses;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static boolean isDone() {
        return isDone;
    }

    public static player getCurrentPlayer() {
        return currentPlayer;
    }

    public static Subclass getSelectedSubclass() {
        if (selectedIndex >= 0 && selectedIndex < availableSubclasses.size()) {
            return availableSubclasses.get(selectedIndex);
        }
        return null;
    }

    public static boolean isPureClassSelected() {
        return selectedIndex == availableSubclasses.size();
    }
}

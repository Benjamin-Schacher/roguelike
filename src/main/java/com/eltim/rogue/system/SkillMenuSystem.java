package com.eltim.rogue.system;

import com.eltim.rogue.entity.classe.Skill;
import com.eltim.rogue.entity.classe.SkillTree;
import com.eltim.rogue.entity.classe.classe;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la navigation et les achats dans le menu de compétences avec 4 emplacements d'arbres.
 */
public class SkillMenuSystem {

    private static int selectedSlot = 0; // 0 à 3
    private static int selectedTier = 0; // 0 à 4
    private static classe currentClasse = null;

    // État du modal de choix d'arbre pour un slot vide
    private static boolean isSelectingTree = false;
    private static int treePickerIndex = 0;
    private static List<SkillTree> unslottedTrees = new ArrayList<>();
    private static String statusMessage = null;

    public static void open(classe c) {
        currentClasse = c;
        selectedSlot = 0;
        selectedTier = 0;
        isSelectingTree = false;
        treePickerIndex = 0;
        statusMessage = null;
    }

    public static void handleInput(KeyEvent key, classe playerClasse) {
        if (playerClasse == null) return;
        currentClasse = playerClasse;
        int code = key.getKeyCode();

        // 1. Mode Modal de Sélection d'un nouvel Arbre
        if (isSelectingTree) {
            unslottedTrees = playerClasse.getUnslottedAvailableTrees();
            int total = unslottedTrees.size();

            if (code == KeyEvent.VK_ESCAPE) {
                isSelectingTree = false;
                statusMessage = null;
                return;
            }

            if (total == 0) {
                isSelectingTree = false;
                statusMessage = "Aucun arbre supplémentaire disponible !";
                return;
            }

            switch (code) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_Z:
                case KeyEvent.VK_W:
                    treePickerIndex = (treePickerIndex - 1 + total) % total;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    treePickerIndex = (treePickerIndex + 1) % total;
                    break;
                case KeyEvent.VK_ENTER:
                    SkillTree chosenTree = unslottedTrees.get(treePickerIndex);
                    boolean assigned = playerClasse.assignTreeToSlot(selectedSlot, chosenTree);
                    if (assigned) {
                        isSelectingTree = false;
                        selectedTier = 0;
                        statusMessage = "Arbre [" + chosenTree.name + "] débloqué et équipé au Tier 1 !";
                    } else {
                        statusMessage = "Impossible d'assigner l'arbre (1 point requis) !";
                    }
                    break;
            }
            return;
        }

        // 2. Mode Navigation dans les 4 emplacements
        switch (code) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_Q:
            case KeyEvent.VK_A:
                selectedSlot = (selectedSlot - 1 + 4) % 4;
                statusMessage = null;
                clampSelectedTier(playerClasse);
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                selectedSlot = (selectedSlot + 1) % 4;
                statusMessage = null;
                clampSelectedTier(playerClasse);
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_W:
                if (playerClasse.activeSlots[selectedSlot] != null) {
                    selectedTier = Math.max(0, selectedTier - 1);
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (playerClasse.activeSlots[selectedSlot] != null) {
                    SkillTree tree = playerClasse.activeSlots[selectedSlot];
                    selectedTier = Math.min(tree.skills.size() - 1, selectedTier + 1);
                }
                break;

            case KeyEvent.VK_ENTER:
                SkillTree slotTree = playerClasse.activeSlots[selectedSlot];
                if (slotTree == null) {
                    // Clic sur un slot vide avec [+]
                    if (playerClasse.skillPoints < 1) {
                        statusMessage = "Points insuffisants ! (1 point requis pour débloquer le Tier 1)";
                    } else {
                        unslottedTrees = playerClasse.getUnslottedAvailableTrees();
                        if (unslottedTrees.isEmpty()) {
                            statusMessage = "Aucun arbre disponible à équiper !";
                        } else {
                            isSelectingTree = true;
                            treePickerIndex = 0;
                            statusMessage = null;
                        }
                    }
                } else {
                    // Clic sur un talent d'un arbre déjà équipé
                    Skill skill = slotTree.skills.get(selectedTier);
                    boolean success = playerClasse.unlockSkill(slotTree, skill);
                    if (success) {
                        statusMessage = "Talent [" + skill.name + "] débloqué !";
                    } else {
                        if (skill.unlocked) {
                            statusMessage = "Ce talent est déjà débloqué.";
                        } else {
                            int cost = playerClasse.getSkillCost(skill);
                            if (playerClasse.skillPoints < cost) {
                                statusMessage = "Points insuffisants ! Requis: " + cost + " pt(s).";
                            } else {
                                statusMessage = "Le talent de rang précédent doit d'abord être débloqué.";
                            }
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    private static void clampSelectedTier(classe playerClasse) {
        if (playerClasse.activeSlots[selectedSlot] != null) {
            SkillTree tree = playerClasse.activeSlots[selectedSlot];
            selectedTier = Math.min(selectedTier, tree.skills.size() - 1);
        } else {
            selectedTier = 0;
        }
    }

    public static int getSelectedSlot() { return selectedSlot; }
    public static int getSelectedTree() { return selectedSlot; } // Rétrocompatibilité
    public static int getSelectedTier() { return selectedTier; }
    public static boolean isSelectingTree() { return isSelectingTree; }
    public static int getTreePickerIndex() { return treePickerIndex; }
    public static List<SkillTree> getUnslottedTrees() { return unslottedTrees; }
    public static String getStatusMessage() { return statusMessage; }
}

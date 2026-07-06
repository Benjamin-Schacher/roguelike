package com.eltim.rogue.system;

import com.eltim.rogue.entity.classe.Skill;
import com.eltim.rogue.entity.classe.SkillTree;
import com.eltim.rogue.entity.classe.classe;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Gère la navigation et les achats dans le menu de compétences.
 */
public class SkillMenuSystem {

    private static int selectedTree = 0;
    private static int selectedTier = 0;
    private static classe currentClasse = null;

    public static void open(classe c) {
        currentClasse = c;
        selectedTree = 0;
        selectedTier = 0;
    }

    public static void handleInput(KeyEvent key, classe playerClasse) {
        if (playerClasse == null || playerClasse.trees == null || playerClasse.trees.isEmpty()) return;

        currentClasse = playerClasse;
        List<SkillTree> trees = playerClasse.trees;
        int numTrees = trees.size();

        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                selectedTree = (selectedTree - 1 + numTrees) % numTrees;
                selectedTier = Math.min(selectedTier, trees.get(selectedTree).skills.size() - 1);
                break;
            case KeyEvent.VK_RIGHT:
                selectedTree = (selectedTree + 1) % numTrees;
                selectedTier = Math.min(selectedTier, trees.get(selectedTree).skills.size() - 1);
                break;
            case KeyEvent.VK_UP:
                selectedTier = Math.max(0, selectedTier - 1);
                break;
            case KeyEvent.VK_DOWN:
                selectedTier = Math.min(trees.get(selectedTree).skills.size() - 1, selectedTier + 1);
                break;
            case KeyEvent.VK_ENTER:
                // Tenter d'acheter le skill sélectionné
                SkillTree tree = trees.get(selectedTree);
                Skill skill = tree.skills.get(selectedTier);
                playerClasse.unlockSkill(tree, skill);
                break;
            default:
                break;
        }
    }

    public static int getSelectedTree() { return selectedTree; }
    public static int getSelectedTier() { return selectedTier; }
}

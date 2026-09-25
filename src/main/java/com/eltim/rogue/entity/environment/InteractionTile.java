package com.eltim.rogue.entity.environment;

import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.entity.base.Interactable;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.system.ExplorationLog;
import com.eltim.rogue.world.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuile d'interaction personnalisée issue de la section [INTERACTIONS].
 * Sépare proprement le nom de l'action de l'effet secret pour éviter tout spoil d'avance.
 */
public class InteractionTile extends entity implements Interactable {

    private final String actionName;
    private final String secretEffectText;

    public InteractionTile(int x, int y, char symbol, String rawText) {
        super(x, y, symbol);
        
        if (rawText != null && rawText.contains(",")) {
            String[] parts = rawText.split(",", 2);
            this.actionName = parts[0].trim();
            this.secretEffectText = parts[1].trim();
        } else {
            this.actionName = rawText != null ? rawText.trim() : "Interagir";
            this.secretEffectText = "";
        }

        // Nom d'affichage propre dans l'interface sans déborder
        String lowerAction = this.actionName.toLowerCase();
        if (lowerAction.contains("karin") || lowerAction.contains("prier")) {
            this.name = "Autel de Karin";
        } else if (lowerAction.length() > 25) {
            this.name = this.actionName.substring(0, 22) + "...";
        } else {
            this.name = this.actionName;
        }
    }

    public String getActionName() {
        return actionName;
    }

    public String getSecretEffectText() {
        return secretEffectText;
    }

    @Override
    public List<String> getInteractionOptions(player p) {
        List<String> options = new ArrayList<>();
        options.add(actionName);
        options.add("Examiner");
        options.add("Quitter");
        return options;
    }

    @Override
    public void handleInteraction(player p, String action, map currentMap) {
        if (action.equalsIgnoreCase("Examiner")) {
            ExplorationLog.addDescription("Un élément interactif : " + getName());
        } else if (!action.equalsIgnoreCase("Quitter") && !action.equalsIgnoreCase("Partir")) {
            ExplorationLog.addDescription("Interaction réalisée.");
        }
    }
}

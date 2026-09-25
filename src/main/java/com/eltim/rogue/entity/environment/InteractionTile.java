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

    private String actionName;
    private String tag = "";
    private String description = "";
    private String secretEffectText = "";

    public InteractionTile(int x, int y, char symbol, String rawText) {
        super(x, y, symbol);
        parseRawConfig(rawText);
    }

    public InteractionTile(int x, int y, char symbol, String actionName, String tag, String description) {
        super(x, y, symbol);
        this.actionName = actionName;
        this.tag = tag != null ? tag : "";
        this.description = description != null ? description : "";
        this.secretEffectText = "";
        applyDefaultName();
    }

    private void parseRawConfig(String rawText) {
        if (rawText != null && rawText.contains("|")) {
            String[] segments = rawText.split("\\|");
            for (int i = 0; i < segments.length; i++) {
                String seg = segments[i].trim();
                if (seg.isEmpty()) continue;
                String segUpper = seg.toUpperCase();

                if (segUpper.startsWith("TAG:")) {
                    this.tag = seg.substring(4).trim();
                } else if (segUpper.startsWith("DESC:") || segUpper.startsWith("DESCRIPTION:")) {
                    int colon = seg.indexOf(":");
                    String d = seg.substring(colon + 1).trim();
                    if (d.startsWith("\"") && d.endsWith("\"") && d.length() >= 2) {
                        d = d.substring(1, d.length() - 1);
                    }
                    this.description = d;
                } else if (segUpper.startsWith("ACTION:") || segUpper.startsWith("ACT:")) {
                    int colon = seg.indexOf(":");
                    this.actionName = seg.substring(colon + 1).trim();
                } else if (segUpper.startsWith("NAME:") || segUpper.startsWith("NOM:")) {
                    int colon = seg.indexOf(":");
                    this.setName(seg.substring(colon + 1).trim());
                } else if (i == 0) {
                    this.actionName = seg;
                } else {
                    if (this.secretEffectText == null || this.secretEffectText.isEmpty()) {
                        this.secretEffectText = seg;
                    }
                }
            }
        } else if (rawText != null && rawText.contains(",")) {
            String[] parts = rawText.split(",", 2);
            this.actionName = parts[0].trim();
            this.secretEffectText = parts[1].trim();
            this.description = "";
        } else {
            this.actionName = rawText != null ? rawText.trim() : "Interagir";
            this.secretEffectText = "";
            this.description = "";
        }

        // Rétrocompatibilité tag pour Karin
        if ((this.tag == null || this.tag.isEmpty()) &&
                (this.actionName + " " + this.secretEffectText + " " + this.description).toLowerCase().contains("karin")) {
            this.tag = "tutoLevel";
        }

        applyDefaultName();
    }

    private void applyDefaultName() {
        if (this.getName() == null || this.getName().isEmpty()) {
            String lower = this.actionName != null ? this.actionName.toLowerCase() : "";
            if (lower.contains("karin") || lower.contains("prier")) {
                this.setName("Autel de Karin");
            } else if (this.actionName != null && this.actionName.length() > 25) {
                this.setName(this.actionName.substring(0, 22) + "...");
            } else {
                this.setName(this.actionName != null ? this.actionName : "Élément interactif");
            }
        }
    }

    public String getActionName() {
        return actionName != null ? actionName : "Interagir";
    }

    public String getTag() {
        return tag != null ? tag : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getSecretEffectText() {
        return secretEffectText != null ? secretEffectText : "";
    }

    @Override
    public List<String> getInteractionOptions(player p) {
        List<String> options = new ArrayList<>();
        options.add(getActionName());
        options.add("Examiner");
        options.add("Quitter");
        return options;
    }

    @Override
    public void handleInteraction(player p, String action, map currentMap) {
        if (action.equalsIgnoreCase("Examiner")) {
            if (description != null && !description.isEmpty()) {
                ExplorationLog.addDescription(description);
            } else {
                ExplorationLog.addDescription("Un élément interactif : " + getName());
            }
        } else if (!action.equalsIgnoreCase("Quitter") && !action.equalsIgnoreCase("Partir")) {
            ExplorationLog.addDescription("Interaction réalisée.");
        }
    }
}

package com.eltim.rogue.entity.environment;

import com.eltim.rogue.entity.base.entity;

/**
 * Marqueur de description placé dans le niveau.
 * Affiché comme '?' sur la carte. Quand le joueur interagit avec,
 * une popup de description s'affiche.
 * Non-bloquant : le joueur peut passer dessus.
 */
public class DescriptionMarker extends entity {

    private final String description;
    private boolean alreadyRead = false;

    public DescriptionMarker(int x, int y, String description) {
        super(x, y, '?');
        this.description = description;
        this.name = "Description";
    }

    public String getDescription() {
        return description;
    }

    public boolean isAlreadyRead() {
        return alreadyRead;
    }

    public void markRead() {
        this.alreadyRead = true;
        // On garde le '?' mais on pourrait le changer pour indiquer qu'il a été lu
    }
}

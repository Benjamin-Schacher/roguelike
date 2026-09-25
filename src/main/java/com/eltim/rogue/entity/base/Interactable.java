package com.eltim.rogue.entity.base;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.world.map;
import java.util.List;

/**
 * Interface pour tout objet ou élément du monde pouvant afficher un menu d'options
 * et réagir à une action du joueur.
 */
public interface Interactable {

    /**
     * Retourne les options d'interaction disponibles pour le joueur.
     * Si la liste retournée est vide ou null, le menu ne s'ouvre pas (ex: porte déjà ouverte).
     */
    List<String> getInteractionOptions(player p);

    /**
     * Traite l'action sélectionnée par le joueur.
     */
    void handleInteraction(player p, String action, map currentMap);
}

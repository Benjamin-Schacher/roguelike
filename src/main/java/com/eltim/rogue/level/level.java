package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import java.util.List;

public interface level {
    map generate(player p);

    /**
     * Permet au niveau de configurer des options d'interaction personnalisées (ex: PNJ spécial avec arbre de dialogue).
     * @param attacker l'entité qui interagit (généralement le joueur)
     * @param target l'entité ciblée (PNJ, coffre, objet interactif)
     * @param options la liste des options à remplir
     * @return true si le niveau prend en charge les options d'interaction, false pour les options par défaut.
     */
    default boolean onConfigureInteractionOptions(entity attacker, entity target, List<String> options) {
        return false;
    }

    /**
     * Permet au niveau d'exécuter une interaction personnalisée choisie dans le menu.
     * @param attacker l'entité qui agit
     * @param target l'entité ciblée
     * @param action le libellé de l'action choisie
     * @param selection l'index de l'option choisie
     * @param currentMap la carte courante
     * @param options la liste des options du menu (modifiable si le dialogue se poursuit)
     * @return true si l'action a été prise en charge par le niveau, false sinon.
     */
    default boolean onCustomInteraction(entity attacker, entity target, String action, int selection, map currentMap, List<String> options) {
        return false;
    }

    /**
     * Permet au niveau de fournir une description personnalisée lors de l'examen d'une entité.
     * @param target l'entité examinée
     * @return le texte de description, ou null pour la description standard.
     */
    default String getCustomExamineText(entity target) {
        return null;
    }
}

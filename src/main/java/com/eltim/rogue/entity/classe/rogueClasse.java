package com.eltim.rogue.entity.classe;

import java.util.Arrays;

public class rogueClasse extends classe {

    public rogueClasse() {
        this.name = "Voleur";
        this.description = "Maître de la discrétion, de la vitesse et des coups mortels dans le dos.";

        // ==========================================
        // ARBRE 1 : Assassinat
        // ==========================================
        SkillTree assassinat = new SkillTree("Assassinat", "Spécialisation dans les frappes mortelles et poisons.", Arrays.asList(
            new Skill("attaque_sournoise", "Attaque Sournoise",
                "Vos attaques infligent un bonus de dégâts égal à votre modificateur d'Agilité au combat.",
                1, "Assassinat").withSound("medieval-fantasy/woosh-1"),
            new Skill("lame_empoisonnee", "Lame Empoisonnée",
                "Vos attaques à la dague appliquent un poison infligeant 3 dégâts par tour pendant 3 tours.",
                2, "Assassinat").withSound("vrac/Verre-casser"),
            new Skill("ombre_tranchante", "Ombre Tranchante",
                "Vos coups critiques infligent un saignement persistant réduisant la résistance adverse.",
                3, "Assassinat"),
            new Skill("point_faible", "Point Faible",
                "Vos attaques ignorent 50% de la défense physique de la cible.",
                4, "Assassinat"),
            new Skill("execution", "Exécution",
                "Inflige le triple de dégâts si la cible possède moins de 35% de ses PV max.",
                5, "Assassinat").withSound("vrac/coup-metalique")
        ));

        // ==========================================
        // ARBRE 2 : Furtivité
        // ==========================================
        SkillTree furtivite = new SkillTree("Furtivité", "Maîtrise de l'esquive, de la dissimulation et de la survie.", Arrays.asList(
            new Skill("pas_legers", "Pas Légers",
                "Augmente votre esquive et réduit les risques d'être pris en embuscade.",
                1, "Furtivité"),
            new Skill("camouflage", "Camouflage",
                "Les monstres sont moins susceptibles de vous cibler en priorité au combat.",
                2, "Furtivité"),
            new Skill("voile_ombre", "Voile d'Ombre",
                "Esquive garantie de la toute première attaque ennemie subie lors d'un combat.",
                3, "Furtivité").withSound("medieval-fantasy/woosh-2"),
            new Skill("echappatoire", "Échappatoire Rapide",
                "La première fois que vous devriez succomber, vous survivez avec 1 PV et fuyez dans l'ombre.",
                4, "Furtivité"),
            new Skill("maitre_ombres", "Maître des Ombres",
                "Pendant 2 tours, vous devenez indétectable : tous vos coups portés sont des critiques assurés.",
                5, "Furtivité").withSound("rpg-battle-system/9")
        ));

        // ==========================================
        // ARBRE 3 : Roublardise
        // ==========================================
        SkillTree roublardise = new SkillTree("Roublardise", "Ruses déloyales, tours de passe-passe et objets trompeurs.", Arrays.asList(
            new Skill("coup_bas", "Coup Bas",
                "Une frappe vicieuse qui réduit l'attaque de la cible de 3 pour son prochain assaut.",
                1, "Roublardise").withSound("rpg-battle-system/2"),
            new Skill("fumigene_voleur", "Fumigène Troublant",
                "Lance un fumigène réduisant de 3 la précision de tous les adversaires pour 2 tours.",
                2, "Roublardise").withSound("rpg-battle-system/37"),
            new Skill("vol_poche", "Vol à la Tire",
                "Chaque ennemi vaincu rapporte 30% d'or ou de consommables supplémentaires.",
                3, "Roublardise"),
            new Skill("feinte", "Feinte Trompeuse",
                "Force l'adversaire ciblé à frapper dans le vide lors de sa prochaine action.",
                4, "Roublardise"),
            new Skill("chaos_sournois", "K.O. Vicieux",
                "Vos coups critiques étourdissent désormais la cible pendant 1 tour entier.",
                5, "Roublardise").withSound("prehistoric-platformer/wood-4")
        ));

        // ==========================================
        // ARBRE 4 : Agilité Martiale
        // ==========================================
        SkillTree agilite = new SkillTree("Agilité Martiale", "Dextérité surhumaine, maniement ambidextre et vélocité.", Arrays.asList(
            new Skill("reflexes_aceres", "Réflexes Acérés",
                "Ajoutez +2 permanent à votre Agilité et gagnez +2 en Défense physique.",
                1, "Agilité Martiale"),
            new Skill("ambidextrie", "Ambidextrie Mortelle",
                "Vous pouvez attaquer avec votre arme secondaire sans aucune pénalité de toucher.",
                2, "Agilité Martiale").withSound("medieval-fantasy/5"),
            new Skill("pirouette", "Pirouette Réflexe",
                "Quand vous esquivez une attaque, vous ripostez instantanément avec un coup de dague.",
                3, "Agilité Martiale"),
            new Skill("danse_mortelle", "Danse Mortelle",
                "Abattre un ennemi vous confère immédiatement une action d'attaque supplémentaire.",
                4, "Agilité Martiale"),
            new Skill("vitesse_eclair", "Vitesse de l'Éclair",
                "Vous agissez toujours en premier au combat et commencez chaque affrontement avec 2 actions.",
                5, "Agilité Martiale").withSound("medieval-fantasy/woosh-1")
        ));

        this.availableTrees.add(assassinat);
        this.availableTrees.add(furtivite);
        this.availableTrees.add(roublardise);
        this.availableTrees.add(agilite);
    }
}

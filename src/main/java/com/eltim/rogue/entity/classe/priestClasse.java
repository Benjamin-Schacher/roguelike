package com.eltim.rogue.entity.classe;

import java.util.Arrays;

public class priestClasse extends classe {

    public priestClasse() {
        this.name = "Prêtre";
        this.description = "Dévot des puissances saintes, dispensateur de soins divins et de châtiments sacrés.";

        // ==========================================
        // ARBRE 1 : Soins & Guérison
        // ==========================================
        SkillTree soins = new SkillTree("Soins & Guérison", "Prières restauratrices, soins continus et régénération vitale.", Arrays.asList(
            new Skill("priere_soin", "Prière de Soin",
                "Restaure immédiatement un montant de PV égal à 10 + modificateur de Sagesse.",
                1, "Soins & Guérison").withSound("rpg-battle-system/1"),
            new Skill("benediction_vie", "Bénédiction de Vie",
                "Augmente les PV maximaux de toute l'équipe de +10 pour la durée du combat.",
                2, "Soins & Guérison").withSound("rpg-battle-system/9"),
            new Skill("regeneration_sacree", "Régénération Sacrée",
                "Confère un soin périodique régénérant 4 PV au début de chaque tour pendant 4 tours.",
                3, "Soins & Guérison"),
            new Skill("purge_divine", "Purge Divine",
                "Dissipe tous les poisons, aveuglements et malédictions affectant les alliés.",
                4, "Soins & Guérison").withSound("rpg-battle-system/2"),
            new Skill("resurrection_divine", "Miracle de Résurrection",
                "Ramène à la vie un allié tombé au combat avec 50% de ses PV maximaux.",
                5, "Soins & Guérison").withSound("rpg-battle-system/1")
        ));

        // ==========================================
        // ARBRE 2 : Lumière Sacrée
        // ==========================================
        SkillTree lumiere = new SkillTree("Lumière Sacrée", "Faisceaux de lumière céleste et anéantissement des forces du mal.", Arrays.asList(
            new Skill("eclat_sacre", "Éclat Sacré",
                "Projette un flash radiant infligeant des dégâts sacrés et éblouissant la cible.",
                1, "Lumière Sacrée").withSound("rpg-battle-system/2"),
            new Skill("chatiment_divin", "Châtiment Divin",
                "Frappe une cible avec la foudre céleste : dégâts doublés contre morts-vivants et démons.",
                2, "Lumière Sacrée").withSound("vrac/boule-de-feu"),
            new Skill("rayon_celeste", "Rayon Céleste",
                "Une colonne de clarté pure qui foudroie un ennemi tout en soignant les alliés proches.",
                3, "Lumière Sacrée").withSound("rpg-battle-system/3"),
            new Skill("jugement_foi", "Jugement de Foi",
                "Condamne l'adversaire : chacune de ses actions lui inflige des dégâts de retour sacrés.",
                4, "Lumière Sacrée"),
            new Skill("colere_dieux", "Colère des Dieux",
                "Une explosion radieuse embrase tout le camp ennemi d'un feu divin destructeur.",
                5, "Lumière Sacrée").withSound("western-fps-2d/explosion-3")
        ));

        // ==========================================
        // ARBRE 3 : Protection Divine
        // ==========================================
        SkillTree protection = new SkillTree("Protection Divine", "Auras protectrices, sanctuaire et résistance mystique.", Arrays.asList(
            new Skill("bouclier_foi", "Bouclier de Foi",
                "Augmente la Défense magique et physique du prêtre d'un bonus égal à sa Sagesse.",
                1, "Protection Divine").withSound("rpg-battle-system/9"),
            new Skill("sanctuaire", "Sanctuaire Protecteur",
                "Entoure le groupe d'un dôme sacré réduisant de 3 tous les dégâts subis.",
                2, "Protection Divine"),
            new Skill("barriere_sorts", "Barrière Anti-Magie",
                "Immunise le prêtre contre les sorts magiques ennemis pendant 2 tours.",
                3, "Protection Divine"),
            new Skill("aura_garde", "Aura de Garde",
                "Renvoie 25% des dégâts subis directement à l'assaillant sous forme de dégâts sacrés.",
                4, "Protection Divine"),
            new Skill("invulnerabilite", "Invulnérabilité Temporaire",
                "Le prêtre devient absolument insensible à tout dégât pendant un tour entier.",
                5, "Protection Divine").withSound("rpg-battle-system/9")
        ));

        // ==========================================
        // ARBRE 4 : Zèle & Ferveur
        // ==========================================
        SkillTree zele = new SkillTree("Zèle & Ferveur", "Inspirations martiales, régénération de mana et transe sainte.", Arrays.asList(
            new Skill("chant_courage", "Chant de Courage",
                "Augmente l'attaque et les jets de toucher de toute l'équipe de +2.",
                1, "Zèle & Ferveur").withSound("vrac/hurlemenent-de-rage"),
            new Skill("inspiration_sacree", "Inspiration Sacrée",
                "Régénère 3 points de mana pour chaque membre de l'équipe au début du combat.",
                2, "Zèle & Ferveur"),
            new Skill("frappe_benie", "Frappe Bénie",
                "Vos attaques physiques sont bénies et ajoutent votre Sagesse aux dégâts infligés.",
                3, "Zèle & Ferveur").withSound("vrac/coup-metalique"),
            new Skill("marque_martyr", "Marque du Martyr",
                "Quand un allié subit des dégâts critiques, vous encaissez la moitié à sa place.",
                4, "Zèle & Ferveur"),
            new Skill("avatar_divin", "Avatar Divin",
                "Incarnation vivante de votre divinité : tous vos soins et dégâts sont multipliés par deux pendant 3 tours.",
                5, "Zèle & Ferveur").withSound("rpg-battle-system/3")
        ));

        this.availableTrees.add(soins);
        this.availableTrees.add(lumiere);
        this.availableTrees.add(protection);
        this.availableTrees.add(zele);
    }
}

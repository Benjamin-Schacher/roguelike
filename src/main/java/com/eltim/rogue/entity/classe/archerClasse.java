package com.eltim.rogue.entity.classe;

import java.util.Arrays;

public class archerClasse extends classe {

    public archerClasse() {
        this.name = "Archer";
        this.description = "Tireur d'élite virtuose de l'arc, maître de la traque et des flèches spécialisées.";

        // ==========================================
        // ARBRE 1 : Tir de Précision
        // ==========================================
        SkillTree precision = new SkillTree("Tir de Précision", "Précision chirurgicale à longue portée et coups critiques fatals.", Arrays.asList(
            new Skill("visee_calme", "Visée Calme",
                "Vous ajoutez votre modificateur d'Agilité à tous vos jets de toucher à l'arc ou l'arbalète.",
                1, "Tir de Précision").withSound("vrac/arc"),
            new Skill("tir_mille", "Tir dans le Mille",
                "Vos tirs à distance infligent un coup critique sur un résultat naturel de 19 ou 20.",
                2, "Tir de Précision").withSound("medieval-fantasy/woosh-1"),
            new Skill("fleche_perforante", "Flèche Perforante",
                "Vos flèches percent les armures les plus épaisses en ignorant 50% de la défense adverse.",
                3, "Tir de Précision"),
            new Skill("tir_destabilisant", "Tir Déstabilisant",
                "Blesse la cible au membre inférieur : son esquive et sa vitesse sont divisées par deux.",
                4, "Tir de Précision"),
            new Skill("tir_mortel", "Tir Mortel",
                "Vos coups critiques à distance infligent le triple des dégâts normaux.",
                5, "Tir de Précision").withSound("vrac/Crossbow")
        ));

        // ==========================================
        // ARBRE 2 : Tir Rapide
        // ==========================================
        SkillTree tirRapide = new SkillTree("Tir Rapide", "Cadence effrénée, flèches multiples et déluges de projectiles.", Arrays.asList(
            new Skill("cadence", "Cadence de Tir",
                "Vous gagnez +2 aux dégâts de base avec toutes les armes à distance.",
                1, "Tir Rapide").withSound("vrac/arc"),
            new Skill("double_fleche", "Flèche Double",
                "Décochez deux flèches successives en une unique action.",
                2, "Tir Rapide").withSound("vrac/arc"),
            new Skill("salve_ailee", "Salve Ailée",
                "Tire une volée de flèches touchant jusqu'à 3 cibles distinctes.",
                3, "Tir Rapide").withSound("medieval-fantasy/woosh-2"),
            new Skill("tir_reflexe", "Tir Réflexe",
                "Décoche automatiquement une flèche de riposte lorsqu'un ennemi s'approche au contact.",
                4, "Tir Rapide"),
            new Skill("pluie_fleches", "Pluie de Flèches",
                "Inonde le camp ennemi d'un ouragan de flèches infligeant de lourds dégâts de zone.",
                5, "Tir Rapide").withSound("vrac/Crossbow")
        ));

        // ==========================================
        // ARBRE 3 : Traqueur & Survie
        // ==========================================
        SkillTree traqueur = new SkillTree("Traqueur & Survie", "Connaissance du terrain, détection des dangers et pièges mortels.", Arrays.asList(
            new Skill("sens_affutes", "Sens Affûtés",
                "Ajoutez +2 permanent à votre Agilité et devenez insensible aux effets d'aveuglement.",
                1, "Traqueur & Survie"),
            new Skill("camouflage_naturel", "Camouflage Naturel",
                "Vous vous fondez dans votre environnement : votre Défense augmente de +3 contre les attaques à distance.",
                2, "Traqueur & Survie"),
            new Skill("piege_loup", "Piège à Mâchoires",
                "Pose un piège immobilisant et blessant le premier ennemi de la ligne de front.",
                3, "Traqueur & Survie").withSound("prehistoric-platformer/hit-2"),
            new Skill("pister_gibier", "Pister l'Ennemi",
                "Vous infligez +4 dégâts additionnels contre les bêtes sauvages et monstres déjà blessés.",
                4, "Traqueur & Survie"),
            new Skill("maitre_traqueur", "Maître Traqueur",
                "Vous commencez chaque combat avec l'initiative absolue et un bonus d'esquive de 20%.",
                5, "Traqueur & Survie").withSound("rpg-battle-system/9")
        ));

        // ==========================================
        // ARBRE 4 : Flèches Spéciales
        // ==========================================
        SkillTree flechesSpe = new SkillTree("Flèches Spéciales", "Flèches artisanales aux effets élémentaires et affaiblissants.", Arrays.asList(
            new Skill("fleche_enflammee", "Flèche Enflammée",
                "Vos flèches brûlent la cible, infligeant des dégâts de feu périodiques pendant 3 tours.",
                1, "Flèches Spéciales").withSound("vrac/boule-de-feu"),
            new Skill("fleche_barbelee", "Flèche Barbelée",
                "Une pointe crochetée qui arrache les chairs et provoque une grave hémorragie.",
                2, "Flèches Spéciales").withSound("medieval-fantasy/5"),
            new Skill("fleche_cryo", "Flèche Cryogénique",
                "Imprégnée de givre, cette flèche ralentit l'ennemi et réduit son attaque.",
                3, "Flèches Spéciales").withSound("vrac/Verre-casser"),
            new Skill("fleche_explosive", "Flèche Explosive",
                "Une charge pyrotechnique explose à l'impact, blessant tous les monstres regroupés.",
                4, "Flèches Spéciales").withSound("western-fps-2d/explosion-1"),
            new Skill("fleche_spectrale", "Flèche Spectrale",
                "Flèche éthérée qui traverse la matière et frappe directement les organes vitaux avec 100% de précision.",
                5, "Flèches Spéciales").withSound("rpg-battle-system/3")
        ));

        this.availableTrees.add(precision);
        this.availableTrees.add(tirRapide);
        this.availableTrees.add(traqueur);
        this.availableTrees.add(flechesSpe);
    }
}

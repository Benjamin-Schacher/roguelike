package com.eltim.rogue.entity.classe;

import java.util.Arrays;

public class mageClasse extends classe {

    public mageClasse() {
        this.name = "Mage";
        this.description = "Érudit des arts mystiques, manipulant les éléments dévastateurs et le mana pur.";

        // ==========================================
        // ARBRE 1 : Évocation Pyrique
        // ==========================================
        SkillTree pyromancie = new SkillTree("Évocation Pyrique", "Magie du feu dévastatrice et dégâts continus par brûlure.", Arrays.asList(
            new Skill("flammes_vives", "Flammes Vives",
                "Vos attaques magiques de feu infligent +2 dégâts supplémentaires par modificateur d'Intelligence.",
                1, "Évocation Pyrique").withSound("vrac/boule-de-feu"),
            new Skill("boule_de_feu", "Boule de Feu",
                "Projette un globe ardent explosant sur la première ligne adverse.",
                2, "Évocation Pyrique").withSound("vrac/boule-de-feu"),
            new Skill("brulure_persistante", "Brûlure Persistante",
                "Tous vos sorts de feu consument la cible, infligeant 4 dégâts par tour pendant 3 tours.",
                3, "Évocation Pyrique"),
            new Skill("fournaise", "Fournaise Intérieure",
                "Votre chaleur magique convertit la moitié de votre Sagesse en bonus direct aux dégâts magiques.",
                4, "Évocation Pyrique"),
            new Skill("meteore", "Météore Cataclysmique",
                "Fait s'abattre une comète stellaire incinérant tous les monstres sur le champ de bataille.",
                5, "Évocation Pyrique").withSound("western-fps-2d/explosion-2")
        ));

        // ==========================================
        // ARBRE 2 : Cryomancie
        // ==========================================
        SkillTree cryomancie = new SkillTree("Cryomancie", "Sorts de glace glacials, contrôle des ennemis et armure gelée.", Arrays.asList(
            new Skill("eclat_givre", "Éclat de Givre",
                "Projette un pieu de givre infligeant des dégâts et réduisant la vitesse adverse.",
                1, "Cryomancie").withSound("rpg-battle-system/2"),
            new Skill("armure_glace", "Armure de Glace",
                "Entoure le mage d'une carapace de givre augmentant la Défense de +4 et blessant les attaquants au corps à corps.",
                2, "Cryomancie").withSound("rpg-battle-system/9"),
            new Skill("nova_glace", "Nova de Glace",
                "Une onde glaciale gèle les ennemis proches, les empêchant d'attaquer pendant 1 tour.",
                3, "Cryomancie").withSound("vrac/Verre-casser"),
            new Skill("coeur_polaire", "Cœur Polaire",
                "Résistance au froid accrue de 50% et coût en mana de tous les sorts réduit de 1 (minimum 1).",
                4, "Cryomancie"),
            new Skill("zero_absolu", "Zéro Absolu",
                "Emprisonne un adversaire dans un cercueil de glace pure : il est gelé pendant 2 tours et subit le double de dégâts.",
                5, "Cryomancie").withSound("rpg-battle-system/3")
        ));

        // ==========================================
        // ARBRE 3 : Foudre & Énergie
        // ==========================================
        SkillTree foudre = new SkillTree("Foudre & Énergie", "Décharges voltaïques instantanées, rebonds et paralysie.", Arrays.asList(
            new Skill("etincelle", "Étincelle Vive",
                "Une vive décharge électrique ignorant l'armure de la cible.",
                1, "Foudre & Énergie").withSound("vrac/arc"),
            new Skill("eclair_chaine", "Éclair en Chaîne",
                "Foudroie une cible puis ricoche avec violence sur 2 ennemis adjacents.",
                2, "Foudre & Énergie").withSound("rpg-battle-system/3"),
            new Skill("surcharge", "Surcharge Statique",
                "Les attaques électriques ont 30% de chance d'interrompre et paralyser la cible.",
                3, "Foudre & Énergie"),
            new Skill("champ_magnetique", "Champ Magnétique",
                "Crée une bulle d'énergie déviant automatiquement les flèches et tirs à distance.",
                4, "Foudre & Énergie"),
            new Skill("orage_perpetuel", "Orage Perpétuel",
                "Invoque une tempête d'éclairs s'abattant automatiquement sur un ennemi à chaque tour de combat.",
                5, "Foudre & Énergie").withSound("western-fps-2d/explosion-1")
        ));

        // ==========================================
        // ARBRE 4 : Arcanes & Métamagie
        // ==========================================
        SkillTree arcanes = new SkillTree("Arcanes & Métamagie", "Perfectionnement du mana, absorption d'énergie et distorsions cosmiques.", Arrays.asList(
            new Skill("meditation_arcane", "Méditation des Arcanes",
                "Ajoutez +2 permanent à votre Intelligence et augmentez votre Mana maximal de 10.",
                1, "Arcanes & Métamagie"),
            new Skill("bouclier_mana", "Bouclier de Mana",
                "50% des dégâts que vous subissez sont prélevés sur votre réserve de mana au lieu de vos PV.",
                2, "Arcanes & Métamagie").withSound("rpg-battle-system/9"),
            new Skill("siphon_arcane", "Siphon Arcanique",
                "Vos incantations drainent du mana à leurs cibles, rechargeant vos propres réserves.",
                3, "Arcanes & Métamagie"),
            new Skill("distorsion_temps", "Distorsion Temporelle",
                "Une fois par combat, courbez le temps pour rejouer immédiatement un tour complet.",
                4, "Arcanes & Métamagie"),
            new Skill("singularite", "Singularité Cosmique",
                "Détruit tous les buffs défensifs adverses et inflige d'immenses dégâts purs et inévitables.",
                5, "Arcanes & Métamagie").withSound("western-fps-2d/explosion-3")
        ));

        this.availableTrees.add(pyromancie);
        this.availableTrees.add(cryomancie);
        this.availableTrees.add(foudre);
        this.availableTrees.add(arcanes);
    }
}

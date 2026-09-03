package com.eltim.rogue.entity.classe;

import java.util.Arrays;

public class warriorClasse extends classe {

    public warriorClasse() {
        this.name = "Guerrier";
        this.description = "Maître du combat physique, le Guerrier excelle au corps à corps.";

        // ==========================================
        // ARBRE 1 : Maître d'Arme
        // ==========================================
        SkillTree maitreArme = new SkillTree("Maître d'Arme", Arrays.asList(
            new Skill("choisir_arme", "Choisir son Arme", 
                "Choisissez une maîtrise sur un type d'arme. Vous pouvez relancer votre jet de toucher avec ce type d'arme une fois par tour.", 
                1, "Maître d'Arme"),
            new Skill("habilete_exceptionnelle", "Habileté Exceptionnelle", 
                "Vous ne pouvez pas faire d'échec critique avec votre arme maîtrisée. Un 19 ou 20 est considéré comme un coup critique.", 
                2, "Maître d'Arme"),
            new Skill("sans_defaut", "Sans Défaut", 
                "Vous ignorez tout malus au jet de toucher. Votre attaque est toujours un d20 pur.", 
                3, "Maître d'Arme"),
            new Skill("savoir_martial", "Savoir Martial", 
                "Vous ajoutez votre modificateur de Sagesse aux dégâts de l'arme maîtrisée (si positif).", 
                4, "Maître d'Arme"),
            new Skill("maitrise_totale", "Maîtrise Totale", 
                "Vous ignorez la contrainte des deux mains pour manier les armes de votre type maîtrisé.", 
                5, "Maître d'Arme")
        ));

        // ==========================================
        // ARBRE 2 : Bête de Guerre
        // ==========================================
        SkillTree beteGuerre = new SkillTree("Bête de Guerre", Arrays.asList(
            new Skill("constitution_except", "Constitution Exceptionnelle", 
                "Ajoutez +2 à votre Constitution et gagnez 10 points de vie supplémentaires.", 
                1, "Bête de Guerre"),
            new Skill("resist_poison", "Résistance au Poison", 
                "Les dégâts de poison que vous recevez sont divisés par 2.", 
                2, "Bête de Guerre"),
            new Skill("combat_main_nue", "Combat à Main Nue", 
                "Ajoutez votre Constitution aux dégâts quand vous frappez à mains nues (si positive).", 
                3, "Bête de Guerre"),
            new Skill("second_souffle", "Second Souffle", 
                "La première fois par combat que vous tombez à 0 PV, vous regagnez un nombre de PV égal à votre modificateur de Constitution.", 
                4, "Bête de Guerre"),
            new Skill("resist_elementaire", "Résistance Élémentaire", 
                "Les dégâts élémentaires (feu, froid, foudre) que vous subissez sont divisés par 2.", 
                5, "Bête de Guerre")
        ));

        // ==========================================
        // ARBRE 3 : Technique de Combat
        // ==========================================
        SkillTree technique = new SkillTree("Technique Combat", Arrays.asList(
            new Skill("jet_sable", "Jet de Sable", 
                "L'adversaire touché subit -2 pour toucher pendant 1d4 + points investis dans cet arbre tours.", 
                1, "Technique Combat").withSound("rpg-battle-system/2"),
            new Skill("posture_defensive", "Posture Défensive", 
                "Gagnez un bonus à la Défense égal à votre Sagesse (si positive) pendant autant de tours que de points dans cet arbre.", 
                2, "Technique Combat").withSound("rpg-battle-system/9"),
            new Skill("attaque_ampleur", "Attaque d'Ampleur", 
                "Balayez la première ligne ennemie accessible et frappez tous les adversaires présents. Ils font un jet de Dextérité (DC12) pour diviser les dégâts par 2.", 
                3, "Technique Combat").withSound("medieval-fantasy/woosh-2"),
            new Skill("riposte", "Riposte", 
                "Quand un ennemi vous attaque, vous le frappez immédiatement avec l'arme en main droite.", 
                4, "Technique Combat"),
            new Skill("etourdissement", "Étourdissement", 
                "L'adversaire ciblé passe son prochain tour.", 
                5, "Technique Combat").withSound("prehistoric-platformer/wood-4")
        ));

        // ==========================================
        // ARBRE 4 : Berzerker
        // ==========================================
        SkillTree berzerker = new SkillTree("Berzerker", Arrays.asList(
            new Skill("vengeance", "Vengeance !", 
                "Quand un adversaire vous fait perdre des PV, vous avez un avantage sur votre prochaine attaque contre lui (lancez 2d20, prenez le meilleur).", 
                1, "Berzerker"),
            new Skill("hurlement_guerrier", "Hurlement de Guerrier", 
                "Vous et vos équipiers gagnez un bonus d'attaque égal à votre modificateur de Force pendant un nombre de tours égal aux points dans cet arbre.", 
                2, "Berzerker").withSound("vrac/hurlemenent-de-rage"),
            new Skill("frenesie_meurtriere", "Frénésie Meurtrière", 
                "Quand vous tuez un adversaire, vous gagnez +1 au toucher et aux dégâts par ennemi abattu (marche aussi sur les invocations).", 
                3, "Berzerker"),
            new Skill("hurlement_provocation", "Hurlement de Provocation", 
                "Pendant 1d4 tours, les adversaires vous attaquent en priorité.", 
                4, "Berzerker").withSound("vrac/hurlemenent-de-rage"),
            new Skill("double_attaque", "Double Attaque", 
                "Vous obtenez une action supplémentaire par tour pour attaquer.", 
                5, "Berzerker")
        ));

        this.availableTrees.add(maitreArme);
        this.availableTrees.add(beteGuerre);
        this.availableTrees.add(technique);
        this.availableTrees.add(berzerker);
    }
}

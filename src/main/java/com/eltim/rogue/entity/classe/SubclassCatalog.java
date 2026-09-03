package com.eltim.rogue.entity.classe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubclassCatalog {

    private static final List<Subclass> ALL_SUBCLASSES = new ArrayList<>();

    static {
        // 1. Mage-lame (Mage + Guerrier)
        SkillTree lameEnchantee = new SkillTree("Lame Enchanteresse", "Combat au corps à corps infusé d'arcanes et enchantements élémentaires.", Arrays.asList(
            new Skill("arme_enchantee", "Arme Enchantée", "Vos attaques au corps à corps infligent +3 dégâts élémentaires supplémentaires.", 1, "Lame Enchanteresse").withSound("vrac/boule-de-feu"),
            new Skill("frappe_arcanique", "Frappe Arcanique", "Frappe physique imprégnée d'arcanes réduisant la résistance adverse.", 2, "Lame Enchanteresse").withSound("vrac/coup-metalique"),
            new Skill("bouclier_elementaire", "Bouclier Élémentaire", "Convertit 25% des dégâts subis en mana.", 3, "Lame Enchanteresse"),
            new Skill("lame_runique", "Lame Runique", "Vos coups au corps à corps régénèrent 2 PV et 2 MP à l'impact.", 4, "Lame Enchanteresse"),
            new Skill("tempete_acier_magique", "Tempête d'Acier Magique", "Projette une tornade de lames spectrales balayant tous les ennemis.", 5, "Lame Enchanteresse").withSound("medieval-fantasy/woosh-2")
        ));
        SkillTree canalisation = new SkillTree("Canalisation Martiale", "Canalisation du savoir ésotérique pour accroître la puissance martiale.", Arrays.asList(
            new Skill("posture_concentree", "Posture Concentrée", "Ajoutez votre Intelligence aux jets de toucher au corps à corps.", 1, "Canalisation Martiale"),
            new Skill("riposte_electrique", "Riposte Voltaïque", "Contre-attaque automatiquement avec un éclair foudroyant sur esquive ou parade.", 2, "Canalisation Martiale").withSound("vrac/arc"),
            new Skill("armure_de_force", "Armure de Force", "Ajoutez votre modificateur d'Intelligence à votre score de Défense.", 3, "Canalisation Martiale"),
            new Skill("pas_dimensionnel", "Pas Dimensionnel", "Téléportation fulgurante derrière un ennemi conférant un critique garanti.", 4, "Canalisation Martiale").withSound("rpg-battle-system/9"),
            new Skill("avatar_arcanique", "Avatar Arcanique", "Double tous vos dégâts physiques et magiques pendant 3 tours.", 5, "Canalisation Martiale").withSound("rpg-battle-system/3")
        ));
        ALL_SUBCLASSES.add(new Subclass("Mage-lame", "Mage", "Guerrier",
            "Guerrier-mage combinant la robustesse martiale aux sorts offensifs dévastateurs.",
            Arrays.asList(lameEnchantee, canalisation)));

        // 2. Rodeur (Voleur + Archer)
        SkillTree embuscade = new SkillTree("Tir d'Embuscade", "Frappes surprises à distance et éliminations furtives.", Arrays.asList(
            new Skill("premier_coup", "Premier Sang", "Inflige +8 dégâts si vous attaquez en premier au combat.", 1, "Tir d'Embuscade").withSound("vrac/arc"),
            new Skill("fleche_silencieuse", "Flèche Silencieuse", "Tir furtif infligeant un critique si la cible n'a pas encore agi.", 2, "Tir d'Embuscade").withSound("medieval-fantasy/woosh-1"),
            new Skill("visee_ombre", "Visée d'Ombre", "Votre seuil de coup critique à distance passe à 18-20.", 3, "Tir d'Embuscade"),
            new Skill("tir_destabilisant_r", "Tir Énervant", "Réduit l'attaque de la cible de 4 et divise sa vitesse par deux.", 4, "Tir d'Embuscade"),
            new Skill("fleche_fantome", "Flèche Fantôme", "Flèche spectrale ignorant totalement l'armure pour infliger des dégâts purs.", 5, "Tir d'Embuscade").withSound("vrac/Crossbow")
        ));
        SkillTree compagnon = new SkillTree("Compagnon Sylvestre", "Harmonie avec la faune et maîtrise des bêtes.", Arrays.asList(
            new Skill("oeil_du_faucon", "Œil du Faucon", "Augmente la précision de toute l'équipe de +3.", 1, "Compagnon Sylvestre"),
            new Skill("savoir_bestial", "Savoir Bestial", "Inflige +4 dégâts additionnels contre les bêtes et humanoïdes.", 2, "Compagnon Sylvestre"),
            new Skill("piege_repulsif", "Piège Répulsif", "Immobilise et empoisonne la première ligne ennemie.", 3, "Compagnon Sylvestre").withSound("prehistoric-platformer/hit-2"),
            new Skill("instinct_survie", "Instinct de Survie", "Esquive automatique garantie de la première attaque ennemie subie.", 4, "Compagnon Sylvestre"),
            new Skill("loup_spectral", "Loup Spectral", "Invoque un loup spectral pour combattre férocement à vos côtés.", 5, "Compagnon Sylvestre").withSound("vrac/hurlemenent-de-rage")
        ));
        ALL_SUBCLASSES.add(new Subclass("Rodeur", "Voleur", "Archer",
            "Chasseur des étendues sauvages combinant discrétion, tir d'élite et pièges retors.",
            Arrays.asList(embuscade, compagnon)));

        // 3. Paladin (Prêtre + Guerrier)
        SkillTree croisade = new SkillTree("Croisade Vindicative", "Châtiments lumineux et frappes sacrées destructrices.", Arrays.asList(
            new Skill("chatiment_sacre", "Châtiment Sacré", "Frappe au corps à corps ajoutant votre Sagesse en dégâts radieux.", 1, "Croisade Vindicative").withSound("vrac/coup-metalique"),
            new Skill("jugement_divin", "Jugement Divin", "Marque la cible : toutes les attaques alliées lui infligent +3 dégâts.", 2, "Croisade Vindicative").withSound("rpg-battle-system/2"),
            new Skill("zele_inflexible", "Zèle Inflexible", "Immunité absolue à l'étourdissement, à la peur et au poison.", 3, "Croisade Vindicative"),
            new Skill("vengeance_sainte", "Vengeance Sainte", "Restaure 8 PV au paladin chaque fois qu'il assène un coup critique.", 4, "Croisade Vindicative"),
            new Skill("lame_du_soleil", "Lame du Soleil", "Fend les rangs ennemis d'un éclair de lumière aveuglant les monstres.", 5, "Croisade Vindicative").withSound("rpg-battle-system/3")
        ));
        SkillTree auraProt = new SkillTree("Aura Protectrice", "Bastion inexpugnable protégeant ses alliés au péril de sa vie.", Arrays.asList(
            new Skill("aura_devotion", "Aura de Dévotion", "Confère +3 en Défense physique et magique à tous les alliés.", 1, "Aura Protectrice"),
            new Skill("imposition_mains", "Imposition des Mains", "Soigne instantanément un allié d'un montant massif de 20 PV.", 2, "Aura Protectrice").withSound("rpg-battle-system/1"),
            new Skill("bastion_foi", "Bastion de Foi", "Réduit tous les dégâts subis par le groupe de 4 points.", 3, "Aura Protectrice"),
            new Skill("bouclier_miroir", "Bouclier Miroir", "Renvoie 30% des dégâts magiques subis au lanceur ennemi.", 4, "Aura Protectrice"),
            new Skill("ange_gardien", "Ange Gardien", "Empêche un allié de mourir et le ressuscite instantanément à 50% PV.", 5, "Aura Protectrice").withSound("rpg-battle-system/9")
        ));
        ALL_SUBCLASSES.add(new Subclass("Paladin", "Prêtre", "Guerrier",
            "Chevalier saint voué à la défense des justes et au châtiment du mal.",
            Arrays.asList(croisade, auraProt)));

        // 4. Moine (Voleur + Prêtre)
        SkillTree poingSacre = new SkillTree("Voie du Poing Sacré", "Combats à mains nues canalisant l'énergie divine du Ki.", Arrays.asList(
            new Skill("poing_de_fer", "Poing de Fer", "Vos attaques à mains nues infligent des dégâts sacrés supplémentaires.", 1, "Voie du Poing Sacré").withSound("prehistoric-platformer/hit-2"),
            new Skill("frappe_ki", "Frappe du Ki", "Interrompt l'incantation adverse et déstabilise l'adversaire.", 2, "Voie du Poing Sacré").withSound("medieval-fantasy/5"),
            new Skill("paume_energetique", "Paume Énergétique", "Projette une onde de choc magique repoussant la première ligne.", 3, "Voie du Poing Sacré"),
            new Skill("cent_coups", "Cent Coups Fulgurants", "Enchaîne 3 frappes consécutives à mains nues en une seule action.", 4, "Voie du Poing Sacré").withSound("medieval-fantasy/woosh-1"),
            new Skill("transcendance_martiale", "Transcendance Martiale", "Immunité à toute altération et 2 actions par tour.", 5, "Voie du Poing Sacré").withSound("rpg-battle-system/9")
        ));
        SkillTree meditation = new SkillTree("Méditation & Équilibre", "Sérénité intérieure et fluidité absolue des mouvements.", Arrays.asList(
            new Skill("clarte_esprit", "Clarté de l'Esprit", "Régénère 2 points de mana au début de chaque tour de jeu.", 1, "Méditation & Équilibre"),
            new Skill("esquive_mystique", "Esquive Mystique", "Ajoutez votre Sagesse à votre valeur d'esquive physique.", 2, "Méditation & Équilibre"),
            new Skill("toucher_guerisseur", "Toucher Guérisseur", "Soigne 12 PV et dissipe tous les poisons affectant un allié.", 3, "Méditation & Équilibre").withSound("rpg-battle-system/1"),
            new Skill("corps_inalterable", "Corps Inaltérable", "Réduit tous les dégâts subis de 25% de manière passive.", 4, "Méditation & Équilibre"),
            new Skill("serenite_totale", "Sérénité Totale", "Annule entièrement toutes les attaques ennemies pendant un tour entier.", 5, "Méditation & Équilibre").withSound("rpg-battle-system/9")
        ));
        ALL_SUBCLASSES.add(new Subclass("Moine", "Voleur", "Prêtre",
            "Guerrier spirituel alliant l'agilité martiale aux miracles intérieurs.",
            Arrays.asList(poingSacre, meditation)));

        // 5. Artificier (Archer + Mage)
        SkillTree munitions = new SkillTree("Munitions Runiques", "Projectiles magiques gravés de runes alchimiques destructrices.", Arrays.asList(
            new Skill("fleche_etincelle", "Flèche d'Étincelles", "Flèche magique foudroyant la cible et les ennemis adjacents.", 1, "Munitions Runiques").withSound("vrac/arc"),
            new Skill("cartouche_incendiaire", "Cartouche Incendiaire", "Balle ardente enflammant la zone d'impact pour 3 tours.", 2, "Munitions Runiques").withSound("vrac/boule-de-feu"),
            new Skill("fleche_fusion", "Flèche à Fusion", "Dissout l'armure de la cible, réduisant sa Défense à 0 pour 2 tours.", 3, "Munitions Runiques"),
            new Skill("trait_cryo_explosif", "Trait Cryo-Explosif", "Gèle la cible qui explose au tour suivant en projetant des éclats de glace.", 4, "Munitions Runiques").withSound("vrac/Verre-casser"),
            new Skill("salve_elementaire", "Salve Élémentaire Cataclysmique", "Tire 4 projectiles élémentaires combinés (feu, givre, foudre, arcanes).", 5, "Munitions Runiques").withSound("western-fps-2d/explosion-2")
        ));
        SkillTree gadgets = new SkillTree("Gadgets & Alchimie", "Inventions technologiques et alchimie de pointe.", Arrays.asList(
            new Skill("tourelle_auto", "Tourelle Déployable", "Déploie une baliste automatique tirant sur un ennemi à chaque tour.", 1, "Gadgets & Alchimie").withSound("vrac/Crossbow"),
            new Skill("grenade_flash", "Grenade Aveuglante", "Étourdit et aveugle tous les monstres en première ligne.", 2, "Gadgets & Alchimie").withSound("western-fps-2d/explosion-1"),
            new Skill("poudre_instable", "Poudre Instable", "Augmente les dégâts de tous les objets explosifs de 50%.", 3, "Gadgets & Alchimie"),
            new Skill("distillateur", "Distillateur Alchimique", "Double l'efficacité de toutes les potions consommées en combat.", 4, "Gadgets & Alchimie"),
            new Skill("golem_mecanique", "Automate de Combat", "Invoque un puissant golem mécanique blindé pour encaisser et frapper.", 5, "Gadgets & Alchimie").withSound("vrac/coup-metalique")
        ));
        ALL_SUBCLASSES.add(new Subclass("Artificier", "Archer", "Mage",
            "Ingénieur de génie combinant balistique de précision et sorcellerie alchimique.",
            Arrays.asList(munitions, gadgets)));

        // 6. Chamane (Mage + Prêtre)
        SkillTree tellurique = new SkillTree("Éléments Primitifs", "Pouvoirs bruts de la terre, des cieux et des cataclysmes naturels.", Arrays.asList(
            new Skill("decharge_tellurique", "Décharge Tellurique", "Soulève la roche pour blesser et ralentir l'adversaire.", 1, "Éléments Primitifs").withSound("prehistoric-platformer/hit-2"),
            new Skill("eclair_guide", "Éclair Guidé", "Sort de foudre mystique touchant sa cible à coup sûr.", 2, "Éléments Primitifs").withSound("vrac/arc"),
            new Skill("vague_purificatrice", "Vague Purificatrice", "Soigne tous les alliés de 10 PV et annule les altérations néfastes.", 3, "Éléments Primitifs").withSound("rpg-battle-system/1"),
            new Skill("chaine_eclairs_prim", "Chaîne d'Éclairs Primordiale", "Arc voltaïque sautant de cible en cible avec violence.", 4, "Éléments Primitifs").withSound("rpg-battle-system/3"),
            new Skill("fureur_elements", "Fureur des Éléments", "Déchaîne un séisme combiné à un orage ardent dévastateur.", 5, "Éléments Primitifs").withSound("western-fps-2d/explosion-3")
        ));
        SkillTree totems = new SkillTree("Totems & Esprits", "Évocation de totems sacrés et communion avec les ancêtres.", Arrays.asList(
            new Skill("totem_soin", "Totem de Guérison", "Plante un totem restaurant 4 PV à tous les alliés à chaque tour.", 1, "Totems & Esprits").withSound("rpg-battle-system/9"),
            new Skill("totem_force", "Totem de Force", "Augmente l'attaque de tout le groupe de +3 tant qu'il est actif.", 2, "Totems & Esprits"),
            new Skill("esprit_vent", "Esprit du Vent", "Augmente la vitesse et l'esquive du groupe de 20%.", 3, "Totems & Esprits"),
            new Skill("totem_provoc", "Totem Gardien", "Attire les attaques des monstres sur le totem à la place des héros.", 4, "Totems & Esprits"),
            new Skill("appel_ancetres", "Appel des Ancêtres", "Invoque deux esprits guerriers ancestraux pour combattre à vos côtés.", 5, "Totems & Esprits").withSound("vrac/hurlemenent-de-rage")
        ));
        ALL_SUBCLASSES.add(new Subclass("Chamane", "Mage", "Prêtre",
            "Médiateur mystique des éléments primordiaux et des esprits tutélaires.",
            Arrays.asList(tellurique, totems)));

        // 7. Druide (Archer + Prêtre)
        SkillTree harmonie = new SkillTree("Harmonie Sauvage", "Force de la nature, lianes vivantes et tempêtes végétales.", Arrays.asList(
            new Skill("ronces_epineuses", "Ronces Épineuses", "Empêtre l'ennemi dans des lianes épineuses le blessant s'il agit.", 1, "Harmonie Sauvage").withSound("prehistoric-platformer/hit-2"),
            new Skill("ecorce_vivante", "Écorce Vivante", "Augmente votre Constitution de +2 et votre Défense de +3.", 2, "Harmonie Sauvage"),
            new Skill("appel_meute", "Appel de la Meute", "Des loups des bois surgissent et attaquent les lignes arrière ennemies.", 3, "Harmonie Sauvage").withSound("vrac/hurlemenent-de-rage"),
            new Skill("rosee_restauratrice", "Rosée Restauratrice", "Soigne 6 PV par tour à l'ensemble du groupe pendant 3 tours.", 4, "Harmonie Sauvage").withSound("rpg-battle-system/1"),
            new Skill("colere_gaia", "Colère de Gaïa", "Séisme colossal renversant et étourdissant l'ensemble des ennemis.", 5, "Harmonie Sauvage").withSound("western-fps-2d/explosion-1")
        ));
        SkillTree metamorphose = new SkillTree("Métamorphose Sylvestre", "Mutation corporelle en créatures féroces de la forêt.", Arrays.asList(
            new Skill("forme_feline", "Forme Féline", "+4 Agilité permanente et +15% de chances de coup critique.", 1, "Métamorphose Sylvestre"),
            new Skill("forme_ours", "Forme d'Ours", "Gagnez +20 PV max et une frappe au corps à corps étourdissante.", 2, "Métamorphose Sylvestre").withSound("vrac/hurlemenent-de-rage"),
            new Skill("forme_arbre", "Forme d'Arbre Ancien", "Aura permanente régénérant PV et MP à toute l'équipe chaque tour.", 3, "Métamorphose Sylvestre").withSound("rpg-battle-system/9"),
            new Skill("griffes_venimeuses", "Griffes Toxiques", "Toutes vos attaques au corps à corps inoculent un venin mortel.", 4, "Métamorphose Sylvestre"),
            new Skill("dragon_sylvestre", "Dragon Sylvestre", "Métamorphose suprême crachant un souffle de vie et de destruction.", 5, "Métamorphose Sylvestre").withSound("vrac/boule-de-feu")
        ));
        ALL_SUBCLASSES.add(new Subclass("Druide", "Archer", "Prêtre",
            "Gardien de la faune et de la flore, guérisseur et bête métamorphe.",
            Arrays.asList(harmonie, metamorphose)));

        // 8. Breteur (Archer + Guerrier)
        SkillTree escrime = new SkillTree("Escrime & Parade", "Duels élégants, frappes millimétrées et contre-attaques fatales.", Arrays.asList(
            new Skill("fente_precise", "Fente Précise", "Attaque perforante à l'épée ignorant 50% de l'armure de la cible.", 1, "Escrime & Parade").withSound("medieval-fantasy/5"),
            new Skill("parade_gracieuse", "Parade Gracieuse", "Bloque la prochaine attaque et riposte avec un bonus de dégâts.", 2, "Escrime & Parade").withSound("vrac/coup-metalique"),
            new Skill("desarmement", "Désarmement", "Prive l'adversaire de son arme : attaque réduite de moitié pendant 2 tours.", 3, "Escrime & Parade"),
            new Skill("botte_secrete", "Botte Secrète", "Coup fatal infligeant 2.5x les dégâts normaux.", 4, "Escrime & Parade").withSound("medieval-fantasy/woosh-1"),
            new Skill("danse_lames", "Danse des Lames", "Enchaîne 4 frappes virevoltantes sur des cibles aléatoires.", 5, "Escrime & Parade").withSound("medieval-fantasy/woosh-2")
        ));
        SkillTree mobilite = new SkillTree("Tactique & Mobilité", "Sens tactique aiguisé et agilité sur le champ de bataille.", Arrays.asList(
            new Skill("pas_de_cote", "Pas de Côté", "Augmente votre esquive d'un montant proportionnel à votre Agilité.", 1, "Tactique & Mobilité"),
            new Skill("provocation_courtoise", "Provocation Courtoise", "Incite l'ennemi à vous attaquer pour mieux le contrer au tour suivant.", 2, "Tactique & Mobilité"),
            new Skill("tir_opportunite", "Tir d'Opportunité", "Décoche un carreau réflexe dès qu'un monstre tente de charger.", 3, "Tactique & Mobilité").withSound("vrac/Crossbow"),
            new Skill("jeu_de_jambes", "Jeu de Jambes", "Vous permet d'attaquer et de vous repositionner sans aucune pénalité.", 4, "Tactique & Mobilité"),
            new Skill("maitre_bretteur", "Maître Bretteur", "Tous vos coups critiques appliquent un saignement et réduisent la défense ennemie.", 5, "Tactique & Mobilité").withSound("medieval-fantasy/5")
        ));
        ALL_SUBCLASSES.add(new Subclass("Breteur", "Archer", "Guerrier",
            "Duelliste intrépide maîtrisant l'acier comme les projectiles rapides.",
            Arrays.asList(escrime, mobilite)));

        // 9. Assassin (Voleur + Guerrier)
        SkillTree execution = new SkillTree("Exécution de l'Ombre", "Frappes ciblées dans les points vitaux et carnage méthodique.", Arrays.asList(
            new Skill("frappe_sournoise_g", "Frappe de l'Ombre", "Dégâts doublés si vous attaquez un ennemi dont c'est le tour d'agir.", 1, "Exécution de l'Ombre").withSound("medieval-fantasy/woosh-1"),
            new Skill("poison_neuro", "Poison Neurotoxique", "Paralyse partiellement la cible et lui inflige 4 dégâts par tour.", 2, "Exécution de l'Ombre").withSound("vrac/Verre-casser"),
            new Skill("evisceration", "Éviscération", "Attaque sanglante infligeant des dégâts proportionnels aux PV manquants de la cible.", 3, "Exécution de l'Ombre").withSound("vrac/coup-metalique"),
            new Skill("frappe_mortelle", "Frappe Mortelle", "Chance d'éliminer sur le coup un monstre sous 30% de ses PV.", 4, "Exécution de l'Ombre"),
            new Skill("danse_macabre", "Danse Macabre", "Frappe mortelle en chaîne : si elle tue la cible, se répète sur la suivante.", 5, "Exécution de l'Ombre").withSound("medieval-fantasy/woosh-2")
        ));
        SkillTree tueur = new SkillTree("Art du Tueur", "Expertise morbide dans l'art de donner la mort sans un bruit.", Arrays.asList(
            new Skill("maitrise_armes_furtives", "Armes Furtives", "Bonus de +3 aux dégâts avec dagues, épées courtes et arbalètes.", 1, "Art du Tueur"),
            new Skill("bombe_aveuglante", "Bombe Fumigène Noire", "Aveugle complètement le camp ennemi pendant 2 tours.", 2, "Art du Tueur").withSound("rpg-battle-system/37"),
            new Skill("precision_chirurgicale", "Précision Chirurgicale", "Votre seuil de coup critique descend à 18-20 sur toutes les armes.", 3, "Art du Tueur"),
            new Skill("soif_de_sang", "Soif de Sang", "Chaque ennemi abattu régénère 15 PV et accorde une action immédiate.", 4, "Art du Tueur"),
            new Skill("ombre_insaisissable", "Ombre Insaisissable", "50% de chance d'ignorer purement et simplement toute attaque subie.", 5, "Art du Tueur").withSound("rpg-battle-system/9")
        ));
        ALL_SUBCLASSES.add(new Subclass("Assassin", "Voleur", "Guerrier",
            "Machine à tuer impitoyable frappant depuis les ténèbres.",
            Arrays.asList(execution, tueur)));

        // 10. Occulteur (Mage + Voleur)
        SkillTree magieNoire = new SkillTree("Magie Noire & Corruption", "Arts interdits, malédictions funestes et vampirisme.", Arrays.asList(
            new Skill("toucher_vampirique", "Toucher Vampirique", "Draine 8 PV à la cible pour soigner directement l'occulteur.", 1, "Magie Noire & Corruption").withSound("rpg-battle-system/1"),
            new Skill("malediction_faiblesse", "Malédiction d'Affaiblissement", "Réduit l'attaque et la défense de la cible de 3.", 2, "Magie Noire & Corruption").withSound("rpg-battle-system/3"),
            new Skill("combustion_mana", "Combustion de Mana", "Détruit 5 points de mana à la cible et lui inflige des dégâts équivalents.", 3, "Magie Noire & Corruption"),
            new Skill("liens_ames", "Liens d'Âmes", "50% des dégâts reçus par l'occulteur sont renvoyés à l'ennemi maudit.", 4, "Magie Noire & Corruption"),
            new Skill("moisson_ames", "Moisson des Âmes", "Anéantit une cible et invoque une ombre servile sous vos ordres.", 5, "Magie Noire & Corruption").withSound("western-fps-2d/explosion-3")
        ));
        SkillTree manipulationOmbre = new SkillTree("Manipulation des Ombres", "Tissage des ombres pour s'évanouir et frapper dans l'ombre.", Arrays.asList(
            new Skill("deplacement_ombrique", "Déplacement Ombrique", "Se fond dans le néant : bonus d'esquive de 25% pendant 2 tours.", 1, "Manipulation des Ombres"),
            new Skill("dague_ombre", "Dague d'Ombre", "Projette une dague d'énergie ténébreuse perforant toutes les défenses.", 2, "Manipulation des Ombres").withSound("medieval-fantasy/woosh-1"),
            new Skill("vol_energie", "Vol d'Énergie", "Vole un effet bénéfique sur l'ennemi pour se l'attribuer immédiatement.", 3, "Manipulation des Ombres"),
            new Skill("voile_noir", "Voile Ténébreux", "Plonge le combat dans les ténèbres : les ennemis ratent la moitié de leurs coups.", 4, "Manipulation des Ombres").withSound("rpg-battle-system/37"),
            new Skill("forme_ombre", "Forme d'Ombre Absolue", "Devient éthéré : immunité physique et dégâts magiques augmentés de 50%.", 5, "Manipulation des Ombres").withSound("rpg-battle-system/9")
        ));
        ALL_SUBCLASSES.add(new Subclass("Occulteur", "Mage", "Voleur",
            "Sorcier insaisissable dérobant les secrets arcaniques et manipulant les ombres.",
            Arrays.asList(magieNoire, manipulationOmbre)));
    }

    public static List<Subclass> getAll() {
        return ALL_SUBCLASSES;
    }

    public static List<Subclass> getAvailableFor(classe baseClass) {
        List<Subclass> compatible = new ArrayList<>();
        if (baseClass == null || baseClass.name == null) return compatible;
        for (Subclass sc : ALL_SUBCLASSES) {
            if (sc.isCompatibleWith(baseClass.name)) {
                compatible.add(sc);
            }
        }
        return compatible;
    }
}

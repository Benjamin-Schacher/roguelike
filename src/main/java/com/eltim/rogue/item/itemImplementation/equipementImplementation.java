package com.eltim.rogue.item.itemImplementation;

import com.eltim.rogue.item.potion;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.equipement;
import com.eltim.rogue.item.objectItem;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;
import com.eltim.rogue.item.enumerateur.potionTypeEnum;
import com.eltim.rogue.item.enumerateur.itemQualityTypeEnum;
import com.eltim.rogue.item.enumerateur.weaponTypeEnum;
import com.eltim.rogue.item.enumerateur.itemWeightEnum;
import com.eltim.rogue.item.enumerateur.itemKnowledgeEnum;
import com.eltim.rogue.item.enumerateur.attackeTypeEnum;
import com.eltim.rogue.system.enumarateur.damageTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class equipementImplementation {

    // =========================================
    // 1. ARMES COMMUNES
    // =========================================
    public weapon epee = new weapon("Épée", 1, 6, itemTypeEnum.SWORD, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("medieval-fantasy/9");
    public weapon arc = new weapon("Arc", 1, 6, itemTypeEnum.BOW, itemQualityTypeEnum.COMMON, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.PIERCING, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("vrac/arc");
    public weapon dague = new weapon("Dague", 1, 4, itemTypeEnum.DAGGER, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.PIERCING, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("medieval-fantasy/5");
    public weapon bouclierBois = new weapon("Bouclier en bois", 1, 4, itemTypeEnum.SHIELD, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("prehistoric-platformer/hit-2");
    public weapon baton = new weapon("Baton", 1, 6, itemTypeEnum.STAFF, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("prehistoric-platformer/wood-1");
    public weapon marteau = new weapon("Marteau", 1, 6, itemTypeEnum.HAMMER, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 1).withSound("medieval-fantasy/5");
    public weapon hache2main = new weapon("Hache a deux main", 1, 8, itemTypeEnum.AXE, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.LOURD, itemKnowledgeEnum.COMMUN, 2).withSound("medieval-fantasy/10");
    public weapon hache = new weapon("Hache", 1, 6, itemTypeEnum.AXE, itemQualityTypeEnum.COMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.LEGER, itemKnowledgeEnum.COMMUN, 1).withSound("medieval-fantasy/10");
    public weapon arcLong = new weapon("Arc long", 1, 8, itemTypeEnum.BOW, itemQualityTypeEnum.COMMON, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.PIERCING, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 2).withSound("vrac/arc");

    // =========================================
    // 2. POTIONS COMMUNES
    // =========================================
    public potion LowHealpotion = new potion("Potion de soin mineur", 1, 4, itemTypeEnum.POTION, itemQualityTypeEnum.COMMON, potionTypeEnum.HEAL, 3).withSound("medieval-fantasy/1");
    public potion LowManaPotion = new potion("Potion de mana mineur", 1, 4, itemTypeEnum.POTION, itemQualityTypeEnum.COMMON, potionTypeEnum.MANA, 3).withSound("medieval-fantasy/2");

    // =========================================
    // 3. ÉQUIPEMENTS COMMUNS
    // =========================================
    public equipement bottesCuir = new equipement("Bottes en cuire", 1, itemTypeEnum.FOOT, itemQualityTypeEnum.COMMON, itemWeightEnum.LEGER, 1);
    public equipement robeEnTissu = new equipement("Robe en tissu", 1, itemTypeEnum.ARMOR, itemQualityTypeEnum.COMMON, itemWeightEnum.LEGER, 1);
    public equipement cuissardesCuir = new equipement("Cuissardes en cuire", 1, itemTypeEnum.LEGS, itemQualityTypeEnum.COMMON, itemWeightEnum.MOYEN, 1);
    public equipement gantCuir = new equipement("Gant en cuire", 1, itemTypeEnum.GLOVES, itemQualityTypeEnum.COMMON, itemWeightEnum.LEGER, 1);
    public equipement casqueEnCuir = new equipement("Casque en cuire", 1, itemTypeEnum.HELMET, itemQualityTypeEnum.COMMON, itemWeightEnum.LEGER, 1);
    public equipement plaqueDeCuir = new equipement("Plaque de Cuir", 2, itemTypeEnum.ARMOR, itemQualityTypeEnum.COMMON, itemWeightEnum.MOYEN, 2);

    // =========================================
    // 4. OBJETS COMMUNS
    // =========================================
    public objectItem dagueDeLancer = new objectItem("Dague de lancer", 1, 4, damageTypeEnum.DISTANCE, attackeTypeEnum.PIERCING, itemKnowledgeEnum.COMMUN, 1).withSound("medieval-fantasy/woosh-1");
    public objectItem sacDeSable = new objectItem("Sac de sable", "attack_-2", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.COMMON, itemKnowledgeEnum.COMMUN, 0.25).withSound("rpg-battle-system/2");
    public objectItem bandage = new objectItem("Bandage", 1, itemTypeEnum.OBJECT, itemQualityTypeEnum.COMMON, itemKnowledgeEnum.TECHNIQUE, 0.5).withSound("medieval-fantasy/1");
    public objectItem pierre = new objectItem("Pierre", 1, 2, damageTypeEnum.DISTANCE, attackeTypeEnum.BLUNT, itemKnowledgeEnum.COMMUN, 0.1).withSound("medieval-fantasy/woosh-2");

    // =========================================
    // 5. ARMES INHABITUELLES
    // =========================================
    public weapon bouclierFer = new weapon("Bouclier en fer", 1, 6, itemTypeEnum.SHIELD, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.LOURD, itemKnowledgeEnum.COMMUN, 8).withSound("vrac/coup-metalique");
    public weapon epee2main = new weapon("Épée a deux main", 1, 8, itemTypeEnum.SWORD, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 6).withSound("medieval-fantasy/10");
    public weapon marteau2main = new weapon("Marteau a deux main", 1, 8, itemTypeEnum.HAMMER, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.STOMPING, itemWeightEnum.LOURD, itemKnowledgeEnum.COMMUN, 4).withSound("vrac/coup-metalique");
    public weapon halebarde = new weapon("Halebarde", 1, 10, itemTypeEnum.POLEARM, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.LOURD, itemKnowledgeEnum.COMMUN, 8).withSound("medieval-fantasy/10");
    public weapon batonFerer = new weapon("Baton férer", 1, 6, itemTypeEnum.STAFF, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.MELEE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 5).withSound("vrac/coup-metalique");
    public weapon oldGrimoire = new weapon("Grimoire poussiéreux", 1, 6, itemTypeEnum.BOOK, itemQualityTypeEnum.COMMON, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL, attackeTypeEnum.BLUNT, itemWeightEnum.LEGER, itemKnowledgeEnum.TECHNIQUE, 12).withSound("rpg-battle-system/2");
    public weapon arbaleteHand = new weapon("Arbalète de poing", 1, 6, itemTypeEnum.CROSSBOW, itemQualityTypeEnum.UNCOMMON, weaponTypeEnum.DISTANCE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.PIERCING, itemWeightEnum.LOURD, itemKnowledgeEnum.TECHNIQUE, 10).withSound("vrac/Crossbow");

    // =========================================
    // 6. POTIONS INHABITUELLES
    // =========================================
    public potion Healpotion = new potion("Potion de soin", 2, 4, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.HEAL, 6).withSound("rpg-battle-system/1");
    public potion ManaPotion = new potion("Potion de mana", 2, 4, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.MANA, 6).withSound("rpg-battle-system/2");
    public potion AgilityPotion = new potion("Potion d'agilité", "agilite", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/1");
    public potion StrengthPotion = new potion("Potion de force", "force", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/1");
    public potion IntelligencePotion = new potion("Potion d'intelligence", "intelligence", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/1");
    public potion ConstitutionPotion = new potion("Potion de constitution", "constitution", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/1");
    public potion CharismaPotion = new potion("Potion de charisme", "charisme", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/1");
    public potion SagessePotion = new potion("Potion de sagesse", "sagesse", 2, 3, itemTypeEnum.POTION, itemQualityTypeEnum.UNCOMMON, potionTypeEnum.BUFF, 8).withSound("rpg-battle-system/3");

    // =========================================
    // 7. ÉQUIPEMENTS INHABITUELS
    // =========================================
    public equipement plaqueDeFer = new equipement("Plaque de fer", 3, itemTypeEnum.ARMOR, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LOURD, 8);
    public equipement cuirasseDeCuir = new equipement("Cuirasse de Cuir", 3, itemTypeEnum.ARMOR, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.MOYEN, 5);
    public equipement gantDeFer = new equipement("Gant de fer", 2, itemTypeEnum.GLOVES, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.MOYEN, 3);
    public equipement casqueEnFer = new equipement("Casque en fer", 2, itemTypeEnum.HELMET, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.MOYEN, 3);
    public equipement botteDeFer = new equipement("Botte de fer", 2, itemTypeEnum.FOOT, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.MOYEN, 3);
    public equipement cuissardeDeFer = new equipement("Cuissarde de fer", 3, itemTypeEnum.LEGS, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LOURD, 6);
    public equipement robeEpaisse = new equipement("Robe epaisse", 2, itemTypeEnum.ARMOR, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LEGER, 2);
    public equipement anneauDeProtection = new equipement("Anneau de protection", 1, itemTypeEnum.RING, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LEGER, 12);
    public equipement amuletteDeProtection = new equipement("amulette de protection", 1, itemTypeEnum.NECK, itemQualityTypeEnum.UNCOMMON, itemWeightEnum.LEGER, 12);

    // =========================================
    // 8. OBJETS INHABITUELS
    // =========================================
    public objectItem shuriken = new objectItem("Shuriken", 1, 4, damageTypeEnum.DISTANCE, attackeTypeEnum.SHARPNESS, itemKnowledgeEnum.TECHNIQUE, 1).withSound("medieval-fantasy/woosh-1");
    public objectItem parcheminDeResistancePhysique = new objectItem("Parchemin de resistance physique", "resistance_physique", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.UNCOMMON, itemKnowledgeEnum.TECHNIQUE, 5).withSound("rpg-battle-system/3");
    public objectItem parcheminDeResistanceMagique = new objectItem("Parchemin de resistance magique", "resistance_magique", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.UNCOMMON, itemKnowledgeEnum.TECHNIQUE, 5).withSound("rpg-battle-system/3");
    public objectItem parcheminDeTraitDeFeu = new objectItem("Parchemin de trait de feu", 1, 6, damageTypeEnum.DISTANCE, attackeTypeEnum.FIRE, itemKnowledgeEnum.MYSTIQUE, 2).withSound("vrac/boule-de-feu");
    public objectItem fioledepoison = new objectItem("Fiole de poison", 1, 3, attackeTypeEnum.POISON, itemKnowledgeEnum.COMMUN, 2).withSound("vrac/Verre-casser");
    public objectItem fumigene = new objectItem("Fumigene", "team_defense_1", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.UNCOMMON, itemKnowledgeEnum.TECHNIQUE, 5).withSound("rpg-battle-system/37");
    public objectItem fioledepoudreNoire = new objectItem("Fiole de poudre noire", 2, 4, damageTypeEnum.DISTANCE, attackeTypeEnum.EXPLOSION, itemKnowledgeEnum.TECHNIQUE, 3).withSound("western-fps-2d/explosion-1");

    // =========================================
    // 9. ARMES RARES
    // =========================================
    public weapon grimoireUncommen = new weapon("Grimoire", 1, 8, itemTypeEnum.BOOK, itemQualityTypeEnum.RARE, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL, attackeTypeEnum.BLUNT, itemWeightEnum.LEGER, itemKnowledgeEnum.MYSTIQUE, 20).withSound("rpg-battle-system/2");
    public weapon grimoireRouge = new weapon("Grimoire de feu", 1, 8, itemTypeEnum.BOOK, itemQualityTypeEnum.RARE, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL, attackeTypeEnum.FIRE, itemWeightEnum.LEGER, itemKnowledgeEnum.MYSTIQUE, 20).withSound("rpg-battle-system/2");
    public weapon grimoireBleu = new weapon("Grimoire de froid", 1, 8, itemTypeEnum.BOOK, itemQualityTypeEnum.RARE, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL, attackeTypeEnum.FROST, itemWeightEnum.LEGER, itemKnowledgeEnum.MYSTIQUE, 20).withSound("rpg-battle-system/2");
    public weapon grimoireVert = new weapon("Grimoire de poison", 1, 8, itemTypeEnum.BOOK, itemQualityTypeEnum.RARE, weaponTypeEnum.MAGIC, false, damageTypeEnum.MAGICAL, attackeTypeEnum.POISON, itemWeightEnum.LEGER, itemKnowledgeEnum.MYSTIQUE, 20).withSound("rpg-battle-system/2");
    public weapon arbalete = new weapon("Arbalète", 1, 12, itemTypeEnum.CROSSBOW, itemQualityTypeEnum.RARE, weaponTypeEnum.DISTANCE, true, damageTypeEnum.PHYSICAL, attackeTypeEnum.PIERCING, itemWeightEnum.LOURD, itemKnowledgeEnum.TECHNIQUE, 10).withSound("vrac/Crossbow");

    // =========================================
    // 10. POTIONS RARES
    // =========================================
    public potion BigHealpotion = new potion("Grande Potion de soin", 3, 4, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.HEAL, 15).withSound("rpg-battle-system/1");
    public potion BigManaPotion = new potion("Grande Potion de mana", 3, 4, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.MANA, 15).withSound("rpg-battle-system/2");
    public potion PotiondeResistanceAuFeu = new potion("Potion de resistance au feu", "resistance_feu_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuFroid = new potion("Potion de resistance au froid", "resistance_froid_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuPoison = new potion("Potion de resistance au poison", "resistance_poison_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuDistance = new potion("Potion de resistance au distance", "resistance_distance_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuPhysique = new potion("Potion de resistance au physique", "resistance_physique_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuMagique = new potion("Potion de resistance au magique", "resistance_magique_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");
    public potion PotiondeResistanceAuExplosion = new potion("Potion de resistance au explosion", "resistance_explosion_2", 2, 5, itemTypeEnum.POTION, itemQualityTypeEnum.RARE, potionTypeEnum.BUFF, 10).withSound("rpg-battle-system/3");

    // =========================================
    // 11. ÉQUIPEMENTS RARES
    // =========================================
    public equipement plastronDeFer = new equipement("Plastron de fer", 4, itemTypeEnum.ARMOR, itemQualityTypeEnum.RARE, itemWeightEnum.LOURD, 20);
    public equipement robeEnchanter = new equipement("Robe enchanter", 3, itemTypeEnum.ARMOR, itemQualityTypeEnum.RARE, itemWeightEnum.LEGER, 30);
    public equipement anneauDuGuerrier = createRingWithForce();
    public equipement anneauDeMagie = createRingWithIntelligence();
    public equipement colierDeRégenération = createNecklaceWithRegen();

    // =========================================
    // 12. OBJETS RARES
    // =========================================
    public objectItem grenade = new objectItem("Grenade", 5, 6, itemQualityTypeEnum.RARE, damageTypeEnum.DISTANCE, attackeTypeEnum.EXPLOSION, itemKnowledgeEnum.TECHNIQUE, 10).withSound("western-fps-2d/explosion-3");
    public objectItem parcheminDefaiblissement = new objectItem("Parchemin de défaiblissement", "weaken_all", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.RARE, itemKnowledgeEnum.TECHNIQUE, 10).withSound("rpg-battle-system/3");
    public objectItem parcheminDeResistanceElementaire = new objectItem("Parchemin de resistance elementaire", "resistance_elemental_all", 3, itemTypeEnum.OBJECT, itemQualityTypeEnum.RARE, itemKnowledgeEnum.TECHNIQUE, 20).withSound("rpg-battle-system/3");

    // =========================================
    // 13. ARMES LÉGENDAIRES
    // =========================================
    public weapon lakua = new weapon("La kua", 2, 10, itemTypeEnum.SWORD, itemQualityTypeEnum.LEGENDARY, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 100).withSound("medieval-fantasy/10");
    public weapon lacoubee = new weapon("La coubée", 2, 10, itemTypeEnum.SWORD, itemQualityTypeEnum.LEGENDARY, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.SHARPNESS, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 100).withSound("medieval-fantasy/10");
    public weapon dagueAllusiner = createDagueAllusiner().withSound("medieval-fantasy/10");
    public weapon bouclierdesoufrance = createBouclierDesoufrance().withSound("medieval-fantasy/10");

    // =========================================
    // 14. POTIONS LÉGENDAIRES
    // =========================================
    public potion ExellentHealpotion = new potion("Potion de soin ultime", true, itemTypeEnum.POTION, itemQualityTypeEnum.LEGENDARY, potionTypeEnum.HEAL, 30).withSound("rpg-battle-system/1");
    public potion ExellentManaPotion = new potion("Potion de mana ultime", true, itemTypeEnum.POTION, itemQualityTypeEnum.LEGENDARY, potionTypeEnum.MANA, 30).withSound("rpg-battle-system/2");

    // =========================================
    // 15. ÉQUIPEMENTS LÉGENDAIRES
    // =========================================
    public equipement monocleNain = createMonocleNain();
    public equipement armureEthere = new equipement("Armure Ether", 5, itemTypeEnum.ARMOR, itemQualityTypeEnum.LEGENDARY, itemWeightEnum.LEGER, 100);

    // =========================================
    // 16. OBJETS LÉGENDAIRES
    // =========================================
    public potion potionderesurection = new potion("Potion de resurrection", true, true, itemTypeEnum.POTION, itemQualityTypeEnum.LEGENDARY, potionTypeEnum.HEAL, 100).withSound("rpg-battle-system/1");

    // --- Mêmes déclarations pour garder la rétro-compatibilité ---
    public weapon epeeCourte = epee;
    public weapon bouclier = bouclierBois;
    public weapon grimoire = oldGrimoire;
    public weapon marteau2main_old = marteau2main;

    private static equipement createRingWithForce() {
        equipement eq = new equipement("Anneau du guerrier", 0, itemTypeEnum.RING, itemQualityTypeEnum.RARE, itemWeightEnum.LEGER, 30);
        eq.setBonusForce(2);
        return eq;
    }

    private static equipement createRingWithIntelligence() {
        equipement eq = new equipement("Anneau de mage", 0, itemTypeEnum.RING, itemQualityTypeEnum.RARE, itemWeightEnum.LEGER, 30);
        eq.setBonusIntelligence(2);
        return eq;
    }

    private static equipement createNecklaceWithRegen() {
        equipement eq = new equipement("Colier de régénération", 0, itemTypeEnum.NECK, itemQualityTypeEnum.RARE, itemWeightEnum.LEGER, 30);
        eq.setManaRegenPerTurn(1);
        return eq;
    }

    private static weapon createDagueAllusiner() {
        weapon w = new weapon("Dague hallucinée", 1, 4, itemTypeEnum.DAGGER, itemQualityTypeEnum.LEGENDARY, weaponTypeEnum.MELEE, false, damageTypeEnum.MAGICAL, attackeTypeEnum.PIERCING, itemWeightEnum.LEGER, itemKnowledgeEnum.MYSTIQUE, 100);
        w.setAutoSuccess(true);
        return w;
    }

    private static weapon createBouclierDesoufrance() {
        weapon w = new weapon("Bouclier de souffrance", 1, 4, itemTypeEnum.SHIELD, itemQualityTypeEnum.LEGENDARY, weaponTypeEnum.MELEE, false, damageTypeEnum.PHYSICAL, attackeTypeEnum.BLUNT, itemWeightEnum.MOYEN, itemKnowledgeEnum.COMMUN, 100);
        w.setDamageReturn(4);
        return w;
    }

    private static equipement createMonocleNain() {
        equipement eq = new equipement("Monocle Nain", 0, itemTypeEnum.HELMET, itemQualityTypeEnum.LEGENDARY, itemWeightEnum.LEGER, 100);
        eq.setBonusDistanceAttack(5);
        return eq;
    }

    /**
     * Retourne la liste complète de tous les équipements et objets définis.
     */
    public List<item> getAllItems() {
        List<item> list = new ArrayList<>();
        // Communs
        list.add(epee); list.add(arc); list.add(dague); list.add(bouclierBois); list.add(baton);
        list.add(marteau); list.add(hache2main); list.add(hache); list.add(arcLong);
        list.add(LowHealpotion); list.add(LowManaPotion);
        list.add(bottesCuir); list.add(robeEnTissu); list.add(cuissardesCuir); list.add(gantCuir); list.add(casqueEnCuir); list.add(plaqueDeCuir);
        list.add(dagueDeLancer); list.add(sacDeSable); list.add(bandage); list.add(pierre);

        // Inhabituels
        list.add(bouclierFer); list.add(epee2main); list.add(marteau2main); list.add(halebarde); list.add(batonFerer); list.add(oldGrimoire); list.add(arbaleteHand);
        list.add(Healpotion); list.add(ManaPotion); list.add(AgilityPotion); list.add(StrengthPotion); list.add(IntelligencePotion); list.add(ConstitutionPotion); list.add(CharismaPotion); list.add(SagessePotion);
        list.add(plaqueDeFer); list.add(cuirasseDeCuir); list.add(gantDeFer); list.add(casqueEnFer); list.add(botteDeFer); list.add(cuissardeDeFer); list.add(robeEpaisse); list.add(anneauDeProtection); list.add(amuletteDeProtection);
        list.add(shuriken); list.add(parcheminDeResistancePhysique); list.add(parcheminDeResistanceMagique); list.add(parcheminDeTraitDeFeu); list.add(fioledepoison); list.add(fumigene); list.add(fioledepoudreNoire);

        // Rares
        list.add(grimoireUncommen); list.add(grimoireRouge); list.add(grimoireBleu); list.add(grimoireVert); list.add(arbalete);
        list.add(BigHealpotion); list.add(BigManaPotion); list.add(PotiondeResistanceAuFeu); list.add(PotiondeResistanceAuFroid); list.add(PotiondeResistanceAuPoison); list.add(PotiondeResistanceAuDistance); list.add(PotiondeResistanceAuPhysique); list.add(PotiondeResistanceAuMagique); list.add(PotiondeResistanceAuExplosion);
        list.add(plastronDeFer); list.add(robeEnchanter); list.add(anneauDuGuerrier); list.add(anneauDeMagie); list.add(colierDeRégenération);
        list.add(grenade); list.add(parcheminDefaiblissement); list.add(parcheminDeResistanceElementaire);

        // Légendaires
        list.add(lakua); list.add(lacoubee); list.add(dagueAllusiner); list.add(bouclierdesoufrance);
        list.add(ExellentHealpotion); list.add(ExellentManaPotion);
        list.add(monocleNain); list.add(armureEthere);
        list.add(potionderesurection);

        return list;
    }
}

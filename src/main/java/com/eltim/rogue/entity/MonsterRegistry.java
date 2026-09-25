package com.eltim.rogue.entity;

import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.itemImplementation.equipementImplementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre central des profils et modèles de monstres basés sur niveauSchema.txt.
 */
public class MonsterRegistry {

    public static class MonsterBlueprint {
        public String name;
        public int level = 1;
        public char symbol = 'M';
        public String color = "red";
        public String soundName;
        public int hp = 10;
        public int force = 10;
        public int agilite = 10;
        public int constitution = 10;
        public int intelligence = 10;
        public int sagesse = 10;
        public int charisme = 10;
        public List<String> weaponNames = new ArrayList<>();
        public List<String> lootItemNames = new ArrayList<>();
        public int minGold = 0;
        public int maxGold = 0;
        public String description;
        public int xp = 10;
        public String iaType;
    }

    private static final Map<String, MonsterBlueprint> BLUEPRINTS = new HashMap<>();

    static {
        registerBlueprints();
    }

    private static void register(MonsterBlueprint bp) {
        BLUEPRINTS.put(bp.name.toLowerCase().trim(), bp);
    }

    public static boolean hasBlueprint(String name) {
        if (name == null) return false;
        String clean = name.toLowerCase().trim();
        if (BLUEPRINTS.containsKey(clean)) return true;
        for (String k : BLUEPRINTS.keySet()) {
            if (k.contains(clean) || clean.contains(k)) return true;
        }
        return false;
    }

    public static monster createMonster(String name, int x, int y) {
        MonsterBlueprint bp = BLUEPRINTS.get(name.toLowerCase().trim());
        if (bp == null) {
            for (Map.Entry<String, MonsterBlueprint> entry : BLUEPRINTS.entrySet()) {
                if (entry.getKey().contains(name.toLowerCase().trim()) || name.toLowerCase().trim().contains(entry.getKey())) {
                    bp = entry.getValue();
                    break;
                }
            }
        }

        if (bp == null) {
            monster fallback = new monster(x, y, 'M');
            fallback.setName(name);
            return fallback;
        }

        monster m = new monster(x, y, bp.symbol);
        m.setName(bp.name);
        m.setLevel(bp.level);
        m.setMaxLifePoint(bp.hp);
        m.setLifePoint(bp.hp);
        m.setForce(bp.force);
        m.setAgilite(bp.agilite);
        m.setConstitution(bp.constitution);
        m.setIntelligence(bp.intelligence);
        m.setSagesse(bp.sagesse);
        m.setCharisme(bp.charisme);
        m.setXpReward(bp.xp);
        m.setSoundName(bp.soundName);
        m.setGoldReward(bp.minGold, bp.maxGold);

        equipementImplementation eqFactory = new equipementImplementation();

        // 1. Équiper une arme aléatoire tirée des armes potentielles
        if (!bp.weaponNames.isEmpty()) {
            String randomWepName = bp.weaponNames.get((int) (Math.random() * bp.weaponNames.size()));
            item wepItem = findItemByName(eqFactory, randomWepName);
            if (wepItem instanceof weapon) {
                m.rightHand = (weapon) wepItem;
            }
        }

        // 2. Remplir la table de loot avec les objets spécifiés
        for (String lootName : bp.lootItemNames) {
            item lootItem = findItemByName(eqFactory, lootName);
            if (lootItem != null) {
                m.addLoot(lootItem);
            }
        }

        return m;
    }

    public static item findItemByName(equipementImplementation eq, String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String clean = name.toLowerCase().trim();

        if (clean.contains("épée") || clean.contains("epee")) {
            if (clean.contains("2main") || clean.contains("deux")) return eq.epee2main;
            return eq.epee;
        }
        if (clean.contains("arc")) {
            if (clean.contains("long")) return eq.arcLong;
            return eq.arc;
        }
        if (clean.contains("dague")) {
            if (clean.contains("lancer")) return eq.dagueDeLancer;
            return eq.dague;
        }
        if (clean.contains("hache")) {
            if (clean.contains("2main") || clean.contains("deux")) return eq.hache2main;
            return eq.hache;
        }
        if (clean.contains("marteau")) {
            if (clean.contains("2main") || clean.contains("deux")) return eq.marteau2main;
            return eq.marteau;
        }
        if (clean.contains("baton") || clean.contains("bâton")) {
            if (clean.contains("fer") || clean.contains("fér")) return eq.batonFerer;
            return eq.baton;
        }
        if (clean.contains("halebarde") || clean.contains("hallebarde")) return eq.halebarde;
        if (clean.contains("arbalete") || clean.contains("arbalète")) {
            if (clean.contains("poing") || clean.contains("hand")) return eq.arbaleteHand;
            return eq.arbalete;
        }
        if (clean.contains("kua")) return eq.lakua;
        if (clean.contains("coubée") || clean.contains("coubee")) return eq.lacoubee;
        if (clean.contains("grimoire")) return eq.oldGrimoire;
        if (clean.contains("bandage")) return eq.bandage;
        if (clean.contains("pierre")) return eq.pierre;
        if (clean.contains("sable")) return eq.sacDeSable;
        if (clean.contains("shuriken")) return eq.shuriken;
        if (clean.contains("poison")) return eq.fioledepoison;
        if (clean.contains("lowhealpotion") || clean.contains("soin mineur")) return eq.LowHealpotion;
        if (clean.contains("lowmanapotion") || clean.contains("mana mineur")) return eq.LowManaPotion;
        if (clean.contains("bighealpotion") || clean.contains("grande potion de soin")) return eq.BigHealpotion;
        if (clean.contains("exellenthealpotion") || clean.contains("soin ultime")) return eq.ExellentHealpotion;
        if (clean.contains("healpotion") || clean.contains("potion de soin")) return eq.Healpotion;
        if (clean.contains("manapotion") || clean.contains("potion de mana")) return eq.ManaPotion;
        if (clean.contains("plaquedefer") || clean.contains("plaque de fer")) return eq.plaqueDeFer;
        if (clean.contains("plaquedecuir") || clean.contains("plaque de cuir")) return eq.plaqueDeCuir;
        if (clean.contains("cuirassedecuir") || clean.contains("cuirasse de cuir")) return eq.cuirasseDeCuir;
        if (clean.contains("bottescuir") || clean.contains("bottes en cuire")) return eq.bottesCuir;
        if (clean.contains("cuissardescuir") || clean.contains("cuissardes")) return eq.cuissardesCuir;
        if (clean.contains("gantcuir") || clean.contains("gant")) return eq.gantCuir;
        if (clean.contains("casqueenfer") || clean.contains("casque")) return eq.casqueEnFer;
        if (clean.contains("anneauduguerrier")) return eq.anneauDuGuerrier;
        if (clean.contains("anneaudeprotection")) return eq.anneauDeProtection;
        if (clean.contains("amulettedeprotection")) return eq.amuletteDeProtection;
        if (clean.contains("colierderégenération") || clean.contains("colier")) return eq.colierDeRégenération;
        if (clean.contains("lakua")) return eq.lakua;
        if (clean.contains("lacoubee")) return eq.lacoubee;
        if (clean.contains("armureethere")) return eq.armureEthere;
        if (clean.contains("parcheminderesistancemagique")) return eq.parcheminDeResistanceMagique;
        if (clean.contains("parcheminderesistancephysique")) return eq.parcheminDeResistancePhysique;
        if (clean.contains("parchemindetraitdefeu")) return eq.parcheminDeTraitDeFeu;

        return null;
    }

    private static void registerBlueprints() {
        // 1. Garde mort vivant
        MonsterBlueprint gmv = new MonsterBlueprint();
        gmv.name = "Garde mort vivant";
        gmv.level = 1;
        gmv.symbol = 'A';
        gmv.color = "red";
        gmv.soundName = "sfx/NPC/shade/shade12";
        gmv.hp = 18; gmv.force = 11; gmv.agilite = 7; gmv.constitution = 18; gmv.intelligence = 2; gmv.sagesse = 2; gmv.charisme = 8;
        gmv.weaponNames = Arrays.asList("Épée", "Arc", "Dague");
        gmv.lootItemNames = Arrays.asList("bandage", "Épée", "Arc", "bottesCuir", "cuissardesCuir", "gantCuir", "plaqueDeCuir", "pierre");
        gmv.minGold = 0; gmv.maxGold = 1;
        gmv.xp = 10;
        gmv.description = "Un ancien garde de Cead Nua transformé en mort vivant...";
        register(gmv);

        // 2. Squelette
        MonsterBlueprint sq = new MonsterBlueprint();
        sq.name = "Squelette";
        sq.level = 1;
        sq.symbol = 'S';
        sq.color = "red";
        sq.soundName = "sfx/80-CC0-creature-sfx-2/stomp_01";
        sq.hp = 6; sq.force = 6; sq.agilite = 14; sq.constitution = 18; sq.intelligence = 6; sq.sagesse = 2; sq.charisme = 12;
        sq.weaponNames = Arrays.asList("Épée", "Dague", "Arc", "Hache");
        sq.lootItemNames = Arrays.asList("dague", "epee", "pierre", "Arc", "Hache");
        sq.minGold = 0; sq.maxGold = 0;
        sq.xp = 5;
        sq.description = "Un tas d'os réanimé brandissant une arme rouillée.";
        register(sq);

        // 3. Mort vivant
        MonsterBlueprint mv = new MonsterBlueprint();
        mv.name = "Mort vivant";
        mv.level = 1;
        mv.symbol = 'Z';
        mv.color = "red";
        mv.soundName = "sfx/NPC/shade/shade12";
        mv.hp = 15; mv.force = 12; mv.agilite = 6; mv.constitution = 14; mv.intelligence = 2; mv.sagesse = 2; mv.charisme = 2;
        mv.weaponNames = Arrays.asList("Mains nues", "Baton", "Hache");
        mv.lootItemNames = Arrays.asList("bandage", "LowHealpotion", "pierre", "Hache", "Baton", "LowManaPotion");
        mv.minGold = 0; mv.maxGold = 1;
        mv.xp = 5;
        mv.description = "Un cadavre ambulant en lente décomposition.";
        register(mv);

        // 4. Fantome
        MonsterBlueprint ft = new MonsterBlueprint();
        ft.name = "Fantome";
        ft.level = 2;
        ft.symbol = 'F';
        ft.color = "red";
        ft.soundName = "sfx/80-CC0-creature-sfx-2/slime_19";
        ft.hp = 14; ft.force = 6; ft.agilite = 16; ft.constitution = 8; ft.intelligence = 12; ft.sagesse = 14; ft.charisme = 6;
        ft.weaponNames = Arrays.asList("Mains nues", "Grimoire");
        ft.lootItemNames = Arrays.asList("parcheminDeResistanceMagique", "parcheminDeResistancePhysique", "parcheminDeTraitDeFeu");
        ft.minGold = 0; ft.maxGold = 0;
        ft.xp = 15;
        ft.description = "Une apparition spectrale glaciale traversant les murs.";
        register(ft);

        // 5. Loup phantomatique
        MonsterBlueprint lp = new MonsterBlueprint();
        lp.name = "Loup phantomatique";
        lp.level = 2;
        lp.symbol = 'W';
        lp.color = "red";
        lp.soundName = "sfx/NPC/gutteral beast/mnstr2";
        lp.hp = 16; lp.force = 13; lp.agilite = 15; lp.constitution = 12; lp.intelligence = 6; lp.sagesse = 8; lp.charisme = 4;
        lp.weaponNames = Arrays.asList("crocs");
        lp.lootItemNames = Arrays.asList("parcheminDeResistanceMagique", "parcheminDeResistancePhysique", "parcheminDeTraitDeFeu");
        lp.minGold = 0; lp.maxGold = 0;
        lp.xp = 16;
        lp.description = "Un loup brumeux aux yeux étincelants d'énergie éthérée.";
        register(lp);

        // 6. Loup
        MonsterBlueprint loup = new MonsterBlueprint();
        loup.name = "Loup";
        loup.level = 1;
        loup.symbol = 'L';
        loup.color = "red";
        loup.soundName = "sfx/NPC/gutteral beast/mnstr5";
        loup.hp = 14; loup.force = 12; loup.agilite = 14; loup.constitution = 12; loup.intelligence = 4; loup.sagesse = 6; loup.charisme = 4;
        loup.weaponNames = Arrays.asList("crocs");
        loup.lootItemNames = Arrays.asList("bandage", "pierre");
        loup.minGold = 0; loup.maxGold = 0;
        loup.xp = 8;
        loup.description = "Un prédateur sauvage agile et féroce.";
        register(loup);

        // 7. Serpent Géant
        MonsterBlueprint sg = new MonsterBlueprint();
        sg.name = "Serpent Géant";
        sg.level = 3;
        sg.symbol = 'V';
        sg.color = "red";
        sg.soundName = "sfx/80-CC0-creature-sfx-2/monster_14";
        sg.hp = 20; sg.force = 14; sg.agilite = 13; sg.constitution = 14; sg.intelligence = 4; sg.sagesse = 6; sg.charisme = 2;
        sg.weaponNames = Arrays.asList("crocs");
        sg.lootItemNames = Arrays.asList("fioledepoison");
        sg.minGold = 0; sg.maxGold = 0;
        sg.xp = 18;
        sg.description = "Un serpent reptilien massif au venin mortel.";
        register(sg);

        // 8. Araigner Géante
        MonsterBlueprint ag = new MonsterBlueprint();
        ag.name = "Araigner Géante";
        ag.level = 2;
        ag.symbol = 'X';
        ag.color = "red";
        ag.soundName = "sfx/80-CC0-creature-sfx-2/slime_06";
        ag.hp = 12; ag.force = 11; ag.agilite = 16; ag.constitution = 12; ag.intelligence = 6; ag.sagesse = 8; ag.charisme = 2;
        ag.weaponNames = Arrays.asList("pattes");
        ag.lootItemNames = Arrays.asList("fioledepoison", "AgilityPotion", "bandage");
        ag.minGold = 0; ag.maxGold = 0;
        ag.xp = 12;
        ag.description = "Une araignée gigantesque tissant des toiles piégées.";
        register(ag);

        // 9. Chevalier mort vivant
        MonsterBlueprint cmv = new MonsterBlueprint();
        cmv.name = "Chevalier mort vivant";
        cmv.level = 3;
        cmv.symbol = 'K';
        cmv.color = "red";
        cmv.soundName = "sfx/80-CC0-creature-sfx-2/weird_07";
        cmv.hp = 32; cmv.force = 16; cmv.agilite = 10; cmv.constitution = 16; cmv.intelligence = 8; cmv.sagesse = 8; cmv.charisme = 10;
        cmv.weaponNames = Arrays.asList("epee2main", "hache2main", "marteau2main", "halebarde");
        cmv.lootItemNames = Arrays.asList("plaqueDeFer", "epee2main", "Healpotion", "hache2main", "marteau2main", "halebarde", "anneauDuGuerrier", "botteDeFer", "cuissardeDeFer", "casqueEnFer");
        cmv.minGold = 10; cmv.maxGold = 25;
        cmv.xp = 35;
        cmv.description = "Un ancien commandant en cuirasse d'acier noir.";
        register(cmv);

        // 10. Amas de corps
        MonsterBlueprint ac = new MonsterBlueprint();
        ac.name = "Amas de corps";
        ac.level = 4;
        ac.symbol = 'O';
        ac.color = "red";
        ac.soundName = "sfx/80-CC0-creature-sfx-2/weird_07";
        ac.hp = 45; ac.force = 18; ac.agilite = 5; ac.constitution = 20; ac.intelligence = 2; ac.sagesse = 4; ac.charisme = 1;
        ac.weaponNames = Arrays.asList("Mains nues", "Marteau");
        ac.lootItemNames = Arrays.asList("BigHealpotion", "bandage", "amuletteDeProtection", "shuriken", "anneauDeProtection", "colierDeRégenération");
        ac.minGold = 5; ac.maxGold = 5;
        ac.xp = 40;
        ac.description = "Une monstruosité grotesque faite de membres cousus ensemble.";
        register(ac);

        // 11. Ours
        MonsterBlueprint ours = new MonsterBlueprint();
        ours.name = "Ours";
        ours.level = 3;
        ours.symbol = 'B';
        ours.color = "red";
        ours.soundName = "sfx/80-CC0-creature-sfx-2/roar_06";
        ours.hp = 35; ours.force = 18; ours.agilite = 10; ours.constitution = 17; ours.intelligence = 4; ours.sagesse = 6; ours.charisme = 4;
        ours.weaponNames = Arrays.asList("Mains nues");
        ours.lootItemNames = Arrays.asList("shuriken", "cuirasseDeCuir", "dagueDeLancer", "gantCuir", "casqueEnCuir", "plaqueDeCuir", "bottesCuir");
        ours.minGold = 0; ours.maxGold = 0;
        ours.xp = 22;
        ours.description = "Un colosse poilu doté d'une force brutale.";
        register(ours);

        // 12. Gobelin
        MonsterBlueprint gob = new MonsterBlueprint();
        gob.name = "Gobelin";
        gob.level = 1;
        gob.symbol = 'g';
        gob.color = "red";
        gob.soundName = "sfx/80-CC0-creature-sfx-2/weird_09";
        gob.hp = 7; gob.force = 6; gob.agilite = 15; gob.constitution = 9; gob.intelligence = 4; gob.sagesse = 6; gob.charisme = 3;
        gob.weaponNames = Arrays.asList("Dague", "dagueDeLancer", "Sac de sable");
        gob.lootItemNames = Arrays.asList("dague", "dagueDeLancer", "sacDeSable");
        gob.minGold = 1; gob.maxGold = 5;
        gob.xp = 7;
        gob.description = "Une créature hargneuse et rusée aimant les coups bas.";
        register(gob);

        // 13. Seigneur maudit
        MonsterBlueprint sm = new MonsterBlueprint();
        sm.name = "Seigneur maudit";
        sm.level = 5;
        sm.symbol = 'N';
        sm.color = "red";
        sm.soundName = "sfx/80-CC0-creature-sfx-2/weird_07";
        sm.hp = 80; sm.force = 20; sm.agilite = 14; sm.constitution = 18; sm.intelligence = 16; sm.sagesse = 14; sm.charisme = 16;
        sm.weaponNames = Arrays.asList("lakua", "lacoubee", "epee2main");
        sm.lootItemNames = Arrays.asList("ExellentHealpotion", "lakua", "armureEthere");
        sm.minGold = 50; sm.maxGold = 100;
        sm.xp = 150;
        sm.description = "Le maître souverain des ténèbres régnant sur le donjon.";
        register(sm);

        // 14. Le Baveur
        MonsterBlueprint bav = new MonsterBlueprint();
        bav.name = "Le Baveur";
        bav.level = 4;
        bav.symbol = '8';
        bav.color = "red";
        bav.soundName = "sfx/NPC/beetle/bite-small.wav";
        bav.hp = 8; bav.force = 6; bav.agilite = 8; bav.constitution = 8; bav.intelligence = 1; bav.sagesse = 2; bav.charisme = 1;
        bav.weaponNames = Arrays.asList("Mains nues");
        bav.lootItemNames = Arrays.asList("pierre");
        bav.minGold = 0; bav.maxGold = 0;
        bav.xp = 5;
        bav.description = "Une vermine visqueuse et rampant dans la fange.";
        register(bav);
    }
}

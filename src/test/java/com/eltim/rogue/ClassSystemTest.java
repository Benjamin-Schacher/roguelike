package com.eltim.rogue;

import com.eltim.rogue.entity.classe.*;
import com.eltim.rogue.entity.player;
import junit.framework.TestCase;

import java.util.List;

public class ClassSystemTest extends TestCase {

    public void testBaseClassesInitialization() {
        classe[] classes = new classe[] {
            new warriorClasse(),
            new rogueClasse(),
            new mageClasse(),
            new archerClasse(),
            new priestClasse()
        };

        for (classe c : classes) {
            assertNotNull("La classe doit avoir un nom", c.name);
            assertEquals("La classe doit démarrer avec 2 points", 2, c.skillPoints);
            assertEquals("Chaque classe de base doit posséder 4 arbres de talents", 4, c.availableTrees.size());

            // 4 emplacements actifs initialement vides
            assertEquals("Doit comporter 4 emplacements actifs", 4, c.activeSlots.length);
            for (int i = 0; i < 4; i++) {
                assertNull("L'emplacement " + i + " doit être vide au départ", c.activeSlots[i]);
            }
            assertTrue("La liste synchronisée doit être vide", c.trees.isEmpty());

            // Chaque arbre doit comporter exactement 5 tiers
            for (SkillTree tree : c.availableTrees) {
                assertEquals("L'arbre " + tree.name + " doit avoir 5 tiers", 5, tree.skills.size());
                for (int t = 0; t < 5; t++) {
                    assertEquals(t + 1, tree.skills.get(t).tier);
                    assertFalse("Le talent ne doit pas être débloqué par défaut", tree.skills.get(t).unlocked);
                }
            }
        }
    }

    public void testTreeSlottingAndTier1Unlock() {
        classe warrior = new warriorClasse();
        assertEquals(2, warrior.skillPoints);

        SkillTree tree1 = warrior.availableTrees.get(0);
        SkillTree tree2 = warrior.availableTrees.get(1);

        // Assignation au slot 0 (coûte 1 point et débloque le Tier 1)
        boolean success1 = warrior.assignTreeToSlot(0, tree1);
        assertTrue("L'assignation du premier arbre doit réussir", success1);
        assertEquals(1, warrior.skillPoints);
        assertEquals(tree1, warrior.activeSlots[0]);
        assertTrue("Le Tier 1 doit être débloqué automatiquement", tree1.skills.get(0).unlocked);
        assertFalse("Le Tier 2 ne doit pas encore être débloqué", tree1.skills.get(1).unlocked);
        assertEquals(1, warrior.trees.size());

        // Assignation au slot 1 (coûte le 2ème point)
        boolean success2 = warrior.assignTreeToSlot(1, tree2);
        assertTrue("L'assignation du deuxième arbre doit réussir", success2);
        assertEquals(0, warrior.skillPoints);
        assertEquals(tree2, warrior.activeSlots[1]);
        assertTrue("Le Tier 1 du second arbre doit être débloqué", tree2.skills.get(0).unlocked);
        assertEquals(2, warrior.trees.size());

        // Tentative d'assignation au slot 2 sans point disponible
        SkillTree tree3 = warrior.availableTrees.get(2);
        boolean failNoPoints = warrior.assignTreeToSlot(2, tree3);
        assertFalse("L'assignation sans point disponible doit échouer", failNoPoints);
        assertNull(warrior.activeSlots[2]);

        // Tentative d'assigner le même arbre dans un autre slot
        warrior.skillPoints = 5;
        boolean failDuplicate = warrior.assignTreeToSlot(2, tree1);
        assertFalse("Un même arbre ne peut pas être assigné en double", failDuplicate);
    }

    public void testSubclassCatalogAndCompatibility() {
        List<Subclass> all = SubclassCatalog.getAll();
        assertEquals("Il doit y avoir exactement 10 sous-classes", 10, all.size());

        for (Subclass sc : all) {
            assertNotNull(sc.name);
            assertNotNull(sc.class1);
            assertNotNull(sc.class2);
            assertEquals("Chaque sous-classe doit avoir 2 arbres exclusifs", 2, sc.trees.size());
            for (SkillTree st : sc.trees) {
                assertEquals("Chaque arbre de sous-classe doit avoir 5 tiers", 5, st.skills.size());
            }
        }

        // Vérification des 4 sous-classes compatibles avec le Guerrier
        classe warrior = new warriorClasse();
        List<Subclass> warriorSubs = SubclassCatalog.getAvailableFor(warrior);
        assertEquals(4, warriorSubs.size());
        boolean hasMageLame = false, hasPaladin = false, hasBreteur = false, hasAssassin = false;
        for (Subclass sc : warriorSubs) {
            if (sc.name.equals("Mage-lame")) hasMageLame = true;
            if (sc.name.equals("Paladin")) hasPaladin = true;
            if (sc.name.equals("Breteur")) hasBreteur = true;
            if (sc.name.equals("Assassin")) hasAssassin = true;
        }
        assertTrue(hasMageLame && hasPaladin && hasBreteur && hasAssassin);
    }

    public void testLevelUpAndSubclassPointRules() {
        player p = new player(0, 0);
        classe warrior = new warriorClasse();
        p.chooseClass(warrior);
        p.setLevel(1);
        warrior.skillPoints = 2;

        // Niveau 1 -> 2 : Classe pure gagne 2 points
        p.addXp(100);
        assertEquals(2, p.getLevel());
        assertEquals(4, warrior.skillPoints); // 2 de base + 2 au niveau 2
        assertFalse(p.isPendingSubclassChoice());

        // Niveau 2 -> 3 : Déclenche le choix de sous-classe
        p.addXp(200);
        assertEquals(3, p.getLevel());
        assertEquals(6, warrior.skillPoints); // +2 points au niveau 3
        assertTrue("Au niveau 3, le joueur doit avoir le choix de sous-classe en attente", p.isPendingSubclassChoice());

        // Choix de la sous-classe Mage-lame (Bi-classement)
        List<Subclass> subs = SubclassCatalog.getAvailableFor(warrior);
        Subclass mageLame = subs.get(0);
        warrior.setSubclass(mageLame.name, mageLame.trees);
        assertTrue(warrior.hasSubclass());
        assertEquals("Mage-lame", warrior.subclass);
        assertEquals("Les 2 arbres de la sous-classe doivent s'ajouter aux 4 arbres de base", 6, warrior.availableTrees.size());

        // Niveau 3 -> 4 avec bi-classe : Ne gagne plus qu'1 point !
        int pointsBefore = warrior.skillPoints;
        p.addXp(300);
        assertEquals(4, p.getLevel());
        assertEquals("En bi-classe, le joueur doit gagner exactement 1 point par niveau", pointsBefore + 1, warrior.skillPoints);
    }

    public void testPureClassRefusalKeepsTwoPoints() {
        player p = new player(0, 0);
        classe rogue = new rogueClasse();
        p.chooseClass(rogue);
        p.setLevel(3);
        rogue.skillPoints = 4;
        rogue.hasRefusedSubclass = true; // Refuse la bi-classe pour rester pur

        p.addXp(300); // Passe niveau 4
        assertEquals(4, p.getLevel());
        assertEquals("En classe pure, le joueur doit continuer à gagner 2 points par niveau", 6, rogue.skillPoints);
    }
}

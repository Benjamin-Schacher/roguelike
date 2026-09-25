package com.eltim.rogue;

import com.eltim.rogue.entity.player;
import com.eltim.rogue.item.itemImplementation.equipementImplementation;
import com.eltim.rogue.system.InventorySystem;
import junit.framework.TestCase;

public class WeaponAndInventoryTest extends TestCase {

    private equipementImplementation eq;

    @Override
    protected void setUp() {
        eq = new equipementImplementation();
    }

    public void testWeaponNamesPrecision() {
        // Armes à 1 main
        assertEquals("Épée à une main", eq.epee.getName());
        assertEquals("Hache à une main", eq.hache.getName());
        assertEquals("Marteau à une main", eq.marteau.getName());
        assertEquals("La kua (à une main)", eq.lakua.getName());
        assertEquals("La coubée (à une main)", eq.lacoubee.getName());

        // Armes à 2 mains
        assertEquals("Bâton à deux mains", eq.baton.getName());
        assertEquals("Bâton ferré à deux mains", eq.batonFerer.getName());
        assertEquals("Hache à deux mains", eq.hache2main.getName());
        assertEquals("Épée à deux mains", eq.epee2main.getName());
        assertEquals("Marteau à deux mains", eq.marteau2main.getName());
        assertEquals("Halebarde à deux mains", eq.halebarde.getName());
        assertEquals("Arbalète à deux mains", eq.arbalete.getName());
        assertEquals("Arc à deux mains", eq.arc.getName());
        assertEquals("Arc long à deux mains", eq.arcLong.getName());
    }

    public void testTwoHandedWeaponEquipAndLeftHandHandling() {
        player p = new player(0, 0);

        // Équiper l'épée en main droite et bouclier en main gauche manuellement
        p.rightHand = eq.epee;
        p.leftHand = eq.bouclierBois;

        // Ajouter uniquement le bâton (arme à 2 mains) dans l'inventaire
        p.getInventory().clear();
        p.getInventory().add(eq.baton);

        // Ouvrir l'inventaire avec le joueur
        InventorySystem.open(p, null);

        // Vérifier que le bâton est bien à l'index 0
        assertEquals(1, InventorySystem.getFilteredInventory().size());
        assertEquals(eq.baton, InventorySystem.getFilteredInventory().get(0));

        java.awt.event.KeyEvent enterKey = new java.awt.event.KeyEvent(
                new java.awt.Component() {}, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_ENTER, '\n'
        );

        // 1. Sélectionner le bâton dans l'inventaire -> Ouvre le prompt
        InventorySystem.handleInput(enterKey);
        assertTrue(InventorySystem.isPromptingWeaponSlot());
        assertEquals(0, InventorySystem.getWeaponPromptIndex()); // Main droite par défaut

        // 2. Confirmer l'équipement en main droite
        InventorySystem.handleInput(enterKey);

        assertEquals(eq.baton, p.rightHand);
        assertNull("La main gauche doit être vidée lors de l'équipement d'une arme 2 mains", p.leftHand);
        assertTrue("Le bouclier déséquipé doit retourner dans l'inventaire", p.getInventory().contains(eq.bouclierBois));
        assertTrue("L'ancienne arme main droite doit retourner dans l'inventaire", p.getInventory().contains(eq.epee));
    }

    public void testLeftHandUnequipReleasesTwoHandedWeapon() {
        player p = new player(0, 0);
        p.rightHand = eq.baton;
        p.leftHand = null;

        InventorySystem.open(p, null);

        // Naviguer vers la colonne équipement (RIGHT)
        java.awt.event.KeyEvent rightKey = new java.awt.event.KeyEvent(
                new java.awt.Component() {}, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_RIGHT, ' '
        );
        InventorySystem.handleInput(rightKey);
        assertEquals(InventorySystem.Column.EQUIPMENT, InventorySystem.getCurrentColumn());

        // Naviguer vers l'emplacement 9 (Main Gauche)
        java.awt.event.KeyEvent downKey = new java.awt.event.KeyEvent(
                new java.awt.Component() {}, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_DOWN, ' '
        );
        for (int i = 0; i < 9; i++) {
            InventorySystem.handleInput(downKey);
        }
        assertEquals(9, InventorySystem.getEquipmentIndex());

        // Appuyer sur ENTRÉE sur l'emplacement Main Gauche
        java.awt.event.KeyEvent enterKey = new java.awt.event.KeyEvent(
                new java.awt.Component() {}, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, java.awt.event.KeyEvent.VK_ENTER, '\n'
        );
        InventorySystem.handleInput(enterKey);

        // L'arme à 2 mains en main droite doit avoir été déséquipée
        assertNull("La main droite doit être déséquipée", p.rightHand);
        assertNull("La main gauche reste null", p.leftHand);
        assertTrue("L'arme 2 mains doit retourner dans l'inventaire", p.getInventory().contains(eq.baton));
    }

    public void testFindDarkTombAudio() throws Exception {
        com.eltim.rogue.engine.sound.SoundManager sm = com.eltim.rogue.engine.sound.SoundManager.getInstance();

        // 1. Dark Tomb (fichier avec emoji et tirets)
        javax.sound.sampled.AudioInputStream ais1 = sm.findAudioStream("Dark Tomb");
        assertNotNull("Dark Tomb doit être trouvé par SoundManager", ais1);
        ais1.close();

        // 2. short_adventure
        javax.sound.sampled.AudioInputStream ais2 = sm.findAudioStream("short_adventure");
        assertNotNull("short_adventure doit être trouvé par SoundManager", ais2);
        ais2.close();

        // 3. combat (doit résoudre le fallback hurryup)
        javax.sound.sampled.AudioInputStream ais3 = sm.findAudioStream("combat");
        assertNotNull("combat doit résoudre vers un stream audio", ais3);
        ais3.close();

        // 4. level1 (doit résoudre vers un stream audio)
        javax.sound.sampled.AudioInputStream ais4 = sm.findAudioStream("level1");
        assertNotNull("level1 doit résoudre vers un stream audio", ais4);
        ais4.close();
    }

    public void testPlayAndStopMusic() throws Exception {
        com.eltim.rogue.engine.sound.SoundManager sm = com.eltim.rogue.engine.sound.SoundManager.getInstance();
        sm.playMusic("short_adventure");
        Thread.sleep(150);
        sm.stopMusic();
        Thread.sleep(50);
    }
}

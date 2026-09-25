package com.eltim.rogue;

import com.eltim.rogue.alteration.alteration;
import com.eltim.rogue.entity.base.Belief;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.environment.InteractionTile;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.system.InteractionSysteme;
import com.eltim.rogue.world.map;
import junit.framework.TestCase;

import com.eltim.rogue.item.key;
import java.util.List;

public class AlterationAndDoorTest extends TestCase {

    public void testKarinPrayerOnlyOpensCellDoor() {
        map gameMap = new map(20, 20);
        player p = new player(12, 13);
        p.setBelief(Belief.KARIN);

        // Porte de la cellule du joueur (état OLD)
        door cellDoor = new door(15, 13, 'D', doorStateEnum.OLD, 152);
        // Autre porte normale
        door normalDoor = new door(5, 5, 'D', doorStateEnum.NORMAL, 152);
        // Porte verrouillée
        door lockedDoor = new door(2, 2, 'D', doorStateEnum.LOCKED, 152);

        gameMap.addEntity(cellDoor);
        gameMap.addEntity(normalDoor);
        gameMap.addEntity(lockedDoor);

        // Autel de Karin
        InteractionTile altar = new InteractionTile(10, 13, '£', "Prier le dieu Karin, le gond de la porte lâche");

        InteractionSysteme.setCurrentLevel(new com.eltim.rogue.level.tutoLevel());
        InteractionSysteme.onEncounter(p, altar, gameMap);
        InteractionSysteme.executeAction(altar.getActionName());

        // Vérification : Seule la porte de cellule s'ouvre !
        assertEquals("La porte de la cellule doit être ouverte", doorStateEnum.OPEN, cellDoor.getState());
        assertEquals("La porte normale ne doit pas être ouverte", doorStateEnum.NORMAL, normalDoor.getState());
        assertEquals("La porte verrouillée ne doit pas être ouverte", doorStateEnum.LOCKED, lockedDoor.getState());
    }

    public void testAlterationsBuffsAndDebuffs() {
        player p = new player(0, 0);

        alteration buff = new alteration("Posture Défensive", alteration.Type.BUFF, 3, 3);
        alteration debuff = new alteration("Poison", alteration.Type.MALUS, 2, 4);

        p.addAlteration(buff);
        p.addAlteration(debuff);

        List<alteration> buffs = p.getBuffs();
        List<alteration> debuffs = p.getDebuffs();

        assertEquals(1, buffs.size());
        assertEquals("Posture Défensive", buffs.get(0).getName());
        assertEquals("[+ Posture Défensive +3 (3t)]", buffs.get(0).getFormattedTag());

        assertEquals(1, debuffs.size());
        assertEquals("Poison", debuffs.get(0).getName());
        assertEquals("[- Poison 4 (2t)]", debuffs.get(0).getFormattedTag());

        // Test tick et expiration
        buff.tickTurn();
        assertEquals(2, buff.getDuration());
        assertFalse(buff.isExpired());

        debuff.tickTurn();
        debuff.tickTurn();
        assertEquals(0, debuff.getDuration());
        assertTrue(debuff.isExpired());
    }

    public void testTutoLevelVarainAndKarinHooks() {
        com.eltim.rogue.level.tutoLevel tuto = new com.eltim.rogue.level.tutoLevel();
        InteractionSysteme.setCurrentLevel(tuto);

        map gameMap = new map(10, 10);
        player p = new player(1, 1);
        p.setBelief(Belief.SANS_RELIGION);

        // Test Varain
        com.eltim.rogue.entity.npc varain = new com.eltim.rogue.entity.npc(2, 1, 'V');
        varain.setName("Varain");
        InteractionSysteme.onEncounter(p, varain, gameMap);
        assertTrue("Le menu doit être ouvert avec Varain", InteractionSysteme.isMenuOpen());
        assertTrue("VarainDialogue doit être actif", com.eltim.rogue.system.dialogue.VarainDialogue.isDialogueActive());
        com.eltim.rogue.system.dialogue.VarainDialogue.closeDialogue();

        // Test échec prière si pas fidèle de Karin
        door cellDoor = new door(3, 1, 'D', doorStateEnum.OLD, 152);
        gameMap.addEntity(cellDoor);
        InteractionTile altar = new InteractionTile(2, 1, '£', "Prier le dieu Karin, le gond de la porte lâche");

        InteractionSysteme.onEncounter(p, altar, gameMap);
        InteractionSysteme.executeAction(altar.getActionName());

        assertTrue("Le joueur doit être marqué comme ayant prié", p.hasPrayedAtAltar);
        assertEquals("La porte ne doit pas s'ouvrir si le joueur n'est pas fidèle de Karin", doorStateEnum.OLD, cellDoor.getState());
        assertEquals("Un ancien autel à la gloire de Karin, dieu des voleurs.", tuto.getCustomExamineText(altar));
    }

    public void testDoorAndChestInteractable() {
        map gameMap = new map(10, 10);
        player p = new player(1, 1);
        p.setForce(16);

        // 1. Porte normale : options et ouverture
        door normalDoor = new door(2, 1, 'D', doorStateEnum.NORMAL, 0);
        List<String> doorOpts = normalDoor.getInteractionOptions(p);
        assertTrue("Options porte normale", doorOpts.contains("Ouvrir"));
        normalDoor.handleInteraction(p, "Ouvrir", gameMap);
        assertEquals(doorStateEnum.OPEN, normalDoor.getState());
        assertTrue(normalDoor.isOpen());
        assertTrue(normalDoor.getInteractionOptions(p).isEmpty());

        // 2. Porte verrouillée avec clé
        door lockedDoor = new door(3, 1, 'D', doorStateEnum.LOCKED, 42);
        key rightKey = new key("Clé en fer", 42);
        key wrongKey = new key("Clé en cuivre", 99);
        p.getInventory().add(wrongKey);

        lockedDoor.handleInteraction(p, "Déverrouiller (Clé)", gameMap);
        assertEquals("Porte doit rester fermée avec la mauvaise clé", doorStateEnum.LOCKED, lockedDoor.getState());

        p.getInventory().add(rightKey);
        lockedDoor.handleInteraction(p, "Déverrouiller (Clé)", gameMap);
        assertEquals("Porte doit s'ouvrir avec la bonne clé", doorStateEnum.OPEN, lockedDoor.getState());
        assertFalse("La clé doit avoir été consommée", p.getInventory().contains(rightKey));

        // 3. Coffre : options, ouverture et transfert de butin
        key testLoot = new key("Clé d'or", 999);
        com.eltim.rogue.entity.environment.chest c = new com.eltim.rogue.entity.environment.chest(4, 1, "Coffre Test", List.of(testLoot));
        List<String> chestOpts = c.getInteractionOptions(p);
        assertTrue(chestOpts.contains("Ouvrir"));
        assertFalse(c.isOpen());

        int initialInvSize = p.getInventory().size();
        c.handleInteraction(p, "Ouvrir", gameMap);
        assertTrue(c.isOpen());
        assertEquals("Le joueur doit avoir reçu l'objet", initialInvSize + 1, p.getInventory().size());
        assertTrue(p.getInventory().contains(testLoot));

        // Une fois pillé, le coffre ne propose plus d'options d'interaction
        assertTrue("Les coffres pillés ne doivent plus être ré-interagibles", c.getInteractionOptions(p).isEmpty());
    }

    public void testInteractionTileTagAndDescription() {
        InteractionTile it = new InteractionTile(5, 5, '£', "Prier le dieu Karin | TAG: tutoLevel | DESC: Un ancien autel à la gloire de Karin, dieu des voleurs.");
        assertEquals("Prier le dieu Karin", it.getActionName());
        assertEquals("tutoLevel", it.getTag());
        assertEquals("Un ancien autel à la gloire de Karin, dieu des voleurs.", it.getDescription());
        assertEquals("Autel de Karin", it.getName());
    }

    public void testTutoOnlyCorridorMonsterHasKey() {
        com.eltim.rogue.level.LevelLoader.LevelData data = com.eltim.rogue.level.LevelLoader.parseFile("levels/tuto.txt");
        player p = new player(15, 13);
        map m = com.eltim.rogue.level.LevelLoader.generateMap(data, p);

        int monstersWithKey = 0;
        int totalMonsters = 0;
        com.eltim.rogue.entity.monster corridorMonster = null;

        for (com.eltim.rogue.entity.base.entity e : m.getEntities()) {
            if (e instanceof com.eltim.rogue.entity.monster) {
                com.eltim.rogue.entity.monster mon = (com.eltim.rogue.entity.monster) e;
                totalMonsters++;
                if (mon.getY() == 10 && mon.getX() == 19) {
                    corridorMonster = mon;
                }
                boolean hasKey = mon.getGuaranteedLoots().stream().anyMatch(l -> l.getName().contains("Clé"));
                if (hasKey) {
                    monstersWithKey++;
                }
            }
        }

        assertNotNull("Le monstre du couloir doit exister en (19, 10)", corridorMonster);
        assertEquals("Seul le monstre du couloir doit posséder la clé", 1, monstersWithKey);
        assertTrue("Le monstre du couloir doit avoir la clé", corridorMonster.getGuaranteedLoots().stream().anyMatch(l -> l.getName().contains("Clé")));
        assertTrue("Il doit y avoir d'autres monstres sans clé dans le niveau", totalMonsters > 1);
    }

    public void testCoordinateBasedMonsterConfig() {
        com.eltim.rogue.level.LevelLoader.LevelData data = new com.eltim.rogue.level.LevelLoader.LevelData();
        data.layout.add(".....");
        data.layout.add(".M.M.");
        data.layout.add(".....");
        data.monstersConfig.put('M', "Mort vivant");
        data.specificMonstersConfig.put("3,1", "Garde d'élite | HP:50 | LOOT: Clé secrète : 42");

        map m = com.eltim.rogue.level.LevelLoader.generateMap(data, null);
        com.eltim.rogue.entity.monster m1 = (com.eltim.rogue.entity.monster) m.getEntityAt(1, 1);
        com.eltim.rogue.entity.monster m2 = (com.eltim.rogue.entity.monster) m.getEntityAt(3, 1);

        assertNotNull(m1);
        assertNotNull(m2);
        assertEquals("Mort vivant", m1.getName());
        assertEquals("Garde d'élite", m2.getName());
        assertEquals(50, m2.getMaxLifePoint());
        assertTrue(m2.getGuaranteedLoots().stream().anyMatch(l -> l.getName().contains("Clé secrète")));
        assertTrue(m1.getGuaranteedLoots().isEmpty());
    }
}

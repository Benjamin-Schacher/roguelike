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
}

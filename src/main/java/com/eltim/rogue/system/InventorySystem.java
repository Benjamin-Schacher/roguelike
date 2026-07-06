package com.eltim.rogue.system;

import com.eltim.rogue.engine.game;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.base.entity;
import com.eltim.rogue.item.base.item;
import com.eltim.rogue.item.equipement;
import com.eltim.rogue.item.weapon;
import com.eltim.rogue.item.enumerateur.itemTypeEnum;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class InventorySystem {

    public enum Column { EQUIPMENT, INVENTORY }
    public enum Tab { EQUIPMENT, CONSUMABLES, OBJECTS }
    
    private static Column currentColumn = Column.INVENTORY;
    private static Tab currentTab = Tab.EQUIPMENT;
    private static int equipmentIndex = 0;
    private static int inventoryIndex = 0;
    
    private static int currentCharacterIndex = 0;
    
    private static boolean isPromptingWeaponSlot = false;
    private static int weaponPromptIndex = 0; // 0: Right, 1: Left, 2: Secondary
    private static weapon weaponToEquip = null;
    
    private static player p;
    private static game g;
    
    public static String statusMessage = "";
    public static int statusMessageTimer = 0;

    // The dynamically filtered list of items in the right column
    private static List<item> filteredInventory = new ArrayList<>();

    public static void open(player playerRef, game gameRef) {
        p = playerRef;
        g = gameRef;
        currentColumn = Column.INVENTORY;
        currentTab = Tab.EQUIPMENT;
        currentCharacterIndex = 0;
        inventoryIndex = 0;
        equipmentIndex = 0;
        isPromptingWeaponSlot = false;
        statusMessage = "";
        updateFilteredInventory();
    }

    public static player getPlayer() { return p; }
    
    public static entity getActiveCharacter() {
        if (p == null) return null;
        if (currentCharacterIndex == 0) return p;
        if (currentCharacterIndex - 1 < p.getParty().size()) {
            return p.getParty().get(currentCharacterIndex - 1);
        }
        return p;
    }

    public static void updateFilteredInventory() {
        if (p == null) return;
        filteredInventory.clear();
        for (item it : p.getInventory()) {
            boolean isEquipment = (it instanceof weapon || it instanceof equipement);
            boolean isConsumable = (it.getType() == itemTypeEnum.POTION);
            
            if (currentTab == Tab.EQUIPMENT && isEquipment) {
                filteredInventory.add(it);
            } else if (currentTab == Tab.CONSUMABLES && isConsumable) {
                filteredInventory.add(it);
            } else if (currentTab == Tab.OBJECTS && !isEquipment && !isConsumable) {
                filteredInventory.add(it);
            }
        }
        if (inventoryIndex >= filteredInventory.size()) {
            inventoryIndex = Math.max(0, filteredInventory.size() - 1);
        }
    }

    public static List<item> getFilteredInventory() {
        return filteredInventory;
    }

    public static void update() {
        if (statusMessageTimer > 0) statusMessageTimer--;
    }

    public static void showMessage(String msg) {
        statusMessage = msg;
        statusMessageTimer = 60;
    }

    public static void handleInput(KeyEvent key) {
        int code = key.getKeyCode();

        if (isPromptingWeaponSlot) {
            if (code == KeyEvent.VK_UP) {
                weaponPromptIndex--;
                if (weaponPromptIndex < 0) weaponPromptIndex = 2;
            } else if (code == KeyEvent.VK_DOWN) {
                weaponPromptIndex++;
                if (weaponPromptIndex > 2) weaponPromptIndex = 0;
            } else if (code == KeyEvent.VK_ESCAPE) {
                isPromptingWeaponSlot = false;
                weaponToEquip = null;
            } else if (code == KeyEvent.VK_ENTER) {
                equipWeaponToSlot(weaponPromptIndex);
                isPromptingWeaponSlot = false;
                weaponToEquip = null;
                updateFilteredInventory();
            }
            return;
        }

        switch (code) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_E:
                g.closeInventory();
                break;
            case KeyEvent.VK_TAB:
                currentCharacterIndex++;
                if (currentCharacterIndex > p.getParty().size()) {
                    currentCharacterIndex = 0;
                }
                equipmentIndex = 0;
                break;
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_1:
                currentTab = Tab.EQUIPMENT;
                updateFilteredInventory();
                break;
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_2:
                currentTab = Tab.CONSUMABLES;
                updateFilteredInventory();
                break;
            case KeyEvent.VK_NUMPAD3:
            case KeyEvent.VK_3:
                currentTab = Tab.OBJECTS;
                updateFilteredInventory();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                currentColumn = (currentColumn == Column.INVENTORY) ? Column.EQUIPMENT : Column.INVENTORY;
                break;
            case KeyEvent.VK_UP:
                if (currentColumn == Column.INVENTORY) {
                    if (filteredInventory.size() > 0) {
                        inventoryIndex = (inventoryIndex - 1 + filteredInventory.size()) % filteredInventory.size();
                    }
                } else {
                    equipmentIndex = (equipmentIndex - 1 + 11) % 11;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (currentColumn == Column.INVENTORY) {
                    if (filteredInventory.size() > 0) {
                        inventoryIndex = (inventoryIndex + 1) % filteredInventory.size();
                    }
                } else {
                    equipmentIndex = (equipmentIndex + 1) % 11;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (currentColumn == Column.INVENTORY) {
                    if (filteredInventory.size() > 0) {
                        interactWithItem(inventoryIndex);
                    }
                } else {
                    unequipSlot(equipmentIndex);
                }
                break;
        }
    }

    private static void interactWithItem(int idx) {
        if (idx < 0 || idx >= filteredInventory.size()) return;
        item it = filteredInventory.get(idx);

        if (currentTab == Tab.EQUIPMENT) {
            if (it instanceof weapon) {
                weaponToEquip = (weapon) it;
                isPromptingWeaponSlot = true;
                weaponPromptIndex = 0;
            } else if (it instanceof equipement) {
                equipArmor((equipement) it);
            }
        } else if (currentTab == Tab.CONSUMABLES) {
            if (it instanceof com.eltim.rogue.item.potion) {
                entity activeChar = getActiveCharacter();
                com.eltim.rogue.item.potion pot = (com.eltim.rogue.item.potion) it;
                pot.applyEffect(activeChar, null); // Wait, player.useItem does this
                // actually we can just do:
                if (activeChar instanceof player) {
                    ((player) activeChar).useItem(it);
                } else {
                    pot.applyEffect(activeChar, null);
                    p.getInventory().remove(it);
                }
                showMessage(activeChar.getName() + " utilise " + it.getName());
                updateFilteredInventory();
            }
        } else {
            showMessage("Impossible d'utiliser cet objet ici.");
        }
    }

    private static void equipArmor(equipement eq) {
        entity activeChar = getActiveCharacter();
        if (activeChar == null) return;
        
        switch (eq.getType()) {
            case ARMOR:
                if (activeChar.armor != null) p.getInventory().add(activeChar.armor);
                activeChar.armor = eq;
                break;
            case HELMET:
                if (activeChar.helmet != null) p.getInventory().add(activeChar.helmet);
                activeChar.helmet = eq;
                break;
            case LEGGINGS:
                if (activeChar.leggings != null) p.getInventory().add(activeChar.leggings);
                activeChar.leggings = eq;
                break;
            case SHOES:
                if (activeChar.shoes != null) p.getInventory().add(activeChar.shoes);
                activeChar.shoes = eq;
                break;
            case GLOVES:
                if (activeChar.gloves != null) p.getInventory().add(activeChar.gloves);
                activeChar.gloves = eq;
                break;
            case NECKLACE:
                if (activeChar.necklace != null) p.getInventory().add(activeChar.necklace);
                activeChar.necklace = eq;
                break;
            case RING:
                if (activeChar.ring1 == null) {
                    activeChar.ring1 = eq;
                } else if (activeChar.ring2 == null) {
                    activeChar.ring2 = eq;
                } else {
                    p.getInventory().add(activeChar.ring1);
                    activeChar.ring1 = eq;
                }
                break;
            default:
                showMessage("Cet objet ne s'équipe pas.");
                return;
        }
        p.getInventory().remove(eq);
        updateFilteredInventory();
    }

    private static void equipWeaponToSlot(int slot) {
        entity activeChar = getActiveCharacter();
        if (activeChar == null) return;
        
        if (slot == 1 && weaponToEquip.isTwoHanded()) {
            showMessage("Une arme à 2 mains ne va pas en main gauche.");
            return;
        }
        if (slot == 2 && weaponToEquip.isForbidsSecondary()) {
            showMessage("Arme interdite en secondaire.");
            return;
        }

        p.getInventory().remove(weaponToEquip);

        if (slot == 0) {
            if (activeChar.rightHand != null) p.getInventory().add(activeChar.rightHand);
            activeChar.rightHand = weaponToEquip;
            if (weaponToEquip.isTwoHanded() && activeChar.leftHand != null) {
                p.getInventory().add(activeChar.leftHand);
                activeChar.leftHand = null;
                showMessage("Main gauche déséquipée (arme 2M).");
            }
        } else if (slot == 1) {
            if (activeChar.rightHand != null && activeChar.rightHand.isTwoHanded()) {
                p.getInventory().add(activeChar.rightHand);
                activeChar.rightHand = null;
                showMessage("Main droite déséquipée (arme 2M).");
            }
            if (activeChar.leftHand != null) p.getInventory().add(activeChar.leftHand);
            activeChar.leftHand = weaponToEquip;
        } else if (slot == 2) {
            if (activeChar.secondaryWeapon != null) p.getInventory().add(activeChar.secondaryWeapon);
            activeChar.secondaryWeapon = weaponToEquip;
        }

        updateFilteredInventory();
    }

    private static void unequipSlot(int idx) {
        entity activeChar = getActiveCharacter();
        if (activeChar == null) return;
        
        item unequipped = null;
        switch(idx) {
            case 0: unequipped = activeChar.helmet; activeChar.helmet = null; break;
            case 1: unequipped = activeChar.armor; activeChar.armor = null; break;
            case 2: unequipped = activeChar.leggings; activeChar.leggings = null; break;
            case 3: unequipped = activeChar.shoes; activeChar.shoes = null; break;
            case 4: unequipped = activeChar.gloves; activeChar.gloves = null; break;
            case 5: unequipped = activeChar.necklace; activeChar.necklace = null; break;
            case 6: unequipped = activeChar.ring1; activeChar.ring1 = null; break;
            case 7: unequipped = activeChar.ring2; activeChar.ring2 = null; break;
            case 8: unequipped = activeChar.rightHand; activeChar.rightHand = null; break;
            case 9: unequipped = activeChar.leftHand; activeChar.leftHand = null; break;
            case 10: unequipped = activeChar.secondaryWeapon; activeChar.secondaryWeapon = null; break;
        }
        if (unequipped != null) {
            p.getInventory().add(unequipped);
            updateFilteredInventory();
        }
    }

    public static Column getCurrentColumn() { return currentColumn; }
    public static Tab getCurrentTab() { return currentTab; }
    public static int getEquipmentIndex() { return equipmentIndex; }
    public static int getInventoryIndex() { return inventoryIndex; }
    public static boolean isPromptingWeaponSlot() { return isPromptingWeaponSlot; }
    public static int getWeaponPromptIndex() { return weaponPromptIndex; }
}

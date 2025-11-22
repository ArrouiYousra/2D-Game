package com.tlse1.twodgame.entities;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe Inventory.
 */
public class InventoryTest {
    
    private Inventory inventory;
    
    @Before
    public void setUp() {
        inventory = new Inventory();
    }
    
    @Test
    public void testAddItem() {
        assertTrue(inventory.addItem(Inventory.ItemType.DAMAGE_BOOST));
        assertEquals(1, inventory.getItemCount());
        assertEquals(1, inventory.getItemCount(Inventory.ItemType.DAMAGE_BOOST));
    }
    
    @Test
    public void testAddMultipleItems() {
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        inventory.addItem(Inventory.ItemType.SPEED_BOOST);
        inventory.addItem(Inventory.ItemType.SHIELD_POTION);
        inventory.addItem(Inventory.ItemType.HEAL_POTION);
        
        assertEquals(4, inventory.getItemCount());
        assertEquals(1, inventory.getItemCount(Inventory.ItemType.DAMAGE_BOOST));
        assertEquals(1, inventory.getItemCount(Inventory.ItemType.SPEED_BOOST));
        assertEquals(1, inventory.getItemCount(Inventory.ItemType.SHIELD_POTION));
        assertEquals(1, inventory.getItemCount(Inventory.ItemType.HEAL_POTION));
    }
    
    @Test
    public void testUnlimitedCapacity() {
        // Ajouter 100 items
        for (int i = 0; i < 100; i++) {
            assertTrue(inventory.addItem(Inventory.ItemType.DAMAGE_BOOST));
        }
        
        assertEquals(100, inventory.getItemCount());
        assertFalse(inventory.isFull());
        assertEquals(Integer.MAX_VALUE, inventory.getMaxCapacity());
    }
    
    @Test
    public void testUseItem() {
        inventory.addItem(Inventory.ItemType.HEAL_POTION);
        assertTrue(inventory.useItem(Inventory.ItemType.HEAL_POTION));
        assertEquals(0, inventory.getItemCount(Inventory.ItemType.HEAL_POTION));
        assertEquals(0, inventory.getItemCount());
    }
    
    @Test
    public void testUseItemNotInInventory() {
        assertFalse(inventory.useItem(Inventory.ItemType.HEAL_POTION));
    }
    
    @Test
    public void testUseFirstItemOfType() {
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        
        assertTrue(inventory.useItem(Inventory.ItemType.DAMAGE_BOOST));
        assertEquals(2, inventory.getItemCount(Inventory.ItemType.DAMAGE_BOOST));
    }
    
    @Test
    public void testClear() {
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        inventory.addItem(Inventory.ItemType.SPEED_BOOST);
        inventory.clear();
        
        assertEquals(0, inventory.getItemCount());
    }
    
    @Test
    public void testGetItems() {
        inventory.addItem(Inventory.ItemType.DAMAGE_BOOST);
        inventory.addItem(Inventory.ItemType.SPEED_BOOST);
        
        assertEquals(2, inventory.getItems().size());
    }
}


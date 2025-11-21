package com.tlse1.twodgame.entities.handlers;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe CombatHandler.
 */
public class CombatHandlerTest {
    
    private CombatHandler combatHandler;
    private AnimationHandler animationHandler;
    
    @Before
    public void setUp() {
        animationHandler = new AnimationHandler();
        combatHandler = new CombatHandler(100, animationHandler);
    }
    
    @Test
    public void testInitialHealth() {
        assertEquals(100, combatHandler.getMaxHealth());
        assertEquals(100, combatHandler.getHealth());
    }
    
    @Test
    public void testInitialShield() {
        combatHandler.setMaxShield(50);
        assertEquals(50, combatHandler.getMaxShield());
        assertEquals(0, combatHandler.getShield());
    }
    
    @Test
    public void testTakeDamageShieldFirst() {
        combatHandler.setMaxShield(50);
        combatHandler.setShield(50);
        
        // Prendre 30 dégâts
        combatHandler.takeDamage(30);
        
        assertEquals(20, combatHandler.getShield());
        assertEquals(100, combatHandler.getHealth());
    }
    
    @Test
    public void testTakeDamageExceedsShield() {
        combatHandler.setMaxShield(50);
        combatHandler.setShield(50);
        
        // Prendre 80 dégâts (dépasse le shield)
        combatHandler.takeDamage(80);
        
        assertEquals(0, combatHandler.getShield());
        assertEquals(70, combatHandler.getHealth()); // 100 - 30 = 70
    }
    
    @Test
    public void testTakeDamageNoShield() {
        // Pas de shield, les dégâts vont directement aux HP
        combatHandler.takeDamage(30);
        
        assertEquals(70, combatHandler.getHealth());
    }
    
    @Test
    public void testHeal() {
        combatHandler.setHealth(50);
        combatHandler.heal(20);
        
        assertEquals(70, combatHandler.getHealth());
    }
    
    @Test
    public void testHealExceedsMax() {
        combatHandler.setHealth(90);
        combatHandler.heal(20);
        
        assertEquals(100, combatHandler.getHealth()); // Ne dépasse pas le max
    }
    
    @Test
    public void testSetShield() {
        combatHandler.setMaxShield(50);
        combatHandler.setShield(30);
        
        assertEquals(30, combatHandler.getShield());
    }
    
    @Test
    public void testSetShieldExceedsMax() {
        combatHandler.setMaxShield(50);
        combatHandler.setShield(100);
        
        assertEquals(50, combatHandler.getShield()); // Ne dépasse pas le max
    }
    
    @Test
    public void testIsAlive() {
        assertTrue(combatHandler.isAlive());
        
        combatHandler.setHealth(0);
        assertFalse(combatHandler.isAlive());
    }
}


package com.tlse1.twodgame.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour l'enum Direction.
 */
public class DirectionTest {
    
    @Test
    public void testDirectionValues() {
        Direction[] directions = Direction.values();
        
        assertEquals(4, directions.length);
        assertTrue(containsDirection(directions, Direction.DOWN));
        assertTrue(containsDirection(directions, Direction.UP));
        assertTrue(containsDirection(directions, Direction.SIDE_LEFT));
        assertTrue(containsDirection(directions, Direction.SIDE));
    }
    
    @Test
    public void testDirectionValueOf() {
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.SIDE_LEFT, Direction.valueOf("SIDE_LEFT"));
        assertEquals(Direction.SIDE, Direction.valueOf("SIDE"));
    }
    
    private boolean containsDirection(Direction[] directions, Direction direction) {
        for (Direction d : directions) {
            if (d == direction) {
                return true;
            }
        }
        return false;
    }
}


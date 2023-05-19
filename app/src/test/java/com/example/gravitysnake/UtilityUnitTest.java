package com.example.gravitysnake;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.gravitysnake.Utility;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilityUnitTest {
    @Test
    public void testCollides() {
        Coordinate left = new Coordinate(0, 0);
        Coordinate right = new Coordinate(0, 0);
        assertTrue(Utility.collides(left, right, 1));
    }

    @Test
    public void testDoesNotCollide() {
        Coordinate left = new Coordinate(0, 0);
        Coordinate right = new Coordinate(1, 1);
        assertFalse(Utility.collides(left, right, 1));
    }
}
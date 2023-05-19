package com.example.gravitysnake;

import org.junit.Test;
import static org.junit.Assert.*;

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

        Coordinate left2 = new Coordinate(10, 25);
        Coordinate right2 = new Coordinate(-5, -13);
        assertTrue(Utility.collides(left2, right2, 50));
    }

    @Test
    public void testDoesNotCollide() {
        Coordinate left = new Coordinate(0, 0);
        Coordinate right = new Coordinate(1, 1);
        assertFalse(Utility.collides(left, right, 1));
    }
}
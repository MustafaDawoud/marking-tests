package ca.uwo.eng.se2205b.lab7.marking;

import ca.uwo.eng.se2205.lab7.mars.MarsPlanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link MarsPlanner}
 */
class MarsPlannerTest {


    static void checkCoords(Set<int[]> correctCoords, int[] coords) {
        assertNotNull(coords, "coords == null");

        Set<int[]> inv = invertedCoords(correctCoords);

        if (!correctCoords.contains(coords)) {
            // Could not find the coords
            assertTrue(inv.contains(coords), "Incorrect result found.");
            fail(() -> "Found coords in inverted form " + Arrays.toString(coords));
        }
    }

    @Nested
    class Small {
        int[][] topology = new int[][] {{6, 4, 3},
                                        {4, 12, 2},
                                        {8, 10, 3}};

        MarsPlanner underTest;

        Set<int[]> correctAnswers;

        @BeforeEach
        public void init() {
            underTest = new MarsPlanner(topology,
                    Arrays.asList(new int[]{ 1, 0 }, new int[]{ 2, 2 }));

            correctAnswers = new TreeSet<>(MarsPlannerTest::lexCompareArrs);
            correctAnswers.add(new int[]{ 2, 0 });
            correctAnswers.add(new int[]{ 2, 1 });
        }

        @Test
        void check() {
            int[] coords = underTest.bestLandingSpot(4);
            checkCoords(correctAnswers, coords);
        }
    }

    @Nested
    class Large {
        final int[][] topology =
                new int[][] {{8,10,16,14,10,6,3,0,1,1},
                        {12,14,20,16,12,8,4,0,2,1},
                        {14,18,24,18,16,10,0,1,3,1},
                        {14,26,26,23,20,8,0,3,2,0},
                        {18,25,28,25,20,8,0,2,0,3},
                        {13,24,26,26,14,0,0,0,2,6},
                        {8,16,24,13,3,1,1,2,6,8},
                        {4,10,3,3,0,0,5,6,8,13},
                        {4,4,2,0,0,4,9,15,16,20},
                        {2,2,2,1,0,10,14,18,23,24}};

        MarsPlanner underTest;

        Set<int[]> correctAnswers;

        @BeforeEach
        public void init() {
            underTest = new MarsPlanner(topology,
                    Arrays.asList(new int[]{ 2, 0 }, new int[]{ 8, 1 },
                            new int[]{ 7, 9 }));

            correctAnswers = new TreeSet<>(MarsPlannerTest::lexCompareArrs);
            correctAnswers.add(new int[]{ 7, 0 });
            correctAnswers.add(new int[]{ 7, 1 });
        }

        @Test
        void check() {
            int[] coords = underTest.bestLandingSpot(40);
            checkCoords(correctAnswers, coords);
        }

        @Test
        void noPath() {
            int[] coords = underTest.bestLandingSpot(3);
            assertNull(coords, () -> "Should have not found valid result, found: " + Arrays.toString(coords));
        }
    }


    private static Set<int[]> invertedCoords(Set<int[]> toInvert) {
        Set<int[]> reversed = new TreeSet<>(MarsPlannerTest::lexCompareArrs);
        for (int[] coords: toInvert) {
            reversed.add(new int[] { coords[1], coords[0] });
        }

        return reversed;
    }

    private static int lexCompareArrs(int[] lhs, int[] rhs) {

        for (int i = 0; i < lhs.length; ++i) {
            int out = Integer.compare(lhs[i], rhs[i]);
            if (out != 0) {
                return out;
            }
        }

        return 0;
    }
}
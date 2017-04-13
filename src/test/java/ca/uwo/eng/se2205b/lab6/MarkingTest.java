package ca.uwo.eng.se2205b.lab6;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.fail;

/**
 * Marking Tests for Lab 6
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MarkingTest.Insertion.class,
        MarkingTest.Selection.class,
        MarkingTest.Merge.class,
        MarkingTest.Quick.class
})
public class MarkingTest {


    private static final List<Integer> unsorted = Arrays.asList(4, 8, 8, 2, 1, 6);
    private static final List<Integer> sorted = Arrays.asList(1, 2, 4, 6, 8, 8);

    public static class Base {

        private final Supplier<? extends Sorter> sorterFactory;

        private Sorter sorter;

        Base(Supplier<? extends Sorter> sorterFactory) {
            this.sorterFactory = sorterFactory;
        }

        @Before
        public void before() {
            sorter = sorterFactory.get();
        }

        @Test
        public void simple() throws Exception {
            DelayedList<Integer> toSort = DelayedList.create(Delayed.Time.Fast, unsorted);
            sorter.sort(toSort, DelayedComparator.create(Integer::compareTo, Delayed.Time.Fast));

            assertEquals("Simple sorting of integers failed", sorted, toSort);
        }

        @Test
        public void empty() throws Exception {
            DelayedList<Integer> toSort = DelayedList.create(Delayed.Time.Fast, Collections.emptyList());
            sorter.sort(toSort, DelayedComparator.create(Integer::compareTo, Delayed.Time.Fast));

            assertEquals("Sorting empty list", Collections.emptyList(), toSort);
        }

        @Test
        public void nullList() throws Exception {
            try {
                sorter.sort(null, DelayedComparator.create(Integer::compareTo, Delayed.Time.Fast));
                fail("Sorter failed to throw from null List");
            } catch (NullPointerException | IllegalArgumentException e) {
                // expected
            }
        }

        @Test
        public void nonInteger() throws Exception {
            DelayedList<String> toSort = DelayedList.create(Delayed.Time.Fast, Arrays.asList("c", "d", "a"));
            sorter.sort(toSort, DelayedComparator.create(String::compareTo, Delayed.Time.Fast));

            assertEquals("Sort non-integers", Arrays.asList("a", "c", "d"), toSort);
        }

        @Test
        public void otherComparator() throws Exception {
            DelayedList<String> toSort = DelayedList.create(Delayed.Time.Fast, Arrays.asList("C", "d", "A"));
            sorter.sort(toSort, DelayedComparator.create(String::compareToIgnoreCase, Delayed.Time.Fast));

            assertEquals("Sort non-integer w/ non-standard comparator",
                    Arrays.asList("A", "C", "d"), toSort);
        }
    }

    public static class Insertion extends Base {

        public Insertion() {
            super(InsertionSorter::new);
        }

    }

    public static class Selection extends Base {

        public Selection() {
            super(SelectionSorter::new);
        }

    }

    public static class Merge extends Base {

        public Merge() {
            super(MergeSorter::new);
        }

    }

    public static class Quick extends Base {

        public Quick() {
            super(QuickSorter::new);
        }

    }
}
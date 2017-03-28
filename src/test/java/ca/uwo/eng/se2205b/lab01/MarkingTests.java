package ca.uwo.eng.se2205b.lab01;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Marking tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MarkingTests.MarkArray.class,
    MarkingTests.MarkLinked.class
})
public class MarkingTests {


    private static class Marker<T extends List<?>> {

        private Class<T> toTest;

        protected Constructor<T> listCtor;
        protected Constructor<T> capacityCtor;

        @SuppressWarnings("unchecked")
        Marker(Class<?> testing) {
            this.toTest = (Class<T>)testing;

            for (Constructor<T> ctor: (Constructor<T>[])(testing.getDeclaredConstructors())) {
                Parameter[] params = ctor.getParameters();
                if (params.length == 1) {
                    if (params[0].getType() == List.class) {
                        listCtor = ctor;
                    } else if (params[0].getType() == int.class) {
                        capacityCtor = ctor;
                    }
                }
            }

            // Implementations that do not have a proper List<? extends T> constructor will be broken
            assertNotNull("No List<? extends T> constructor defined", listCtor);
        }

        @SuppressWarnings("unchecked")
        protected <U> List<U> create(List<? extends U> base) {
            try {
                return (List<U>)listCtor.newInstance(base);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError("Failed construction with: " + base, e);
            }
        }

        @Test
        public void ctorsList() throws Exception {
            // Implementations that do not have a proper List<? extends T> constructor will be broken
            assertNotNull("No List<? extends T> constructor defined", listCtor);

            List<String> list = create(Arrays.asList("a", "b", "c"));

            assertEquals("Size mismatched", 3, list.size());
            assertFalse("Empty", list.isEmpty());

            list = create(Arrays.asList());
            assertEquals("Size mismatched", 0, list.size());
            assertTrue("Not empty", list.isEmpty());
        }

        @Test
        public void get() throws Exception {
            List<String> test = Arrays.asList("a", "b", "c");
            List<String> list = create(test);

            assertEquals("Size mismatched", test.size(), list.size());

            for (int i = 0; i < list.size(); ++i) {
                assertEquals("get(" + i + ") incorrect", test.get(i), list.get(i));
            }

            try {
                list.get(-1 * test.size());
                fail("Failed to throw IndexOutOfBoundsException if index < 0");
            } catch (IndexOutOfBoundsException e) { }

            try {
                list.get(test.size());
                fail("Failed to throw IndexOutOfBoundsException if index > size");
            } catch (IndexOutOfBoundsException e) { }
        }

        @Test
        public void set() throws Exception {
            List<String> test = Arrays.asList("a", "b", "c");
            List<String> list = create(test);
            assertEquals("Size mismatched", test.size(), list.size());

            for (int i = 0; i < list.size(); ++i) {
                list.set(i, "check");
                assertEquals("check", list.get(i));
            }

            try {
                list.set(-1 * test.size(), "check");
                fail("Failed to throw IndexOutOfBoundsException if index < 0");
            } catch (IndexOutOfBoundsException e) { }

            try {
                list.set(test.size(), "check");
                fail("Failed to throw IndexOutOfBoundsException if index > size");
            } catch (IndexOutOfBoundsException e) { }

            list.set(0, null);
            assertNull(list.get(0));
        }

        @Test
        public void add() throws Exception {
            List<String> test = Arrays.asList("a", "b", "c");
            List<String> list = create(Arrays.<String>asList());
            assertEquals("Size mismatched", 0, list.size());

            list.add(0, "check");
            assertEquals("Failed on adding to empty list", "check", list.get(0));
            assertEquals("Size mismatched", 1, list.size());

            for (int i = 0; i <= test.size(); ++i) {
                list = create(test);

                list.add(i, "check");
                assertEquals("Add: Size mismatched", test.size() + 1, list.size());

                for (int j = 0; j < i; ++j) {
                    assertEquals("Add @ " + i + ", failed on j = " + j,
                            test.get(j), list.get(j));
                }

                assertEquals("Add @ " + i + ", failed on j = " + i,
                        "check", list.get(i));

                for (int j = i+1; j < test.size() + 1; ++j) {
                    assertEquals("Add @ " + i + ", failed on j = " + j,
                            test.get(j-1), list.get(j));
                }
            }

            list = create(test);

            try {
                list.add(-1 * test.size(), "check");
                fail("Failed to throw IndexOutOfBoundsException if index < 0");
            } catch (IndexOutOfBoundsException e) { }

            try {
                list.add(test.size()+1, "check");
                fail("Failed to throw IndexOutOfBoundsException if index > size");
            } catch (IndexOutOfBoundsException e) { }

            list.add(0, null);
            assertNull(list.get(0));
        }

        @Test
        public void remove() throws Exception {
            List<String> test = Arrays.asList("a", "b", "c");

            List<String> list = create(Arrays.<String>asList());
            assertEquals("Size mismatched", 0, list.size());
            try {
                list.remove(0);
                fail("Successfully removed from empty list (should have thrown)");
            } catch (IndexOutOfBoundsException e) {}

            for (int i = 0; i < test.size(); ++i) {
                list = create(test);

                String v = list.remove(i);
                assertEquals("Remove @ " + i + " wrong value", test.get(i), v);
                assertEquals("Remove @ " + i + " size mismatched",
                        test.size() - 1, list.size());

                for (int j = 0; j < i; ++j) {
                    assertEquals("Remove @ " + i + ", failed on j = " + j,
                            test.get(j), list.get(j));
                }

                for (int j = i; j < list.size(); ++j) {
                    assertEquals("Remove @ " + i + ", failed on j = " + (j+1),
                            test.get(j + 1), list.get(j));
                }
            }

            list = create(test);

            try {
                list.remove(-1 * test.size());
                fail("Failed to throw IndexOutOfBoundsException if index < 0");
            } catch (IndexOutOfBoundsException e) { }

            try {
                list.remove(test.size());
                fail("Failed to throw IndexOutOfBoundsException if index >= size");
            } catch (IndexOutOfBoundsException e) { }
        }

        @Test
        public void toStringCheck() throws Exception {
            List<String> list = create(Arrays.<String>asList());

            assertEquals("toString failed: empty", "[]", list.toString());

            list = create(Arrays.asList("a"));
            assertEquals("toString failed: [a]", "[a]", list.toString());

            list = create(Arrays.asList("a", "b"));
            assertEquals("toString failed: [a, b]", "[a, b]", list.toString());
        }

        @Test
        public void equalsCheck() throws Exception {
            List<String> test = Arrays.asList();
            List<String> list = create(Arrays.<String>asList());

            assertEquals("Equals failed: empty", test, list);

            test = Arrays.asList("a");
            list = create(test);
            assertEquals("Equals failed: [a]", test, list);

            test = Arrays.asList("a", "b", null);
            list = create(test);
            assertEquals("Equals failed: [a, b, null]", test, list);
        }
    }

    public static class MarkLinked extends Marker<MyArrayList<String>> {
        public MarkLinked() {
            super(MyLinkedList.class);
        }
    }

    public static class MarkArray extends Marker<MyArrayList<String>> {

        public MarkArray() {
            super(MyArrayList.class);
        }

        @Test
        public void ctorsCapacity() throws Exception {
            // Create a list with an initial capacity
            assertNotNull("No List<? extends T> constructor defined", capacityCtor);

            List<String> list = capacityCtor.newInstance(5);

            assertEquals("Size mismatched", 0, list.size());
            assertTrue("Not empty", list.isEmpty());
        }
    }

}

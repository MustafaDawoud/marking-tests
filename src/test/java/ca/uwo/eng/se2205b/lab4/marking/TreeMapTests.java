package ca.uwo.eng.se2205b.lab4.marking;

import ca.uwo.eng.se2205b.lab4.AVLTreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kevin on 22/03/2017.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreeMapTests.Size.class,
        TreeMapTests.Put.class,
        TreeMapTests.Get.class,
        TreeMapTests.Contains.class,
        TreeMapTests.EntrySet.class,
        TreeMapTests.Equals.class
})
public class TreeMapTests {

    private static class Base {
        AVLTreeMap<String, String> underTest = new AVLTreeMap<>();
    }


    public static class Size extends Base {

        @Test
        public void empty() {
            assertEquals(0, underTest.size());

        }

        @Test
        public void single() {
            underTest.put("a", "b");
            assertEquals(1, underTest.size());
        }

        @Test
        public void multi() {
            underTest.put("a", "b");
            underTest.put("c", "b");
            underTest.put("b", "b");
            assertEquals(3, underTest.size());
        }
    }

    public static class Equals extends Base {

        @Test
        public void sameInstance() {
            assertTrue("Same instance differs", underTest.equals(underTest));
        }

        @Test
        public void bothEmpty() {
            assertTrue("Two maps should be equal", underTest.equals(new HashMap<>()));
        }

        @Test
        public void singleElement() {
            underTest.put("a", "b");

            HashMap<String, String> hash = new HashMap<>();
            hash.put("a", "b");
            assertTrue("Single element", underTest.equals(hash));
        }

        @Test
        public void multiElement() {
            underTest.put("a", "b");
            underTest.put("c", "d");

            HashMap<String, String> hash = new HashMap<>();
            hash.put("c", "d");
            hash.put("a", "b");
            assertTrue("Single element", underTest.equals(hash));
        }
    }


    public static class Put extends Base {

        @Test
        public void intoEmpty() {
            assertNull("New key returns null", underTest.put("Bob", "Burgers"));
            assertEquals("Invalid size", 1, underTest.size());
        }

        @Test
        public void nullKey() {
            try {
                underTest.put(null, "bad");
                fail("Should have thrown NPE or IllegalArgumentException");
            } catch (NullPointerException | IllegalArgumentException e) {
                // no fail! :)
            }
        }

        @Test
        public void replaceKey() {
            assertNull("New key returns null", underTest.put("Bob", "Burgers"));
            assertEquals("Old value was not returned", "Burgers", underTest.put("Bob", "Something Else"));
        }
    }

    public static class Get extends Base {

        @Test
        public void intoEmpty() {
            assertNull("Did not return null from empty map", underTest.get("Bob"));
        }

        @Test
        public void nullKey() {
            try {
                underTest.get(null);
                fail("Should have thrown NPE or IllegalArgumentException");
            } catch (NullPointerException | IllegalArgumentException e) {
                // no fail! :)
            }
        }

        @Test
        public void wrongType() {
            try {
                assertNull(underTest.get(2));

                underTest.put("2", "bob");
                assertNull(underTest.get(2));

                fail("Invalid key did not throw");
            } catch (ClassCastException | IllegalArgumentException e) {
                // expected
            }
        }
    }

    public static class Contains extends Base {

        @Test
        public void empty() {
            assertFalse("Did not return null from empty map", underTest.containsKey("Bob"));
            assertFalse("Did not return null from empty map", underTest.containsValue("Bob"));
        }

        @Test
        public void nullKey() {
            try {
                underTest.containsKey(null);
                fail("Should have thrown NPE or IllegalArgumentException");
            } catch (NullPointerException | IllegalArgumentException e) {
                // no fail! :)
            }
        }

        @Test
        public void nullValue_notPresent() {
            assertFalse("should return true", underTest.containsValue(null));
        }

        @Test
        public void nullValue_present() {
            underTest.put("bob", null);
            assertTrue("should return true", underTest.containsValue(null));
        }


        @Test
        public void wrongType_key() {
            try {
                assertFalse(underTest.containsKey(2));

                underTest.put("2", "bob");
                assertFalse(underTest.containsKey(2));

                fail("Invalid key did not throw");
            } catch (ClassCastException | IllegalArgumentException e) {
                // expected
            }
        }

        @Test
        public void wrongType_value() {
            assertFalse(underTest.containsValue(2));

            underTest.put("2", "2");
            assertFalse(underTest.containsValue(2));
        }
    }

    public static class EntrySet extends Base {

        List<String> keys = Arrays.asList("a", "b", "c", "d");

        public EntrySet() {
            for (String key : keys) {
                underTest.put(key, "a");
            }
        }

        @Test
        public void present() {

            Set<Map.Entry<String, String>> entries = underTest.entrySet();
            Iterator<Map.Entry<String, String>> eit = entries.iterator();
            Iterator<String> lit = keys.iterator();

            while (lit.hasNext() && eit.hasNext()) {
                assertEquals("Keys are in the wrong order", lit.next(), eit.next().getKey());
            }
        }


        @Test
        public void order() {
            Set<Map.Entry<String, String>> entries = underTest.entrySet();
            Iterator<Map.Entry<String, String>> i1 = entries.iterator();
            Iterator<Map.Entry<String, String>> i2 = entries.iterator();

            assertTrue("Iterator is empty", i1.hasNext());
            assertNotNull(i2.next());
            while (i2.hasNext()) {
                Map.Entry<String, String> e1 = i1.next();
                Map.Entry<String, String> e2 = i2.next();

                assertNotNull(e1);
                assertNotNull(e2);

                assertTrue("i1 {" + e1 + "} > i2 {" + e2 + "}",
                        e1.getKey().compareTo(e2.getKey()) < 0);
            }
        }

        @Test
        public void remove() {
            Set<Map.Entry<String, String>> entries = underTest.entrySet();
            Iterator<Map.Entry<String, String>> it = entries.iterator();

            it.next();
            Map.Entry<String, String> e1 = it.next();

            it.remove();

            assertFalse("Key is still found in map", underTest.containsKey(e1.getKey()));
        }
    }

}

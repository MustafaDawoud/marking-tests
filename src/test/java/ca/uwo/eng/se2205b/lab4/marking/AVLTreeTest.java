package ca.uwo.eng.se2205b.lab4.marking;

import ca.uwo.eng.se2205b.lab4.AVLTree;
import ca.uwo.eng.se2205b.lab4.Tree;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.List;

import static ca.uwo.eng.se2205b.lab4.BinaryTreeAssertions.checkStructure;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevin on 22/03/2017.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AVLTreeTest.ComparatorUse.class,
        AVLTreeTest.Contains.class,
        AVLTreeTest.Equals.class,
        AVLTreeTest.HashCode.class,
        AVLTreeTest.Height.class,
        AVLTreeTest.IsBalanced.class,
        AVLTreeTest.IsProper.class,
        AVLTreeTest.Iterators.class,
        AVLTreeTest.Put.class,
        AVLTreeTest.Remove.class,
        AVLTreeTest.SizeAndEmpty.class,
})
public class AVLTreeTest {

    // small base class to save some lines
    private static class AVLTreeTestBase {

        final AVLTree<Integer> underTest = new AVLTree<>();
    }

    //      44
    //  /       \
    // 17        62
    //   \     /    \
    //   32   50    78
    //       /  \     \
    //      48  54     88
    private static final List<Integer> LARGE_TREE_VALUES = Arrays.asList(44, 17, 62, 50, 78, 32, 48, 54, 88);


    //     44
    //   /    \
    // 17      62
    //   \
    //   32
    private static final List<Integer> SMALL_TREE_VALUES = Arrays.asList(44, 17, 62, 32);

    private static void buildLargeBalancedTree(AVLTree<Integer> tree) {

        for (int v : LARGE_TREE_VALUES) {
            tree.put(v);
        }
    }

    private static void buildBalancedTree(AVLTree<Integer> tree) {

        for (int v : SMALL_TREE_VALUES) {
            tree.put(v);
        }
    }

    public static class Remove extends AVLTreeTestBase {

        @Test
        public void empty() {
            assertFalse("Successfully removed from empty tree", underTest.remove(0));
            assertEquals("Successfully removed from empty tree", 0, underTest.size());
        }

        @Test
        public void notPresent() {
            buildSmallTree(underTest);

            assertFalse("Successfully removed non-existant element", underTest.remove(20));
            assertEquals("Size changed, should not have", SMALL_TREE_VALUES.size(), underTest.size());
        }

        @Test
        public void noRebalance_leaf() {
            buildLargeBalancedTree(underTest);

            assertTrue("Failed to remove leaf node", underTest.remove(48));
            checkStructure(Arrays.asList(44,
                    17, null, 32, null, null,
                    62, 50, null, 54, null, null, 78, null, 88, null, null),
                    underTest);
        }

        @Test
        public void noRebalance_interior() {
            buildLargeBalancedTree(underTest);

            assertTrue("Failed to remove interior node", underTest.remove(50));
            checkStructure(Arrays.asList(44,
                    17, null, 32, null, null,
                    62, 54, 48, null, null, null, 78, null, 88, null, null),
                    underTest);
        }

        @Test
        public void singleRotation() throws Exception {
            buildLargeBalancedTree(underTest);

            underTest.put(14);
            underTest.put(10);
            checkStructure(Arrays.asList(44,
                    17, 14, 10, null, null, null, 32, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);

            assertTrue("Failed to remove interior node with single rotation", underTest.remove(32));
            checkStructure(Arrays.asList(44,
                    14, 10, null, null, 17, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);

        }

        @Test
        public void doubleRotation() throws Exception {
            buildLargeBalancedTree(underTest);

            underTest.put(14);
            underTest.put(20);
            underTest.put(40);
            checkStructure(Arrays.asList(44,
                    17, 14, null, null, 32, 20, null, null, 40, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);

            assertTrue("Failed to remove interior node with no rotation", underTest.remove(32));
            checkStructure(Arrays.asList(44,
                    17, 14, null, null, 40, 20, null, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);

            assertTrue("Failed to remove leaf node with double rotation", underTest.remove(14));
            checkStructure(Arrays.asList(44,
                    20, 17, null, null, 40, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);

        }

        @Test
        public void throwsNPE() {
            try {
                assertFalse("Must fail to remove null or throw NPE", underTest.remove(null));
            } catch (NullPointerException npe) {
                // expected
            }
        }
    }

    private static void buildSmallTree(AVLTree<Integer> underTest) {
        buildBalancedTree(underTest);

        assertEquals("Could not insert all values", SMALL_TREE_VALUES.size(), underTest.size());

        checkStructure(Arrays.asList(44, 17, null, 32, null, null, 62, null, null),
                underTest);
    }

    public static class Equals extends AVLTreeTestBase {

        @Test
        public void sameInstance() {
            assertTrue("same instance not equal", underTest.equals(underTest));
        }


        @Test
        public void sameStructure() {
            AVLTree<Integer> other = new AVLTree<>();
            buildBalancedTree(underTest);
            buildBalancedTree(other);

            assertTrue("same structure not equal", underTest.equals(other));
        }

        @Test
        public void sameValues() {
            AVLTree<Integer> other = new AVLTree<>();
            buildBalancedTree(underTest);

            other.put(-4);
            other.put(-3);
            buildBalancedTree(other);
            other.remove(-4);
            other.remove(-3);

            assertTrue("same values not equal", underTest.equals(other));
        }

    }

    public static class HashCode extends AVLTreeTestBase {

        @Test
        public void sameInstance() {
            assertEquals("same instance not equal", underTest.hashCode(), underTest.hashCode());
        }


        @Test
        public void sameStructure() {
            AVLTree<Integer> other = new AVLTree<>();
            buildBalancedTree(underTest);
            buildBalancedTree(other);

            assertEquals("same structure not equal", other.hashCode(), underTest.hashCode());
        }

        @Test
        public void sameValues() {
            AVLTree<Integer> other = new AVLTree<>();
            buildBalancedTree(underTest);

            other.put(-4);
            other.put(-3);
            buildBalancedTree(other);
            other.remove(-4);
            other.remove(-3);

            assertEquals("same values not equal", other.hashCode(), underTest.hashCode());
        }

        @Test
        public void changeValues() {

            int old = underTest.hashCode();

            underTest.put(-1);
            assertNotEquals("HashCode did not change with insert", old, underTest.hashCode());
        }

    }

    public static class Put extends AVLTreeTestBase {

        @Test
        public void simple() throws Exception {
            buildSmallTree(underTest);

            assertTrue("Failed to insert: 12", underTest.put(12));
            checkStructure(Arrays.asList(44, 17, 12, null, null, 32, null, null, 62, null, null),
                    underTest);

            assertFalse("Successfully to inserted: 12 (already present)", underTest.put(12));
            checkStructure(Arrays.asList(44, 17, 12, null, null, 32, null, null, 62, null, null),
                    underTest);
        }

        @Test
        public void singleRotation() {
            buildSmallTree(underTest);

            assertTrue("Failed to insert: 34 (single rotation)", underTest.put(34));
            checkStructure(Arrays.asList(44, 32, 17, null, null, 34, null, null, 62, null, null, null),
                    underTest);
        }

        @Test
        public void recursiveBalance() {
            buildSmallTree(underTest);

            assertTrue("Failed to insert: 67 (single rotation -> single rotation)", underTest.put(67));
            checkStructure(Arrays.asList(44, 17, null, 32, null, null, 62, null, 67, null, null),
                    underTest);
        }

        @Test
        public void doubleRotation() {
            buildSmallTree(underTest);

            assertTrue("Failed to insert: 30 (double rotation)", underTest.put(30));
            checkStructure(Arrays.asList(44, 30, 17, null, null, 32, null, null, 62, null, null),
                    underTest);
        }

        @Test
        public void doubleRotationWithSubtrees() {
            buildLargeBalancedTree(underTest);

            assertEquals("Could not insert all values", LARGE_TREE_VALUES.size(), underTest.size());

            assertTrue("Failed to insert: 58 (double rotation)", underTest.put(58));
            checkStructure(Arrays.asList(50,
                    44, 17, null, 32, null, null, 48, null, null,
                    62, 54, null, 58, null, null, 78, null, 88, null, null),
                    underTest);
        }

        @Test
        public void largeTree() {
            buildLargeBalancedTree(underTest);

            assertEquals("Could not insert all values", LARGE_TREE_VALUES.size(), underTest.size());

            checkStructure(Arrays.asList(44, 17, null, 32, null, null,
                    62, 50, 48, null, null, 54, null, null, 78, null, 88, null, null),
                    underTest);
        }

        @Test
        public void throwsNPE() {
            try {
                assertFalse("Must fail to remove null or throw NPE", underTest.put(null));
            } catch (NullPointerException npe) {
                // expected
            }
        }
    }


    public static class SizeAndEmpty extends AVLTreeTestBase {

        @Test
        public void empty() throws Exception {
            assertEquals("size of empty tree should be zero", 0, underTest.size());
            assertTrue("Empty tree does not return true", underTest.isEmpty());
        }

        @Test
        public void single() throws Exception {
            underTest.put(4);
            assertEquals("size of empty tree should be zero", 1, underTest.size());
            assertFalse("Single element tree does not return false", underTest.isEmpty());
        }

        @Test
        public void multiple() throws Exception {
            int size = 0;
            for (int i = 2; i < 6; ++i) {
                underTest.put(i);
                size++;

                assertEquals("size of tree incorrect", size, underTest.size());
                assertFalse("Single element tree does not return false", underTest.isEmpty());
            }
        }
    }

    public static class Height extends AVLTreeTestBase {

        @Test
        public void empty() throws Exception {
            assertEquals("Empty tree should have height zero", 0, underTest.height());
        }

        @Test
        public void single() throws Exception {
            underTest.put(4);
            assertEquals("height of single element tree should be 1", 1, underTest.height());
        }

        @Test
        public void two() throws Exception {
            for (int i = 0; i < 2; ++i) {
                underTest.put(i);
            }

            assertEquals("Height of two element tree should be 2", 2, underTest.height());

            for (int i = 4; i < 6; ++i) {
                underTest.put(i);
            }

            assertEquals("Height of four element tree should be 3", 3, underTest.height());

        }
    }

    public static class Iterators {

        private final AVLTree<Integer> underTest = new AVLTree<>();

        @Test
        public void inOrder() throws Exception {
            buildBalancedTree(underTest);

            List<Integer> inOrder = Arrays.asList(17, 32, 44, 62);

            assertEquals("In-order traversal is incorrect",
                    inOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.InOrder)));
        }

        @Test
        public void preOrder() throws Exception {
            buildBalancedTree(underTest);

            List<Integer> preOrder = Arrays.asList(44, 17, 32, 62);

            assertEquals("Pre-order traversal is incorrect",
                    preOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.PreOrder)));
        }

        @Test
        public void postOrder() throws Exception {
            buildBalancedTree(underTest);
            List<Integer> postOrder = Arrays.asList(32, 17, 62, 44);

            assertEquals("Post-order traversal is incorrect",
                    postOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.PostOrder)));
        }
    }

    public static class Contains extends AVLTreeTestBase {

        @Test
        public void check() {
            buildSmallTree(underTest);

            // Actually in the tree, not in..

            assertTrue("Does not contain 32", underTest.contains(32));
            assertTrue("Does not contain 17", underTest.contains(17));
            assertFalse("Does contain 22", underTest.contains(22));
            assertFalse("Does contain 6", underTest.contains(6));
        }
    }

    public static class IsProper extends AVLTreeTestBase {
        @Test
        public void empty() {
            assertTrue("empty tree is full", underTest.isProper());
        }

        @Test
        public void single() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("single element tree is full", underTest.isProper());
        }

        @Test
        public void two() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("Could not insert element", underTest.put(5));
            assertFalse("single element tree is full", underTest.isProper());
        }

        @Test
        public void three() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("Could not insert element", underTest.put(5));
            assertTrue("Could not insert element", underTest.put(1));
            assertTrue("Full tree is not full", underTest.isProper());
        }

        @Test
        public void multiLevel() {
            buildSmallTree(underTest);

            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("Full tree is not full", underTest.isProper());
        }
    }

    public static class IsBalanced extends AVLTreeTestBase {
        @Test
        public void empty() {
            assertTrue("empty tree is balanced", underTest.isBalanced());
        }

        @Test
        public void single() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("single element tree is balanced", underTest.isBalanced());
        }

        @Test
        public void two() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("Could not insert element", underTest.put(5));
            assertTrue("double element tree is balanced", underTest.isBalanced());
        }

        @Test
        public void three() {
            assertTrue("Could not insert element", underTest.put(4));
            assertTrue("Could not insert element", underTest.put(5));
            assertTrue("Could not insert element", underTest.put(1));
            assertTrue("Full tree is not full", underTest.isBalanced());
        }

        @Test
        public void multiLevel() {
            buildSmallTree(underTest);
            assertTrue("Balanced tree is not balanced", underTest.isBalanced());
        }
    }

    public static class ComparatorUse {
        private AVLTree<String> underTest = new AVLTree<>(String::compareToIgnoreCase);

        @Test
        public void put() {
            assertTrue(underTest.put("a"));
            assertFalse("Successfully inserted duplicate (by comparator)", underTest.put("A"));
            assertTrue(underTest.put("b"));

            checkStructure(Arrays.asList("a", null, "b", null, null), underTest);
        }

        @Test
        public void put_rotation() {
            assertTrue(underTest.put("a"));
            assertTrue(underTest.put("B"));
            assertTrue(underTest.put("c"));

            checkStructure(Arrays.asList("B", "a", null, null, "c", null, null), underTest);
        }

        @Test
        public void contains() {
            assertTrue(underTest.put("b"));
            assertTrue(underTest.put("A"));
            assertTrue(underTest.put("c"));

            checkStructure(Arrays.asList("b", "A", null, null, "c", null, null), underTest);

            assertTrue(underTest.contains("A"));
            assertTrue(underTest.contains("a"));
            assertTrue(underTest.contains("C"));
            assertTrue(underTest.contains("B"));
        }

        @Test
        public void remove() {
            assertTrue(underTest.put("D"));
            assertTrue(underTest.put("a"));
            assertTrue(underTest.put("g"));
            assertTrue(underTest.put("B"));
            assertTrue(underTest.put("e"));
            assertTrue(underTest.put("Z"));
            assertTrue(underTest.put("f"));

            checkStructure(Arrays.asList("D",
                    "a", null, "B", null, null,
                    "g", "e", null, "f", null, null, "Z", null, null),
                    underTest);

            assertTrue(underTest.remove("A"));
            checkStructure(Arrays.asList("e",
                    "D", "B", null, null, null,
                    "g", "f", null, null, "Z", null, null),
                    underTest);
        }
    }
}

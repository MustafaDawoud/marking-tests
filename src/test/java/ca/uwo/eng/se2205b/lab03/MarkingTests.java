package ca.uwo.eng.se2205b.lab03;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sun.text.normalizer.Trie;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Created by tyler on 2017-03-07.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MarkingTests.BinarySearchTreeTests.class,
        MarkingTests.TrieTests.class,
})
public class MarkingTests {

    public static class BinarySearchTreeTests {

        private final BinarySearchTree<Integer> underTest = new BinarySearchTree<>();

        @Test
        public void sizeAndIsEmpty() throws Exception {
            assertEquals(0, underTest.size());
            assertTrue(underTest.isEmpty());

            underTest.put(5);
            assertEquals(1, underTest.size());
            assertFalse(underTest.isEmpty());

            underTest.put(4);
            assertEquals(2, underTest.size());
            assertFalse(underTest.isEmpty());

            underTest.put(1);
            assertEquals(3, underTest.size());
            assertFalse(underTest.isEmpty());

            underTest.put(6);
            assertEquals(4, underTest.size());
            assertFalse(underTest.isEmpty());
        }

        @Test
        public void height() throws Exception {
            assertEquals(0, underTest.height());

            underTest.put(5);
            assertEquals(1, underTest.height());

            underTest.put(4);
            assertEquals(2, underTest.height());

            underTest.put(8);
            assertEquals(2, underTest.height());

            underTest.put(7);
            assertEquals(3, underTest.height());
        }

        /**
         * Builds the following tree:
         * <p>
         * 10
         * /    \
         * 5      20
         * /  \    /
         * 3    8  14
         */
        private void buildTree(BinarySearchTree<Integer> tree) {
            tree.put(10);
            tree.put(20);
            tree.put(5);
            tree.put(3);
            tree.put(14);
            tree.put(8);
        }

        private <E extends Comparable<E>> void checkPreOrderRec(Iterator<E> expectedIter, BinarySearchTree<E> tree, BinarySearchTree.BinaryNode<E> n) {

            assertTrue("Tree is too large, no more elements", expectedIter.hasNext());
            E expected = expectedIter.next();

            if (expected == null) {
                assertNull("Node was not null\n" + tree, n);
            } else {
                assertNotNull("Node is null, supposed to have element\n" + tree, n);
                assertEquals("Node element is incorrect\n" + tree, expected, n.getElement());

                checkPreOrderRec(expectedIter, tree, n.getLeft());
                checkPreOrderRec(expectedIter, tree, n.getRight());
            }
        }

        private <E extends Comparable<E>> void checkStructure(List<E> expected, BinarySearchTree<E> tree) {
            assertEquals("Tree is incorrect size",
                    expected.stream().filter(Objects::nonNull).count(), tree.size());
            checkPreOrderRec(expected.iterator(), tree, (BinarySearchTree.BinaryNode<E>) tree.getRoot());
        }


        @Test
        public void put() throws Exception {
            buildTree(underTest);

            checkStructure(Arrays.asList(10, 5, 3, null, null, 8, null, null, 20, 14, null, null, null), underTest);

            assertFalse(underTest.put(10));

            assertTrue(underTest.put(16));
            assertEquals(7, underTest.size());
        }

        @Test
        public void remove_root() throws Exception {

            BinarySearchTree<Integer> tree = new BinarySearchTree<>();
            tree.put(5);

            assertTrue("Failed to remove root, leaf", tree.remove(5));
            checkStructure(Arrays.asList(new Integer[]{null}), tree);

            tree.put(5);
            tree.put(4);
            assertTrue("Failed to remove root, 1-child", tree.remove(5));
            checkStructure(Arrays.asList(4, null, null), tree);

            tree.put(3);
            tree.put(6);
            assertTrue("Failed to remove root, 2-child", tree.remove(4));
            checkStructure(Arrays.asList(6, 3, null, null, null), tree);
        }

        @Test
        public void remove_leaf() throws Exception {
            buildTree(underTest);

            assertTrue("Failed to remove leaf node", underTest.remove(14));
            checkStructure(Arrays.asList(10, 5, 3, null, null, 8, null, null, 20, null, null), underTest);
        }

        @Test
        public void remove() throws Exception {
            buildTree(underTest);

            assertTrue("Failed to remove 1-child", underTest.remove(20));
            checkStructure(Arrays.asList(10, 5, 3, null, null, 8, null, null, 14, null, null), underTest);

            assertTrue("Failed to remove 2-child", underTest.remove(5));
            checkStructure(Arrays.asList(10, 8, 3, null, null, null, 14, null, null, null, null), underTest);
        }

        @Test
        public void iterator_inOrder() throws Exception {
            buildTree(underTest);

            List<Integer> inOrder = Arrays.asList(3, 5, 8, 10, 14, 20);

            assertEquals("In-order traversal is incorrect",
                    inOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.InOrder)));
        }

        @Test
        public void iterator_preOrder() throws Exception {
            buildTree(underTest);
            List<Integer> preOrder = Arrays.asList(10, 5, 3, 8, 20, 14);

            assertEquals("Pre-order traversal is incorrect",
                    preOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.PreOrder)));
        }

        @Test
        public void iterator_postOrder() throws Exception {
            buildTree(underTest);
            List<Integer> postOrder = Arrays.asList(3, 8, 5, 14, 20, 10);

            assertEquals("Post-order traversal is incorrect",
                    postOrder, Lists.newArrayList(underTest.iterator(Tree.Traversal.PostOrder)));

        }

        @Test
        public void contains() throws Exception {
            buildTree(underTest);

            assertTrue("Does not contain 3", underTest.contains(3));
            assertTrue("Does not contain 10", underTest.contains(10));
            assertFalse("Does not contain 22", underTest.contains(22));
            assertFalse("Does not contain 6", underTest.contains(6));
        }

        @Test
        public void isProper() throws Exception {
            assertTrue("Empty tree is not complete", (new BinarySearchTree<String>()).isProper());

            buildTree(underTest);
            assertFalse(underTest.isProper());

            assertTrue(underTest.put(22));
            assertTrue(underTest.isProper());
        }

        @Test
        public void isBalanced() throws Exception {
            BinarySearchTree<Integer> tree = new BinarySearchTree<>();
            assertTrue("Empty tree is not balanced", tree.isBalanced());
            tree.put(4);
            assertTrue("Single-element tree is not balanced", tree.isBalanced());

            buildTree(underTest);
            assertTrue("Full tree is not balanced", underTest.isBalanced());

            assertTrue("Failed to add element", underTest.put(-5));
            assertTrue("Balanced tree is unbalanced", underTest.isBalanced());

            assertTrue("Failed to add another element", underTest.put(0));
            assertFalse("Balanced tree is unbalanced", underTest.isBalanced());
        }
    }

    public static class TrieTests {

        private Trie underTest = new LinkedTrie();

        @Test
        public void size() throws Exception {
            assertEquals("Size of Trie is incorrect",0, underTest.size());

            underTest.put("wat");
            assertEquals("Size of Trie is incorrect",1, underTest.size());

            underTest.put("wat");
            assertEquals("Size of Trie is incorrect",1, underTest.size());

            TreeSet<String> set = new TreeSet<>();
            set.add("wat");
            set.add("water");
            set.add("waten");
            underTest.putAll(set);

            assertEquals("Size of Trie is incorrect",3, underTest.size());
        }

        @Test
        public void isEmpty() throws Exception {
            assertTrue("Trie was not empty",underTest.isEmpty());

            underTest.put("wat");
            assertFalse("Trie is said to be empty when it has an element",underTest.isEmpty());
        }

        @Test
        public void put() throws Exception {
            assertTrue("Unable to add a word to the Trie",underTest.put("wat"));
            assertFalse("Word already exists in the Trie",underTest.put("wat"));

            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wat"));

            assertTrue("Unable to add a word to the Trie",underTest.put("other"));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("other"));

            assertTrue("Unable to add a word to the Trie",underTest.put("wa"));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wa"));

            assertFalse("Word already exists in the Trie",underTest.put("wat"));
        }

        @Test
        public void putAll() throws Exception {

            TreeSet<String> set = new TreeSet<>();
            set.add("wat");
            assertEquals("Number of words inserted is incorrect",1, underTest.putAll(set));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wat"));

            set.add("water");
            set.add("other");
            assertEquals("Number of words inserted is incorrect",2, underTest.putAll(set));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("water"));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("other"));

            set.clear();
            assertEquals("Number of words inserted is incorrect",0, underTest.putAll(set));

            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wat"));

            set.add("wa");
            set.add("oth");

            assertEquals("Number of words inserted is incorrect",2, underTest.putAll(set));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wa"));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("oth"));
        }

        @Test
        public void getNextN() throws Exception {
            TreeSet<String> set = new TreeSet<>();
            set.add("wat");
            set.add("water");
            set.add("waters");
            set.add("wah");
            set.add("waste");

            assertEquals("Number of words inserted is incorrect",5, underTest.putAll(set));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("waste"));
            assertTrue("Previously added word could not be found in the Trie",underTest.contains("wat"));

            SortedSet<String> fromW = underTest.getNextN("w", 6);
            assertEquals("Number of retrieved words differs from the requested number",5, fromW.size());

            assertEquals("Retrieved word set differs from the word set of the Trie",set, fromW);

            SortedSet<String> fromWa = underTest.getNextN("wa", 6);
            assertEquals("Retrieved word set differs from the word set of the Trie",set, fromWa);

            TreeSet<String> e_fromWa2 = new TreeSet<>();
            e_fromWa2.add("waste");
            e_fromWa2.add("wah");

            fromWa = underTest.getNextN("wa", 2);
            assertEquals("Retrieved word set differs from the comparing word set",e_fromWa2, fromWa);

            SortedSet<String> fromWaters = underTest.getNextN("waters", 10);

            TreeSet<String> e_fromWaters10 = new TreeSet<>();
            e_fromWaters10.add("waters");
            assertEquals("Retrieved word set differs from the comparing word set",e_fromWaters10, fromWaters);
        }
    }
}

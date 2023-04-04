import arx.dukalis.collections.RedBlackTree;
import arx.dukalis.unit.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static arx.dukalis.unit.api.Assertions.*;

public class RedBlackTreeTest {

    @Test
    void properlyHandlesInsertionAndSearch() {
        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));
        tree.add(5);
        tree.add(1);
        tree.add(10);

        assertTrue(tree.contains(1));
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(10));

        assertFalse(tree.contains(42));
    }

    @Test
    void properlyHandlesRemoval() {
        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));
        tree.add(42);
        tree.add(10);
        tree.add(404);
        tree.add(451);

        tree.remove(404);

        assertTrue(tree.contains(42));
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(451));

        assertFalse(tree.contains(404));

        tree.remove(451);

        assertFalse(tree.contains(451));

        tree.remove(10);
        tree.remove(42);

        assertFalse(tree.contains(10));
        assertFalse(tree.contains(42));
    }

    @Test
    void properlyHandlesSize() {
        final int EXPECTED_SIZE = 10_000;

        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));

        assertEquals(0, tree.size());

        for (int i = 0; i < EXPECTED_SIZE; i++) {
            tree.add(i);
        }

        assertEquals(EXPECTED_SIZE, tree.size());
        assertFalse(tree.isEmpty());

        tree.remove(42);

        assertEquals(EXPECTED_SIZE - 1, tree.size());

        tree.clear();

        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    void properlyHandlesIterator() {
        final int TREE_SIZE = 10_000;

        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));

        for (int i = 0; i < TREE_SIZE; i++) {
            tree.add(i);
        }

        {
            int i = 0;
            for (Integer elem : tree) {
                assertEquals(i, elem);
                i++;
            }
        }
    }

    @Test
    void properlyHandlesToArray() {
        final int SIZE = 10_000;
        final int AUX = 100;

        final Object[] expectedArr = IntStream.range(0, SIZE).boxed().toArray();
        final Object[] expectedArrWithExcess = Arrays.copyOf(expectedArr, SIZE + AUX);

        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));

        for (int i = 0; i < SIZE; i++) {
            tree.add(i);
        }

        assertArrayEquals(expectedArr, tree.toArray());
        assertArrayEquals(expectedArrWithExcess, tree.toArray(new Object[SIZE + AUX]));
        assertArrayEquals(expectedArr, tree.toArray(new Object[SIZE - AUX]));
    }

    @Test
    void properlyHandlesGroupOperations() {
        final int SIZE = 10_000;

        List<Integer> otherCollection = IntStream.range(0, SIZE).boxed().toList();
        List<Integer> halfOfCollection = IntStream.range(0, SIZE / 2).boxed().toList();

        Collection<Integer> tree = new RedBlackTree<>(Comparator.comparing(o -> ((Integer) o)));

        tree.addAll(otherCollection);

        for (int i = 0; i < SIZE; i++) {
            assertTrue(tree.contains(i));
        }

        tree.removeAll(halfOfCollection);

        for (int i = 0; i < SIZE / 2; i++) {
            assertFalse(tree.contains(i));
        }
        for (int i = SIZE /2; i < SIZE; i++) {
            assertTrue(tree.contains(i));
        }

        tree.retainAll(halfOfCollection);
        assertTrue(tree.isEmpty());
    }
}

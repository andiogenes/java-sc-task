package arx.dukalis.collections;

import java.util.*;

/**
 * Implementation of self-balancing binary search tree with set-like programming interface.
 * <p>
 * Insert - O(log n)
 * Lookup - O(log n)
 * Remove - O(log n)
 * <p>
 * Originally it was supposed to be an implementation of Okasaki's red-black tree from
 * "Purely functional data structures" with Matt Might's complicated `remove()` implementation,
 * but it turned out, that vanilla Java isn't suitable for purely functional programming
 * even with support of records and pattern matching.
 */
public class RedBlackTree<T> implements Collection<T> {

    private final Comparator<Object> comparator;

    /**
     * Root of red-black tree.
     */
    private Node<T> root = null;

    /**
     * The size of the RedBlackTree (the number of elements it contains).
     */
    private int size = 0;

    public RedBlackTree(Comparator<Object> comparator) {
        this.comparator = comparator;
    }

    /**
     * Color of red-black tree.
     */
    private enum Color {RED, BLACK}

    /**
     * Node of red-black tree.
     */
    private static final class Node<T> {
        T key;
        Node<T> parent;
        Node<T> leftChild;
        Node<T> rightChild;
        Color color;

        public Node(T key,
                    Node<T> parent,
                    Node<T> leftChild,
                    Node<T> rightChild,
                    Color color) {
            this.key = key;
            this.parent = parent;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.color = color;
        }

        Node<T> findGrandparent() {
            if (this.parent == null) return null;
            return this.parent.parent;
        }

        Node<T> findUncle() {
            Node<T> grandpa = findGrandparent();
            if (grandpa == null) return null;
            if (this.parent == grandpa.leftChild) {
                return grandpa.rightChild;
            }
            return grandpa.leftChild;
        }

        Node<T> findSibling(Node<T> parent) {
            if (parent == null) return null;
            if (this == parent.leftChild) return parent.rightChild;
            return parent.leftChild;
        }

        boolean isRed() {
            return this.color == Color.RED;
        }
    }

    private void leftRotate(Node<T> u) {
        if (u == null) return;
        if (u.rightChild == null) return;

        Node<T> v = u.rightChild;
        u.rightChild = v.leftChild;

        if (v.leftChild != null) {
            v.leftChild.parent = u;
        }

        v.parent = u.parent;
        if (u.parent == null) {
            root = v;
        } else {
            if (u == u.parent.leftChild) {
                u.parent.leftChild = v;
            } else {
                u.parent.rightChild = v;
            }
        }

        v.leftChild = u;
        u.parent = v;
    }

    private void rightRotate(Node<T> u) {
        if (u == null) return;
        if (u.leftChild == null) return;

        Node<T> v = u.leftChild;
        u.leftChild = v.rightChild;

        if (v.rightChild != null) {
            v.rightChild.parent = u;
        }

        v.parent = u.parent;
        if (u.parent == null) {
            root = v;
        } else {
            if (u == u.parent.leftChild) {
                u.parent.leftChild = v;
            } else {
                u.parent.rightChild = v;
            }
        }

        v.rightChild = u;
        u.parent = v;
    }

    private void insert(T item) {
        Node<T> current = root;
        Node<T> previous = null;

        while (current != null) {
            previous = current;

            if (Objects.compare(item, current.key, comparator) < 0) {
                current = current.leftChild;
            } else {
                current = current.rightChild;
            }
        }

        Node<T> inserting = new Node<>(item, previous, null, null, Color.RED);

        if (previous == null) {
            root = inserting;
        } else {
            if (Objects.compare(item, previous.key, comparator) < 0) {
                previous.leftChild = inserting;
            } else {
                previous.rightChild = inserting;
            }
        }

        insertFixup(inserting);
    }

    private void insertFixup(Node<T> node) {
        insertCaseNo1(node);
    }

    private void insertCaseNo1(Node<T> z) {
        if (z == null) return;

        if (z.parent == null) {
            z.color = Color.BLACK;
        } else {
            insertCaseNo2(z);
        }
    }

    private void insertCaseNo2(Node<T> z) {
        if (z.parent.color != Color.BLACK) {
            insertCaseNo3(z);
        }
    }

    private void insertCaseNo3(Node<T> z) {
        Node<T> u = z.findUncle();

        if (u != null && u.isRed()) {
            z.parent.color = Color.BLACK;
            u.color = Color.BLACK;

            Node<T> g = z.findGrandparent();
            Objects.requireNonNull(g).color = Color.RED;
            insertCaseNo1(g);
        } else {
            insertCaseNo4(z);
        }
    }

    private void insertCaseNo4(Node<T> node) {
        Node<T> z = node;
        Node<T> g = Objects.requireNonNull(z.findGrandparent());

        if (z == z.parent.rightChild && z.parent == g.leftChild) {
            leftRotate(z.parent);
            z = z.leftChild;
        } else if (z == z.parent.leftChild && z.parent == g.rightChild) {
            rightRotate(z.parent);
            z = z.rightChild;
        }

        insertCaseNo5(z);
    }

    private void insertCaseNo5(Node<T> z) {
        Node<T> g = Objects.requireNonNull(z.findGrandparent());

        z.parent.color = Color.BLACK;
        g.color = Color.RED;

        if (z == z.parent.leftChild && z.parent == g.leftChild) {
            rightRotate(g);
        } else {
            leftRotate(g);
        }
    }

    private Node<T> removeImpl(Node<T> current) {
        if (current == null) return null;

        Node<T> removable;
        if (current.leftChild == null || current.rightChild == null) {
            removable = current;
        } else {
            removable = successorOf(current);
        }

        Node<T> child = null;
        if (removable != null) {
            if (removable.leftChild != null) {
                child = removable.leftChild;
            } else {
                child = removable.rightChild;
            }
        }

        if (child != null) {
            child.parent = removable.parent;
        }

        if (removable != null && removable.parent == null) {
            root = child;
        } else {
            if (removable == Objects.requireNonNull(removable).parent.leftChild) {
                removable.parent.leftChild = child;
            } else {
                removable.parent.rightChild = child;
            }
        }

        if (removable != current) {
            current.key = removable.key;
        }

        if (removable.color == Color.BLACK) {
            if (child != null && child.isRed()) {
                child.color = Color.BLACK;
            } else {
                removeFixup(child, child != null ? child.parent : null);
            }
        }

        removable.leftChild = null;
        removable.rightChild = null;

        return removable;
    }

    private void removeFixup(Node<T> x, Node<T> parent) {
        removeCaseNo1(x, parent);
    }

    private void removeCaseNo1(Node<T> z, Node<T> parent) {
        if (parent != null) {
            removeCaseNo2(z, parent);
        }
    }

    private void removeCaseNo2(Node<T> z, Node<T> parent) {
        Node<T> s = z.findSibling(parent);
        if (s.isRed()) {
            parent.color = Color.RED;
            s.color = Color.BLACK;

            if (z == parent.leftChild) {
                leftRotate(parent);
            } else {
                rightRotate(parent);
            }
        }
        removeCaseNo3(z, parent);
    }

    private void removeCaseNo3(Node<T> z, Node<T> parent) {
        Node<T> s = z.findSibling(parent);

        if (!parent.isRed() &&
                s != null &&
                s.color == Color.BLACK &&
                !s.leftChild.isRed() &&
                !s.rightChild.isRed()) {
            s.color = Color.RED;
            removeCaseNo1(parent, parent.parent);
        } else {
            removeCaseNo4(z, parent);
        }
    }

    private void removeCaseNo4(Node<T> z, Node<T> parent) {
        Node<T> s = z.findSibling(parent);

        if (parent.isRed() &&
                s != null &&
                s.color == Color.BLACK &&
                !s.leftChild.isRed() &&
                !s.rightChild.isRed()) {
            s.color = Color.RED;
            parent.color = Color.BLACK;
        } else {
            removeCaseNo5(z, parent);
        }
    }

    private void removeCaseNo5(Node<T> z, Node<T> parent) {
        Node<T> s = z.findSibling(parent);

        if (s != null && s.color == Color.BLACK) {
            if (z == parent.leftChild &&
                    !s.rightChild.isRed() &&
                    s.leftChild.isRed()) {
                s.color = Color.RED;
                s.leftChild.color = Color.BLACK;
                rightRotate(s);
            } else if (z == parent.rightChild &&
                    !s.leftChild.isRed() &&
                    s.rightChild.isRed()) {
                s.color = Color.RED;
                s.rightChild.color = Color.BLACK;
                leftRotate(s);
            }
        }
        removeCaseNo6(z, parent);
    }

    private void removeCaseNo6(Node<T> z, Node<T> parent) {
        Node<T> s = z.findSibling(parent);

        if (s != null) {
            s.color = parent.color;
            parent.color = Color.BLACK;

            if (z == parent.leftChild) {
                s.rightChild.color = Color.BLACK;
                leftRotate(parent);
            } else {
                s.rightChild.color = Color.BLACK;
                rightRotate(parent);
            }
        }
    }

    private Node<T> minimum(Node<T> node) {
        if (node == null) return null;

        Node<T> current =  node;
        while (current.leftChild != null) {
            current = current.leftChild;
        }

        return current;
    }

    private Node<T> successorOf(Node<T> node) {
        if (node == null) return null;

        if (node.rightChild != null) {
            return minimum(node.rightChild);
        }

        Node<T> current = node;
        Node<T> upward = current.parent;

        while (upward != null && current == upward.rightChild) {
            current = upward;
            upward = current.parent;
        }

        return upward;
    }

    private Node<T> find(Object key) {
        Node<T> current = root;

        while (true) {
            if (current == null) return null;
            if (Objects.equals(current.key, key)) return current;

            if (Objects.compare(key, current.key, comparator) < 0) {
                current = current.leftChild;
            } else {
                current = current.rightChild;
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Object o) {
        return find(o) != null;
    }

    public class RedBlackTreeIterator implements Iterator<T> {
        private Node<T> next;

        public RedBlackTreeIterator(Node<T> root) {
            next = minimum(root);
        }

        public boolean hasNext(){
            return next != null;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T result = next.key;
            next = successorOf(next);
            return result;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new RedBlackTreeIterator(root);
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[size];
        int i = 0;
        for (T v : this) {
            a[i++] = v;
        }
        return a;
    }

    @Override
    public <U> U[] toArray(U[] a) {
        if (a.length < size) {
            return (U[]) Arrays.copyOf(toArray(), size, a.getClass());
        }
        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        if (contains(t)) {
            return false;
        }
        insert(t);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> node = find(o);
        if (node == null) {
            return false;
        }
        removeImpl(node);
        size--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T o : c) {
            changed |= add(o);
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= remove(o);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (T elem : this) {
            if (!c.contains(elem)) {
                remove(elem);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }
}
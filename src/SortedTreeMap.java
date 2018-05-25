import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class SortedTreeMap<K extends Comparable<? super K>, V> implements ISortedTreeMap<K, V> {

    private Node root;
    private Node head;
    private int size;
    private Comparator<? super K> comparator;
    private Comparator<Node> nodeComparator;

    public SortedTreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.nodeComparator = new nodeComparator();
    }

    //TODO Remove
    @Override
    public String toString(){
        StringBuilder treeStringBuilder = new StringBuilder();
        iterator().forEachRemaining(node -> {
            treeStringBuilder.append(node.entry.key + ", ");
        });
        return treeStringBuilder.toString();
    }

    private class Node {
        private Node rightChild;
        private Node leftChild;
        private Node parent;
        private Entry<K, V> entry;
        private boolean traversed;

        public Node(Entry<K, V> entry) {
            this.entry = entry;
        }

        public String toString() {
            return "Key: " + entry.key + "\n"+"Value: " + entry.value + "\n";
        }
    }

    @Override
    public Entry<K, V> min() {
        if (isEmpty()) {
            return null;
        } else {
            Node searchNode = root;
            while (searchNode.leftChild != null) {
                searchNode = searchNode.leftChild;
            }
            return searchNode.entry;
        }
    }

    @Override
    public Entry<K, V> max() {
        if (isEmpty()) {
            return null;
        } else {
            Node searchNode = root;
            while(searchNode.rightChild != null) {
                searchNode = searchNode.rightChild;
            }
            return searchNode.entry;
        }
    }


    @Override
    public V add(K key, V value) {
        Entry<K, V> entry = new Entry<>(key, value);
        Node node = new Node(entry);
        if (isEmpty()) {
            root = node;
            head = root;
            size++;
            return null;
        }
        if (comparator.compare(key, root.entry.key) == 0) {
            V val = root.entry.value;
            root.entry = entry;
            root = head;
            return val;
        }
        if (comparator.compare(key, root.entry.key) > 0) {
            if (root.rightChild != null) {
                root = root.rightChild;
                return add(key, value);
            }
            if (root.equals(head)) {
                root.rightChild = node;
                node.parent = root;
                size++;
                return null;
            } else {
                node.parent = root;
                root.rightChild = node;
                size++;
                root = head;
                return null;
            }
        }
        if (comparator.compare(key, root.entry.key) == -1) {
            if (root.leftChild != null) {
                root = root.leftChild;
                return add(key, value);
            }
            root.leftChild = node;
            node.parent = root;
            size++;
            root = head;
            return null;
        }
        return null;
    }

    @Override
    public V add(Entry<K, V> entry) {
        return add(entry.key, entry.value);
    }

    @Override
    public void replace(K key, V value) throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            Node nodeToReplace = findNode(key);
            if (nodeToReplace != null) {
                nodeToReplace.entry = new Entry<>(key, value);
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    @Override
    public void replace(K key, BiFunction<K, V, V> f) throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            Node nodeToGet = findNode(key);
            if (nodeToGet != null) {
                nodeToGet.entry = new Entry<>(key, f.apply(key, nodeToGet.entry.value));
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    private Node findNode(K key) {
        if (isEmpty()) {
            return null;
        } else {
            if (comparator.compare(key, root.entry.key) == 0) {
                Node foundNode = root;
                root = head;
                return foundNode;
            }
            if (comparator.compare(key, root.entry.key) > 0) {
                if (root.rightChild == null) {
                    root = head;
                    return null;
                }
                root = root.rightChild;
                return findNode(key);
            }
            if (comparator.compare(key, root.entry.key) < 0) {
                if (root.leftChild == null) {
                    root = head;
                    return null;
                }
                root = root.leftChild;
                return findNode(key);
            }
        }
        return null;
    }

    private Node findReplacement(Node node) {
        if (node.leftChild != null) {
            node = node.leftChild;
            while (node.rightChild != null) {
                node = node.rightChild;
            }
            System.out.println("icky vicky");
            return node;
        } else if (node.rightChild != null) {
            node = node.rightChild;
            while (node.leftChild != null) {
                node = node.leftChild;
            }
            System.out.println("ew ew");
            return node;
        } else {
            return node;
        }
    }

    @Override
    public V remove(Object key) throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            Node node = findNode((K)key);
            if (node != null) {
//                System.out.println(size + " size");
                V value = node.entry.value;

                if (size == 1) {
                    clear();
                    return value;
                }
                Node replacementNode = findReplacement(node);
                Entry entry = replacementNode.entry;
                if (replacementNode.leftChild != null) {
                    replacementNode.entry = replacementNode.leftChild.entry;
                    replacementNode.leftChild = null;
//                    System.out.println("første");
                } else if (replacementNode.rightChild != null) {
                    replacementNode.entry = replacementNode.rightChild.entry;
                    replacementNode.rightChild = null;
                } else {
                    if (replacementNode.equals(replacementNode.parent.leftChild)) {
                        replacementNode.parent.leftChild = null;
                    } else if (replacementNode.equals(replacementNode.parent.rightChild)) {
                        replacementNode.parent.rightChild = null;
                    }
                }
                System.out.println(node.entry.key + " key som fjernes");
                System.out.println(key + "^ skal være lik");
                System.out.println(entry.key + " key som skal erstatte");
                node.entry = entry;
                size--;
                return value;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    @Override
    public V getValue(Object key) throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            Node node = findNode((K)key);
            if (node != null) {
                return node.entry.value;
            }
            return null;
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (!isEmpty()) {
            Node node = findNode(key);
            if (node != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(V value) {
        if (!isEmpty()) {
            Node node;
            Iterator<Node> iter = this.iterator();
            while (iter.hasNext()) {
                node = iter.next();
                if (node.entry.value.equals(value)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public Iterable<K> keys() {
        return new KeyIterator(new nodeIterator());
    }

    @Override
    public Iterable<V> values() {
        return new ValueIterator(new nodeIterator());
    }

    @Override
    public Iterable<Entry<K,V>> entries() {
        return new EntryIterator(new nodeIterator());
    }


    @Override
    public Entry<K, V> higherOrEqualEntry(K key) {
        if (isEmpty()) {
            return null;
        }
        int cmp = comparator.compare(key, root.entry.key);

        if (cmp == 0) {
            Entry entry = root.entry;
            root = head;
            System.out.println(entry.key + " EQUAL!");
            return entry;
        }
        else if (cmp > 0) {
            if (root.rightChild != null) {
                root = root.rightChild;
                return higherOrEqualEntry(key);
            }
            root = head;
            System.out.println(key + " NO HIGHER THING!");
            return null;
        }
        else {
            if (root.leftChild != null) {
                if (comparator.compare(key,root.leftChild.entry.key) < 0) {
                    if (root.leftChild.leftChild != null || root.leftChild.rightChild != null) {
                        Entry entry = root.entry;
                        root = head;
                        System.out.println(entry.key + " IS ONE HIGHER THAN " + key);
                        return entry;
                    }
                    root = root.leftChild;
                    return higherOrEqualEntry(key);
                }
            } else {
                Entry entry = root.entry;
                root = head;
                System.out.println(entry.key + " IS ONE HIGHER THAN "+ key);
                return entry;
            }
        }
        System.out.println("NOT HERE");
        return null;
    }

    @Override
    public Entry<K, V> lowerOrEqualEntry(K key) {
        if (isEmpty()) {
            return null;
        }
        Entry<K,V> ent = null;
        for (Entry<K,V> entry : entries()) {
            if (comparator.compare(key,entry.key) == 0) {
                return entry;
            }
            if (comparator.compare(key, entry.key) > 0) {
                System.out.println("soup");
                return ent;
            }
            ent = entry;
            System.out.println(ent.key + " " + key);
        }
        return null;
    }

    @Override
    public void merge(ISortedTreeMap<K, V> other) {
        other.entries().forEach(ent -> add(ent));
    }

    @Override
    public void removeIf(BiPredicate<K, V> p) {
        if (!isEmpty()) {
            Iterator<Node> iter = this.iterator();
            ArrayList<Node> midlArray = new ArrayList<>();
            while (iter.hasNext()) {
                Node node = iter.next();
                if (p.test(node.entry.key, node.entry.value)) {
                    midlArray.add(node);
                }
            }
            System.out.println();
            System.out.println(this + " before everything");
            midlArray.forEach(node -> {
                System.out.println(this + " before removing each");
                System.out.println(root.entry.key + " current root!");
                remove(node.entry.key);
                System.out.println(this + " after");
            });
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        head = null;
        size = 0;
    }

    public Iterator<Node> iterator() {
        return new nodeIterator();
    }

    public class EntryIterator implements Iterator<Entry<K,V>>, Iterable<Entry<K,V>> {
        private nodeIterator iterator;

        public EntryIterator(nodeIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            return iterator.next().entry;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return this;
        }
    }

    private class ValueIterator implements Iterator<V>, Iterable<V> {
        private nodeIterator iterator;

        public ValueIterator(nodeIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next().entry.value;
        }

        @Override
        public Iterator<V> iterator() {
            return this;
        }
    }

    private class KeyIterator implements Iterator<K>, Iterable<K> {
        private nodeIterator iterator;

        public KeyIterator(nodeIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public K next() {
            return iterator.next().entry.key;
        }

        @Override
        public Iterator<K> iterator() {
            return this;
        }
    }

    private class nodeIterator implements Iterator<Node>, Iterable<Node> {
        private Node nextNode;

        private nodeIterator() {
            if (isEmpty()) {
                nextNode = null;
            } else {
                Node node = root;
                while (node.leftChild != null) {
                    node = node.leftChild;
                }
                nextNode = node;
            }
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public Node next() {
            Node returnNode = nextNode;
            if (!hasNext()) {
                throw new NoSuchElementException("Illegal call to next(); iterator is after end of list.");
            } else {
                if(nextNode.rightChild != null) {
                    nextNode = nextNode.rightChild;
                    while (nextNode.leftChild != null)
                        nextNode = nextNode.leftChild;
                    return returnNode;
                }

                while(true) {
                    if(nextNode.parent == null) {
                        nextNode = null;
                        return returnNode;
                    }
                    if(nextNode.parent.leftChild == nextNode) {
                        nextNode = nextNode.parent;
                        return returnNode;
                    }
                    nextNode = nextNode.parent;
                }
            }
        }

        @Override
        public Iterator<Node> iterator() {
            return this;
        }
    }

    private class nodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            return comparator.compare(o1.entry.key, o2.entry.key);
        }
    }
}

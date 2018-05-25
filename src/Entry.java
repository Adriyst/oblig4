public class Entry<K, V> {

    public final K key;
    public final V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Entry) {
            Entry other = (Entry)o;
            return this.key.equals(other.key) && this.value.equals(other.value);
        }
        return false;
    }
}

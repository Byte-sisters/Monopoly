package server.datastructure;

public class MyHashTable<T> {

    private static class Entry<T> {
        int key;
        T value;
        Entry<T> next;

        Entry(int key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<T>[] table;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public MyHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new Entry[capacity];
        this.size = 0;
    }

    private int hash(int key) {
        return Math.abs(key) % capacity;
    }

    public void insert(int key, T value) {
        int index = hash(key);
        Entry<T> head = table[index];

        Entry<T> current = head;
        while (current != null) {
            if (current.key == key) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        Entry<T> newEntry = new Entry<>(key, value);
        newEntry.next = head;
        table[index] = newEntry;
        size++;
    }

    public T get(int key) {
        int index = hash(key);
        Entry<T> current = table[index];

        while (current != null) {
            if (current.key == key) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean contains(int key) {
        return get(key) != null;
    }

    public boolean remove(int key) {
        int index = hash(key);
        Entry<T> current = table[index];
        Entry<T> prev = null;

        while (current != null) {
            if (current.key == key) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }
}


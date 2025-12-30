package server.datastructure;

import java.util.Comparator;

public class MyHeap<T> {

    private Object[] heap;
    private int size;
    private Comparator<T> comparator;

    public MyHeap(int capacity, Comparator<T> comparator) {
        this.heap = new Object[capacity];
        this.size = 0;
        this.comparator = comparator;
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    @SuppressWarnings("unchecked")
    private T get(int i) {
        return (T) heap[i];
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public void insert(T item) {
        heap[size] = item;
        int current = size;
        size++;

        while (current > 0 &&
                comparator.compare(get(current), get(parent(current))) > 0) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    public T extractMax() {
        if (size == 0) return null;

        T max = get(0);
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        heapify(0);
        return max;
    }

    private void heapify(int i) {
        int max = i;
        int l = left(i);
        int r = right(i);

        if (l < size && comparator.compare(get(l), get(max)) > 0)
            max = l;

        if (r < size && comparator.compare(get(r), get(max)) > 0)
            max = r;

        if (max != i) {
            swap(i, max);
            heapify(max);
        }
    }

    public void updateKey(T item) {
        int index = findIndex(item);
        if (index == -1) return;
        while (index > 0 &&
                comparator.compare(get(index), get(parent(index))) > 0) {
            swap(index, parent(index));
            index = parent(index);
        }
        heapify(index);
    }

    private int findIndex(T item) {
        for (int i = 0; i < size; i++) {
            if (heap[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public T peek() {
        return size == 0 ? null : get(0);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }
}

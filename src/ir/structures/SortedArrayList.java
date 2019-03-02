package ir.structures;

import java.util.ArrayList;

public class SortedArrayList<T extends Comparable<T>> extends ArrayList<T> {
    public SortedArrayList() {
        super();
    }

    @Override
    public boolean add(T element) {
        int posWhereToAdd = rank(element);
        add(posWhereToAdd, element);
        return true;
    }

    @Override
    public boolean contains(Object object) {
        T toCheck = (T) object;
        int pos = rank(toCheck);
        return pos < size() && get(pos).compareTo(toCheck) == 0;
    }

    public int rank(T element) {
        if (element == null) throw new IllegalArgumentException("argument to rank() is null");

        int lo = 0, hi = size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = element.compareTo(get(mid));
            if (cmp < 0) hi = mid - 1;
            else if (cmp > 0) lo = mid + 1;
            else return mid;
        }
        return lo;
    }
}

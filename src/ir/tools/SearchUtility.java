package ir.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SearchUtility {

    public static <T extends Comparable<T>> Set<T> intersection(Iterator<T> set1, Iterator<T> set2) {
        Set<T> intersection = new HashSet<>();
        if (!set1.hasNext() || !set2.hasNext()) return intersection;
        T fromFirst = set1.next();
        T fromSecond = set2.next();
        while (true) {
            int cmp = fromFirst.compareTo(fromSecond);
            if (cmp == 0) {
                intersection.add(fromFirst);
                if (set1.hasNext() && set2.hasNext()) {
                    fromFirst = set1.next();
                    fromSecond = set2.next();
                } else break;
            } else if (cmp < 0 && set1.hasNext()) {
                fromFirst = set1.next();
            } else if (cmp > 0 && set2.hasNext()) {
                fromSecond = set2.next();
            } else break;
        }
        return intersection;
    }

    public static <T> Set<T> unity(Iterator<T> set1, Iterator<T> set2) {
        Set<T> res = new HashSet<>();
        set1.forEachRemaining(res::add);
        set2.forEachRemaining(res::add);
        return res;
    }

    public static void toLowerCase(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].toLowerCase();
        }
    }

    public static Set<Integer> copySet(Set<Integer> toCopy) {
        Set<Integer> copy = new HashSet<>();
        toCopy.forEach(it -> copy.add(it));
        return copy;
    }
}

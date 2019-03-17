package ir.structures.abstraction;

import java.util.Set;

public interface Searchable<T> {
    Set<Integer> search(T query);
}

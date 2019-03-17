package ir.structures.abstraction;

import java.util.stream.Stream;

public interface Editable<T> {
    void addTerm(String term, T entry);

    void clear();

    int getSize();

    Stream<String> getSortedStream();
}

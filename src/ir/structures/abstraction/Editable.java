package ir.structures.abstraction;

import java.util.stream.Stream;

public interface Editable {
    void addTerm(String term, int docId);

    void clear();

    int getSize();

    Stream<String> getSortedStream();
}

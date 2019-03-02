package ir.structures.abstraction;

import java.util.stream.Stream;

public interface InvertedIndexCoords extends Searchable {
    void addTerm(String term, int docId, int position);

    void clear();

    int getSize();

    Stream<String> getSortedStream();

}

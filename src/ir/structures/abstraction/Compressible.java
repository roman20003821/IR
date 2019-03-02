package ir.structures.abstraction;

import java.util.stream.Stream;

public interface Compressible<T> {
    Stream<T> getSortedEntryStream();

    int getSize();
}

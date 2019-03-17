package ir.fileWork.builder;

import ir.tools.Pair;

import java.io.IOException;
import java.util.stream.Stream;

public interface StreamCreator {
    Stream<Pair<String, Integer>> termAndDocIdStream(String path, int docId) throws IOException;
}

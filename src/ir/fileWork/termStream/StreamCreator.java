package ir.fileWork.termStream;

import ir.tools.Pair;

import java.io.IOException;
import java.util.stream.Stream;

public interface StreamCreator{
    Stream<Pair<StreamEntity, Integer>> termAndDocIdStream(String path, int docId) throws IOException;
}

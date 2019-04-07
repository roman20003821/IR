package ir.fileWork.builder;

import ir.fileWork.termStream.StreamEntity;
import ir.tools.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface Builder {

    void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException;

    void merge(List<String> fileNames, String mergedFile) throws IOException;

    void build(Stream<Pair<StreamEntity, Integer>> entityAndDocIdStream);
}

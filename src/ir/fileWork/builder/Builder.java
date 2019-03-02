package ir.fileWork.builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Builder {

    void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException;

    void merge(List<String> fileNames, String mergedFile) throws IOException;
}

package ir.fileWork.builder;

import ir.structures.impl.InvertedIndexTermCoords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CoordBuider implements Builder {

    private InvertedIndexTermCoords indexTermCoords;

    public CoordBuider(InvertedIndexTermCoords indexTermCoords) {
        this.indexTermCoords = indexTermCoords;
    }

    public void build(int docId, String fileName, int maxBlockSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int pos = 0;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token != null)
                    indexTermCoords.addTerm(token, docId, pos++);
            }
        }
    }


    @Override
    public void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException {

    }

    @Override
    public void merge(List<String> fileNames, String mergedFile) {

    }
}

package ir.fileWork.builder;

import ir.structures.abstraction.Editable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class PhraseBuilder implements Builder {

    private Editable editable;

    public PhraseBuilder(Editable editable) {
        this.editable = editable;
    }

    public void build(int docId, String fileName, int maxBlockSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String tokenFromPreviousLine = null;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.countTokens() < 1) continue;
            String firstToken = tokenFromPreviousLine == null ? tokenFromPreviousLine : tokenizer.nextToken();
            String secondToken = firstToken;
            while (tokenizer.hasMoreTokens()) {
                secondToken = tokenizer.nextToken();
                editable.addTerm(firstToken + " " + secondToken, docId);
                firstToken = secondToken;
            }
            tokenFromPreviousLine = secondToken;
        }
        reader.close();
    }

    @Override
    public void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException {

    }

    @Override
    public void merge(List<String> fileNames, String mergedFile) {

    }
}

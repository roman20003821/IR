package ir.fileWork.builder;

import ir.tools.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleFileStreamCreator implements StreamCreator {

    private Analyser simpleAnalyser;

    public SimpleFileStreamCreator() {
        simpleAnalyser = new SimpleAnalyser();
    }

    @Override
    public Stream<Pair<String, Integer>> termAndDocIdStream(String path, int docId) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<String> termsList = new LinkedList<>();
        String line = null;
        while (true) {
            line = reader.readLine();
            if (line == null) break;
            termsList.addAll(simpleAnalyser.analyse(line));
        }
        return termsList.stream().map(it -> new Pair<>(it, docId));
    }
}

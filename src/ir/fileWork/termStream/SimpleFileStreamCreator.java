package ir.fileWork.termStream;

import ir.tools.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleFileStreamCreator implements StreamCreator {

    private Analyser analyser;

    public SimpleFileStreamCreator(Analyser analyser) {
        this.analyser = analyser;
    }

    @Override
    public Stream<Pair<StreamEntity, Integer>> termAndDocIdStream(String path, int docId) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<StreamEntity> termsList = new LinkedList<>();
        String line;
        while (true) {
            line = reader.readLine();
            if (line == null) break;
            analyser.analyse(line).forEach(term -> termsList.add(new StreamEntity(term)));
        }
        return termsList.stream().map(it -> new Pair<>(it, docId));
    }
}

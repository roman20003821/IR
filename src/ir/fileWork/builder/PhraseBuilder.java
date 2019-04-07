package ir.fileWork.builder;

import ir.fileWork.termStream.StreamEntity;
import ir.structures.abstraction.Editable;
import ir.tools.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class PhraseBuilder implements Builder {

    private Editable editable;

    public PhraseBuilder(Editable editable) {
        this.editable = editable;
    }

    @Override
    public void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException {

    }

    @Override
    public void merge(List<String> fileNames, String mergedFile) {

    }

    @Override
    public void build(Stream<Pair<StreamEntity, Integer>> entityAndDocIdStream) {

    }
}

package ir.fileWork.builder;

import ir.fileWork.termStream.Fb2FileStreamCreator;
import ir.fileWork.termStream.StreamEntity;
import ir.structures.impl.InvertedIndexZone;
import ir.tools.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ZoneBuilder implements Builder {

    private InvertedIndexZone indexZone;

    public ZoneBuilder(InvertedIndexZone indexZone) {
        this.indexZone = indexZone;
    }

    @Override
    public void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException {

    }

    @Override
    public void merge(List<String> fileNames, String mergedFile) throws IOException {

    }

    @Override
    public void build(Stream<Pair<StreamEntity, Integer>> entityAndDocIdStream) {
        entityAndDocIdStream.forEach(pair -> {
            Fb2FileStreamCreator.Fb2StreamEntity entity = (Fb2FileStreamCreator.Fb2StreamEntity) pair.getKey();
            indexZone.addTerm(entity.getTerm(), new InvertedIndexZone.EditableEntry(pair.getValue(), entity.getZone()));
        });
    }
}

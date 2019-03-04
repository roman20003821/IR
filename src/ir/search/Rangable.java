package ir.search;

import java.util.Map;

public interface Rangable {
    int getTermFrequencyInDoc(int docId, String term);

    int getNumberOfDocuments();

    int getDocumentFrequency(String term);

    Map<String, Double> getZoneToWeightMap();

    boolean isTermPresentInZone(int docId, String term, String zone);
}

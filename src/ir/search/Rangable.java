package ir.search;

import ir.structures.abstraction.TermWeightCountable;

import java.util.Map;

public interface Rangable {
    int getTermFrequencyInDoc(int docId, String term);

    Map<String, Double> getZoneToWeightMap();

    boolean isTermPresentInZone(int docId, String term, String zone);

    TermWeightCountable getTermWeightCountable();
}

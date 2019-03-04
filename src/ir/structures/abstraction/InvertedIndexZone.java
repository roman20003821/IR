package ir.structures.abstraction;

import java.util.Map;
import java.util.Set;

public interface InvertedIndexZone {
    void addTerm(String term, int docId,String zone);

    Set<Integer> search(String query, Map<String, Double> zoneWeights);
}

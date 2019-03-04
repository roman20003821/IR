package ir.search;

import java.util.*;

public class Ranging {
    private Rangable rangable;

    public Ranging(Rangable rangable, Set<Integer> docsId) {
        this.rangable = rangable;
    }

    public Set<Integer> range(Set<Integer> docsId, String[] queryTerms) {
        Map<Integer, Double> relevanceMap = relevanceMap(docsId, queryTerms);
        return new TreeSet<>(Comparator.comparingDouble(relevanceMap::get));
    }

    private Map<Integer, Double> relevanceMap(Set<Integer> docsId, String[] queryTerms) {
        Map<Integer, Double> map = new HashMap<>();
        docsId.forEach(docId -> map.put(docId, getRelevanceOfDocToQuery(docId, queryTerms)));
        return map;
    }

    private double getRelevanceOfDocToQuery(int docId, String[] queryTerms) {
        return queryWeightWithZones(docId, queryTerms) * sumOfTermsWeight(docId, queryTerms);
    }

    private double queryWeightWithZones(int docId, String[] queryTerms) {
        Map<String, Double> zoneToWeightMap = rangable.getZoneToWeightMap();
        return queryWeightWithZones(docId, zoneToWeightMap, queryTerms);
    }

    private double queryWeightWithZones(int docId, Map<String, Double> zoneToWeightMap, String[] queryTerms) {
        double weight = 0;
        for (Map.Entry<String, Double> entry : zoneToWeightMap.entrySet()) {
            weight += getZoneWeightToTerm(docId, entry.getKey(), entry.getValue(), queryTerms);
        }
        return weight;
    }

    private double getZoneWeightToTerm(int docId, String zone, double zoneWeight, String[] queryTerms) {
        return isAllTermsPresentInZone(docId, queryTerms, zone) ? zoneWeight : 0;
    }

    private boolean isAllTermsPresentInZone(int docId, String[] queryTerms, String zone) {
        for (String term : queryTerms) {
            if (!rangable.isTermPresentInZone(docId, term, zone)) return false;
        }
        return true;
    }

    private double sumOfTermsWeight(int docId, String[] queryTerms) {
        double sum = 0;
        for (String term : queryTerms) {
            sum += termWeight(docId, term);
        }
        return sum;
    }

    private double termWeight(int docId, String term) {
        return rangable.getTermFrequencyInDoc(docId, term) * Math.log(rangable.getNumberOfDocuments() / (rangable.getDocumentFrequency(term) + .0));
    }
}

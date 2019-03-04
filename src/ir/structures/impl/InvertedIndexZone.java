package ir.structures.impl;

import ir.search.BooleanSearch;
import ir.search.BooleanSearchable;
import ir.search.Rangable;
import ir.search.Ranging;
import ir.searchParser.BooleanSearchParser;
import ir.tools.Pair;

import java.util.*;

public class InvertedIndexZone implements ir.structures.abstraction.InvertedIndexZone, BooleanSearchable, Rangable {
    private Map<String, TermZone> data;
    private Set<Integer> docsId;
    private BooleanSearch search;
    private BooleanSearchParser parser;
    private Ranging ranging;
    private Map<String, Double> zoneToWeightMap;

    public InvertedIndexZone() {
        data = new HashMap<>();
        docsId = new HashSet<>();
        search = new BooleanSearch(this);
        parser = new BooleanSearchParser();
        ranging = new Ranging(this, docsId);
    }

    @Override
    public void addTerm(String term, int docId, String zone) {
        docsId.add(docId);
        TermZone termZone = data.computeIfAbsent(term, k -> new TermZone(new HashMap<>()));
        termZone.addZone(docId, zone);
    }

    @Override
    public Set<Integer> search(String query, Map<String, Double> zoneWeights) {
        this.zoneToWeightMap = zoneWeights;
        BooleanSearchParser.BoolSearchParsedQuery parsedQuery = parser.parse(query);
        Set<Integer> champions = search.search(parsedQuery);
        return ranging.range(champions, (String[]) parsedQuery.getTerms().toArray());
    }

    @Override
    public Set<Integer> getIdSet(String term) {
        TermZone termZone = data.get(term);
        if (termZone == null) return Collections.emptySet();
        return termZone.docInfo.keySet();
    }

    @Override
    public Set<Integer> invertDocIdSet(Set<Integer> docIdSet) {
        Set<Integer> result = new HashSet<>();
        docsId.forEach(it -> {
            if (!docIdSet.contains(it)) {
                result.add(it);
            }
        });
        return result;
    }

    @Override
    public int getTermFrequencyInDoc(int docId, String term) {
        TermZone termZone = data.get(term);
        if (termZone == null) return 0;
        return termZone.getFrequencyInDoc(docId);
    }

    @Override
    public int getNumberOfDocuments() {
        return docsId.size();
    }

    @Override
    public int getDocumentFrequency(String term) {
        TermZone termZone = data.get(term);
        return termZone == null ? 0 : termZone.getDocumentFrequency();
    }

    @Override
    public Map<String, Double> getZoneToWeightMap() {
        if (zoneToWeightMap == null) throw new IllegalStateException("Weight map of zones is null");
        return zoneToWeightMap;
    }

    @Override
    public boolean isTermPresentInZone(int docId, String term, String zone) {
        TermZone termZone = data.get(term);
        if (termZone == null) return false;
        return termZone.isPresentInZone(docId, zone);
    }

    public class TermZone {
        private Map<Integer, Pair<Set<String>, Integer>> docInfo;

        public TermZone(Map<Integer, Pair<Set<String>, Integer>> docInfo) {
            this.docInfo = docInfo;
        }

        private void addZone(int docId, String zone) {
            Pair<Set<String>, Integer> zonesAndFrequency = docInfo.computeIfAbsent(docId, k -> new Pair<>(new HashSet<>(), 0));
            zonesAndFrequency.getKey().add(zone);
            zonesAndFrequency.setValue(zonesAndFrequency.getValue() + 1);
        }

        private int getFrequencyInDoc(int docId) {
            Pair<Set<String>, Integer> zonesAndFrequency = docInfo.get(docId);
            if (zonesAndFrequency == null) return 0;
            return zonesAndFrequency.getValue();
        }

        private int getDocumentFrequency() {
            return docInfo.size();
        }

        private boolean isPresentInZone(int docId, String zone) {
            Pair<Set<String>, Integer> zonesAndFrequency = docInfo.get(docId);
            if (zonesAndFrequency == null) return false;
            return zonesAndFrequency.getKey().contains(zone);
        }
    }
}
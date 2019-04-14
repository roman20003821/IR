package ir.structures.impl;

import ir.search.BM25;
import ir.search.BooleanSearchable;
import ir.search.Rangable;
import ir.search.Ranging;
import ir.searchParser.BooleanSearchParser;
import ir.structures.abstraction.InvertedIndex;
import ir.structures.abstraction.TermWeightCountable;
import ir.tools.Pair;
import ir.tools.SearchUtility;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvertedIndexZone
        implements InvertedIndex<InvertedIndexZone.EditableEntry, String>, BooleanSearchable, Rangable, TermWeightCountable {
    private static final int B1 = 1;
    private static final int B2 = 1;

    private Map<String, TermZone> data;
    private Set<Integer> docsId;
    private BooleanSearchParser parser;
    private Ranging ranging;
    private BM25 termScoreCounter;
    private Map<String, Double> zoneToWeightMap;
    private VectorSpace vectorSpace;
    private int averageDocLengh;
    private Clusters clusters;

    public InvertedIndexZone() {
        data = new HashMap<>();
        docsId = new HashSet<>();
        parser = new BooleanSearchParser();
        ranging = new Ranging(this);
        termScoreCounter = new BM25();
        vectorSpace = new VectorSpace();
    }

    @Override
    public void addTerm(String term, EditableEntry entry) {
        addTerm(term, entry.docId, entry.zone);
    }

    @Override
    public void clear() {
        data.clear();
        docsId.clear();
        vectorSpace.clear();
        clusters.clear();
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        return null;
    }

    private void addTerm(String term, int docId, String zone) {
        docsId.add(docId);
        TermZone termZone = data.computeIfAbsent(term, k -> new TermZone(new HashMap<>()));
        termZone.addZone(docId, zone);
        vectorSpace.addDocIdAndTerm(docId, term);
    }

    public void finishInserting() {
        averageDocLengh = vectorSpace.getAverageVectorLength();
        clusters = new Clusters(docsId, B1);
    }

    @Override
    public Set<Integer> search(String query) {
        BooleanSearchParser.BoolSearchParsedQuery parsedQuery = parser.parse(query);
        return ranging.range(takeRelevanceDocs((String[]) parsedQuery.getTerms().toArray()), (String[]) parsedQuery.getTerms().toArray());
    }

    private Set<Integer> takeRelevanceDocs(String[] queryTokens) {
        return takeRelevanceDocs(Set.of(queryTokens));
    }

    private Set<Integer> takeRelevanceDocs(Set<String> queryTokens) {
        Set<Integer> mostRelevanceLeaders = mostRelevanceLeaders(queryTokens, B2);
        return getSetOfLeadersAndFollowers(mostRelevanceLeaders);
    }

    private Set<Integer> getSetOfLeadersAndFollowers(Set<Integer> leaders) {
        Iterator<Integer> leadersIterator = leaders.iterator();
        if (!leadersIterator.hasNext()) return Collections.emptySet();
        Set<Integer> res = getSetOfLeaderAndFollowers(leadersIterator.next());
        while (leadersIterator.hasNext()) {
            res = SearchUtility.unity(res.iterator(), getSetOfLeaderAndFollowers(leadersIterator.next()).iterator());
        }
        return res;
    }

    private Set<Integer> getSetOfLeaderAndFollowers(int leader) {
        return clusters.setOfLeaderAndFollowers(leader);
    }

    private Set<Integer> mostRelevanceLeaders(Set<String> queryTokens, int b2) {
        Set<Integer> leaders = clusters.leaders();
        VectorSpace.Vector vector = new VectorSpace.Vector(queryTokens);
        Set<Integer> sortedWithSim = leaders.stream().sorted((o1, o2) -> Double.compare(vectorSpace.cosSimilarity(o1, vector, this),
                vectorSpace.cosSimilarity(o2, vector, this))).collect(Collectors.toCollection(TreeSet::new));
        return cutDownSet(sortedWithSim, b2);
    }

    private Set<Integer> cutDownSet(Set<Integer> set, int n) {
        Set<Integer> res = new HashSet<>();
        Iterator<Integer> setIterator = set.iterator();
        for (int i = 0; i < n && setIterator.hasNext(); i++) {
            res.add(setIterator.next());
        }
        return res;
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

    private int getTermFrequencyInDoc(String term, Set<String> termsSet) {
        int res = 0;
        for (String setEntry : termsSet) {
            if (term.equals(setEntry)) ++res;
        }
        return res;
    }

    @Override
    public TermWeightCountable getTermWeightCountable() {
        return this;
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

    @Override
    public double termWeightInDoc(String term, int docId) {
        double tf = getTermFrequencyInDoc(docId, term);
        return termScoreCounter.score(tf, getNumberOfDocuments(), vectorSpace.getVectorLength(docId), averageDocLengh, getDocumentFrequency(term));
    }

    @Override
    public double termWeightInDoc(String term, Set<String> termsFormDoc) {
        double tf = getTermFrequencyInDoc(term, termsFormDoc);
        return termScoreCounter.score(tf, getNumberOfDocuments(), termsFormDoc.size(), averageDocLengh, getDocumentFrequency(term));
    }

    private int getNumberOfDocuments() {
        return docsId.size();
    }

    private int getDocumentFrequency(String term) {
        TermZone termZone = data.get(term);
        return termZone == null ? 0 : termZone.getDocumentFrequency();
    }

    public static class TermZone {
        private Map<Integer, Pair<Set<String>, Integer>> docInfo;

        TermZone(Map<Integer, Pair<Set<String>, Integer>> docInfo) {
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

    public static class EditableEntry {
        private int docId;
        private String zone;

        public EditableEntry(int docId, String zone) {
            this.docId = docId;
            this.zone = zone;
        }
    }
}
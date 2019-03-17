package ir.structures.impl;

import ir.searchParser.GramParser;
import ir.tools.SearchUtility;
import ir.structures.abstraction.InvertedIndex;

import java.util.*;
import java.util.stream.Stream;

public class InvertedIndexGram implements InvertedIndex<Integer, String> {
    private Map<String, Set<String>> gramIndex;
    private InvertedIndexTerm invertedIndexTerm;
    private GramParser parser;

    public InvertedIndexGram(InvertedIndexTerm invertedIndexTerm, int gramType) {
        this.invertedIndexTerm = invertedIndexTerm;
        gramIndex = new HashMap<>();
        parser = new GramParser(gramType);
    }

    @Override
    public void addTerm(String term, Integer docId) {
        List<String> parsedTerm = parser.parseTerm(term);
        parsedTerm.forEach(it -> {
            Set<String> termList = gramIndex.computeIfAbsent(it, k -> new HashSet<>());
            termList.add(term);
        });
    }

    @Override
    public void clear() {
        gramIndex = new HashMap<>();
    }

    @Override
    public int getSize() {
        return gramIndex.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        Stream<Map.Entry<String, Set<String>>> mapStream = gramIndex.entrySet().stream();
        return mapStream.sorted().map(it -> it.getKey() + " " + getSetInLine(it.getValue()));
    }

    private String getSetInLine(Set<String> stringSet) {
        StringBuilder builder = new StringBuilder();
        stringSet.forEach(it -> {
            builder.append(it).append(" ");
        });
        return builder.toString();
    }

    @Override
    public Set<Integer> search(String query) throws IllegalArgumentException {
        List<String> queryParsedParts = parser.parse(query);
        Set<String> termsInterception = getTermsInterception(queryParsedParts);
        return findDocs(termsInterception);
    }

    private Set<String> getTermsInterception(List<String> queryParsedParts) {
        Iterator<String> partsIterator = queryParsedParts.iterator();
        Set<String> res = gramIndex.get(partsIterator.next());
        while (partsIterator.hasNext()) {
            res = SearchUtility.intersection(res.iterator(), gramIndex.get(partsIterator.next()).iterator());
        }
        return res;
    }

    private Set<Integer> findDocs(Set<String> termSet) {
        Set<Integer> docIdSet = new HashSet<>();
        termSet.forEach(it -> {
            docIdSet.addAll(invertedIndexTerm.search(it));
        });
        return docIdSet;
    }
}

package ir.structures.impl;

import ir.searchParser.PhraseParser;
import ir.tools.SearchUtility;
import ir.structures.abstraction.InvertedIndex;

import java.util.*;
import java.util.stream.Stream;

public class InvertedIndexPhrase implements InvertedIndex {

    private Map<String, HashSet<Integer>> index;
    private PhraseParser parser;

    public InvertedIndexPhrase() {
        index = new HashMap<>();
        parser = new PhraseParser();
    }

    @Override
    public void addTerm(String term, int docId) {
        HashSet<Integer> docIdSet = index.computeIfAbsent(term.toLowerCase(), k -> new HashSet<>());
        docIdSet.add(docId);
    }

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public int getSize() {
        return index.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        Stream<Map.Entry<String, HashSet<Integer>>> mapStream = index.entrySet().stream();
        return mapStream.sorted().map(it -> it.getKey() + " " + getSortedSetInLine(it.getValue()));
    }

    private String getSortedSetInLine(HashSet<Integer> stringSet) {
        StringBuilder builder = new StringBuilder();
        stringSet.stream().sorted().forEach(it -> {
            builder.append(it).append(" ");
        });
        return builder.toString();
    }

    @Override
    public Set<Integer> search(String query) {
        Queue<String> operands = parser.parse(query);
        Set<Integer> result = getIdSet(operands.remove());
        while (operands.size() != 0) {
            Set<Integer> anotherSetId = getIdSet(operands.remove());
            result = SearchUtility.intersection(result.iterator(), anotherSetId.iterator());
        }
        return result;
    }


    private Set<Integer> getIdSet(String term) {
        Set<Integer> docIdSet = index.get(term);
        if (docIdSet == null) docIdSet = Collections.emptySet();
        return docIdSet;
    }
}

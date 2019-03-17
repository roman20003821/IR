package ir.structures.impl;

import ir.fileWork.builder.TermBuilder;
import ir.search.BooleanSearch;
import ir.search.BooleanSearchable;
import ir.searchParser.BooleanSearchParser;
import ir.structures.abstraction.Compressible;
import ir.structures.abstraction.InvertedIndex;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvertedIndexTerm implements InvertedIndex<Integer, String>, BooleanSearchable, Compressible<Entry<String, InvertedIndexTerm.TermInvIndexEntry>> {
    private Map<String, TermInvIndexEntry> index;
    private Set<Integer> docsId;
    private BooleanSearch booleanSearch;
    private BooleanSearchParser parser;

    public InvertedIndexTerm() {
        docsId = new HashSet<>();
        index = new HashMap<>();
        booleanSearch = new BooleanSearch(this);
        parser = new BooleanSearchParser();
    }

    @Override
    public void addTerm(String term, Integer docId) {
        docsId.add(docId);
        TermInvIndexEntry termInvIndexEntry = index.computeIfAbsent(term, k -> new TermInvIndexEntry(0, new HashSet<>()));
        termInvIndexEntry.addDocId(docId);
    }

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public Stream<Entry<String, TermInvIndexEntry>> getSortedEntryStream() {
        return index.entrySet().stream().sorted().map(this::sortEntry);
    }

    private Entry<String, TermInvIndexEntry> sortEntry(Entry<String, TermInvIndexEntry> entry) {
        TermInvIndexEntry termInvIndexEntry = new TermInvIndexEntry(entry.getValue().countOfRepeats,
                sortedSet(entry.getValue().docIdSet));
        entry.setValue(termInvIndexEntry);
        return entry;
    }

    private Set<Integer> sortedSet(Set<Integer> set) {
        return set.stream().sorted().collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public int getSize() {
        return index.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        Stream<Map.Entry<String, TermInvIndexEntry>> mapStream = index.entrySet().stream();
        mapStream = mapStream.sorted(Comparator.comparing(Map.Entry::getKey));
        return mapStream.map(it -> it.getKey() + TermBuilder.SPLIT_SIGN + it.getValue().toString());
    }

    @Override
    public Set<Integer> search(String query) {
        return booleanSearch.search(parser.parse(query));
    }

    @Override
    public Set<Integer> getIdSet(String term) {
        TermInvIndexEntry termInvIndexEntry = index.get(term);
        if (termInvIndexEntry == null) return Collections.emptySet();
        return termInvIndexEntry.docIdSet;
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

    public static class TermInvIndexEntry {
        int countOfRepeats;
        Set<Integer> docIdSet;

        public TermInvIndexEntry(int countOfRepeats, Set<Integer> docIdSet) {
            this.countOfRepeats = countOfRepeats;
            this.docIdSet = docIdSet;
        }

        private void addDocId(int docId) {
            docIdSet.add(docId);
            countOfRepeats++;
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(countOfRepeats).append(TermBuilder.SPLIT_SIGN);
            docIdSet.stream().sorted().forEach(it -> {
                builder.append(it).append(" ");
            });
            return builder.toString();
        }
    }
}
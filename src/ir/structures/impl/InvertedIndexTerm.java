package ir.structures.impl;

import ir.fileWork.builder.TermBuilder;
import ir.search.BooleanSearch;
import ir.search.BooleanSearchable;
import ir.structures.abstraction.Compressible;
import ir.structures.abstraction.InvertedIndex;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvertedIndexTerm implements InvertedIndex, BooleanSearchable, Compressible<Entry<String, InvertedIndexTerm.TermInvertedIndex>> {
    private Map<String, TermInvertedIndex> index;
    private Set<Integer> docsId;
    private BooleanSearch booleanSearch;

    public InvertedIndexTerm() {
        docsId = new HashSet<>();
        index = new HashMap<>();
        booleanSearch = new BooleanSearch(this);
    }

    @Override
    public void addTerm(String term, int docId) {
        docsId.add(docId);
        TermInvertedIndex termInvertedIndex = index.computeIfAbsent(term, k -> new TermInvertedIndex(0, new HashSet<>()));
        termInvertedIndex.countOfRepeats += 1;
        termInvertedIndex.docIdSet.add(docId);
    }

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public Stream<Entry<String, TermInvertedIndex>> getSortedEntryStream() {
        return index.entrySet().stream().sorted().map(this::sortEntry);
    }

    private Entry<String, TermInvertedIndex> sortEntry(Entry<String, TermInvertedIndex> entry) {
        TermInvertedIndex termInvertedIndex = new TermInvertedIndex(entry.getValue().countOfRepeats, entry.getValue().docIdSet.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
        entry.setValue(termInvertedIndex);
        return entry;
    }

    @Override
    public int getSize() {
        return index.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        Stream<Map.Entry<String, TermInvertedIndex>> mapStream = index.entrySet().stream();
        mapStream = mapStream.sorted(Comparator.comparing(Map.Entry::getKey));
        return mapStream.map(it -> it.getKey() + TermBuilder.SPLIT_SIGN + it.getValue().toString());
    }


    @Override
    public Set<Integer> search(String query) {
        return booleanSearch.search(query);
    }


    @Override
    public Set<Integer> getIdSet(String term) {
        TermInvertedIndex termInvertedIndex = index.get(term);
        if (termInvertedIndex == null) return Collections.emptySet();
        return termInvertedIndex.docIdSet;
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


    public class TermInvertedIndex {
        int countOfRepeats;
        Set<Integer> docIdSet;

        public TermInvertedIndex(int countOfRepeats, Set<Integer> docIdSet) {
            this.countOfRepeats = countOfRepeats;
            this.docIdSet = docIdSet;
        }

        public int getCountOfRepeats() {
            return countOfRepeats;
        }

        public void setCountOfRepeats(int countOfRepeats) {
            this.countOfRepeats = countOfRepeats;
        }

        public Set<Integer> getDocIdSet() {
            return docIdSet;
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
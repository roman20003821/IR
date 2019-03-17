package ir.structures.impl;

import ir.search.BooleanSearch;
import ir.search.BooleanSearchable;
import ir.structures.abstraction.Compressible;
import ir.structures.abstraction.Searchable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import ir.structures.impl.InvertedIndexTerm.TermInvIndexEntry;

public class CompressedIndex implements Searchable<Set<Integer>>, BooleanSearchable {

    private static final char LENGTH_SIGN = '1';
    private static final char LENGTH_END_SIGN = '0';
    private static final char ZERO_SIGN = '&';

    private Map<String, TermCompressedIndex> data;
    private Set<Integer> docsId;
    private BooleanSearch booleanSearch;

    public CompressedIndex(Compressible<Entry<String, TermInvIndexEntry>> compressible) {
        data = new HashMap<>(compressible.getSize());
        docsId = new HashSet<>();
        booleanSearch = new BooleanSearch(this);
        compress(compressible);
    }

    private void compress(Compressible<Entry<String, TermInvIndexEntry>> compressible) {
        Stream<Entry<String, TermInvIndexEntry>> entryStream = compressible.getSortedEntryStream();
        compress(entryStream);
    }

    private void compress(Stream<Entry<String, TermInvIndexEntry>> entryStream) {
        Iterator<Entry<String, TermInvIndexEntry>> entryIterator = entryStream.iterator();
        compress(entryIterator);
    }

    private void compress(Iterator<Entry<String, TermInvIndexEntry>> entryIterator) {
        entryIterator.forEachRemaining(it -> addTerm(it.getKey(), it.getValue().docIdSet.keySet(), it.getValue().countOfRepeats));
    }

    private void addTerm(String term, Set<Integer> docIdSet, int countOfRepeat) {
        addTerm(term, docIdSet.iterator(), countOfRepeat);
    }

    private void addTerm(String term, Iterator<Integer> docIdIterator, int countOfRepeat) {
        if (!docIdIterator.hasNext()) return;
        int current = docIdIterator.next();
        docsId.add(current);
        addTerm(term, current, countOfRepeat);
        while (docIdIterator.hasNext()) {
            int next = docIdIterator.next();
            docsId.add(next);
            addTerm(term, next - current, countOfRepeat);
            current = next;
        }
    }


    private void addTerm(String term, int distanceFromPrevious, int countOfRepeat) {
        TermCompressedIndex docs = data.computeIfAbsent(term, k -> new TermCompressedIndex(countOfRepeat, ""));
        addTerm(new StringBuilder(docs.docsId), distanceFromPrevious);
    }

    private void addTerm(StringBuilder builder, int distanceFromPrevious) {
        if (distanceFromPrevious == 0) builder.append(ZERO_SIGN);
        else builder.append(encode(distanceFromPrevious));
    }

    private String encode(int distanceFromPrevious) {
        String binary = Integer.toBinaryString(distanceFromPrevious);
        binary = removeFirstOne(binary);
        return addOnesAndZeroToBeginning(binary);
    }

    private String removeFirstOne(String binary) {
        if (binary.length() == 1) return "";
        return binary.substring(1);
    }

    private String addOnesAndZeroToBeginning(String binary) {
        return getOnesAndZero(binary.length()) + binary;
    }

    private String getOnesAndZero(int nOnes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nOnes; i++) {
            builder.append(LENGTH_SIGN);
        }
        builder.append(LENGTH_END_SIGN);
        return builder.toString();
    }

    @Override
    public Set<Integer> search(String query) {
        return booleanSearch.search(query);
    }

    @Override
    public Set<Integer> getIdSet(String term) {
        TermCompressedIndex termCompressedIndex = data.get(term);
        if (termCompressedIndex == null) return Collections.emptySet();
        return decode(termCompressedIndex.docsId);
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

    private Set<Integer> decode(String encrypted) {
        Set<Integer> res = new HashSet<>();
        decode(res, encrypted);
        return res;
    }

    private void decode(Set<Integer> docIdSet, String encrypted) {
        Iterator<Integer> docIdIterator = getEncryptedIterator(encrypted);
        if (!docIdIterator.hasNext()) return;
        int currentDocIdDist = docIdIterator.next();
        docIdSet.add(currentDocIdDist);
        while (docIdIterator.hasNext()) {
            int nextDocIdDist = docIdIterator.next();
            currentDocIdDist += nextDocIdDist;
            docIdSet.add(currentDocIdDist);
        }
    }

    private Iterator<Integer> getEncryptedIterator(String encrypted) {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < encrypted.length();
            }

            @Override
            public Integer next() {
                int length = getLengthOfNumber(encrypted, index);
                int number = decodeBinary(encrypted, length, index + length + 1);
                index += length * 2 + 1;
                return number;
            }
        };
    }


    private int getLengthOfNumber(String encrypted, int index) {
        int res = 0;
        while (encrypted.charAt(index++) != LENGTH_END_SIGN) res++;
        return res;
    }

    private int decodeBinary(String encrypted, int length, int index) {
        if (length == 0) {
            if (encrypted.charAt(index) == ZERO_SIGN) return 0;
            return 1;
        }
        return decodeBinary(encrypted.substring(index, index + length));
    }

    private int decodeBinary(String binary) {
        return Integer.parseInt("1" + binary, 2);
    }

    public class TermCompressedIndex {
        private int countOfRepeats;
        String docsId;

        public TermCompressedIndex(int countOfRepeats, String docsId) {
            this.countOfRepeats = countOfRepeats;
            this.docsId = docsId;
        }

        public int getCountOfRepeats() {
            return countOfRepeats;
        }

        public void setCountOfRepeats(int countOfRepeats) {
            this.countOfRepeats = countOfRepeats;
        }

        public String getDocsId() {
            return docsId;
        }
    }
}

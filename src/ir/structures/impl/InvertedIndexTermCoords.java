package ir.structures.impl;

import ir.searchParser.CoordinateParser;
import ir.searchParser.CoordinateParser.SearchToken;
import ir.structures.abstraction.InvertedIndex;
import ir.tools.SearchUtility;
import ir.structures.SortedArrayList;

import java.util.*;
import java.util.stream.Stream;


public class InvertedIndexTermCoords implements InvertedIndex<InvertedIndexTermCoords.EditableEntry, String> {

    private Map<String, CoordsInvIndexEntry> index;
    private CoordinateParser parser;

    public InvertedIndexTermCoords() {
        index = new HashMap<>();
        parser = new CoordinateParser();
    }

    @Override
    public void addTerm(String term, EditableEntry entry) {
        addTerm(term, entry.docId, entry.pos);
    }

    private void addTerm(String term, int docId, int position) {
        CoordsInvIndexEntry coordsInvIndexEntry = index.computeIfAbsent(term.toLowerCase(), k -> new CoordsInvIndexEntry());
        ArrayList<Integer> termPositions = coordsInvIndexEntry.docIdToPositions.computeIfAbsent(docId, k -> new SortedArrayList<>());
        termPositions.add(position);
        coordsInvIndexEntry.countOfRepeats++;
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
        Stream<Map.Entry<String, CoordsInvIndexEntry>> mapStream = index.entrySet().stream();
        return mapStream.sorted().map(it -> it.getKey() + " " + it.getValue().toString());
    }

    @Override
    public Set<Integer> search(String query) {
        Queue<SearchToken> operands = parser.parse(query);
        Set<Integer> docIdSet = inOneDoc(operands.iterator());
        if (docIdSet.size() > 1)
            filterToNearTerms(operands, docIdSet);
        return null;
    }

    private Set<Integer> inOneDoc(Iterator<SearchToken> operands) {
        if (!operands.hasNext()) return Collections.emptySet();
        Set<Integer> result = SearchUtility.copySet(getDocId(operands.next().getToken()));
        while (operands.hasNext()) {
            Set<Integer> anotherDocIdSet = getDocId(operands.next().getToken());
            result = SearchUtility.intersection(result.iterator(), anotherDocIdSet.iterator());
        }
        return result;
    }

    private Set<Integer> getDocId(String term) {
        CoordsInvIndexEntry coordsInvIndexEntry = getTermInfo(term);
        return coordsInvIndexEntry.getDocIdToPositions().keySet();
    }

    private void filterToNearTerms(Queue<SearchToken> searchTokens, Set<Integer> toFilter) {
        Iterator<Integer> setIterator = toFilter.iterator();
        setIterator.forEachRemaining(it -> {
            if (areTermsNear(searchTokens, it)) {
                toFilter.remove(it);
            }
        });
    }

    private boolean areTermsNear(Queue<SearchToken> searchTokens, int docId) {
        SearchToken firstToken = searchTokens.peek();
        List<Integer> firstTermPos = getDocIdPositions(firstToken.getToken(), docId);
        for (int positionOfFirstTerm : firstTermPos) {
            if (areTermAllFromPositionInDoc(positionOfFirstTerm, searchTokens.iterator(), docId)) return true;
        }
        return false;
    }

    private boolean areTermAllFromPositionInDoc(int position, Iterator<SearchToken> searchTokenIterator, int docId) {
        SearchToken previous = searchTokenIterator.next();
        while (searchTokenIterator.hasNext()) {
            SearchToken anotherToken = searchTokenIterator.next();
            List<Integer> anotherTermPos = getDocIdPositions(anotherToken.getToken(), docId);
            int posOfElementBetween = findInListNumBetween(anotherTermPos, position + 1,
                    position + previous.getPrecisionToNext());
            if (posOfElementBetween == -1)
                break;
            else
                position += posOfElementBetween - position;
            previous = anotherToken;
            if (!searchTokenIterator.hasNext()) return true;
        }
        return false;
    }

    private List<Integer> getDocIdPositions(String term, int docId) {
        CoordsInvIndexEntry coordsInvIndexEntry = index.get(term);
        if (coordsInvIndexEntry == null) return Collections.emptyList();
        List<Integer> positions = coordsInvIndexEntry.docIdToPositions.get(docId);
        return positions == null ? Collections.emptyList() : positions;
    }

    private int findInListNumBetween(List<Integer> data, int from, int to) {
        for (int i = from; i <= to; i++) {
            if (data.contains(i)) return i;
        }
        return -1;
    }

    public CoordsInvIndexEntry getTermInfo(String term) {
        return index.get(term);
    }

    public static class CoordsInvIndexEntry {
        int countOfRepeats;
        Map<Integer, SortedArrayList<Integer>> docIdToPositions;

        public CoordsInvIndexEntry() {
            docIdToPositions = new HashMap<>();
        }

        public Map<Integer, SortedArrayList<Integer>> getDocIdToPositions() {
            return docIdToPositions;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(countOfRepeats).append(" ");
            addMapInLine(docIdToPositions, builder);
            return builder.toString();
        }

        private void addMapInLine(Map<Integer, SortedArrayList<Integer>> map, StringBuilder builder) {
            Stream<Map.Entry<Integer, SortedArrayList<Integer>>> mapStream = map.entrySet().stream();
            mapStream.sorted().forEach(it -> {
                builder.append(it.getKey()).append(" ");
                addListInLineWithCommas(it.getValue(), builder);
            });
        }

        private void addListInLineWithCommas(SortedArrayList<Integer> stringList, StringBuilder builder) {
            stringList.forEach(it -> {
                builder.append(it).append(",");
            });
        }

    }

    public static class EditableEntry {
        private int docId;
        private int pos;

        public EditableEntry(int docId, int pos) {
            this.docId = docId;
            this.pos = pos;
        }
    }
}

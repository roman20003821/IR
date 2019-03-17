package ir.structures.impl;

import ir.searchParser.BooleanSearchParser;
import ir.searchParser.BooleanSearchParser.BoolSearchParsedQuery;
import ir.structures.abstraction.Editable;
import ir.structures.abstraction.Searchable;

import java.util.*;
import java.util.stream.Stream;

public class IncidenceMatrix implements Searchable<String>, Editable<Integer> {

    private int[] documentsId;
    private Map<String, byte[]> matrix;
    private BooleanSearchParser parser;

    public IncidenceMatrix(int[] documentsId) {
        assert (isSorted(documentsId));
        this.documentsId = documentsId;
        matrix = new TreeMap<>();
        parser = new BooleanSearchParser();
    }

    @Override
    public void addTerm(String term, Integer docId) {
        int docPos = Arrays.binarySearch(documentsId, docId);
        if (docId == -1) throw new IllegalArgumentException("Doc id was not found");
        byte[] idLine = matrix.computeIfAbsent(term.toLowerCase(), k -> new byte[documentsId.length]);
        idLine[docPos] = 1;
    }

    @Override
    public void clear() {
        matrix = new TreeMap<>();
    }

    @Override
    public int getSize() {
        return matrix.size();
    }

    @Override
    public Stream<String> getSortedStream() {
        Stream<Map.Entry<String, byte[]>> mapStream = matrix.entrySet().stream();
        return mapStream.map(it -> it.getKey() + " " + getByteArrayInLine(it.getValue()));
    }

    private String getByteArrayInLine(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) builder.append(b).append(" ");
        return builder.toString();
    }

    private boolean isSorted(int[] documentsId) {
        for (int i = 0; i < documentsId.length - 1; i++) {
            if (documentsId[i] > documentsId[i + 1]) return false;
        }
        return true;
    }

    @Override
    public Set<Integer> search(String query) {
        BoolSearchParsedQuery parsedQuery = parser.parse(query);
        Iterator<String> operationIterator = parsedQuery.getOperations().iterator();
        Iterator<String> termIterator = parsedQuery.getTerms().iterator();
        byte[] res = getCopy(getNeededDocId(termIterator.next()));
        while (operationIterator.hasNext()) {
            String operation = operationIterator.next();
            switch (operation) {
                case BooleanSearchParser.AND_QUERY:
                    twoWordsQuery(res, getNeededDocId(termIterator.next()), res, (first, second) -> first == 1 && second == 1);
                    break;
                case BooleanSearchParser.OR_QUERY:
                    twoWordsQuery(res, getNeededDocId(termIterator.next()), res, (first, second) -> first == 1 || second == 1);
                    break;
                default:
                    break;
            }
        }
        return toIdSet(res);
    }

    private void twoWordsQuery(byte[] firstLine, byte[] secondLine, byte[] resultLine, LogicOperation operation) {
        for (int i = 0; i < firstLine.length; i++) {
            if (operation.isResultAterOperation(firstLine[i], secondLine[i]))
                resultLine[i] = 1;
            else resultLine[i] = 0;
        }
    }

    private byte[] getCopy(byte[] bytes) {
        byte[] copy = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            copy[i] = bytes[i];
        }
        return copy;
    }

    private byte[] getNeededDocId(String term) {
        byte[] docIdLine = getDocIdLine(term);
        return term.startsWith(BooleanSearchParser.NOT_SIGN) ? invertDocIdLine(docIdLine) : docIdLine;
    }

    private byte[] getDocIdLine(String term) {
        byte[] docIdLine = matrix.get(term);
        if (docIdLine == null) docIdLine = new byte[documentsId.length];
        return docIdLine;
    }

    private byte[] invertDocIdLine(byte[] docIdLine) {
        byte[] result = new byte[docIdLine.length];
        for (int i = 0; i < result.length; i++) {
            if (docIdLine[i] == 0) result[i] = 1;
            else result[i] = 0;
        }
        return result;
    }

    private Set<Integer> toIdSet(byte[] line) {
        Set<Integer> idSet = new HashSet<>();
        for (int i = 0; i < line.length; i++) {
            if (line[i] != 0) idSet.add(documentsId[i]);
        }
        return idSet;
    }

    interface LogicOperation {
        boolean isResultAterOperation(byte first, byte second);
    }
}

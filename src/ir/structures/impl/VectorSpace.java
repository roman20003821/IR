package ir.structures.impl;

import ir.structures.abstraction.TermWeightCountable;

import java.util.*;

public class VectorSpace {
    public static final int UNMENTIONED_DOC_ID = -1;
    private Map<Integer, Vector> data;

    public VectorSpace() {
        data = new HashMap<>();
    }

    public void addDocIdAndTerm(int docId, String term) {
        Vector vector = data.computeIfAbsent(docId, k -> Vector.emptyVector());
        vector.addTerm(term);
    }

    public void clear() {
        data.clear();
    }

    public double cosSimilarity(int docId, Vector vector, TermWeightCountable termWeightCountable) {
        Vector vectorWithDocId = data.get(docId);
        if (vectorWithDocId == null)
            vectorWithDocId = Vector.emptyVector();
        return cosSimilarity(vectorWithDocId, docId, vector, UNMENTIONED_DOC_ID, termWeightCountable);
    }

    public static double cosSimilarity(Vector vector1, int docId1, Vector vector2, int docId2, TermWeightCountable termWeightCountable) {
        double res = 0;
        Iterator<String> vectorIterator1 = vector1.getIterator();
        Iterator<String> vectorIterator2 = vector1.getIterator();
        while (vectorIterator1.hasNext() && vectorIterator2.hasNext()) {
            res += getTermWeightInDoc(vectorIterator1.next(), docId1, termWeightCountable)
                    * getTermWeightInDoc(vectorIterator2.next(), docId2, termWeightCountable);
        }
        return res;
    }

    private static double getTermWeightInDoc(String term, int docId, TermWeightCountable termWeightCountable) {
        return termWeightCountable.termWeightInDoc(term, docId);
    }

    public static class Vector {
        private Set<String> termSet;

        public Vector() {
            this(new HashSet<>());
        }

        public Vector(Set<String> termSet) {
            this.termSet = termSet;
        }

        public void addTerm(String term) {
            termSet.add(term);
        }

        public Iterator<String> getIterator() {
            return termSet.iterator();
        }

        public static Vector emptyVector() {
            return new Vector();
        }

    }
}

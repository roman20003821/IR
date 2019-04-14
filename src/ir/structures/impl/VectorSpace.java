package ir.structures.impl;

import ir.structures.abstraction.TermWeightCountable;

import java.util.*;

public class VectorSpace {
    public static final int UNMENTIONED_DOC_ID = -1;
    private Map<Integer, Vector> data;

    VectorSpace() {
        data = new HashMap<>();
    }

    void addDocIdAndTerm(int docId, String term) {
        Vector vector = data.computeIfAbsent(docId, k -> Vector.emptyVector());
        vector.addTerm(term);
    }

    public void clear() {
        data.clear();
    }

    public int getAverageVectorLength() {
        int average = 0;
        for (Map.Entry<Integer, Vector> mapEntry : data.entrySet()) {
            average = (average + mapEntry.getValue().getSize()) / 2;
        }
        return average;
    }

    public int getVectorLength(int docId) {
        Vector vector = data.get(docId);
        if (vector == null) return 0;
        return vector.getSize();
    }

    double cosSimilarity(int docId, Vector vector, TermWeightCountable termWeightCountable) {
        Vector vectorWithDocId = data.get(docId);
        if (vectorWithDocId == null)
            vectorWithDocId = Vector.emptyVector();
        return cosSimilarity(vectorWithDocId, docId, vector, termWeightCountable);
    }

    private double cosSimilarity(Vector vector1, int docId1, Vector vector2, TermWeightCountable termWeightCountable) {
        double res = 0;
        Iterator<String> vectorIterator1 = vector1.getIterator();
        Iterator<String> vectorIterator2 = vector2.getIterator();
        while (vectorIterator1.hasNext() && vectorIterator2.hasNext()) {
            res += getTermWeightInDoc(vectorIterator1.next(), docId1, termWeightCountable)
                    * getTermWeightInDoc(vectorIterator2.next(), vector2.termsSet, termWeightCountable);
        }
        return res;
    }

    private double getTermWeightInDoc(String term, int docId, TermWeightCountable termWeightCountable) {
        return termWeightCountable.termWeightInDoc(term, docId);
    }

    private double getTermWeightInDoc(String term, Set<String> termsSet, TermWeightCountable termWeightCountable) {
        return termWeightCountable.termWeightInDoc(term, termsSet);
    }

    public static class Vector {
        private Set<String> termsSet;

        public Vector() {
            this(new HashSet<>());
        }

        public Vector(Set<String> termsSet) {
            this.termsSet = termsSet;
        }

        public void addTerm(String term) {
            termsSet.add(term);
        }

        public int getSize() {
            return termsSet.size();
        }

        public Iterator<String> getIterator() {
            return termsSet.iterator();
        }

        public static Vector emptyVector() {
            return new Vector();
        }

    }
}

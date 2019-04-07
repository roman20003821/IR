package ir.structures.abstraction;

import java.util.Set;

public interface TermWeightCountable {
    double termWeightInDoc(String term, int docId);

    double termWeightInDoc(String term, Set<String> termsFormDoc);
}

package ir.search;

import java.util.Set;

public interface BooleanSearchable {
    Set<Integer> getIdSet(String term);

    Set<Integer> invertDocIdSet(Set<Integer> docIdSet);
}

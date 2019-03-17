package ir.search;

import ir.tools.SearchUtility;
import ir.searchParser.BooleanSearchParser;
import ir.structures.abstraction.Searchable;

import java.util.Iterator;
import java.util.Set;

public class BooleanSearch{
    private BooleanSearchable structure;
    private BooleanSearchParser parser;

    public BooleanSearch(BooleanSearchable structure) {
        this.structure = structure;
        parser = new BooleanSearchParser();
    }

    public Set<Integer> search(BooleanSearchParser.BoolSearchParsedQuery parsedQuery) {
        Iterator<String> operationIterator = parsedQuery.getOperations().iterator();
        Iterator<String> termIterator = parsedQuery.getTerms().iterator();
        Set<Integer> res = getNeededDocIdSet(termIterator.next());
        while (operationIterator.hasNext()) {
            String operation = operationIterator.next();
            switch (operation) {
                case BooleanSearchParser.AND_QUERY:
                    res = SearchUtility.intersection(res.iterator(), getNeededDocIdSet(termIterator.next()).iterator());
                    break;
                case BooleanSearchParser.OR_QUERY:
                    res = SearchUtility.unity(res.iterator(), getNeededDocIdSet(termIterator.next()).iterator());
                    break;
                default:
                    break;
            }
        }
        return res;
    }

    private Set<Integer> getNeededDocIdSet(String term) {
        Set<Integer> docIdSet = structure.getIdSet(term);
        return term.startsWith(BooleanSearchParser.NOT_SIGN) ? structure.invertDocIdSet(docIdSet) : docIdSet;
    }
}

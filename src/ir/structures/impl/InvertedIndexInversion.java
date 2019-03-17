package ir.structures.impl;

import ir.tools.SearchUtility;
import ir.searchParser.InversionParser;
import ir.structures.abstraction.InvertedIndex;

import java.util.*;
import java.util.stream.Stream;

public class InvertedIndexInversion implements InvertedIndex<Integer, String> {
    private static final int ALPHABET_SIZE = 27;
    private InversionParser inversionParser;
    private Trie trie;
    private InvertedIndexTerm invertedIndexTerm;

    public InvertedIndexInversion(InvertedIndexTerm invertedIndexTerm) {
        this.invertedIndexTerm = invertedIndexTerm;
        inversionParser = new InversionParser();
        trie = new Trie(ALPHABET_SIZE);
    }

    @Override
    public void addTerm(String term, Integer docId) {
        List<String> parsedTerm = inversionParser.parseTerm(term);
        parsedTerm.forEach(it -> trie.addTerm(it));
    }

    @Override
    public void clear() {
        trie = new Trie(ALPHABET_SIZE);
    }

    @Override
    public int getSize() {
        return trie.getTermsCount();
    }

    @Override
    public Stream<String> getSortedStream() {
        return null;
    }

    @Override
    public Set<Integer> search(String query) {
        String parsed = inversionParser.parse(query);
        Set<String> terms = trie.search(parsed);
        terms = removeAdditionalSign(terms);
        return toDocId(terms);
    }

    private Set<String> removeAdditionalSign(Set<String> terms) {
        Set<String> res = new HashSet<>();
        terms.forEach(it -> {
            res.add(inversionParser.parseBack(it));
        });
        return res;
    }

    private Set<Integer> toDocId(Set<String> terms) {
        if (terms.size() < 1) return Collections.emptySet();
        Iterator<String> termIterator = terms.iterator();
        Set<Integer> res = invertedIndexTerm.search(termIterator.next());
        while (termIterator.hasNext()) {
            res = SearchUtility.unity(res.iterator(), invertedIndexTerm.search(termIterator.next()).iterator());
        }
        return res;
    }
}

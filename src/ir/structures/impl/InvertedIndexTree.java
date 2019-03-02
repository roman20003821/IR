package ir.structures.impl;

import ir.structures.abstraction.InvertedIndex;

import java.util.Set;
import java.util.stream.Stream;

public class InvertedIndexTree implements InvertedIndex {

    private static int ALPHABET_SIZE = 26;

    private Trie trie;
    private Trie reversed;

    public InvertedIndexTree() {
        trie = new Trie(ALPHABET_SIZE);
        reversed = new Trie(ALPHABET_SIZE);
    }

    @Override
    public void addTerm(String term, int docId) {
        trie.addTerm(term);
        reversed.addTerm(new StringBuilder(term).reverse().toString());
    }

    @Override
    public void clear() {
        trie = new Trie(ALPHABET_SIZE);
        reversed = new Trie(ALPHABET_SIZE);
    }

    @Override
    public int getSize() {
        return trie.getTermsCount() * 2;
    }

    @Override
    public Stream<String> getSortedStream() {
        return null;
    }

    @Override
    public Set<Integer> search(String query) {
        return null;
    }
}

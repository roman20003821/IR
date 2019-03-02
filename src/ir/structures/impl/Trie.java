package ir.structures.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Trie {
    private int lettersCount;
    private TrieNode root;
    private int termsCount;

    public Trie(int initLettersCount) {
        this.lettersCount = initLettersCount;
    }

    private class TrieNode {
        TrieNode[] children = new TrieNode[lettersCount];
        boolean isEndOfWord;
    }

    public void addTerm(String term) {
        TrieNode node = root;
        for (int level = 0; level < term.length(); level++) {
            int index = term.charAt(level) - 'a';
            if (node.children[index] == null)
                node.children[index] = new TrieNode();
            node = node.children[index];
        }
        if (!node.isEndOfWord) {
            termsCount++;
            node.isEndOfWord = true;
        }
    }

    public Set<String> search(String term) {
        TrieNode node = root;
        for (int level = 0; level < term.length(); level++) {
            int index = term.charAt(level) - 'a';
            if (node.children[index] == null)
                return Collections.emptySet();
            node = node.children[index];
        }
        Set<String> res = new HashSet<>();
        collectTerms(node, res, term);
        return res;
    }

    private void collectTerms(TrieNode node, Set<String> terms, String base) {
        if (node.isEndOfWord) terms.add(base);
        for (int index = 0; index < node.children.length; index++) {
            if (node.children[index] == null)
                return;
            node = node.children[index];
            collectTerms(node, terms, base + (char) ('a' + index));
        }
    }

    public int getTermsCount() {
        return termsCount;
    }

}

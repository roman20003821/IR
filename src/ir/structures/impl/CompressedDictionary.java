package ir.structures.impl;

import ir.structures.abstraction.Compressible;
import ir.structures.abstraction.Searchable;
import ir.structures.impl.Dictionary.DictionaryEntry;

import java.util.Iterator;
import java.util.stream.Stream;

public class CompressedDictionary implements Searchable<Integer> {
    private static final char PREFIX_END_SIGN = '*';
    private static final int SMALLER = -2;
    private static final int BIGGER = -1;
    private int[] frequencies;
    private int[] blockPointers;
    private int blockSize;
    private int currentBlocksCount;
    private String dictionary;

    public CompressedDictionary(Compressible<DictionaryEntry> compressible, int blockSize) {
        this.blockSize = blockSize;
        Stream<DictionaryEntry> vocabEntryStream = compressible.getSortedEntryStream();
        Stream<String> termStream = vocabEntryStream.map(DictionaryEntry::getTerm);
        initFields(compressible.getSize());
        fillInFrequencyArray(vocabEntryStream);
        compress(termStream);
    }

    private void initFields(int termCount) {
        frequencies = new int[termCount];
        int blockPointersSize = termCount % blockSize == 0 ? termCount / blockSize : termCount / blockSize + 1;
        blockPointers = new int[blockPointersSize];
        dictionary = "";
    }

    private void fillInFrequencyArray(Stream<DictionaryEntry> entryStream) {
        Iterator<DictionaryEntry> dictionaryEntryIterator = entryStream.iterator();
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = dictionaryEntryIterator.next().getCountOfRepeats();
        }
    }

    private void compress(Stream<String> termsStream) {
        Iterator<String> termsIterator = termsStream.iterator();
        while (termsIterator.hasNext()) {
            String[] block = getNextBlock(termsIterator);
            compress(block);
        }
    }

    private String[] getNextBlock(Iterator<String> vocabEntryIterator) {
        String[] oneBlockEntries = new String[blockSize];
        for (int i = 0; i < 4 && vocabEntryIterator.hasNext(); i++) {
            oneBlockEntries[i] = vocabEntryIterator.next();
        }
        return oneBlockEntries;
    }

    private void compress(String[] oneBlockEntries) {
        StringBuilder builder = new StringBuilder(dictionary);
        compress(builder, oneBlockEntries);
    }

    private void compress(StringBuilder builder, String[] oneBlockEntries) {
        String commonPrefix = commonPrefix(oneBlockEntries);
        addPointerToBlock(builder.toString().length());
        builder.append(oneBlockEntries[0].length()).append(commonPrefix).append(PREFIX_END_SIGN).append(getEnding(commonPrefix, oneBlockEntries[0]));
        for (int i = 1; i < oneBlockEntries.length; i++) {
            String ending = getEnding(commonPrefix, oneBlockEntries[i]);
            builder.append(ending.length()).append(ending);
        }
    }

    private String commonPrefix(String[] terms) {
        StringBuilder builder = new StringBuilder();
        int minTermLength = getMinLength(terms);
        for (int i = 0; i < minTermLength; i++) {
            if (charsSameAt(terms, i)) {
                builder.append(terms[0].charAt(i));
            }
        }
        return builder.toString();
    }

    private void addPointerToBlock(int blockStartIndex) {
        blockPointers[currentBlocksCount++] = blockStartIndex;
    }

    private int getMinLength(String[] terms) {
        if (terms.length == 0) return -1;
        int min = terms[0].length();
        for (String term : terms) {
            if (min > term.length()) min = term.length();
        }
        return min;
    }

    private boolean charsSameAt(String[] terms, int index) {
        for (int i = 1; i < terms.length; i++) {
            if (terms[i].charAt(index) != terms[i - 1].charAt(index)) return false;
        }
        return true;
    }

    private String getEnding(String prefix, String term) {
        if (prefix.length() == term.length()) return "";
        return term.substring(prefix.length());
    }

    @Override
    public Integer search(String term) {
        return search(term, 0, blockPointers.length - 1);
    }

    public int search(String term, int lo, int hi) {
        if (lo > hi) return 0;
        int mid = lo + (hi - lo) / 2;
        int cmp = compare(term, mid);
        if (cmp == SMALLER) {
            return search(term, lo, mid - 1);
        } else if (cmp == BIGGER) {
            return search(term, mid + 1, hi);
        } else {
            return frequencies[mid * blockSize + cmp];
        }
    }

    private int compare(String term, int blockPointer) {
        Iterator<String> blockIterator = blockIterator(blockPointer);
        String next = blockIterator.next();
        for (int i = 0; i < blockSize; i++) {
            int cmp = term.compareTo(next);
            if (cmp < 0) return SMALLER;
            else if (cmp == 0) return i;
            next = blockIterator.next();
        }
        return BIGGER;
    }

    private Iterator<String> blockIterator(int blockPointer) {
        return new Iterator<>() {
            int currentTerm = 1;
            String commonPrefix = getCommonPrefix(blockPointer);
            int index = blockPointer;

            @Override
            public boolean hasNext() {
                return currentTerm != blockSize && index < dictionary.length();
            }

            @Override
            public String next() {
                if (currentTerm == 1) index = skipPrefix(index);
                String next = getNextTerm(commonPrefix, index);
                index = skipDigits(index) + next.length();
                return next;
            }
        };
    }

    private String getCommonPrefix(int index) {
        StringBuilder builder = new StringBuilder();
        index = skipDigits(index);
        while (dictionary.charAt(index) != PREFIX_END_SIGN) builder.append(dictionary.charAt(index++));
        return builder.toString();
    }

    private int skipDigits(int index) {
        while (Character.isDigit(dictionary.charAt(index))) index++;
        return index;
    }

    private int skipPrefix(int index) {
        while (dictionary.charAt(index) != PREFIX_END_SIGN) index++;
        return ++index;
    }

    private String getNextTerm(String prefix, int indexOfEnding) {
        StringBuilder builder = new StringBuilder(prefix);
        while (!Character.isDigit(dictionary.charAt(indexOfEnding))) builder.append(dictionary.charAt(indexOfEnding));
        return builder.toString();
    }
}

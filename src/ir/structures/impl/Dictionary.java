package ir.structures.impl;

import ir.structures.abstraction.Compressible;
import ir.structures.abstraction.Editable;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class Dictionary implements Editable, Compressible<Dictionary.DictionaryEntry>, Serializable {
    private int size;
    private DictionaryEntry[] data;

    public Dictionary() {
        this(10);
    }

    public Dictionary(int initCapacity) {
        data = new DictionaryEntry[initCapacity];
    }

    @Override
    public void addTerm(String term, int docId) {
        if (term == null) throw new IllegalArgumentException("Term is null");
        if (data.length == size) resize(data.length * 2);
        int pos = getPos(term);
        if (data[pos].getTerm().equals(term)) {
            data[pos].incrementRepeats();
        }
        for (int i = size; i > pos; i--) {
            data[i] = data[i - 1];
        }
        data[pos] = new DictionaryEntry(term, 1);
        size++;
    }

    private int getPos(String word) {
        int lo = 0;
        int hi = size - 1;
        int mid = lo + (hi - lo) / 2;
        while (hi > lo) {
            int cmp = word.compareTo(data[mid].term);
            if (cmp == 0) return mid;
            else if (cmp < 0) hi = mid - 1;
            else lo = mid + 1;
            mid = lo + (hi - lo) / 2;
        }
        return lo;
    }

    private void resize(int capacity) {
        DictionaryEntry temp[] = new DictionaryEntry[capacity];
        if (size >= 0) System.arraycopy(data, 0, temp, 0, size);
        data = temp;
    }

    @Override
    public void clear() {
        data = new DictionaryEntry[10];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Stream<String> getSortedStream() {
        return Arrays.stream(data).map(DictionaryEntry::toString);
    }

    @Override
    public Stream<DictionaryEntry> getSortedEntryStream() {
        return Arrays.stream(data);
    }

    public class DictionaryEntry {
        private String term;
        private int countOfRepeats;

        public DictionaryEntry(String term, int countOfRepeats) {
            this.term = term;
            this.countOfRepeats = countOfRepeats;
        }

        private void incrementRepeats() {
            countOfRepeats++;
        }

        public String getTerm() {
            return term;
        }

        public int getCountOfRepeats() {
            return countOfRepeats;
        }

        public void setCountOfRepeats(int countOfRepeats) {
            this.countOfRepeats = countOfRepeats;
        }

        @Override
        public String toString() {
            return term + " " + countOfRepeats;
        }
    }
}

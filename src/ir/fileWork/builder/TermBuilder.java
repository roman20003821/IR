package ir.fileWork.builder;

import ir.Tester;
import ir.fileWork.FileWorker;
import ir.structures.abstraction.Editable;
import ir.tools.Pair;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class TermBuilder implements Builder {

    private Editable editable;
    private static String INDEX_DIR_NAME;
    private static final int TERM_POS_IN_LINE = 0;
    private static final int COUNT_OF_REPEATS_POS_IN_LINE = 1;
    private static final int DOC_ID_START_POS = 2;
    public static final char SPLIT_SIGN = ' ';
    public static int indexCounter;


    public TermBuilder(Editable editable) {
        this.editable = editable;
        INDEX_DIR_NAME = "/home/roman/Documents/" + editable.getClass().getSimpleName();
    }

    @Override
    public void buildStructureOnDisk(int maxBlockSize, Map<Integer, String> docIdToPath) throws IOException {
        createBlocksInDir(maxBlockSize, docIdToPath);
        List<String> files = FileWorker.getFilesInTempDirectory(INDEX_DIR_NAME);
        merge(files, FileWorker.findEmptyPath(INDEX_DIR_NAME, Tester.INDEX_NAME_PREFIX, Tester.INDEX_EXTENSION));
        FileWorker.deleteTempDirectory(INDEX_DIR_NAME);
    }

    private void createBlocksInDir(int maxBlockSize, Map<Integer, String> docIdToPath) {
        Set<Map.Entry<Integer, String>> mapEntrySet = docIdToPath.entrySet();
        mapEntrySet.forEach(entry -> {
            try {
                buildFromFileAndWrite(entry.getKey(), entry.getValue(), maxBlockSize);
                FileWorker.filesReaded++;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("File " + entry.getValue() + " was not read");
            }
        });

        writeAndClear();

    }

    public void buildFromFileAndWrite(int docId, String fileName, int maxBlockSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (editable.getSize() >= maxBlockSize) writeAndClear();
                if (token != null) {
                    editable.addTerm(FileWorker.parseWord(token), docId);
                    indexCounter++;
                }
            }
        }
        reader.close();
    }

    public void writeAndClear() {
        write();
        editable.clear();
        System.gc();
    }

    public void write() {
        try {
            FileWorker.writeToDisk(editable.getSortedStream(), FileWorker.getDirectoryPathWithBlocks(INDEX_DIR_NAME),
                    Tester.BLOCK_NAME_PREFIX, Tester.BLOCK_EXTENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void merge(List<String> fileNames, String mergedFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFile));
        List<BufferedReader> readers = getReaders(fileNames);
        List<String[]> parsedLines = new ArrayList<>();
        fillParsedLinesFromReaders(readers, parsedLines);
        merge(writer, readers, parsedLines);
        writer.close();
    }

    @Override
    public void build(Stream<Pair<String, Integer>> termAndDocIdStream) {
        termAndDocIdStream.forEach(pair -> editable.addTerm(pair.getKey(), pair.getValue()));
    }

    private void merge(BufferedWriter writer, List<BufferedReader> readers, List<String[]> parsedLines) {
        int smallestIndex = findSmallestIndex(parsedLines);
        String[] toWrite = parsedLines.get(findSmallestIndex(parsedLines));
        tryToGetNextLine(readers, parsedLines, smallestIndex);
        while (readers.size() != 0) {
            smallestIndex = findSmallestIndex(parsedLines);
            String[] next = parsedLines.get(smallestIndex);
            if (areTermsEqual(toWrite, next)) {
                toWrite = mergeLines(toWrite, next);
            } else {
                writeLine(writer, toWrite);
                toWrite = next;
            }
            tryToGetNextLine(readers, parsedLines, smallestIndex);
        }
    }

    private List<BufferedReader> getReaders(List<String> fileNames) {
        List<BufferedReader> readers = new ArrayList<>();
        fileNames.forEach(it -> {
            try {
                readers.add(new BufferedReader(new FileReader(it)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        return readers;
    }

    private void fillParsedLinesFromReaders(List<BufferedReader> readers, List<String[]> parsedLines) {
        for (BufferedReader reader : readers) {
            parsedLines.add(parseLine(readLine(reader)));
        }
    }

    private String readLine(BufferedReader reader) {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private String[] parseLine(String line) {
        return line.split(String.valueOf(SPLIT_SIGN));
    }

    private int findSmallestIndex(List<String[]> parsedLines) {
        if (parsedLines.size() == 0) throw new IllegalArgumentException("parsed lines array is empty");
        Iterator<String[]> linesIterator = parsedLines.iterator();
        String smallest = linesIterator.next()[TERM_POS_IN_LINE];
        int smallestIndex = 0;
        for (int i = 1; linesIterator.hasNext(); i++) {
            String term = linesIterator.next()[TERM_POS_IN_LINE];
            if (smallest.compareTo(term) > 0) {
                smallest = term;
                smallestIndex = i;
            }
        }
        return smallestIndex;
    }

    private void tryToGetNextLine(List<BufferedReader> readers, List<String[]> lines, int index) {
        String line = readLine(readers.get(index));
        if (line == null) {
            tryToCloseReader(readers.remove(index));
            lines.remove(index);
        } else {
            lines.set(index, parseLine(line));
        }
    }

    private void tryToCloseReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean areTermsEqual(String[] parsedLine1, String[] parsedLine2) {
        if (parsedLine1 == null) return false;
        return parsedLine1[TERM_POS_IN_LINE].equals(parsedLine2[TERM_POS_IN_LINE]);
    }

    private String[] mergeLines(String[] line1, String[] line2) {
        String builder = line1[TERM_POS_IN_LINE] + SPLIT_SIGN + getRepeatsSum(line1, line2) + SPLIT_SIGN +
                mergeDocId(line1, line2);
        return parseLine(builder);
    }

    private int getRepeatsSum(String[] line1, String[] line2) {
        return Integer.parseInt(line1[COUNT_OF_REPEATS_POS_IN_LINE]) + Integer.parseInt(line2[COUNT_OF_REPEATS_POS_IN_LINE]);
    }

    private String mergeDocId(String[] line1, String[] line2) {
        StringBuilder builder = new StringBuilder();
        int i = DOC_ID_START_POS;
        int j = DOC_ID_START_POS;
        for (int k = 0; k < line1.length + line2.length - DOC_ID_START_POS * 2; k++) {
            if (i >= line1.length && j >= line2.length) break;
            else if (i >= line1.length) {
                builder.append(line2[j++]).append(SPLIT_SIGN);
                continue;
            } else if (j >= line2.length) {
                builder.append(line1[i++]).append(SPLIT_SIGN);
                continue;
            }
            int firstId = Integer.parseInt(line1[i]);
            int secondId = Integer.parseInt(line2[j]);
            if (firstId > secondId) builder.append(line2[j++]).append(SPLIT_SIGN);
            else if (firstId < secondId) builder.append(line1[i++]).append(SPLIT_SIGN);
            else {
                builder.append(line1[i++]).append(SPLIT_SIGN);
                j++;
            }
        }
        return builder.toString();
    }

    private void writeLine(BufferedWriter writer, String[] line) {
        try {
            writer.write(convertToSting(line) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertToSting(String[] line) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            builder.append(line[i]).append(SPLIT_SIGN);
        }
        return builder.toString();
    }
}

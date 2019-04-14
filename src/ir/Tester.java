package ir;

import ir.fileWork.builder.Builder;
import ir.fileWork.builder.BuilderFactory;
import ir.fileWork.builder.TermBuilder;
import ir.fileWork.FileWorker;
import ir.fileWork.termStream.SimpleAnalyser;
import ir.fileWork.termStream.SimpleFileStreamCreator;
import ir.fileWork.termStream.StreamCreator;
import ir.structures.IndexType;
import ir.structures.abstraction.InvertedIndex;
import ir.structures.impl.InvertedIndexTerm;
import ir.structures.impl.InvertedIndexZone;
import ir.tools.Console;
import ir.tools.IdHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Tester {

    public static final String SOURCE_DIRECTORY = "/home/roman/Downloads/gutenberg/gutenberg";
    public static final String BLOCK_NAME_PREFIX = "Block";
    public static final String INDEX_NAME_PREFIX = "Index";
    public static final String INDEX_EXTENSION = ".txt";
    public static final String BLOCK_EXTENSION = "";
    public static final String TEMP_DIR = "temp";
    public static int FILES_TO_READ;

    private static final int BLOCK_SIZE = 1000000;

    public static void main(String[] args) {
        Console console = Console.getInstance();
        IdHelper idHelper = IdHelper.getInstance();
        List<String> files = FileWorker.collectAllFilesFromDirectories(SOURCE_DIRECTORY);
        FILES_TO_READ = files.size();
        idHelper.addFiles(files);
        System.out.println("Start building...");
        InvertedIndex invertedIndex = new InvertedIndexZone();
        BuilderFactory builderFactory = new BuilderFactory();
        Builder builder = builderFactory.getEditableBuilder(invertedIndex);
        StreamCreator streamCreator = new SimpleFileStreamCreator(new SimpleAnalyser());
        long currentTime = System.currentTimeMillis();
        try {
            for (Map.Entry<Integer, String> entry : idHelper.getFileInfos().entrySet()) {
                builder.build(streamCreator.termAndDocIdStream(entry.getValue(),entry.getKey()));
                System.out.println(System.currentTimeMillis() - currentTime);
                System.out.println(TermBuilder.indexCounter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

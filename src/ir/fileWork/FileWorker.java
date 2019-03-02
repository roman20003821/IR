package ir.fileWork;

import ir.Tester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FileWorker {

    public static int filesReaded;

    public static List<String> getFilesInTempDirectory(String directoryPath) {
        return getFilesInDirectory(directoryPath + "/" + Tester.TEMP_DIR);
    }

    public static List<String> getFilesInDirectory(String directoryPath) {
        List<String> files = new LinkedList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) return files;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                files.addAll(getFilesInDirectory(file.getPath()));
            else
                files.add(file.getPath());
        }
        return files;
    }

    public static List<String> collectAllFilesFromDirectories(String directoryPath) {
        List<String> files = new LinkedList<>();
        collectAllFilesFromDirectories(directoryPath, files);
        return files;
    }

    private static void collectAllFilesFromDirectories(String directoryPath, List<String> files) {
        File directory = new File(directoryPath);
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                collectAllFilesFromDirectories(file.getPath(), files);
            else files.add(file.getPath());
        }
    }

    public static void writeToDisk(Stream<String> editableStream, String directoryName, String prefixName, String fileExtension) throws IOException {
        createDirectoriesIfNotExist(directoryName);
        String filePath = findEmptyPath(directoryName, prefixName, fileExtension);
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        Iterator<String> streamIterator = editableStream.iterator();
        while (streamIterator.hasNext()) {
            writer.write(streamIterator.next() + "\n");
        }
        System.out.println(filesReaded / Tester.FILES_TO_READ * 100 + "% loading...");
        writer.close();
    }

    private static void createDirectoriesIfNotExist(String directoryPath) {
        String[] directories = directoryPath.split("/");
        StringBuilder current = new StringBuilder();
        for (String directory : directories) {
            current.append(directory).append("/");
            createDirectoryIfNotExist(current.toString());
        }

    }

    private static void createDirectoryIfNotExist(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists()) file.mkdir();
    }


    public static String findEmptyPath(String directoryPath, String prefix, String fileExtension) {
        int secondNamePart = 0;
        while (isFileExist(directoryPath + "/" + prefix + secondNamePart + fileExtension)) secondNamePart++;
        return directoryPath + "/" + prefix + secondNamePart + fileExtension;
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void deleteTempDirectory(String indexClassName) {
        File tempDirectory = new File(getDirectoryPathWithBlocks(indexClassName));
        for (File file : Objects.requireNonNull(tempDirectory.listFiles())) {
            file.delete();
        }
        tempDirectory.delete();
    }

    public static String parseWord(String word) {
        StringBuilder builder = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) builder.append(Character.toLowerCase(c));
        }
        return builder.toString();
    }

    public static String getDirectoryPathWithBlocks(String indexClassName) {
        return indexClassName + "/" + Tester.TEMP_DIR;
    }
}

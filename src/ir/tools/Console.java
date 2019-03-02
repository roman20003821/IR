package ir.tools;

import ir.Tester;
import ir.fileWork.FileWorker;
import ir.structures.abstraction.Searchable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Console {
    private static Console INSTANCE;
    private BufferedReader reader;

    private Console() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static Console getInstance() {
        if (INSTANCE == null) INSTANCE = new Console();
        return INSTANCE;
    }

    public List<String> readCorrectFileNamesFromSourceDir() {
        List<String> files = new ArrayList<>();
        while (true) {
            System.out.println("Enter file name(or stop): ");
            try {
                String fileName = reader.readLine().split(" ")[0];
                if (fileName.equalsIgnoreCase("stop")) break;
                if (!FileWorker.isFileExist(Tester.SOURCE_DIRECTORY + fileName)) {
                    System.out.println("Wrong file name");
                    continue;
                }
                files.add(Tester.SOURCE_DIRECTORY + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    public void makeSearch(Searchable<Set<Integer>> searchable) {
        while (true) {
            System.out.println("Enter query(or stop):");
            try {
                String query = reader.readLine();
                if (query.equalsIgnoreCase("stop")) break;
                IdHelper.getInstance().namesFromId(searchable.search(query)).forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

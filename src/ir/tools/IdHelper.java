package ir.tools;

import java.util.*;


public class IdHelper {
    private Map<Integer, String> fileInfos;
    private static int currentDocId;

    private static volatile IdHelper idHelper;

    public static synchronized IdHelper getInstance() {
        if (idHelper == null) idHelper = new IdHelper();
        return idHelper;
    }

    private IdHelper() {
        fileInfos = new HashMap<>();
        currentDocId = 0;
    }

    public void addFiles(List<String> filePathes) {
        filePathes.forEach(this::addFile);
    }

    public void addFile(String filePath) {
        fileInfos.put(currentDocId++, filePath);
    }

    public Set<Integer> getDocumentsId() {
        return fileInfos.keySet();
    }

    public List<String> namesFromId(Set<Integer> idList) {
        LinkedList<String> list = new LinkedList<>();
        for (int id : idList) {
            list.add(fileInfos.get(id));
        }
        return list;
    }

    public void forEachFileAndId(FileAndIdAction fileAndIdAction) {
        for (Map.Entry<Integer, String> entry : fileInfos.entrySet()) {
            fileAndIdAction.action(entry.getKey(), entry.getValue());
        }
    }

    public Map<Integer, String> getFileInfos() {
        return fileInfos;
    }

    public interface FileAndIdAction {
        void action(int id, String filePath);
    }
}

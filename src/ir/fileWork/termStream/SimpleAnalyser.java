package ir.fileWork.termStream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleAnalyser implements Analyser {
    @Override
    public List<String> analyse(String line) {
        return Arrays.stream(line.split("\\s++")).collect(Collectors.toList());
    }
}

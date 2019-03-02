package ir.searchParser;

import ir.tools.SearchUtility;

import java.util.*;

public class PhraseParser {

    public Queue<String> parse(String query) {
        String[] tokens = query.split("\\s+");
        if (tokens.length < 2) throw new IllegalArgumentException("Bad query");
        SearchUtility.toLowerCase(tokens);
        return getOperands(tokens);
    }

    private Queue<String> getOperands(String[] tokens) {
        Queue<String> operands = new LinkedList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            operands.add(tokens[i] + " " + tokens[i + 1]);
        }
        return operands;
    }
}

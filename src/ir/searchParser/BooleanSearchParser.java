package ir.searchParser;

import java.util.*;

public class BooleanSearchParser {

    public static final String AND_QUERY = "AND";
    public static final String OR_QUERY = "OR";
    public static final String NOT_SIGN = " ";

    public BooleanSearchParser() {
    }

    public BoolSearchParsedQuery parse(String query) {
        String[] tokens = query.split("\\s+");
        Queue<String> terms = new LinkedList<>();
        Queue<String> operations = new LinkedList<>();
        parseQuery(terms, operations, tokens);
        if (operations.size() + 1 != terms.size() && operations.size() <= 0)
            throw new IllegalArgumentException("Query is bad");
        return new BoolSearchParsedQuery(terms, operations);
    }

    private void parseQuery(Queue<String> terms, Queue<String> operations, String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            if (isTokenForSearch(tokens[i])) operations.add(tokens[i]);
            else if (isNotToken(tokens[i]) && i != tokens.length - 1)
                terms.add(NOT_SIGN + tokens[i]);
            else terms.add(tokens[i]);
        }
    }

    private boolean isTokenForSearch(String token) {
        return token.equals("AND") || token.equals("OR");
    }

    private boolean isNotToken(String token) {
        return token.equals("NOT");
    }

    public static class BoolSearchParsedQuery {
        Queue<String> terms;
        Queue<String> operations;

        public BoolSearchParsedQuery(Queue<String> terms, Queue<String> operations) {
            this.terms = terms;
            this.operations = operations;
        }

        public Queue<String> getTerms() {
            return terms;
        }

        public Queue<String> getOperations() {
            return operations;
        }
    }
}

package ir.searchParser;


import ir.tools.SearchUtility;

import java.util.LinkedList;
import java.util.Queue;

public class CoordinateParser {

    public Queue<SearchToken> parse(String query) {
        String[] tokens = query.split("\\s+");
        SearchUtility.toLowerCase(tokens);
        return toSearchTokens(tokens);
    }

    private Queue<SearchToken> toSearchTokens(String[] tokens) {
        LinkedList<SearchToken> searchTokens = new LinkedList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("/") && i != 0) {
                searchTokens.getLast().precisionToNext = Integer.valueOf(tokens[i].substring(1));
            } else {
                searchTokens.add(new SearchToken(tokens[i]));
            }
        }
        return searchTokens;
    }

    public class SearchToken {
        String token;
        int precisionToNext;

        public SearchToken(String token) {
            this.token = token;
            precisionToNext = 1;
        }

        public String getToken() {
            return token;
        }

        public int getPrecisionToNext() {
            return precisionToNext;
        }
    }
}

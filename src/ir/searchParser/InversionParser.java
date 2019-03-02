package ir.searchParser;

import java.util.LinkedList;
import java.util.List;

public class InversionParser {
    private static final char HELP_SIGN = '$';
    private static final char JOKER = '*';

    public List<String> parseTerm(String term) {
        List<String> res = new LinkedList<>();
        res.add(HELP_SIGN + term);
        res.add(term + HELP_SIGN);
        for (int i = 1; i < term.length(); i++) {
            res.add(term.substring(i) + HELP_SIGN + term.substring(0, i));
        }
        return res;
    }

    public String parse(String query) {
        int helpSignIndex = query.indexOf(JOKER);
        if (helpSignIndex == query.length() - 1) return query;
        return query.substring(helpSignIndex) + JOKER + query.substring(helpSignIndex + 1);
    }

    public String parseBack(String term) {
        int helpSignIndex = term.indexOf(JOKER);
        if (helpSignIndex == term.length() - 1) return term.substring(0, term.length() - 1);
        return term.substring(helpSignIndex) + term.substring(helpSignIndex + 1);
    }
}

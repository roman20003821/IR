package ir.searchParser;

import java.util.LinkedList;
import java.util.List;

public class GramParser {
    private int gramType;
    private static final char ADDITONAL_SIGN = '$';
    private static final char JOKER = '*';

    public GramParser(int gramType) {
        this.gramType = gramType;
    }

    public List<String> parse(String query) throws IllegalArgumentException {
        String[] queryParts = query.split(String.valueOf(JOKER));
        if (!allPartsSuitsGramType(queryParts))
            throw new IllegalArgumentException("This query is not for " + gramType + " gram type");
        return getAllParsedParts(queryParts);
    }

    private List<String> getAllParsedParts(String[] queryParts) {
        List<String> parsedPart = new LinkedList<>();
        for (int i = 0; i < queryParts.length; i++) {
            addInternalParts(parsedPart, queryParts[i]);
            if (i == 0) parsedPart.add(getBeginSubstringWithSign(queryParts[i]));
            else if (i == queryParts.length - 1) parsedPart.add(getEndSubstringWithSign(queryParts[i]));
        }
        return parsedPart;
    }

    public List<String> parseTerm(String term) {
        List<String> res = new LinkedList<>();
        if (term.length() < 2) return res;
        res.add(getBeginSubstringWithSign(term));
        res.add(getEndSubstringWithSign(term));
        addInternalParts(res, term);
        return res;
    }

    private String getBeginSubstringWithSign(String term) {
        return ADDITONAL_SIGN + term.substring(0, gramType - 1);
    }

    private String getEndSubstringWithSign(String term) {
        return term.substring(term.length() - gramType + 1) + ADDITONAL_SIGN;
    }

    private void addInternalParts(List<String> res, String term) {
        for (int i = 1; i < term.length() - gramType + 1; i++) {
            res.add(term.substring(i, i + gramType));
        }
    }

    private boolean allPartsSuitsGramType(String[] parts) {
        for (String part : parts) {
            if (part.length() < gramType - 1) return false;
        }
        return true;
    }
}

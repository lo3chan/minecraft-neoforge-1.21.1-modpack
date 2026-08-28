/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.search;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.gui.search.Token;

public class SearchTokenizer {
    public List<Token> tokenize(String filterText) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        if (filterText.isEmpty()) {
            return tokens;
        }
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        boolean exclusion = false;
        boolean escaped = false;
        for (int i = 0; i < filterText.length(); ++i) {
            char c = filterText.charAt(i);
            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '\"') {
                if (insideQuotes) {
                    this.addToken(tokens, current, exclusion);
                    current.setLength(0);
                    insideQuotes = false;
                    exclusion = false;
                    continue;
                }
                insideQuotes = true;
                continue;
            }
            if (!insideQuotes && Character.isWhitespace(c)) {
                if (!current.isEmpty()) {
                    this.addToken(tokens, current, exclusion);
                    current.setLength(0);
                }
                exclusion = false;
                continue;
            }
            if (!insideQuotes && current.isEmpty() && c == '-') {
                exclusion = true;
                continue;
            }
            current.append(c);
        }
        if (!current.isEmpty() || insideQuotes) {
            this.addToken(tokens, current, exclusion);
        }
        return tokens;
    }

    private void addToken(List<Token> tokens, StringBuilder content, boolean exclusion) {
        String text = content.toString().trim();
        if (!text.isEmpty()) {
            tokens.add(new Token(text, exclusion));
        }
    }
}


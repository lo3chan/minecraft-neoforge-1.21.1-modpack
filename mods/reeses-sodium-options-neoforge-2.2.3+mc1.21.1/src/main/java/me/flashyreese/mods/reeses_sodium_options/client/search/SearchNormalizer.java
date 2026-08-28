/*
 * Decompiled with CFR 0.152.
 */
package me.flashyreese.mods.reeses_sodium_options.client.search;

import java.text.Normalizer;
import java.util.Locale;

public final class SearchNormalizer {
    private final boolean foldDiacritics;

    public SearchNormalizer(boolean foldDiacritics) {
        this.foldDiacritics = foldDiacritics;
    }

    public String normalize(String text) {
        int codePoint;
        if (text == null || text.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        if (this.foldDiacritics) {
            normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        }
        StringBuilder builder = new StringBuilder(normalized.length());
        boolean lastWasSpace = false;
        int length = normalized.length();
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            boolean isPunctuation;
            codePoint = normalized.codePointAt(offset);
            int type = Character.getType(codePoint);
            boolean isSpace = Character.isWhitespace(codePoint) || type == 12 || type == 13 || type == 14;
            boolean bl = isPunctuation = type == 23 || type == 20 || type == 21 || type == 22 || type == 24 || type == 29 || type == 30;
            if (isSpace || isPunctuation) {
                if (lastWasSpace) continue;
                builder.append(' ');
                lastWasSpace = true;
                continue;
            }
            builder.appendCodePoint(codePoint);
            lastWasSpace = false;
        }
        String result = builder.toString().trim();
        return result.isEmpty() ? "" : result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package me.flashyreese.mods.reeses_sodium_options.client.search;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class NgramGenerator {
    private static final int START = 2;
    private static final int END = 3;
    private final int minGram;
    private final int maxGram;
    private final boolean tokenizeOnLetterDigit;

    public NgramGenerator(int minGram, int maxGram, boolean tokenizeOnLetterDigit) {
        if (minGram < 1 || maxGram < minGram) {
            throw new IllegalArgumentException("Invalid gram range");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.tokenizeOnLetterDigit = tokenizeOnLetterDigit;
    }

    private static boolean allBoundary(int[] arr, int off, int n) {
        for (int i = 0; i < n; ++i) {
            int cp = arr[off + i];
            if (cp == 2 || cp == 3) continue;
            return false;
        }
        return true;
    }

    public List<String> generate(String text) {
        int cp;
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        String norm = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).trim();
        if (norm.isEmpty()) {
            return List.of();
        }
        ArrayList<String> out = new ArrayList<String>(Math.max(8, norm.length() * 2));
        if (!this.tokenizeOnLetterDigit) {
            this.addTokenGrams(norm, out);
            return out;
        }
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < norm.length(); i += Character.charCount(cp)) {
            cp = norm.codePointAt(i);
            if (this.isTokenChar(cp)) {
                token.appendCodePoint(cp);
                continue;
            }
            this.flushToken(token, out);
        }
        this.flushToken(token, out);
        return out;
    }

    private boolean isTokenChar(int cp) {
        if (!this.tokenizeOnLetterDigit) {
            return true;
        }
        if (Character.isLetterOrDigit(cp)) {
            return true;
        }
        if (cp <= 127) {
            return NgramGenerator.isResourceLocationPunctuation((char)cp);
        }
        return false;
    }

    private static boolean isResourceLocationPunctuation(char c) {
        return c == '_' || c == '-' || c == '.' || c == '/' || c == ':';
    }

    private void flushToken(StringBuilder token, List<String> out) {
        if (token.isEmpty()) {
            return;
        }
        this.addTokenGrams(token.toString(), out);
        token.setLength(0);
    }

    private void addTokenGrams(String token, List<String> out) {
        int[] cps = token.codePoints().toArray();
        int startPad = Math.max(1, this.maxGram - 1);
        int[] padded = new int[startPad + cps.length + 1];
        for (int i = 0; i < startPad; ++i) {
            padded[i] = 2;
        }
        System.arraycopy(cps, 0, padded, startPad, cps.length);
        padded[padded.length - 1] = 3;
        for (int n = this.minGram; n <= this.maxGram; ++n) {
            if (padded.length < n) continue;
            for (int off = 0; off <= padded.length - n; ++off) {
                if (NgramGenerator.allBoundary(padded, off, n)) continue;
                out.add(new String(padded, off, n));
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 */
package me.flashyreese.mods.reeses_sodium_options.client.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.flashyreese.mods.reeses_sodium_options.client.search.SearchIndexContext;
import me.flashyreese.mods.reeses_sodium_options.client.search.SearchResult;

public final class SearchSession<T> {
    private final SearchIndexContext<T> index;
    private final String query;

    SearchSession(SearchIndexContext<T> index, String query) {
        this.index = index;
        this.query = query == null ? "" : query;
    }

    public List<SearchResult<T>> results() {
        String normalizedQuery = this.index.normalizer().normalize(this.query);
        if (normalizedQuery.isEmpty()) {
            return List.of();
        }
        List<String> queryGrams = this.index.ngramGenerator().generate(normalizedQuery);
        if (queryGrams.isEmpty()) {
            return List.of();
        }
        HashMap<String, Integer> queryTermCounts = new HashMap<String, Integer>();
        for (String gram : queryGrams) {
            queryTermCounts.merge(gram, 1, Integer::sum);
        }
        double queryWeightSum = 0.0;
        for (Map.Entry entry : queryTermCounts.entrySet()) {
            Double idf = this.index.idfWeights().get(entry.getKey());
            if (idf == null) continue;
            queryWeightSum += idf * (double)((Integer)entry.getValue()).intValue();
        }
        if (queryWeightSum == 0.0) {
            return List.of();
        }
        int totalDocs = this.index.size();
        boolean[] seen = new boolean[totalDocs];
        IntArrayList candidates = new IntArrayList();
        for (String gram : queryTermCounts.keySet()) {
            IntArrayList postings = this.index.invertedIndex().get(gram);
            if (postings == null) continue;
            IntListIterator intListIterator = postings.iterator();
            while (intListIterator.hasNext()) {
                int docId = (Integer)intListIterator.next();
                if (seen[docId]) continue;
                seen[docId] = true;
                candidates.add(docId);
            }
        }
        if (candidates.isEmpty()) {
            return List.of();
        }
        ArrayList<SearchResult<T>> results = new ArrayList<SearchResult<T>>();
        IntListIterator intListIterator = candidates.iterator();
        while (intListIterator.hasNext()) {
            int docId = (Integer)intListIterator.next();
            String normalizedDoc = this.index.normalizedTexts().get(docId);
            if (normalizedDoc.isEmpty()) continue;
            Map<String, Integer> docTerms = this.index.documentTermCounts().get(docId);
            double overlap = 0.0;
            for (Map.Entry entry : queryTermCounts.entrySet()) {
                Integer docCount = docTerms.get(entry.getKey());
                if (docCount == null) continue;
                double idf = this.index.idfWeights().getOrDefault(entry.getKey(), 0.0);
                overlap += idf * (double)Math.min((Integer)entry.getValue(), docCount);
            }
            if (!this.matchesQueryTokens(normalizedQuery, normalizedDoc, docTerms)) continue;
            double score = overlap / queryWeightSum;
            if (!((score = this.applyBoosts(score, normalizedQuery, normalizedDoc)) >= this.index.minScore())) continue;
            results.add(new SearchResult<T>(this.index.items().get(docId), score, docId));
        }
        if (results.isEmpty()) {
            return List.of();
        }
        results.sort(Comparator.comparingDouble(SearchResult::score).reversed().thenComparingInt(SearchResult::documentId));
        if (this.index.rerankWithEditDistance()) {
            this.rerank(results, normalizedQuery);
        }
        if (results.size() > this.index.maxResults()) {
            return new ArrayList<SearchResult<T>>(results.subList(0, this.index.maxResults()));
        }
        return results;
    }

    private boolean matchesQueryTokens(String normalizedQuery, String normalizedDoc, Map<String, Integer> docTerms) {
        List<String> queryTokens = SearchSession.splitTokens(normalizedQuery);
        if (queryTokens.isEmpty()) {
            return false;
        }
        List<String> docTokens = SearchSession.splitTokens(normalizedDoc);
        if (docTokens.isEmpty()) {
            return false;
        }
        for (String queryToken : queryTokens) {
            if (this.matchesQueryToken(queryToken, docTokens, docTerms)) continue;
            return false;
        }
        return true;
    }

    private boolean matchesQueryToken(String queryToken, List<String> docTokens, Map<String, Integer> docTerms) {
        int queryLength = queryToken.codePointCount(0, queryToken.length());
        for (String docToken : docTokens) {
            if (!(queryLength <= 2 ? docToken.startsWith(queryToken) : docToken.contains(queryToken) || this.isCloseToken(queryToken, queryLength, docToken))) continue;
            return true;
        }
        return this.tokenCoverage(queryToken, docTerms) >= SearchSession.minTokenCoverage(queryLength);
    }

    private boolean isCloseToken(String queryToken, int queryLength, String docToken) {
        int docLength = docToken.codePointCount(0, docToken.length());
        if (Math.abs(docLength - queryLength) > Math.max(1, queryLength / 3)) {
            return false;
        }
        return this.normalizedLevenshtein(queryToken, docToken) <= SearchSession.maxTokenDistance(queryLength);
    }

    private double tokenCoverage(String token, Map<String, Integer> docTerms) {
        List<String> grams = this.index.ngramGenerator().generate(token);
        if (grams.isEmpty()) {
            return 0.0;
        }
        HashMap<String, Integer> tokenTermCounts = new HashMap<String, Integer>();
        for (String gram : grams) {
            tokenTermCounts.merge(gram, 1, Integer::sum);
        }
        int total = 0;
        int matched = 0;
        for (Map.Entry entry : tokenTermCounts.entrySet()) {
            int count = (Integer)entry.getValue();
            total += count;
            matched += Math.min(count, docTerms.getOrDefault(entry.getKey(), 0));
        }
        return total == 0 ? 0.0 : (double)matched / (double)total;
    }

    private static List<String> splitTokens(String normalizedText) {
        if (normalizedText == null || normalizedText.isBlank()) {
            return List.of();
        }
        ArrayList<String> tokens = new ArrayList<String>();
        for (String token : normalizedText.split(" ")) {
            if (token.isEmpty()) continue;
            tokens.add(token);
        }
        return tokens;
    }

    private static double minTokenCoverage(int queryLength) {
        if (queryLength <= 3) {
            return 0.75;
        }
        if (queryLength <= 5) {
            return 0.6;
        }
        return 0.5;
    }

    private static double maxTokenDistance(int queryLength) {
        if (queryLength <= 4) {
            return 0.25;
        }
        return 0.35;
    }

    private double applyBoosts(double baseScore, String query, String candidate) {
        double score = baseScore;
        if (candidate.equals(query)) {
            score += 0.6;
        } else if (candidate.startsWith(query)) {
            score += 0.3;
        } else if (candidate.contains(query)) {
            score += 0.15;
        }
        int lengthDiff = Math.abs(candidate.length() - query.length());
        int maxLength = Math.max(1, Math.max(candidate.length(), query.length()));
        return score -= 0.05 * ((double)lengthDiff / (double)maxLength);
    }

    private void rerank(List<SearchResult<T>> results, String normalizedQuery) {
        int limit = Math.min(this.index.rerankLimit(), results.size());
        if (limit <= 1) {
            return;
        }
        ArrayList<SearchResult<T>> subset = new ArrayList<SearchResult<T>>(results.subList(0, limit));
        subset.sort(Comparator.comparingDouble(result -> {
            String candidate = this.index.normalizedTexts().get(result.documentId());
            return -this.adjustedScore(result.score(), normalizedQuery, candidate);
        }).thenComparingInt(SearchResult::documentId));
        for (int i = 0; i < limit; ++i) {
            results.set(i, (SearchResult)subset.get(i));
        }
    }

    private double adjustedScore(double base, String query, String candidate) {
        double distance = this.normalizedLevenshtein(query, candidate);
        return base + this.index.rerankWeight() * (1.0 - distance);
    }

    private double normalizedLevenshtein(String a, String b) {
        int[] aPoints = a.codePoints().toArray();
        int[] bPoints = b.codePoints().toArray();
        int aLength = aPoints.length;
        int bLength = bPoints.length;
        if (aLength == 0 && bLength == 0) {
            return 0.0;
        }
        int[] prev = new int[bLength + 1];
        int[] curr = new int[bLength + 1];
        for (int j = 0; j <= bLength; ++j) {
            prev[j] = j;
        }
        for (int i = 1; i <= aLength; ++i) {
            curr[0] = i;
            for (int j = 1; j <= bLength; ++j) {
                int cost = aPoints[i - 1] == bPoints[j - 1] ? 0 : 1;
                int deletion = prev[j] + 1;
                int insertion = curr[j - 1] + 1;
                int substitution = prev[j - 1] + cost;
                curr[j] = Math.min(Math.min(deletion, insertion), substitution);
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        int distance = prev[bLength];
        int maxLength = Math.max(aLength, bLength);
        return (double)distance / (double)maxLength;
    }
}


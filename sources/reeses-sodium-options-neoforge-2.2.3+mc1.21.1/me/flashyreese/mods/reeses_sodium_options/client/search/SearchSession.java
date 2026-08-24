package me.flashyreese.mods.reeses_sodium_options.client.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
      } else {
         List<String> queryGrams = this.index.ngramGenerator().generate(normalizedQuery);
         if (queryGrams.isEmpty()) {
            return List.of();
         } else {
            Map<String, Integer> queryTermCounts = new HashMap<>();

            for (String gram : queryGrams) {
               queryTermCounts.merge(gram, 1, Integer::sum);
            }

            double queryWeightSum = 0.0;

            for (Entry<String, Integer> entry : queryTermCounts.entrySet()) {
               Double idf = this.index.idfWeights().get(entry.getKey());
               if (idf != null) {
                  queryWeightSum += idf * entry.getValue().intValue();
               }
            }

            if (queryWeightSum == 0.0) {
               return List.of();
            } else {
               int totalDocs = this.index.size();
               boolean[] seen = new boolean[totalDocs];
               IntArrayList candidates = new IntArrayList();

               for (String gram : queryTermCounts.keySet()) {
                  IntArrayList postings = this.index.invertedIndex().get(gram);
                  if (postings != null) {
                     IntListIterator normalizedDoc = postings.iterator();

                     while (normalizedDoc.hasNext()) {
                        int docId = (Integer)normalizedDoc.next();
                        if (!seen[docId]) {
                           seen[docId] = true;
                           candidates.add(docId);
                        }
                     }
                  }
               }

               if (candidates.isEmpty()) {
                  return List.of();
               } else {
                  List<SearchResult<T>> results = new ArrayList<>();
                  IntListIterator var26 = candidates.iterator();

                  while (var26.hasNext()) {
                     int docId = (Integer)var26.next();
                     String normalizedDoc = this.index.normalizedTexts().get(docId);
                     if (!normalizedDoc.isEmpty()) {
                        Map<String, Integer> docTerms = this.index.documentTermCounts().get(docId);
                        double overlap = 0.0;

                        for (Entry<String, Integer> entryx : queryTermCounts.entrySet()) {
                           Integer docCount = docTerms.get(entryx.getKey());
                           if (docCount != null) {
                              double idf = this.index.idfWeights().getOrDefault(entryx.getKey(), 0.0);
                              overlap += idf * Math.min(entryx.getValue(), docCount);
                           }
                        }

                        if (this.matchesQueryTokens(normalizedQuery, normalizedDoc, docTerms)) {
                           double score = overlap / queryWeightSum;
                           score = this.applyBoosts(score, normalizedQuery, normalizedDoc);
                           if (score >= this.index.minScore()) {
                              results.add(new SearchResult<>(this.index.items().get(docId), score, docId));
                           }
                        }
                     }
                  }

                  if (results.isEmpty()) {
                     return List.of();
                  } else {
                     results.sort(Comparator.comparingDouble(SearchResult::score).reversed().thenComparingInt(SearchResult::documentId));
                     if (this.index.rerankWithEditDistance()) {
                        this.rerank(results, normalizedQuery);
                     }

                     return (List<SearchResult<T>>)(results.size() > this.index.maxResults()
                        ? new ArrayList<>(results.subList(0, this.index.maxResults()))
                        : results);
                  }
               }
            }
         }
      }
   }

   private boolean matchesQueryTokens(String normalizedQuery, String normalizedDoc, Map<String, Integer> docTerms) {
      List<String> queryTokens = splitTokens(normalizedQuery);
      if (queryTokens.isEmpty()) {
         return false;
      } else {
         List<String> docTokens = splitTokens(normalizedDoc);
         if (docTokens.isEmpty()) {
            return false;
         } else {
            for (String queryToken : queryTokens) {
               if (!this.matchesQueryToken(queryToken, docTokens, docTerms)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean matchesQueryToken(String queryToken, List<String> docTokens, Map<String, Integer> docTerms) {
      int queryLength = queryToken.codePointCount(0, queryToken.length());

      for (String docToken : docTokens) {
         if (queryLength > 2) {
            if (docToken.contains(queryToken) || this.isCloseToken(queryToken, queryLength, docToken)) {
               return true;
            }
         } else if (docToken.startsWith(queryToken)) {
            return true;
         }
      }

      return this.tokenCoverage(queryToken, docTerms) >= minTokenCoverage(queryLength);
   }

   private boolean isCloseToken(String queryToken, int queryLength, String docToken) {
      int docLength = docToken.codePointCount(0, docToken.length());
      return Math.abs(docLength - queryLength) > Math.max(1, queryLength / 3)
         ? false
         : this.normalizedLevenshtein(queryToken, docToken) <= maxTokenDistance(queryLength);
   }

   private double tokenCoverage(String token, Map<String, Integer> docTerms) {
      List<String> grams = this.index.ngramGenerator().generate(token);
      if (grams.isEmpty()) {
         return 0.0;
      } else {
         Map<String, Integer> tokenTermCounts = new HashMap<>();

         for (String gram : grams) {
            tokenTermCounts.merge(gram, 1, Integer::sum);
         }

         int total = 0;
         int matched = 0;

         for (Entry<String, Integer> entry : tokenTermCounts.entrySet()) {
            int count = entry.getValue();
            total += count;
            matched += Math.min(count, docTerms.getOrDefault(entry.getKey(), 0));
         }

         return total == 0 ? 0.0 : (double)matched / total;
      }
   }

   private static List<String> splitTokens(String normalizedText) {
      if (normalizedText != null && !normalizedText.isBlank()) {
         List<String> tokens = new ArrayList<>();

         for (String token : normalizedText.split(" ")) {
            if (!token.isEmpty()) {
               tokens.add(token);
            }
         }

         return tokens;
      } else {
         return List.of();
      }
   }

   private static double minTokenCoverage(int queryLength) {
      if (queryLength <= 3) {
         return 0.75;
      } else {
         return queryLength <= 5 ? 0.6 : 0.5;
      }
   }

   private static double maxTokenDistance(int queryLength) {
      return queryLength <= 4 ? 0.25 : 0.35;
   }

   private double applyBoosts(double baseScore, String query, String candidate) {
      double score = baseScore;
      if (candidate.equals(query)) {
         score = baseScore + 0.6;
      } else if (candidate.startsWith(query)) {
         score = baseScore + 0.3;
      } else if (candidate.contains(query)) {
         score = baseScore + 0.15;
      }

      int lengthDiff = Math.abs(candidate.length() - query.length());
      int maxLength = Math.max(1, Math.max(candidate.length(), query.length()));
      return score - 0.05 * ((double)lengthDiff / maxLength);
   }

   private void rerank(List<SearchResult<T>> results, String normalizedQuery) {
      int limit = Math.min(this.index.rerankLimit(), results.size());
      if (limit > 1) {
         List<SearchResult<T>> subset = new ArrayList<>(results.subList(0, limit));
         subset.sort(Comparator.<SearchResult<T>>comparingDouble(result -> {
            String candidate = this.index.normalizedTexts().get(result.documentId());
            return -this.adjustedScore(result.score(), normalizedQuery, candidate);
         }).thenComparingInt(SearchResult::documentId));

         for (int i = 0; i < limit; i++) {
            results.set(i, subset.get(i));
         }
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
      } else {
         int[] prev = new int[bLength + 1];
         int[] curr = new int[bLength + 1];
         int j = 0;

         while (j <= bLength) {
            prev[j] = j++;
         }

         for (int i = 1; i <= aLength; i++) {
            curr[0] = i;

            for (int jx = 1; jx <= bLength; jx++) {
               int cost = aPoints[i - 1] == bPoints[jx - 1] ? 0 : 1;
               int deletion = prev[jx] + 1;
               int insertion = curr[jx - 1] + 1;
               int substitution = prev[jx - 1] + cost;
               curr[jx] = Math.min(Math.min(deletion, insertion), substitution);
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
         }

         j = prev[bLength];
         int maxLength = Math.max(aLength, bLength);
         return (double)j / maxLength;
      }
   }
}

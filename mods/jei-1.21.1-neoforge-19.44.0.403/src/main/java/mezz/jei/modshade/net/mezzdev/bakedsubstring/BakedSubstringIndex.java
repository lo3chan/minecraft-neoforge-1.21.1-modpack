/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  org.jspecify.annotations.Nullable
 */
package mezz.jei.modshade.net.mezzdev.bakedsubstring;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

public final class BakedSubstringIndex<T> {
    private static final int[] NO_ENTRIES = new int[0];
    private final String[] keys;
    private final Object[] values;
    private final Long2ObjectOpenHashMap<int[]> entriesByGram;
    private final boolean deduplicateResults;

    private BakedSubstringIndex(String[] keys, Object[] values, Long2ObjectOpenHashMap<int[]> entriesByGram, boolean deduplicateResults) {
        this.keys = keys;
        this.values = values;
        this.entriesByGram = entriesByGram;
        this.deduplicateResults = deduplicateResults;
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer) {
        Objects.requireNonNull(resultsConsumer, "resultsConsumer");
        Collection<T> results = this.getSearchResults(token);
        if (!results.isEmpty()) {
            resultsConsumer.accept(results);
        }
    }

    public Collection<T> getSearchResults(String token) {
        boolean verifyCandidates;
        Objects.requireNonNull(token, "token");
        if (token.isEmpty()) {
            return Collections.emptyList();
        }
        int[] candidateEntries = this.candidateEntries(token);
        if (candidateEntries == null || candidateEntries.length == 0) {
            return Collections.emptyList();
        }
        boolean bl = verifyCandidates = token.length() > 3;
        if (this.deduplicateResults) {
            return this.deduplicatedResults(token, candidateEntries, verifyCandidates);
        }
        CandidateResultCollection results = new CandidateResultCollection(token, candidateEntries, verifyCandidates);
        if (results.isEmpty()) {
            return Collections.emptyList();
        }
        return results;
    }

    public Collection<T> getAllElements() {
        if (this.values.length == 0) {
            return Collections.emptyList();
        }
        if (!this.deduplicateResults) {
            ArrayList<T> results = new ArrayList<T>(this.values.length);
            for (Object value : this.values) {
                results.add(BakedSubstringIndex.value(value));
            }
            return Collections.unmodifiableList(results);
        }
        Set results = Collections.newSetFromMap(new IdentityHashMap());
        for (Object value : this.values) {
            results.add(BakedSubstringIndex.value(value));
        }
        return Collections.unmodifiableSet(results);
    }

    private Collection<T> deduplicatedResults(String token, int[] candidateEntries, boolean verifyCandidates) {
        Set results = Collections.newSetFromMap(new IdentityHashMap());
        for (int entryIndex : candidateEntries) {
            if (verifyCandidates && !this.keys[entryIndex].contains(token)) continue;
            results.add(this.valueAt(entryIndex));
        }
        if (results.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableSet(results);
    }

    private @Nullable int[] candidateEntries(String token) {
        int tokenLength = token.length();
        if (tokenLength <= 3) {
            return (int[])this.entriesByGram.get(BakedSubstringIndex.encodeGram(token, 0, tokenLength));
        }
        int maxGramCount = tokenLength - 2;
        long[] grams = new long[maxGramCount];
        int[][] postings = new int[maxGramCount][];
        int gramCount = 0;
        for (int i = 0; i <= tokenLength - 3; ++i) {
            long gram = BakedSubstringIndex.encodeGram(token, i, 3);
            if (BakedSubstringIndex.contains(grams, gramCount, gram)) continue;
            int[] entries = (int[])this.entriesByGram.get(gram);
            if (entries == null) {
                return null;
            }
            for (int insertAt = gramCount; insertAt > 0 && postings[insertAt - 1].length > entries.length; --insertAt) {
                grams[insertAt] = grams[insertAt - 1];
                postings[insertAt] = postings[insertAt - 1];
            }
            grams[insertAt] = gram;
            postings[insertAt] = entries;
            ++gramCount;
        }
        if (gramCount == 0) {
            return NO_ENTRIES;
        }
        if (gramCount == 1) {
            return postings[0];
        }
        return BakedSubstringIndex.intersectPostings(postings, gramCount);
    }

    private T valueAt(int index) {
        return BakedSubstringIndex.value(this.values[index]);
    }

    private static <T> T value(Object value) {
        return (T)value;
    }

    private static long encodeGram(String string, int offset, int length) {
        long gram = (long)length << 48;
        for (int i = 0; i < length; ++i) {
            gram |= (long)string.charAt(offset + i) << (2 - i) * 16;
        }
        return gram;
    }

    private static boolean contains(long[] values, int length, long value) {
        for (int i = 0; i < length; ++i) {
            if (values[i] != value) continue;
            return true;
        }
        return false;
    }

    private static int[] intersectPostings(int[][] postings, int postingCount) {
        int[] current = postings[0];
        int currentLength = current.length;
        for (int i = 1; i < postingCount; ++i) {
            int[] next = postings[i];
            int[] output = new int[Math.min(currentLength, next.length)];
            if ((currentLength = BakedSubstringIndex.intersect(current, currentLength, next, output)) == 0) {
                return NO_ENTRIES;
            }
            current = output;
        }
        if (currentLength == current.length) {
            return current;
        }
        int[] output = new int[currentLength];
        System.arraycopy(current, 0, output, 0, currentLength);
        return output;
    }

    private static int intersect(int[] left, int leftLength, int[] right, int[] output) {
        int leftIndex = 0;
        int rightIndex = 0;
        int outputIndex = 0;
        while (leftIndex < leftLength && rightIndex < right.length) {
            int leftValue = left[leftIndex];
            int rightValue = right[rightIndex];
            if (leftValue == rightValue) {
                output[outputIndex++] = leftValue;
                ++leftIndex;
                ++rightIndex;
                continue;
            }
            if (leftValue < rightValue) {
                ++leftIndex;
                continue;
            }
            ++rightIndex;
        }
        return outputIndex;
    }

    public static final class Builder<T> {
        private final List<String> keys = new ArrayList<String>();
        private final List<T> values = new ArrayList<T>();

        private Builder() {
        }

        public Builder<T> put(String key, T value) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(value, "value");
            this.keys.add(key);
            this.values.add(value);
            return this;
        }

        public BakedSubstringIndex<T> build() {
            String[] keyArray = (String[])this.keys.toArray(String[]::new);
            Object[] valueArray = this.values.toArray();
            Long2ObjectOpenHashMap mutableEntriesByGram = new Long2ObjectOpenHashMap();
            Long2IntOpenHashMap lastEntryByGram = new Long2IntOpenHashMap();
            lastEntryByGram.defaultReturnValue(-1);
            Set seenValues = Collections.newSetFromMap(new IdentityHashMap());
            boolean deduplicateResults = false;
            for (int entryIndex = 0; entryIndex < keyArray.length; ++entryIndex) {
                String key = keyArray[entryIndex];
                Builder.addGrams((Long2ObjectOpenHashMap<IntArrayList>)mutableEntriesByGram, lastEntryByGram, key, entryIndex);
                if (seenValues.add(this.values.get(entryIndex))) continue;
                deduplicateResults = true;
            }
            Long2ObjectOpenHashMap entriesByGram = new Long2ObjectOpenHashMap(mutableEntriesByGram.size());
            for (Long2ObjectMap.Entry entry : mutableEntriesByGram.long2ObjectEntrySet()) {
                entriesByGram.put(entry.getLongKey(), (Object)((IntArrayList)entry.getValue()).toIntArray());
            }
            return new BakedSubstringIndex(keyArray, valueArray, (Long2ObjectOpenHashMap<int[]>)entriesByGram, deduplicateResults);
        }

        private static void addGrams(Long2ObjectOpenHashMap<IntArrayList> entriesByGram, Long2IntOpenHashMap lastEntryByGram, String key, int entryIndex) {
            int keyLength = key.length();
            for (int gramLength = 1; gramLength <= 3 && gramLength <= keyLength; ++gramLength) {
                for (int i = 0; i <= keyLength - gramLength; ++i) {
                    long gram = BakedSubstringIndex.encodeGram(key, i, gramLength);
                    if (lastEntryByGram.get(gram) == entryIndex) continue;
                    IntArrayList entries = (IntArrayList)entriesByGram.get(gram);
                    if (entries == null) {
                        entries = new IntArrayList();
                        entriesByGram.put(gram, (Object)entries);
                    }
                    entries.add(entryIndex);
                    lastEntryByGram.put(gram, entryIndex);
                }
            }
        }
    }

    private final class CandidateResultCollection
    extends AbstractCollection<T> {
        private final String token;
        private final int[] candidateEntries;
        private final boolean verifyCandidates;
        private volatile int size = -1;

        private CandidateResultCollection(String token, int[] candidateEntries, boolean verifyCandidates) {
            this.token = token;
            this.candidateEntries = candidateEntries;
            this.verifyCandidates = verifyCandidates;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>(){
                private int nextCandidateIndex;
                {
                    this.nextCandidateIndex = CandidateResultCollection.this.findNextCandidate(0);
                }

                @Override
                public boolean hasNext() {
                    return this.nextCandidateIndex >= 0;
                }

                @Override
                public T next() {
                    if (this.nextCandidateIndex < 0) {
                        throw new NoSuchElementException();
                    }
                    int entryIndex = CandidateResultCollection.this.candidateEntries[this.nextCandidateIndex];
                    this.nextCandidateIndex = CandidateResultCollection.this.findNextCandidate(this.nextCandidateIndex + 1);
                    return BakedSubstringIndex.this.valueAt(entryIndex);
                }
            };
        }

        @Override
        public int size() {
            if (this.size < 0) {
                int count = 0;
                for (int entryIndex : this.candidateEntries) {
                    if (!this.matches(entryIndex)) continue;
                    ++count;
                }
                this.size = count;
            }
            return this.size;
        }

        private int findNextCandidate(int startIndex) {
            for (int i = startIndex; i < this.candidateEntries.length; ++i) {
                if (!this.matches(this.candidateEntries[i])) continue;
                return i;
            }
            return -1;
        }

        private boolean matches(int entryIndex) {
            return !this.verifyCandidates || BakedSubstringIndex.this.keys[entryIndex].contains(this.token);
        }
    }
}


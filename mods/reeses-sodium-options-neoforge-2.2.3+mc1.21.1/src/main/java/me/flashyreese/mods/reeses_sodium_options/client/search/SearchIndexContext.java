/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package me.flashyreese.mods.reeses_sodium_options.client.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.Map;
import me.flashyreese.mods.reeses_sodium_options.client.search.NgramGenerator;
import me.flashyreese.mods.reeses_sodium_options.client.search.SearchNormalizer;

interface SearchIndexContext<T> {
    public List<T> items();

    public List<String> normalizedTexts();

    public List<Map<String, Integer>> documentTermCounts();

    public Map<String, IntArrayList> invertedIndex();

    public Map<String, Double> idfWeights();

    public SearchNormalizer normalizer();

    public NgramGenerator ngramGenerator();

    public int size();

    public int maxResults();

    public double minScore();

    public boolean rerankWithEditDistance();

    public int rerankLimit();

    public double rerankWeight();
}


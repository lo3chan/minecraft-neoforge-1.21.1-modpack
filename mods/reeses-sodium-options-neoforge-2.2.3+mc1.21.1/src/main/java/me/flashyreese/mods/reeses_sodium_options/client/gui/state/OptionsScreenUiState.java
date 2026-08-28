/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.Holder;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionLayoutState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionUiState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultEntry;
import net.minecraft.resources.ResourceLocation;

public final class OptionsScreenUiState
implements OptionStateStore {
    private final Holder<String> tabFrameSelectedTab = new Holder<Object>(null);
    private final Holder<String> tabFrameSelectedGroup = new Holder<Object>(null);
    private final Holder<Integer> tabFrameScrollBarOffset = new Holder<Integer>(0);
    private final Holder<Boolean> scrollSelectedTabIntoView = new Holder<Boolean>(false);
    private final Holder<Integer> optionPageScrollBarOffset = new Holder<Integer>(0);
    private final Holder<String> lastSearch = new Holder<String>("");
    private final Holder<Integer> lastSearchIndex = new Holder<Object>(null);
    private final List<SearchResultEntry> searchResults = new ArrayList<SearchResultEntry>();
    private boolean searchActive;
    private final Set<String> manuallyCollapsedTabGroups = new HashSet<String>();
    private final Set<ResourceLocation> collapsedOptionGroups = new HashSet<ResourceLocation>();
    private final Map<String, ResourceLocation> focusedOptionIdsByTab = new HashMap<String, ResourceLocation>();
    private final Map<ResourceLocation, OptionUiState> optionUiStates = new HashMap<ResourceLocation, OptionUiState>();
    private final Map<ResourceLocation, OptionLayoutState> optionLayoutStates = new HashMap<ResourceLocation, OptionLayoutState>();

    public Holder<String> tabFrameSelectedTab() {
        return this.tabFrameSelectedTab;
    }

    public Holder<String> tabFrameSelectedGroup() {
        return this.tabFrameSelectedGroup;
    }

    public Holder<Integer> tabFrameScrollBarOffset() {
        return this.tabFrameScrollBarOffset;
    }

    public Holder<Boolean> scrollSelectedTabIntoView() {
        return this.scrollSelectedTabIntoView;
    }

    public Holder<Integer> optionPageScrollBarOffset() {
        return this.optionPageScrollBarOffset;
    }

    public Set<String> manuallyCollapsedTabGroups() {
        return this.manuallyCollapsedTabGroups;
    }

    @Override
    public Set<ResourceLocation> collapsedOptionGroups() {
        return this.collapsedOptionGroups;
    }

    public Map<String, ResourceLocation> focusedOptionIdsByTab() {
        return this.focusedOptionIdsByTab;
    }

    public Holder<String> lastSearch() {
        return this.lastSearch;
    }

    public Holder<Integer> lastSearchIndex() {
        return this.lastSearchIndex;
    }

    @Override
    public boolean searchActive() {
        return this.searchActive;
    }

    @Override
    public List<SearchResultEntry> searchResults() {
        return List.copyOf(this.searchResults);
    }

    @Override
    public OptionUiState optionUiState(ResourceLocation id) {
        return this.optionUiStates.computeIfAbsent(id, unused -> new OptionUiState());
    }

    @Override
    public OptionLayoutState optionLayoutState(ResourceLocation id) {
        return this.optionLayoutStates.computeIfAbsent(id, unused -> new OptionLayoutState());
    }

    public void setHighlightedOptions(List<SearchResultEntry> results) {
        this.optionUiStates.values().forEach(OptionUiState::clearHighlight);
        results.forEach(result -> this.optionUiState(result.optionId()).setHighlighted(true));
    }

    public void clearSelectedOptions() {
        this.optionUiStates.values().forEach(state -> state.setSelected(false));
    }

    public void clearOptionUiStates() {
        this.optionUiStates.clear();
        this.optionLayoutStates.clear();
    }

    public boolean updateSearchResults(boolean active, List<SearchResultEntry> results) {
        if (this.searchActive == active && this.searchResults.equals(results)) {
            return false;
        }
        this.searchActive = active;
        this.searchResults.clear();
        this.searchResults.addAll(results);
        return true;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.ModOptions
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.config.structure.OptionGroup
 *  net.caffeinemc.mods.sodium.client.config.structure.Page
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionLayoutState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionUiState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultEntry;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultOrder;
import me.flashyreese.mods.reeses_sodium_options.client.search.SearchIndex;
import me.flashyreese.mods.reeses_sodium_options.client.search.SearchResult;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.OptionGroup;
import net.caffeinemc.mods.sodium.client.config.structure.Page;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

final class OptionSearch {
    private final List<SearchableOption> options;
    private final SearchIndex<SearchableOption> searchIndex;

    OptionSearch(List<ModOptions> modOptionsList) {
        ArrayList<SearchableOption> options = new ArrayList<SearchableOption>();
        for (ModOptions modOptions : modOptionsList) {
            for (Page page : modOptions.pages()) {
                String tabKey = modOptions.configId() + ":" + page.name().getString();
                for (OptionGroup group : page.groups()) {
                    for (Option option : group.options()) {
                        if (!(option instanceof OptionExtended)) continue;
                        OptionExtended optionExtended = (OptionExtended)option;
                        options.add(new SearchableOption(optionExtended.rso$getId(), tabKey, option, String.format("%s %s", option.getName().getString(), option.getTooltip().getString())));
                    }
                }
            }
        }
        this.options = List.copyOf(options);
        this.searchIndex = SearchIndex.builder(SearchableOption::searchableText).addAll(this.options).foldDiacritics(true).maxResults(ReeseSodiumOptionsConfig.config().getSearchResultLimit()).minScore(0.3).rerankWithEditDistance(true).rerankLimit(50).rerankWeight(0.1).build();
    }

    List<SearchResultEntry> query(String query) {
        return this.searchIndex.newSession(query).results().stream().map(SearchResult::item).map(SearchableOption::toSearchResult).toList();
    }

    List<NavigationTarget> navigationTargets(OptionStateStore optionStateStore, SearchResultOrder order) {
        List<SearchResultEntry> orderedResults = this.orderResults(optionStateStore.searchResults(), order);
        ArrayList<NavigationTarget> targets = new ArrayList<NavigationTarget>(orderedResults.size());
        for (SearchResultEntry result : orderedResults) {
            NavigationTarget target = this.createNavigationTarget(result, optionStateStore);
            if (target == null) continue;
            targets.add(target);
        }
        return targets;
    }

    private List<SearchResultEntry> orderResults(List<SearchResultEntry> results, SearchResultOrder order) {
        if (results.isEmpty()) {
            return List.of();
        }
        if (order == SearchResultOrder.RANKED) {
            return results;
        }
        Set resultOptions = Collections.newSetFromMap(new IdentityHashMap());
        results.forEach(result -> resultOptions.add(result.option()));
        ArrayList<SearchResultEntry> ordered = new ArrayList<SearchResultEntry>(results.size());
        for (SearchableOption option : this.options) {
            if (!resultOptions.contains(option.option())) continue;
            ordered.add(option.toSearchResult());
        }
        return ordered;
    }

    @Nullable
    private NavigationTarget createNavigationTarget(SearchResultEntry result, OptionStateStore optionStateStore) {
        OptionUiState optionUiState = optionStateStore.optionUiState(result.optionId());
        OptionLayoutState optionLayoutState = optionStateStore.optionLayoutState(result.optionId());
        LayoutBounds bounds = optionLayoutState.bounds();
        LayoutBounds parentBounds = optionLayoutState.parentBounds();
        if (!optionUiState.isHighlighted() || parentBounds == null || bounds == null) {
            return null;
        }
        return new NavigationTarget(result.tabKey(), optionUiState, bounds, parentBounds);
    }

    private record SearchableOption(ResourceLocation id, String tabKey, Option option, String searchableText) {
        SearchResultEntry toSearchResult() {
            return new SearchResultEntry(this.tabKey, this.id, this.option);
        }
    }

    record NavigationTarget(String tabKey, OptionUiState optionUiState, LayoutBounds bounds, LayoutBounds parentBounds) {
        int scrollOffset(int viewportHeight) {
            int contentHeight = this.parentBounds.height();
            if (contentHeight <= 0 || contentHeight <= viewportHeight) {
                return 0;
            }
            int maxOffset = contentHeight - viewportHeight;
            int input = this.bounds.y() - this.parentBounds.y();
            int inputOffset = input + this.bounds.height() == contentHeight ? contentHeight : input;
            return (int)((long)inputOffset * (long)maxOffset / (long)contentHeight);
        }
    }
}


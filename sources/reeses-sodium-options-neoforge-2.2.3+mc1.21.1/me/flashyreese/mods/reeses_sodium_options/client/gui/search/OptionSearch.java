package me.flashyreese.mods.reeses_sodium_options.client.gui.search;

import com.google.common.collect.UnmodifiableIterator;
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
   private final List<OptionSearch.SearchableOption> options;
   private final SearchIndex<OptionSearch.SearchableOption> searchIndex;

   OptionSearch(List<ModOptions> modOptionsList) {
      List<OptionSearch.SearchableOption> options = new ArrayList<>();

      for (ModOptions modOptions : modOptionsList) {
         UnmodifiableIterator var5 = modOptions.pages().iterator();

         while (var5.hasNext()) {
            Page page = (Page)var5.next();
            String tabKey = modOptions.configId() + ":" + page.name().getString();
            UnmodifiableIterator var8 = page.groups().iterator();

            while (var8.hasNext()) {
               OptionGroup group = (OptionGroup)var8.next();

               for (Option option : group.options()) {
                  if (option instanceof OptionExtended optionExtended) {
                     options.add(
                        new OptionSearch.SearchableOption(
                           optionExtended.rso$getId(), tabKey, option, String.format("%s %s", option.getName().getString(), option.getTooltip().getString())
                        )
                     );
                  }
               }
            }
         }
      }

      this.options = List.copyOf(options);
      this.searchIndex = SearchIndex.builder(OptionSearch.SearchableOption::searchableText)
         .addAll(this.options)
         .foldDiacritics(true)
         .maxResults(ReeseSodiumOptionsConfig.config().getSearchResultLimit())
         .minScore(0.3)
         .rerankWithEditDistance(true)
         .rerankLimit(50)
         .rerankWeight(0.1)
         .build();
   }

   List<SearchResultEntry> query(String query) {
      return this.searchIndex.newSession(query).results().stream().map(SearchResult::item).map(OptionSearch.SearchableOption::toSearchResult).toList();
   }

   List<OptionSearch.NavigationTarget> navigationTargets(OptionStateStore optionStateStore, SearchResultOrder order) {
      List<SearchResultEntry> orderedResults = this.orderResults(optionStateStore.searchResults(), order);
      List<OptionSearch.NavigationTarget> targets = new ArrayList<>(orderedResults.size());

      for (SearchResultEntry result : orderedResults) {
         OptionSearch.NavigationTarget target = this.createNavigationTarget(result, optionStateStore);
         if (target != null) {
            targets.add(target);
         }
      }

      return targets;
   }

   private List<SearchResultEntry> orderResults(List<SearchResultEntry> results, SearchResultOrder order) {
      if (results.isEmpty()) {
         return List.of();
      } else if (order == SearchResultOrder.RANKED) {
         return results;
      } else {
         Set<Option> resultOptions = Collections.newSetFromMap(new IdentityHashMap<>());
         results.forEach(result -> resultOptions.add(result.option()));
         List<SearchResultEntry> ordered = new ArrayList<>(results.size());

         for (OptionSearch.SearchableOption option : this.options) {
            if (resultOptions.contains(option.option())) {
               ordered.add(option.toSearchResult());
            }
         }

         return ordered;
      }
   }

   @Nullable
   private OptionSearch.NavigationTarget createNavigationTarget(SearchResultEntry result, OptionStateStore optionStateStore) {
      OptionUiState optionUiState = optionStateStore.optionUiState(result.optionId());
      OptionLayoutState optionLayoutState = optionStateStore.optionLayoutState(result.optionId());
      LayoutBounds bounds = optionLayoutState.bounds();
      LayoutBounds parentBounds = optionLayoutState.parentBounds();
      return optionUiState.isHighlighted() && parentBounds != null && bounds != null
         ? new OptionSearch.NavigationTarget(result.tabKey(), optionUiState, bounds, parentBounds)
         : null;
   }

   record NavigationTarget(String tabKey, OptionUiState optionUiState, LayoutBounds bounds, LayoutBounds parentBounds) {
      int scrollOffset(int viewportHeight) {
         int contentHeight = this.parentBounds.height();
         if (contentHeight > 0 && contentHeight > viewportHeight) {
            int maxOffset = contentHeight - viewportHeight;
            int input = this.bounds.y() - this.parentBounds.y();
            int inputOffset = input + this.bounds.height() == contentHeight ? contentHeight : input;
            return (int)((long)inputOffset * maxOffset / contentHeight);
         } else {
            return 0;
         }
      }
   }

   private record SearchableOption(ResourceLocation id, String tabKey, Option option, String searchableText) {
      SearchResultEntry toSearchResult() {
         return new SearchResultEntry(this.tabKey, this.id, this.option);
      }
   }
}

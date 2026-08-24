package mezz.jei.gui.ingredients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.gui.filter.IFilterTextSource;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.elements.IngredientElement;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import mezz.jei.gui.search.ElementPrefixParser;
import mezz.jei.gui.search.ElementSearch;
import mezz.jei.gui.search.ElementSearchLowMem;
import mezz.jei.gui.search.IElementSearch;
import mezz.jei.gui.search.SearchTokenizer;
import mezz.jei.gui.search.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class IngredientFilter
   implements IIngredientGridSource,
   IIngredientManager.IIngredientListener,
   IIngredientVisibility.IListener,
   IClientToggleState.IEditModeListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final SearchTokenizer searchTokenizer = new SearchTokenizer();
   private final IClientConfig clientConfig;
   private final IFilterTextSource filterTextSource;
   private final IIngredientManager ingredientManager;
   private final Comparator<IListElement<?>> ingredientComparator;
   private final IModIdHelper modIdHelper;
   private final IIngredientVisibility ingredientVisibility;
   private final Function<List<IListElementInfo<?>>, Comparator<IListElement<?>>> sortIndexUpdater;
   private final ElementPrefixParser elementPrefixParser;
   private IElementSearch elementSearch;
   @Nullable
   private List<IElement<?>> ingredientListCached;
   private final List<IIngredientGridSource.SourceListChangedListener> listeners = new ArrayList<>();
   private boolean searchIndexDirty;
   private boolean sortIndexesDirty;

   public IngredientFilter(
      IFilterTextSource filterTextSource,
      IClientConfig clientConfig,
      IIngredientFilterConfig config,
      IIngredientManager ingredientManager,
      Function<List<IListElementInfo<?>>, Comparator<IListElement<?>>> sortIndexUpdater,
      List<IListElementInfo<?>> ingredients,
      IModIdHelper modIdHelper,
      IIngredientVisibility ingredientVisibility,
      IColorHelper colorHelper,
      ISearchStorageBuilderFactory searchStorageBuilderFactory,
      IClientToggleState clientToggleState
   ) {
      this.filterTextSource = filterTextSource;
      this.clientConfig = clientConfig;
      this.ingredientManager = ingredientManager;
      this.ingredientComparator = sortIndexUpdater.apply(ingredients);
      this.modIdHelper = modIdHelper;
      this.ingredientVisibility = ingredientVisibility;
      this.sortIndexUpdater = sortIndexUpdater;
      this.elementPrefixParser = new ElementPrefixParser(ingredientManager, config, colorHelper, searchStorageBuilderFactory);
      this.elementSearch = createElementSearch(clientConfig, this.elementPrefixParser, ingredients, ingredientManager);
      this.addConfigListeners(clientConfig, config);
      LOGGER.info("Adding {} ingredients", ingredients.size());

      for (IListElementInfo<?> ingredient : ingredients) {
         this.updateHiddenState(ingredient.getElement());
      }

      this.invalidateCache();
      LOGGER.info("Added {} ingredients", ingredients.size());
      if (DebugConfig.isLogSuffixTreeStatsEnabled()) {
         this.elementSearch.logStatistics();
      }

      this.filterTextSource.addListener((oldFilterText, newFilterText) -> {
         this.invalidateCache();
         this.notifyListenersOfChange();
      });
      clientToggleState.addEditModeToggleListener(this);
   }

   private void addConfigListeners(IClientConfig clientConfig, IIngredientFilterConfig config) {
      clientConfig.lowMemorySlowSearchEnabled().addListener(v -> this.markSearchIndexDirty());
      clientConfig.ingredientSorterStages().addListener(v -> this.markSortIndexesDirty());
      config.modNameSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.tooltipSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.tagSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.colorSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.resourceLocationSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.creativeTabSearchMode().addListener(v -> this.markSearchIndexDirty());
      config.searchAdvancedTooltips().addListener(v -> this.markSearchIndexDirty());
      config.searchModIds().addListener(v -> this.markSearchIndexDirty());
      config.searchModAliases().addListener(v -> this.markSearchIndexDirty());
      config.searchIngredientAliases().addListener(v -> this.markSearchIndexDirty());
      config.searchShortModNames().addListener(v -> this.markSearchIndexDirty());
   }

   private static IElementSearch createElementSearch(
      IClientConfig clientConfig, ElementPrefixParser elementPrefixParser, List<IListElementInfo<?>> elementInfos, IIngredientManager ingredientManager
   ) {
      return (IElementSearch)(clientConfig.lowMemorySlowSearchEnabled().getValue()
         ? new ElementSearchLowMem(elementPrefixParser.getNoPrefix(), elementInfos)
         : new ElementSearch(elementPrefixParser, elementInfos, ingredientManager));
   }

   public <V> void addIngredient(IListElementInfo<V> info) {
      IListElement<V> element = info.getElement();
      this.updateHiddenState(element);
      this.elementSearch.add(info, this.ingredientManager);
      this.invalidateCache();
   }

   public void invalidateCache() {
      this.ingredientListCached = null;
   }

   public void rebuildItemFilter() {
      this.invalidateCache();
      Collection<IListElement<?>> ingredients = this.elementSearch.getAllIngredients();
      List<IListElementInfo<?>> elementInfos = IngredientListElementFactory.rebuildList(this.ingredientManager, ingredients, this.modIdHelper);
      this.sortIndexUpdater.apply(elementInfos);
      this.elementSearch = createElementSearch(this.clientConfig, this.elementPrefixParser, elementInfos, this.ingredientManager);
      this.searchIndexDirty = false;
      this.sortIndexesDirty = false;
   }

   private void markSearchIndexDirty() {
      this.searchIndexDirty = true;
      this.notifyListenersOfChange();
   }

   private void markSortIndexesDirty() {
      this.sortIndexesDirty = true;
      this.notifyListenersOfChange();
   }

   private void updateDirtyState() {
      if (this.searchIndexDirty) {
         this.rebuildItemFilter();
      }

      if (this.sortIndexesDirty) {
         List<IListElementInfo<?>> elementInfos = IngredientListElementFactory.rebuildList(
            this.ingredientManager, this.elementSearch.getAllIngredients(), this.modIdHelper
         );
         this.sortIndexUpdater.apply(elementInfos);
         this.sortIndexesDirty = false;
         this.invalidateCache();
      }
   }

   @Override
   public void onEditModeChanged() {
      this.updateHidden();
   }

   public void updateHidden() {
      boolean changed = false;

      for (IListElement<?> element : this.elementSearch.getAllIngredients()) {
         changed |= this.updateHiddenState(element);
      }

      if (changed) {
         this.invalidateCache();
         this.notifyListenersOfChange();
      }
   }

   private <V> boolean updateHiddenState(IListElement<V> element) {
      ITypedIngredient<V> typedIngredient = element.getTypedIngredient();
      boolean visible = this.ingredientVisibility.isIngredientVisible(typedIngredient);
      if (element.isVisible() != visible) {
         element.setVisible(visible);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public <V> void onIngredientVisibilityChanged(ITypedIngredient<V> ingredient, boolean visible) {
      IIngredientType<V> ingredientType = ingredient.getType();
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
      IListElement<V> match = this.elementSearch.findElement(ingredient, ingredientHelper);
      if (match != null && match.isVisible() != visible) {
         match.setVisible(visible);
         this.invalidateCache();
         this.notifyListenersOfChange();
      }
   }

   @Override
   public <V> void onIngredientsVisibilityChanged(Collection<ITypedIngredient<V>> ingredients, boolean visible) {
      boolean changed = false;

      for (ITypedIngredient<V> ingredient : ingredients) {
         IIngredientType<V> ingredientType = ingredient.getType();
         IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
         IListElement<V> match = this.elementSearch.findElement(ingredient, ingredientHelper);
         if (match != null && match.isVisible() != visible) {
            match.setVisible(visible);
            changed = true;
         }
      }

      if (changed) {
         this.invalidateCache();
         this.notifyListenersOfChange();
      }
   }

   @Override
   public List<IElement<?>> getElements() {
      this.updateDirtyState();
      String filterText = this.filterTextSource.getFilterText();
      filterText = filterText.toLowerCase();
      if (this.ingredientListCached == null) {
         this.ingredientListCached = this.getIngredientListUncached(filterText).map(IngredientElement::new).toList();
      }

      return this.ingredientListCached;
   }

   public <T> List<T> getFilteredIngredients(IIngredientType<T> ingredientType) {
      return this.getElements().stream().map(IElement::getTypedIngredient).map(i -> i.getIngredient(ingredientType)).flatMap(Optional::stream).toList();
   }

   private Stream<ITypedIngredient<?>> getIngredientListUncached(String filterText) {
      String[] filters = filterText.split("\\|");
      List<IngredientFilter.SearchTokens> searchTokens = Arrays.stream(filters).map(this::parseSearchTokens).filter(s -> !s.isEmpty()).toList();
      Stream<IListElement<?>> elementStream;
      if (searchTokens.isEmpty()) {
         elementStream = this.elementSearch.getAllIngredients().parallelStream();
      } else {
         elementStream = searchTokens.stream().map(this::getSearchResults).flatMap(Collection::stream).distinct();
      }

      return elementStream.filter(IListElement::isVisible).sorted(this.ingredientComparator).map(IListElement::getTypedIngredient);
   }

   @Override
   public <V> void onIngredientsAdded(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
      for (ITypedIngredient<V> value : ingredients) {
         IListElement<V> matchingElement = this.elementSearch.findElement(value, ingredientHelper);
         if (matchingElement != null) {
            this.updateHiddenState(matchingElement);
            if (DebugConfig.isDebugIngredientsEnabled()) {
               LOGGER.debug("Updated ingredient: {}", ingredientHelper.getErrorInfo(value.getIngredient()));
            }
         } else {
            IListElementInfo<V> listElementInfo = ListElementInfo.create(value, this.ingredientManager, this.modIdHelper);
            if (listElementInfo != null) {
               this.addIngredient(listElementInfo);
               if (DebugConfig.isDebugIngredientsEnabled()) {
                  LOGGER.debug("Added ingredient: {}", ingredientHelper.getErrorInfo(value.getIngredient()));
               }
            }
         }
      }

      this.invalidateCache();
   }

   @Override
   public <V> void onIngredientsRemoved(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
   }

   private IngredientFilter.SearchTokens parseSearchTokens(String filterText) {
      IngredientFilter.SearchTokens searchTokens = new IngredientFilter.SearchTokens(new ArrayList<>(), new ArrayList<>());
      if (filterText.isEmpty()) {
         return searchTokens;
      } else {
         for (Token token : this.searchTokenizer.tokenize(filterText)) {
            if (!token.isEmpty()) {
               this.elementPrefixParser.parseToken(token.text()).ifPresent(result -> {
                  if (token.exclusion()) {
                     searchTokens.toRemove.add(result);
                  } else {
                     searchTokens.toSearch.add(result);
                  }
               });
            }
         }

         return searchTokens;
      }
   }

   private Set<IListElement<?>> getSearchResults(IngredientFilter.SearchTokens searchTokens) {
      List<Set<IListElement<?>>> resultsPerToken = searchTokens.toSearch.stream().map(this.elementSearch::getSearchResults).toList();
      Set<IListElement<?>> results = intersection(resultsPerToken);
      if (results.isEmpty() && !searchTokens.toRemove.isEmpty()) {
         results.addAll(this.elementSearch.getAllIngredients());
      }

      if (!results.isEmpty() && !searchTokens.toRemove.isEmpty()) {
         for (ElementPrefixParser.TokenInfo tokenInfo : searchTokens.toRemove) {
            Set<IListElement<?>> resultsToRemove = this.elementSearch.getSearchResults(tokenInfo);
            results.removeAll(resultsToRemove);
            if (results.isEmpty()) {
               break;
            }
         }
      }

      return results;
   }

   private static <T> Set<T> intersection(List<Set<T>> sets) {
      Set<T> smallestSet = sets.stream().min(Comparator.comparing(Set::size)).orElseGet(Set::of);
      Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
      results.addAll(smallestSet);

      for (Set<T> set : sets) {
         if (set != smallestSet && results.retainAll(set) && results.isEmpty()) {
            break;
         }
      }

      return results;
   }

   @Override
   public void addSourceListChangedListener(IIngredientGridSource.SourceListChangedListener listener) {
      this.listeners.add(listener);
   }

   private void notifyListenersOfChange() {
      for (IIngredientGridSource.SourceListChangedListener listener : this.listeners) {
         listener.onSourceListChanged();
      }
   }

   private record SearchTokens(List<ElementPrefixParser.TokenInfo> toSearch, List<ElementPrefixParser.TokenInfo> toRemove) {
      public boolean isEmpty() {
         return this.toSearch.isEmpty() && this.toRemove.isEmpty();
      }
   }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.search.ISearchStorage;
import mezz.jei.api.search.ISearchStorageBuilder;
import mezz.jei.common.search.CombinedSearchables;
import mezz.jei.common.search.ISearchable;
import mezz.jei.common.search.PrefixInfo;
import mezz.jei.common.search.PrefixedSearchable;
import mezz.jei.common.search.SearchMode;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.search.ElementPrefixParser;
import mezz.jei.gui.search.IElementSearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ElementSearch
implements IElementSearch {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<PrefixInfo<IListElementInfo<?>, IListElement<?>>, PrefixedSearchable<IListElementInfo<?>, IListElement<?>>> prefixedSearchables = new IdentityHashMap();
    private final CombinedSearchables<IListElement<?>> combinedSearchables = new CombinedSearchables();
    private final Map<Object, IListElement<?>> allElements = new HashMap();
    private final PrefixInfo<IListElementInfo<?>, IListElement<?>> noPrefix;

    public ElementSearch(ElementPrefixParser elementPrefixParser, Collection<IListElementInfo<?>> infos, IIngredientManager ingredientManager) {
        this.noPrefix = elementPrefixParser.getNoPrefix();
        for (IListElementInfo<?> iListElementInfo : infos) {
            IListElement<?> element = iListElementInfo.getElement();
            Object uid = ElementSearch.getUid(iListElementInfo.getTypedIngredient(), ingredientManager);
            this.allElements.put(uid, element);
        }
        for (PrefixInfo prefixInfo : elementPrefixParser.allPrefixInfos()) {
            ISearchStorageBuilder storageBuilder = prefixInfo.createStorageBuilder();
            SearchMode searchMode = prefixInfo.getMode();
            if (searchMode != SearchMode.DISABLED) {
                for (IListElementInfo<?> info : infos) {
                    Collection<String> strings = prefixInfo.getStrings(info);
                    IListElement<?> element = info.getElement();
                    for (String string : strings) {
                        ElementSearch.putIfNotBlank(storageBuilder, string, element);
                    }
                }
            }
            ISearchStorage searchStorage = storageBuilder.build();
            PrefixedSearchable prefixedSearchable = new PrefixedSearchable(searchStorage, prefixInfo);
            this.prefixedSearchables.put(prefixInfo, prefixedSearchable);
            this.combinedSearchables.addSearchable(prefixedSearchable);
        }
    }

    @Override
    public Set<IListElement<?>> getSearchResults(ElementPrefixParser.TokenInfo tokenInfo) {
        ISearchable searchable;
        Set<IListElement<?>> results;
        String token;
        block6: {
            block5: {
                token = tokenInfo.token();
                if (token.isEmpty()) {
                    return Set.of();
                }
                results = Collections.newSetFromMap(new IdentityHashMap());
                PrefixInfo<IListElementInfo<?>, IListElement<?>> prefixInfo = tokenInfo.prefixInfo();
                if (prefixInfo == this.noPrefix) {
                    this.combinedSearchables.getSearchResults(token, results::addAll);
                    return results;
                }
                searchable = this.prefixedSearchables.get(prefixInfo);
                if (searchable == null) break block5;
                if (searchable.getMode() != SearchMode.DISABLED) break block6;
            }
            this.combinedSearchables.getSearchResults(token, results::addAll);
            return results;
        }
        searchable.getSearchResults(token, results::addAll);
        return results;
    }

    @Override
    public <T> void add(IListElementInfo<T> info, IIngredientManager ingredientManager) {
        IListElement<T> element = info.getElement();
        Object uid = ElementSearch.getUid(element.getTypedIngredient(), ingredientManager);
        this.allElements.put(uid, element);
        for (PrefixedSearchable<IListElementInfo<?>, IListElement<?>> prefixedSearchable : this.prefixedSearchables.values()) {
            SearchMode searchMode = prefixedSearchable.getMode();
            if (searchMode == SearchMode.DISABLED) continue;
            Collection<String> strings = prefixedSearchable.getStrings(info);
            ISearchStorage<IListElement<?>> storage = prefixedSearchable.getSearchStorage();
            for (String string : strings) {
                ElementSearch.putIfNotBlank(storage, string, element);
            }
        }
    }

    static <T> void putIfNotBlank(ISearchStorageBuilder<T> storageBuilder, String string, T element) {
        String trimmedString = string.trim();
        if (!trimmedString.isEmpty()) {
            storageBuilder.put(trimmedString, element);
        }
    }

    static <T> void putIfNotBlank(ISearchStorage<T> storage, String string, T element) {
        String trimmedString = string.trim();
        if (!trimmedString.isEmpty()) {
            storage.put(trimmedString, element);
        }
    }

    private static <T> Object getUid(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(typedIngredient.getType());
        return ingredientHelper.getUid(typedIngredient.getIngredient(), UidContext.Ingredient);
    }

    @Override
    @Nullable
    public <T> IListElement<T> findElement(ITypedIngredient<T> ingredient, IIngredientHelper<T> ingredientHelper) {
        Object ingredientUid = ingredientHelper.getUid(ingredient.getIngredient(), UidContext.Ingredient);
        IListElement<?> listElement = this.allElements.get(ingredientUid);
        if (listElement != null && listElement.getTypedIngredient().getType().equals(ingredient.getType())) {
            IListElement<?> cast = listElement;
            return cast;
        }
        return null;
    }

    @Override
    public Collection<IListElement<?>> getAllIngredients() {
        return Collections.unmodifiableCollection(this.allElements.values());
    }

    @Override
    public void logStatistics() {
        this.prefixedSearchables.forEach((prefixInfo, value) -> {
            if (prefixInfo.getMode() != SearchMode.DISABLED) {
                ISearchStorage storage = value.getSearchStorage();
                LOGGER.info("ElementSearch {} Storage Stats: {}", prefixInfo, (Object)storage.statistics());
            }
        });
    }
}


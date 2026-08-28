/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.search;

import java.util.Collection;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.search.ElementPrefixParser;
import org.jetbrains.annotations.Nullable;

public interface IElementSearch {
    public <T> void add(IListElementInfo<T> var1, IIngredientManager var2);

    public Collection<IListElement<?>> getAllIngredients();

    public Set<IListElement<?>> getSearchResults(ElementPrefixParser.TokenInfo var1);

    @Nullable
    public <T> IListElement<T> findElement(ITypedIngredient<T> var1, IIngredientHelper<T> var2);

    public void logStatistics();
}


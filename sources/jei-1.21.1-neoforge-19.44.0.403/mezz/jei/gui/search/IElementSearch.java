package mezz.jei.gui.search;

import java.util.Collection;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import org.jetbrains.annotations.Nullable;

public interface IElementSearch {
   <T> void add(IListElementInfo<T> var1, IIngredientManager var2);

   Collection<IListElement<?>> getAllIngredients();

   Set<IListElement<?>> getSearchResults(ElementPrefixParser.TokenInfo var1);

   @Nullable
   <T> IListElement<T> findElement(ITypedIngredient<T> var1, IIngredientHelper<T> var2);

   void logStatistics();
}

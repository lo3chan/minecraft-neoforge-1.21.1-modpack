package mezz.jei.gui.ingredients;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IIngredientFilterConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Unmodifiable;

public interface IListElementInfo<V> {
   List<String> getNames();

   String getModNameForSorting();

   Collection<String> getModNames(IIngredientFilterConfig var1);

   @Unmodifiable
   Set<String> getTooltipStrings(IIngredientFilterConfig var1, IIngredientManager var2);

   Collection<String> getTagStrings(IIngredientManager var1);

   Stream<ResourceLocation> getTagIds(IIngredientManager var1);

   Iterable<Integer> getColors(IIngredientManager var1);

   @Unmodifiable
   Collection<String> getColorNames(IIngredientManager var1, IColorHelper var2);

   @Unmodifiable
   Collection<String> getCreativeTabsStrings(IIngredientManager var1);

   ResourceLocation getResourceLocation();

   IListElement<V> getElement();

   ITypedIngredient<V> getTypedIngredient();

   int getCreatedIndex();
}

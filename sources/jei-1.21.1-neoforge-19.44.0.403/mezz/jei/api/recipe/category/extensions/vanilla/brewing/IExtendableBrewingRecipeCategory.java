package mezz.jei.api.recipe.category.extensions.vanilla.brewing;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IExtendableBrewingRecipeCategory {
   <R> void addExtension(Class<? extends R> var1, IBrewingCategoryExtension<R> var2);
}

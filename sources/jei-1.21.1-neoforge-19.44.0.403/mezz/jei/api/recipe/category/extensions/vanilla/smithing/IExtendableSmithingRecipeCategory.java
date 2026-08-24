package mezz.jei.api.recipe.category.extensions.vanilla.smithing;

import net.minecraft.world.item.crafting.SmithingRecipe;

public interface IExtendableSmithingRecipeCategory {
   <R extends SmithingRecipe> void addExtension(Class<? extends R> var1, ISmithingCategoryExtension<R> var2);
}

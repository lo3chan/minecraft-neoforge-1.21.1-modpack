package mezz.jei.api.recipe.category.extensions.vanilla.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;

public interface IExtendableCraftingRecipeCategory {
   <R extends CraftingRecipe> void addExtension(Class<? extends R> var1, ICraftingCategoryExtension<R> var2);
}

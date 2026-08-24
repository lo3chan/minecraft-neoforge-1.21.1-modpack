package mezz.jei.api.recipe.vanilla;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

public interface IJeiShapedRecipeBuilder {
   IJeiShapedRecipeBuilder define(Character var1, Ingredient var2);

   IJeiShapedRecipeBuilder pattern(String var1);

   IJeiShapedRecipeBuilder group(String var1);

   CraftingRecipe build();
}

package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import vectorwing.farmersdelight.data.recipe.CookingRecipes;
import vectorwing.farmersdelight.data.recipe.CraftingRecipes;
import vectorwing.farmersdelight.data.recipe.CuttingRecipes;
import vectorwing.farmersdelight.data.recipe.SmeltingRecipes;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Recipes extends RecipeProvider {
   public Recipes(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries);
   }

   protected void buildRecipes(RecipeOutput output) {
      CraftingRecipes.register(output);
      SmeltingRecipes.register(output);
      CookingRecipes.register(output);
      CuttingRecipes.register(output);
   }
}

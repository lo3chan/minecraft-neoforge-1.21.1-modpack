package vectorwing.farmersdelight.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModMenuTypes;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.utility.RecipeUtils;
import vectorwing.farmersdelight.integration.emi.handler.CookingPotEmiRecipeHandler;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;
import vectorwing.farmersdelight.integration.emi.recipe.CuttingEmiRecipe;
import vectorwing.farmersdelight.integration.emi.recipe.DecompositionEmiRecipe;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {
   public void register(EmiRegistry registry) {
      registry.addCategory(FDRecipeCategories.COOKING);
      registry.addCategory(FDRecipeCategories.CUTTING);
      registry.addCategory(FDRecipeCategories.DECOMPOSITION);
      registry.addWorkstation(FDRecipeCategories.COOKING, FDRecipeWorkstations.COOKING_POT);
      registry.addWorkstation(FDRecipeCategories.CUTTING, FDRecipeWorkstations.CUTTING_BOARD);
      registry.addRecipeHandler(ModMenuTypes.COOKING_POT.get(), new CookingPotEmiRecipeHandler());

      for (RecipeHolder<CookingPotRecipe> recipeHolder : registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get())) {
         CookingPotRecipe recipe = (CookingPotRecipe)recipeHolder.value();
         registry.addRecipe(
            new CookingPotEmiRecipe(
               recipeHolder.id(),
               recipe.getIngredients().stream().<EmiIngredient>map(EmiIngredient::of).toList(),
               EmiStack.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())),
               EmiStack.of(recipe.getOutputContainer()),
               recipe.getCookTime(),
               recipe.getExperience()
            )
         );
      }

      for (RecipeHolder<CuttingBoardRecipe> recipeHolder : registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CUTTING.get())) {
         CuttingBoardRecipe recipe = (CuttingBoardRecipe)recipeHolder.value();
         registry.addRecipe(
            new CuttingEmiRecipe(
               recipeHolder.id(),
               EmiIngredient.of(recipe.getTool()),
               EmiIngredient.of((Ingredient)recipe.getIngredients().getFirst()),
               recipe.getRollableResults().stream().map(chanceResult -> EmiStack.of(chanceResult.stack()).setChance(chanceResult.chance())).toList()
            )
         );
      }

      registry.addRecipe(new DecompositionEmiRecipe());
      this.addSpecialRecipes(registry);
   }

   public void addSpecialRecipes(EmiRegistry registry) {
      ResourceLocation doughRecipeId = RecipeUtils.FDLocation("wheat_dough_from_water");
      if (registry.getRecipeManager().byKey(doughRecipeId).isPresent()) {
         ResourceLocation syntheticLocation = ResourceLocation.fromNamespaceAndPath("farmersdelight", "/crafting/wheat_dough_from_water");
         registry.addRecipe(
            new EmiCraftingRecipe(
               List.of(EmiStack.of(Items.WHEAT), EmiStack.of(Items.WATER_BUCKET)), EmiStack.of((ItemLike)ModItems.WHEAT_DOUGH.get()), syntheticLocation, true
            )
         );
      }
   }
}

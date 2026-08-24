package net.joefoxe.hexerei.integration.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class BottlingRecipeJEI {
   public static List<CauldronEmptyingRecipe> getRecipeList(RecipeManager rm) {
      List<CauldronEmptyingRecipe> recipeList = new ArrayList<>(
         new ArrayList(rm.getAllRecipesFor((RecipeType)ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get())).stream().map(RecipeHolder::value).toList()
      );

      for (FluidMixingRecipe recipe : PotionMixingRecipes.ALL) {
         AtomicBoolean atomicBoolean = new AtomicBoolean(false);
         recipeList.forEach(rec -> {
            if (FluidStack.isSameFluidSameComponents(rec.getFluid(), recipe.getLiquidOutput())) {
               atomicBoolean.set(true);
            }
         });
         if (!atomicBoolean.get()) {
            ItemStack potionItem = PotionFluidHandler.fillBottle(recipe.getLiquidOutput());
            recipeList.add(
               new CauldronEmptyingRecipe(
                  Ingredient.of(new ItemStack[]{Items.GLASS_BOTTLE.getDefaultInstance()}), recipe.getLiquidOutput().copyWithAmount(250), potionItem
               )
            );
         }
      }

      return recipeList;
   }
}

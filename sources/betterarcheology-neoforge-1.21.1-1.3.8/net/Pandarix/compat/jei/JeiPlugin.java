package net.Pandarix.compat.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.Pandarix.BACommon;
import net.Pandarix.block.ModBlocks;
import net.Pandarix.recipe.IdentifyingRecipe;
import net.Pandarix.screen.IdentifyingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
   @NotNull
   public ResourceLocation getPluginUid() {
      return BACommon.createResource("jei_plugin");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new IdentifyingCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(@NotNull IRecipeRegistration registration) {
      if (Minecraft.getInstance().level != null) {
         RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
         List<IdentifyingRecipe> identifyingRecipes = new ArrayList<>();
         recipeManager.getAllRecipesFor(IdentifyingRecipe.Type.INSTANCE).forEach(recipe -> identifyingRecipes.add((IdentifyingRecipe)recipe.value()));
         registration.addRecipes(IdentifyingCategory.IDENTIFYING_RECIPE_TYPE, identifyingRecipes);
      }
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addRecipeClickArea(IdentifyingScreen.class, 51, 48, 74, 24, new RecipeType[]{IdentifyingCategory.IDENTIFYING_RECIPE_TYPE});
   }

   public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
      super.registerRecipeCatalysts(registration);
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.ARCHEOLOGY_TABLE.get()), new RecipeType[]{IdentifyingCategory.IDENTIFYING_RECIPE_TYPE});
   }
}

package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public interface RecipeViewerRegistrar {
   <T> RecipeViewerRecipeTypeRegistration<T> registerCustomRecipeType(ResourceLocation var1, Class<T> var2);

   <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> RecipeViewerVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> registerRecipeType(
      ResourceLocation var1, Class<TRecipe> var2
   );

   void registerIngredientInfo(ItemLike var1, Component var2);

   <T extends AbstractContainerScreen<?>> void registerScreenOcclusion(Class<T> var1, RecipeViewerOcclusionProvider<T> var2);

   void registerGlobalScreenOcclusion(RecipeViewerOcclusionProvider<AbstractContainerScreen<?>> var1);

   <T extends AbstractContainerMenu> void registerRecipeTransferHandler(
      Class<T> var1, Holder<MenuType<T>> var2, RecipeType<?> var3, int var4, int var5, int var6, int var7
   );
}

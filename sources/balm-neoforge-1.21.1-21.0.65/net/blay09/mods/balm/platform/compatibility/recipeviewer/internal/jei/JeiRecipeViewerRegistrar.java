package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerOcclusionProvider;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerRecipeTypeRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerRegistrar;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerVanillaRecipeTypeRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.IdentifiableRecipeTypeTransferRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.IngredientInfoRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.ScreenOcclusionRegistration;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

class JeiRecipeViewerRegistrar implements RecipeViewerRegistrar {
   private final List<JeiRecipeTypeRegistration<?>> recipeTypeRegistrations = Collections.synchronizedList(new ArrayList<>());
   private final List<IngredientInfoRegistration> ingredientInfoRegistrations = Collections.synchronizedList(new ArrayList<>());
   private final List<ScreenOcclusionRegistration<?>> screenOcclusions = Collections.synchronizedList(new ArrayList<>());
   private final List<RecipeViewerOcclusionProvider<?>> globalScreenOcclusions = Collections.synchronizedList(new ArrayList<>());
   private final List<IdentifiableRecipeTypeTransferRegistration<?>> identifiableRecipeTypeTransferRegistrations = Collections.synchronizedList(
      new ArrayList<>()
   );

   @Override
   public <T> RecipeViewerRecipeTypeRegistration<T> registerCustomRecipeType(ResourceLocation identifier, Class<T> recipeClass) {
      JeiRecipeTypeRegistration<T> recipeTypeRegistration = new JeiRecipeTypeRegistration<>(identifier, recipeClass);
      this.recipeTypeRegistrations.add(recipeTypeRegistration);
      return recipeTypeRegistration;
   }

   @Override
   public <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> RecipeViewerVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> registerRecipeType(
      ResourceLocation identifier, Class<TRecipe> recipeClass
   ) {
      JeiVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> recipeTypeRegistration = new JeiVanillaRecipeTypeRegistration<>(identifier, recipeClass);
      this.recipeTypeRegistrations.add(recipeTypeRegistration);
      return recipeTypeRegistration;
   }

   @Override
   public void registerIngredientInfo(ItemLike itemLike, Component description) {
      this.ingredientInfoRegistrations.add(new IngredientInfoRegistration(itemLike, description));
   }

   @Override
   public <T extends AbstractContainerScreen<?>> void registerScreenOcclusion(Class<T> screenClass, RecipeViewerOcclusionProvider<T> provider) {
      this.screenOcclusions.add(new ScreenOcclusionRegistration<>(screenClass, provider));
   }

   @Override
   public void registerGlobalScreenOcclusion(RecipeViewerOcclusionProvider<AbstractContainerScreen<?>> provider) {
      this.globalScreenOcclusions.add(provider);
   }

   @Override
   public <T extends AbstractContainerMenu> void registerRecipeTransferHandler(
      Class<T> menuClass,
      Holder<MenuType<T>> menuType,
      RecipeType<?> recipeType,
      int recipeSlotStart,
      int recipeSlotCount,
      int inventorySlotStart,
      int inventorySlotCount
   ) {
      ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
      if (recipeTypeId != null) {
         this.identifiableRecipeTypeTransferRegistrations
            .add(
               new IdentifiableRecipeTypeTransferRegistration<>(
                  menuClass, menuType, recipeTypeId, recipeSlotStart, recipeSlotCount, inventorySlotStart, inventorySlotCount
               )
            );
      }
   }

   public Collection<JeiRecipeTypeRegistration<?>> getRecipeTypes() {
      return this.recipeTypeRegistrations;
   }

   public Collection<IngredientInfoRegistration> getIngredientInfoRegistrations() {
      return this.ingredientInfoRegistrations;
   }

   public Collection<ScreenOcclusionRegistration<?>> getScreenOcclusions() {
      return this.screenOcclusions;
   }

   public List<RecipeViewerOcclusionProvider<?>> getGlobalScreenOcclusions() {
      return this.globalScreenOcclusions;
   }

   public Collection<IdentifiableRecipeTypeTransferRegistration<?>> getIdentifiableRecipeTypeTransferRegistrations() {
      return this.identifiableRecipeTypeTransferRegistrations;
   }
}

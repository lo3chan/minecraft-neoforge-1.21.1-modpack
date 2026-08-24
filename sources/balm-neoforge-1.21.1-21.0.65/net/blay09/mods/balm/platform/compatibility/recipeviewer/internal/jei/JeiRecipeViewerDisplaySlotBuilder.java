package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.jei;

import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerDisplaySlotBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

class JeiRecipeViewerDisplaySlotBuilder implements RecipeViewerDisplaySlotBuilder {
   private final IRecipeSlotBuilder builder;

   public JeiRecipeViewerDisplaySlotBuilder(IRecipeSlotBuilder builder) {
      this.builder = builder;
   }

   @Override
   public RecipeViewerDisplaySlotBuilder add(Ingredient ingredient) {
      this.builder.addIngredients(ingredient);
      return this;
   }

   @Override
   public RecipeViewerDisplaySlotBuilder add(ItemStack itemStack) {
      this.builder.addItemStack(itemStack);
      return this;
   }

   @Override
   public RecipeViewerDisplaySlotBuilder add(ItemLike itemLike) {
      this.builder.addItemLike(itemLike);
      return this;
   }

   @Override
   public RecipeViewerDisplaySlotBuilder withSlotBackground() {
      this.builder.setStandardSlotBackground();
      return this;
   }

   @Override
   public RecipeViewerDisplaySlotBuilder withOutputSlotBackground() {
      this.builder.setOutputSlotBackground();
      return this;
   }
}

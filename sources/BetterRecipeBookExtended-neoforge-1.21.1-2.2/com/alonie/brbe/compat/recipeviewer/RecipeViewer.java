package com.alonie.brbe.compat.recipeviewer;

import net.minecraft.world.item.ItemStack;

public interface RecipeViewer {
   RecipeViewer NONE = new RecipeViewer() {
      @Override
      public boolean isAvailable() {
         return false;
      }

      @Override
      public void showRecipe(ItemStack stack) {
      }

      @Override
      public void showUses(ItemStack stack) {
      }

      @Override
      public boolean matchesShowRecipe(int keyCode, int scanCode) {
         return false;
      }

      @Override
      public boolean matchesShowUses(int keyCode, int scanCode) {
         return false;
      }
   };

   boolean isAvailable();

   void showRecipe(ItemStack var1);

   void showUses(ItemStack var1);

   boolean matchesShowRecipe(int var1, int var2);

   boolean matchesShowUses(int var1, int var2);
}

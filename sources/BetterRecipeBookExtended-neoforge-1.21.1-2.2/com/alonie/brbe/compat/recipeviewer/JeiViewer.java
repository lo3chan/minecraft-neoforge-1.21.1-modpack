package com.alonie.brbe.compat.recipeviewer;

import com.alonie.brbe.compat.ItemViewCompat;
import net.minecraft.world.item.ItemStack;

public final class JeiViewer implements RecipeViewer {
   private static volatile boolean available;

   public static void markAvailable() {
      available = true;
   }

   public static void markUnavailable() {
      available = false;
   }

   @Override
   public boolean isAvailable() {
      return available;
   }

   @Override
   public void showRecipe(ItemStack stack) {
      if (!stack.isEmpty()) {
         try {
            ItemViewCompat.openRecipeView(stack);
         } catch (Exception var3) {
         }
      }
   }

   @Override
   public void showUses(ItemStack stack) {
      if (!stack.isEmpty()) {
         try {
            ItemViewCompat.openUsageView(stack);
         } catch (Exception var3) {
         }
      }
   }

   @Override
   public boolean matchesShowRecipe(int keyCode, int scanCode) {
      return available && ItemViewCompat.matchesShowRecipe(keyCode, scanCode);
   }

   @Override
   public boolean matchesShowUses(int keyCode, int scanCode) {
      return available && ItemViewCompat.matchesShowUses(keyCode, scanCode);
   }
}

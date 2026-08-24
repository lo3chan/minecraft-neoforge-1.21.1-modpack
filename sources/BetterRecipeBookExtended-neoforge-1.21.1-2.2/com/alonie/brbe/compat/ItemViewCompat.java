package com.alonie.brbe.compat;

import net.minecraft.world.item.ItemStack;

public final class ItemViewCompat {
   private static ItemViewCompat.Handler handler;

   private ItemViewCompat() {
   }

   public static void setHandler(ItemViewCompat.Handler h) {
      handler = h;
   }

   public static boolean isLoaded() {
      return handler != null;
   }

   public static boolean openRecipeView(ItemStack stack) {
      return handler != null && handler.openRecipeView(stack);
   }

   public static boolean openUsageView(ItemStack stack) {
      return handler != null && handler.openUsageView(stack);
   }

   public static boolean matchesShowRecipe(int keyCode, int scanCode) {
      return handler != null && handler.matchesShowRecipe(keyCode, scanCode);
   }

   public static boolean matchesShowUses(int keyCode, int scanCode) {
      return handler != null && handler.matchesShowUses(keyCode, scanCode);
   }

   public interface Handler {
      boolean openRecipeView(ItemStack var1);

      boolean openUsageView(ItemStack var1);

      default boolean matchesShowRecipe(int keyCode, int scanCode) {
         return false;
      }

      default boolean matchesShowUses(int keyCode, int scanCode) {
         return false;
      }
   }
}

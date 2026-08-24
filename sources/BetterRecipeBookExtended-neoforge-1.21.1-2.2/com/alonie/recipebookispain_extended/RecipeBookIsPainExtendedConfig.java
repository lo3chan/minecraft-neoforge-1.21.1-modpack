package com.alonie.recipebookispain_extended;

import com.alonie.brbe.BetterRecipeBook;

public final class RecipeBookIsPainExtendedConfig {
   private static boolean lastEnabled = true;
   private static int lastBottomNumber = 16;

   private RecipeBookIsPainExtendedConfig() {
   }

   public static boolean enabled() {
      return BetterRecipeBook.ctx().config() == null ? true : BetterRecipeBook.ctx().config().rbip.enableRecipeBookIsPain;
   }

   public static int bottomNumber() {
      if (BetterRecipeBook.ctx().config() == null) {
         return 16;
      } else {
         return BetterRecipeBook.ctx().config().rbip.enableTabPage ? 16 : 6;
      }
   }

   @Deprecated
   public boolean extendedFeatures() {
      return true;
   }

   public static RecipeBookIsPainExtendedConfig get() {
      return RecipeBookIsPainExtendedConfig.Holder.INSTANCE;
   }

   public static boolean reloadIfChanged() {
      boolean changed = false;
      boolean current = enabled();
      int currentBottom = bottomNumber();
      if (current != lastEnabled || currentBottom != lastBottomNumber) {
         lastEnabled = current;
         lastBottomNumber = currentBottom;
         changed = true;
      }

      return changed;
   }

   private static final class Holder {
      static final RecipeBookIsPainExtendedConfig INSTANCE = new RecipeBookIsPainExtendedConfig();
   }
}

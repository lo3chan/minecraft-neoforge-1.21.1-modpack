package com.alonie.brbe.util;

import net.minecraft.util.Mth;

public final class AlternativeOverlayLayout {
   private static final int DEFAULT_SMALL_COLUMNS = 4;
   private static final int DEFAULT_LARGE_COLUMNS = 5;
   private static final int SMALL_LAYOUT_LIMIT = 16;
   private static final int MAX_ROWS_BEFORE_EXPANDING = 5;

   private AlternativeOverlayLayout() {
   }

   public static int columnsFor(int recipeCount) {
      if (recipeCount <= 0) {
         return 4;
      } else {
         int columns = recipeCount <= 16 ? 4 : 5;
         return Mth.ceil((float)recipeCount / columns) <= 5 ? columns : Mth.ceil(recipeCount / 5.0F);
      }
   }
}

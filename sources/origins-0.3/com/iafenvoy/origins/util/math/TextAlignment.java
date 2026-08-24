package com.iafenvoy.origins.util.math;

import java.util.Optional;

public enum TextAlignment {
   NONE((left, right, textWidth) -> null),
   LEFT((left, right, textWidth) -> left - 1),
   RIGHT((left, right, textWidth) -> right - textWidth + 1),
   CENTER((left, right, textWidth) -> (left + right - textWidth) / 2);

   private final TextAlignment.PositionSupplier horizontalSupplier;

   private TextAlignment(TextAlignment.PositionSupplier horizontalSupplier) {
      this.horizontalSupplier = horizontalSupplier;
   }

   public Optional<Integer> horizontal(int left, int right, int textWidth) {
      return Optional.ofNullable(this.horizontalSupplier.apply(left, right, textWidth));
   }

   @FunctionalInterface
   public interface PositionSupplier {
      Integer apply(int var1, int var2, int var3);
   }
}

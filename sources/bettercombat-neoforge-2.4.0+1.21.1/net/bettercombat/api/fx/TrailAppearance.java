package net.bettercombat.api.fx;

import org.jetbrains.annotations.Nullable;

public class TrailAppearance {
   public TrailAppearance.Part primary = TrailAppearance.Part.DEFAULT_PRIMARY;
   public TrailAppearance.Part secondary = TrailAppearance.Part.DEFAULT_SECONDARY;
   public static final TrailAppearance DEFAULT = new TrailAppearance();

   public TrailAppearance() {
   }

   public TrailAppearance(TrailAppearance.Part primary, @Nullable TrailAppearance.Part secondary) {
      this.primary = primary;
      this.secondary = secondary;
   }

   public record Part(long color_rgba, boolean glows) {
      public static final TrailAppearance.Part DEFAULT_PRIMARY = new TrailAppearance.Part(4294967295L, false);
      public static final TrailAppearance.Part DEFAULT_SECONDARY = new TrailAppearance.Part(2576980377L, false);
   }
}

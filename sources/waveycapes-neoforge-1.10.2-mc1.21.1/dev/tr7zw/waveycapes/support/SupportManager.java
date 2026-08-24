package dev.tr7zw.waveycapes.support;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import lombok.Generated;

public class SupportManager {
   public static Set<ModSupport> mods = new HashSet<>();
   public static Set<AnimationSupport> animationSupport = new HashSet<>();
   public static Supplier<Float> alphaSupplier = () -> 1.0F;

   public static Set<ModSupport> getSupportedMods() {
      return mods;
   }

   @Generated
   public static Supplier<Float> getAlphaSupplier() {
      return alphaSupplier;
   }

   @Generated
   public static void setAlphaSupplier(Supplier<Float> alphaSupplier) {
      SupportManager.alphaSupplier = alphaSupplier;
   }
}

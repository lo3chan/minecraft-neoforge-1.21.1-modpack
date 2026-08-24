package dev.worldgen.lithostitched.platform;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;

public class LithostitchedPlatform {
   public static boolean isModLoaded(String id) {
      return ModList.get().isLoaded(id);
   }

   public static void rebuildSettings(Biome biome, List<HolderSet<PlacedFeature>> features) {
      throw new IllegalStateException("Cannot call LithostitchedPlatform#rebuildSettings on Neoforge.");
   }

   public static <T> Supplier<T> memoize(Supplier<T> delegate) {
      return Lazy.of(delegate);
   }
}

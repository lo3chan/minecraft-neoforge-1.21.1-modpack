package dev.architectury.hooks.level.biome.forge;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.ClimateSettings;

public class BiomeHooksImpl {
   public static ClimateSettings extractClimateSettings(Biome biome) {
      return biome.getModifiedClimateSettings();
   }
}

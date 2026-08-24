package dev.worldgen.lithostitched.duck;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.Condition;

public interface ContextBiomeAccessor {
   Condition biomeMatches(HolderSet<Biome> var1);
}

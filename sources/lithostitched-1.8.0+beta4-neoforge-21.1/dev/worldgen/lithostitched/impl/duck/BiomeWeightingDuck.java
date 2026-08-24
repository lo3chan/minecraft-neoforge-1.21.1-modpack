package dev.worldgen.lithostitched.impl.duck;

import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface BiomeWeightingDuck {
   void lithostitched$clear();

   void lithostitched$accumulate(double var1, Holder<Biome> var3);

   Reference2DoubleArrayMap<Holder<Biome>> lithostitched$getWeights();
}

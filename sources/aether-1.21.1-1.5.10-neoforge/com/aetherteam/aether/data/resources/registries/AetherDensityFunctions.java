package com.aetherteam.aether.data.resources.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;

public class AetherDensityFunctions {
   public static final ResourceKey<DensityFunction> BASE_3D_NOISE_AETHER = createKey("base_3d_noise_aether");

   private static ResourceKey<DensityFunction> createKey(String name) {
      return ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.fromNamespaceAndPath("aether", name));
   }

   public static void bootstrap(BootstrapContext<DensityFunction> context) {
      context.register(BASE_3D_NOISE_AETHER, BlendedNoise.createUnseeded(0.25, 0.25, 80.0, 160.0, 8.0));
   }
}

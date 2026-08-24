package com.aetherteam.aether.data.resources.builders;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherNoises;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.DensityFunctions.HolderHolder;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class AetherNoiseBuilders {
   private static final RuleSource GRASS_BLOCK = SurfaceRules.state(
      (BlockState)((Block)AetherBlocks.AETHER_GRASS_BLOCK.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true)
   );
   private static final RuleSource DIRT = SurfaceRules.state(
      (BlockState)((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true)
   );

   public static NoiseGeneratorSettings skylandsNoiseSettings(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NoiseParameters> noise) {
      BlockState holystone = (BlockState)((Block)AetherBlocks.HOLYSTONE.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
      return new NoiseGeneratorSettings(
         new NoiseSettings(0, 128, 2, 1),
         holystone,
         Blocks.WATER.defaultBlockState(),
         makeNoiseRouter(densityFunctions, noise),
         aetherSurfaceRules(),
         List.of(),
         -64,
         false,
         false,
         false,
         false
      );
   }

   public static RuleSource aetherSurfaceRules() {
      RuleSource surface = SurfaceRules.sequence(new RuleSource[]{SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK), DIRT});
      return SurfaceRules.sequence(new RuleSource[]{SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surface), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)});
   }

   private static NoiseRouter makeNoiseRouter(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NoiseParameters> noise) {
      return createNoiseRouter(densityFunctions, noise, buildFinalDensity(densityFunctions));
   }

   private static DensityFunction buildFinalDensity(HolderGetter<DensityFunction> densityFunctions) {
      DensityFunction density = getFunction(
         densityFunctions, ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.fromNamespaceAndPath("aether", "base_3d_noise_aether"))
      );
      density = DensityFunctions.add(density, DensityFunctions.constant(-0.13));
      density = slide(density, 0, 128, 72, 0, -0.2, 8, 40, -0.1);
      density = DensityFunctions.add(density, DensityFunctions.constant(-0.05));
      density = DensityFunctions.blendDensity(density);
      density = DensityFunctions.interpolated(density);
      return density.squeeze();
   }

   private static DensityFunction slide(
      DensityFunction density, int minY, int maxY, int fromYTop, int toYTop, double offset1, int fromYBottom, int toYBottom, double offset2
   ) {
      DensityFunction topSlide = DensityFunctions.yClampedGradient(minY + maxY - fromYTop, minY + maxY - toYTop, 1.0, 0.0);
      density = DensityFunctions.lerp(topSlide, offset1, density);
      DensityFunction bottomSlide = DensityFunctions.yClampedGradient(minY + fromYBottom, minY + toYBottom, 0.0, 1.0);
      return DensityFunctions.lerp(bottomSlide, offset2, density);
   }

   private static NoiseRouter createNoiseRouter(
      HolderGetter<DensityFunction> densityFunctions, HolderGetter<NoiseParameters> noise, DensityFunction finalDensity
   ) {
      DensityFunction shiftX = getFunction(densityFunctions, ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace("shift_x")));
      DensityFunction shiftZ = getFunction(densityFunctions, ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace("shift_z")));
      DensityFunction temperature = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noise.getOrThrow(AetherNoises.TEMPERATURE));
      DensityFunction vegetation = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noise.getOrThrow(AetherNoises.VEGETATION));
      return new NoiseRouter(
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         temperature,
         vegetation,
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         finalDensity,
         DensityFunctions.zero(),
         DensityFunctions.zero(),
         DensityFunctions.zero()
      );
   }

   private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
      return new HolderHolder(densityFunctions.getOrThrow(key));
   }
}

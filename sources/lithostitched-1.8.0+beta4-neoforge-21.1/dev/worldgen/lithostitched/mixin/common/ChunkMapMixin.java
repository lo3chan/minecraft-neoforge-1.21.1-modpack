package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ChunkMap.class})
public class ChunkMapMixin {
   @WrapOperation(
      method = {"<init>"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;"
      )}
   )
   private RandomState wrapNoiseRouter(
      NoiseGeneratorSettings noiseSettings,
      HolderGetter<NoiseParameters> noiseGetter,
      long seed,
      Operation<RandomState> init,
      ServerLevel level,
      @Local(ordinal = 0) RegistryAccess registries
   ) {
      NoiseGeneratorSettingsAccessor accessor = (NoiseGeneratorSettingsAccessor)noiseSettings;
      NoiseRouter router = noiseSettings.noiseRouter();
      List<WrapNoiseRouterModifier> modifiers = ModifierManager.getModifiersOfType(registries, WrapNoiseRouterModifier.CODEC)
         .stream()
         .map(Entry::getValue)
         .filter(modifier -> level.dimension().equals(modifier.dimension()))
         .toList();
      if (!modifiers.isEmpty()) {
         accessor.setNoiseRouter(
            new NoiseRouter(
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.BARRIER, router.barrierNoise(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.FLUID_LEVEL_FLOODEDNESS, router.fluidLevelFloodednessNoise(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.FLUID_LEVEL_SPREAD, router.fluidLevelSpreadNoise(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.LAVA, router.lavaNoise(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.TEMPERATURE, router.temperature(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.VEGETATION, router.vegetation(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.CONTINENTS, router.continents(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.EROSION, router.erosion(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.DEPTH, router.depth(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.RIDGES, router.ridges(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(
                  NoiseRouterTarget.INITIAL_DENSITY_WITHOUT_JAGGEDNESS, router.initialDensityWithoutJaggedness(), modifiers
               ),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.FINAL_DENSITY, router.finalDensity(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.VEIN_TOGGLE, router.veinToggle(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.VEIN_RIDGED, router.veinRidged(), modifiers),
               WrapNoiseRouterModifier.modifyDensityFunction(NoiseRouterTarget.VEIN_GAP, router.veinGap(), modifiers)
            )
         );
      }

      return (RandomState)init.call(new Object[]{noiseSettings, noiseGetter, seed});
   }
}

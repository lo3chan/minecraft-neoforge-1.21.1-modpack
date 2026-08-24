package dev.worldgen.lithostitched.worldgen.modifier.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.mixin.common.NoiseBasedChunkGeneratorAccessor;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.DensityFunctions.HolderHolder;

public record RereferenceNoiseSettingsModifier() implements WorldgenModifier {
   public static final MapCodec<RereferenceNoiseSettingsModifier> CODEC = MapCodec.unit(RereferenceNoiseSettingsModifier::new);

   @Override
   public Optional<LoadPredicate> predicate() {
      return Optional.empty();
   }

   @Override
   public void apply(RegistryAccess registries) {
      Registry<LevelStem> dimensions = Lithostitched.registry(registries, Registries.LEVEL_STEM);
      Registry<NoiseGeneratorSettings> noiseSettings = Lithostitched.registry(registries, Registries.NOISE_SETTINGS);

      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
         if (entry.getValue().generator() instanceof NoiseBasedChunkGenerator generator) {
            Holder<NoiseGeneratorSettings> savedSettings = generator.generatorSettings();
            if (!savedSettings.kind().equals(Kind.REFERENCE)) {
               for (Holder<NoiseGeneratorSettings> registrySettings : noiseSettings.asHolderIdMap()) {
                  if (doSettingsMatchIgnoringSurfaceRules(
                     (NoiseGeneratorSettings)savedSettings.value(), (NoiseGeneratorSettings)registrySettings.value(), registrySettings
                  )) {
                     ((NoiseBasedChunkGeneratorAccessor)generator).setSettings(registrySettings);
                     Lithostitched.LOGGER
                        .warn(
                           "Patched a possible corruption issue in the world save from previous Lithostitched versions. If there are new issues in this world starting right now, please report them to Lithostitched."
                        );
                     break;
                  }
               }
            }
         }
      }
   }

   private static boolean doSettingsMatchIgnoringSurfaceRules(
      NoiseGeneratorSettings saved, NoiseGeneratorSettings registry, Holder<NoiseGeneratorSettings> holder
   ) {
      if (!saved.noiseSettings().equals(registry.noiseSettings())) {
         return false;
      } else if (!saved.defaultBlock().equals(registry.defaultBlock())) {
         return false;
      } else if (!saved.defaultFluid().equals(registry.defaultFluid())) {
         return false;
      } else if (!doDepthNoiseRouterValuesMatch(saved.noiseRouter(), registry.noiseRouter())) {
         return false;
      } else if (!saved.spawnTarget().equals(registry.spawnTarget())) {
         return false;
      } else if (saved.seaLevel() != registry.seaLevel()) {
         return false;
      } else if (saved.disableMobGeneration() != registry.disableMobGeneration()) {
         return false;
      } else if (saved.aquifersEnabled() != registry.aquifersEnabled()) {
         return false;
      } else {
         return saved.oreVeinsEnabled() != registry.oreVeinsEnabled() ? false : saved.useLegacyRandomSource() == registry.useLegacyRandomSource();
      }
   }

   private static boolean doDepthNoiseRouterValuesMatch(NoiseRouter saved, NoiseRouter registry) {
      ResourceLocation savedId = getDensityFunctionId(NoiseRouterTarget.DEPTH.getDensityFunction(saved));
      ResourceLocation registryId = getDensityFunctionId(NoiseRouterTarget.DEPTH.getDensityFunction(registry));
      return savedId != null && savedId.equals(registryId);
   }

   private static ResourceLocation getDensityFunctionId(DensityFunction function) {
      if (function instanceof MergedDensityFunction merged) {
         function = merged.original();
      }

      if (function instanceof HolderHolder var5) {
         HolderHolder var10000 = var5;

         try {
            var6 = var10000.function();
         } catch (Throwable var4) {
            throw new MatchException(var4.toString(), var4);
         }

         Holder var3 = var6;
         if (var3.unwrapKey().isPresent()) {
            return ((ResourceKey)var3.unwrapKey().get()).location();
         }
      }

      return null;
   }

   @Override
   public int priority() {
      return 2147483647;
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}

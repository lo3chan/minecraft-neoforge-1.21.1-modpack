package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class SurfaceRuleInitializer {
   public static void initializeOnServerStart(MinecraftServer server) {
      Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Starting initialization...");
      Citadel.LOGGER.info("[Citadel] OVERWORLD rules registered: {}", SurfaceRulesManager.hasRulesForCategory(SurfaceRulesManager.RuleCategory.OVERWORLD));
      RegistryAccess registryAccess = server.registryAccess();
      Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStemRegistry.entrySet()) {
         LevelStem stem = entry.getValue();
         Citadel.LOGGER.info("[Citadel] Processing dimension: {}", entry.getKey().location());
         initializeSurfaceRules(stem.type(), entry.getKey(), stem.generator());
      }

      Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Initialization complete.");
   }

   private static void initializeSurfaceRules(Holder<DimensionType> dimensionType, ResourceKey<LevelStem> levelResourceKey, ChunkGenerator chunkGenerator) {
      if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
         NoiseGeneratorSettings generatorSettings = (NoiseGeneratorSettings)noiseBasedChunkGenerator.generatorSettings().value();
         SurfaceRulesManager.RuleCategory ruleCategory = getRuleCategoryForDimension(dimensionType);
         if (ruleCategory != null) {
            if (SurfaceRulesManager.hasRulesForCategory(ruleCategory)) {
               if (!(generatorSettings instanceof IExtendedNoiseGeneratorSettings)) {
                  Citadel.LOGGER.warn("NoiseGeneratorSettings mixin not applied, surface rules will not be injected for: {}", levelResourceKey.location());
               } else {
                  ((IExtendedNoiseGeneratorSettings)generatorSettings).citadel$setRuleCategory(ruleCategory);
                  Citadel.LOGGER.info("Initialized Citadel surface rules for dimension: {} (category: {})", levelResourceKey.location(), ruleCategory);
               }
            }
         }
      }
   }

   private static SurfaceRulesManager.RuleCategory getRuleCategoryForDimension(Holder<DimensionType> dimensionType) {
      DimensionType type = (DimensionType)dimensionType.value();
      if (type.ultraWarm()) {
         return SurfaceRulesManager.RuleCategory.NETHER;
      } else if (!type.hasCeiling() && type.minY() == 0 && type.height() == 256 && !type.natural()) {
         return SurfaceRulesManager.RuleCategory.END;
      } else if (type.natural()) {
         return SurfaceRulesManager.RuleCategory.OVERWORLD;
      } else {
         return type.hasCeiling() ? SurfaceRulesManager.RuleCategory.NETHER : SurfaceRulesManager.RuleCategory.OVERWORLD;
      }
   }
}

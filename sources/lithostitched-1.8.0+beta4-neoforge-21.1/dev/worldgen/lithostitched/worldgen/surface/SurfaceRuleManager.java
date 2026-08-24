package dev.worldgen.lithostitched.worldgen.surface;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.TransientMergedRule;
import dev.worldgen.lithostitched.mixin.common.NoiseGeneratorSettingsAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public class SurfaceRuleManager {
   public static void applySurfaceRules(RegistryAccess registries, Registry<LevelStem> dimensions) {
      List<Entry<ResourceLocation, AddSurfaceRuleModifier>> surfaceRules = ModifierManager.getModifiersOfType(registries, AddSurfaceRuleModifier.CODEC);
      if (!surfaceRules.isEmpty()) {
         HashMap<ResourceLocation, ArrayList<Pair<ResourceLocation, AddSurfaceRuleModifier>>> assignedSurfaceRules = new HashMap<>();

         for (Entry<ResourceLocation, AddSurfaceRuleModifier> entry : surfaceRules) {
            entry.getValue()
               .levels()
               .forEach(level -> assignedSurfaceRules.computeIfAbsent(level.location(), __ -> new ArrayList<>()).add(Pair.of(entry.getKey(), entry.getValue())));
         }

         for (Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
            ResourceLocation location = entry.getKey().location();
            ArrayList<Pair<ResourceLocation, AddSurfaceRuleModifier>> surfaceRulesForKey = assignedSurfaceRules.get(location);
            if (surfaceRulesForKey != null && entry.getValue().generator() instanceof NoiseBasedChunkGenerator generator) {
               NoiseGeneratorSettings settings = (NoiseGeneratorSettings)generator.generatorSettings().value();
               ((NoiseGeneratorSettingsAccessor)settings).setSurfaceRule(buildModdedSurfaceRules(surfaceRulesForKey, settings.surfaceRule()));
               Lithostitched.debug("Applied {} surface rule additions for '{}' dimension", surfaceRulesForKey.size(), location);
            }
         }
      }
   }

   private static RuleSource buildModdedSurfaceRules(ArrayList<Pair<ResourceLocation, AddSurfaceRuleModifier>> surfaceInjections, RuleSource original) {
      List<RuleSource> additions = new ArrayList<>();
      surfaceInjections.sort(Comparator.comparingInt(pair -> ((AddSurfaceRuleModifier)pair.getSecond()).priority()));
      surfaceInjections.forEach(pair -> {
         if (((AddSurfaceRuleModifier)pair.getSecond()).injectionType() == InjectionType.PREPEND) {
            additions.add(((AddSurfaceRuleModifier)pair.getSecond()).surfaceRule());
         }
      });
      additions.add(original);
      surfaceInjections.forEach(pair -> {
         if (((AddSurfaceRuleModifier)pair.getSecond()).injectionType() == InjectionType.APPEND) {
            additions.add(((AddSurfaceRuleModifier)pair.getSecond()).surfaceRule());
         }
      });
      if (original instanceof TransientMergedRule transientMerged) {
         transientMerged.rules().addAll(additions);
         return original;
      } else {
         return new TransientMergedRule(additions, original);
      }
   }
}

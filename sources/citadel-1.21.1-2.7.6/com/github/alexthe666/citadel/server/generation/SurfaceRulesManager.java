package com.github.alexthe666.citadel.server.generation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public class SurfaceRulesManager {
   private static final List<RuleSource> OVERWORLD_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> NETHER_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> END_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> CAVE_REGISTRY = new ArrayList<>();

   public static void registerOverworldSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerOverworldSurfaceRule(RuleSource rule) {
      OVERWORLD_REGISTRY.add(rule);
   }

   public static void registerNetherSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerNetherSurfaceRule(RuleSource rule) {
      NETHER_REGISTRY.add(rule);
   }

   public static void registerEndSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerEndSurfaceRule(RuleSource rule) {
      END_REGISTRY.add(rule);
   }

   public static void registerCaveSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerCaveSurfaceRule(RuleSource rule) {
      CAVE_REGISTRY.add(rule);
   }

   public static RuleSource mergeRules(RuleSource prev, List<RuleSource> toMerge) {
      Builder<RuleSource> builder = ImmutableList.builder();
      builder.add(prev);
      builder.addAll(toMerge);
      return SurfaceRules.sequence((RuleSource[])builder.build().toArray(RuleSource[]::new));
   }

   public static RuleSource mergeOverworldRules(RuleSource rulesIn) {
      return mergeRules(rulesIn, OVERWORLD_REGISTRY);
   }

   public static RuleSource mergeNetherRules(RuleSource rulesIn) {
      return mergeRules(rulesIn, NETHER_REGISTRY);
   }

   public static RuleSource mergeEndRules(RuleSource rulesIn) {
      return mergeRules(rulesIn, END_REGISTRY);
   }

   public static RuleSource mergeRulesForCategory(SurfaceRulesManager.RuleCategory category, RuleSource rulesIn) {
      return switch (category) {
         case OVERWORLD -> mergeOverworldRules(rulesIn);
         case NETHER -> mergeNetherRules(rulesIn);
         case END -> mergeEndRules(rulesIn);
      };
   }

   public static boolean hasRulesForCategory(SurfaceRulesManager.RuleCategory category) {
      return switch (category) {
         case OVERWORLD -> !OVERWORLD_REGISTRY.isEmpty();
         case NETHER -> !NETHER_REGISTRY.isEmpty();
         case END -> !END_REGISTRY.isEmpty();
      };
   }

   public static enum RuleCategory {
      OVERWORLD,
      NETHER,
      END;
   }
}

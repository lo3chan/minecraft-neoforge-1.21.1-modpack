package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import dev.worldgen.lithostitched.impl.predicate.AllOfPredicate;
import dev.worldgen.lithostitched.impl.predicate.AnyOfPredicate;
import dev.worldgen.lithostitched.impl.predicate.LoaderPredicate;
import dev.worldgen.lithostitched.impl.predicate.ModLoadedPredicate;
import dev.worldgen.lithostitched.impl.predicate.NotPredicate;
import dev.worldgen.lithostitched.impl.predicate.PackFormatPredicate;
import dev.worldgen.lithostitched.impl.predicate.TruePredicate;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.BaseBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.RepeatingBand;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.band.WrappedBand;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.AddPoints;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.DispatchAlternateLayout;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ForcePlacement;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplaceFully;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplacePartially;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.AxisDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CeilDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CosDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.FastNoiseDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.FloorDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.MixDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.SelectDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.ShiftDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.SinDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.SqrtDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.WrappedMarkerDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.fastnoise.CellularNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.fastnoise.PerlinNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.fastnoise.SimplexNoiseType;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddBiomeSpawnsModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddFeaturesModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddSpawnCostsModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.RemoveBiomeSpawnsModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.RemoveFeaturesModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ReplaceClimateModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ReplaceEffectsModifier;
import dev.worldgen.lithostitched.impl.worldgen.processor.ApplyRandomStructureProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.BlockSwapStructureProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.ConditionProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.DiscardInputProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.ReferenceStructureProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.ScheduleTickProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.SetBlockProcessor;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.AllOfCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.AnyOfCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.BiomeCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.SampleDensityCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.SlopeCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.BandlandsRule;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.ReferenceRule;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.TransientMergedRule;
import dev.worldgen.lithostitched.platform.neoforge.LithostitchedRegistrations;
import dev.worldgen.lithostitched.platform.neoforge.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.blockpredicate.BlockStatePredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.GridPredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.InStructurePredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.MatchingBiomesPredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.MultipleOfPredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.OffsetPredicate;
import dev.worldgen.lithostitched.worldgen.blockpredicate.RandomChancePredicate;
import dev.worldgen.lithostitched.worldgen.feature.CompositeFeature;
import dev.worldgen.lithostitched.worldgen.feature.DungeonFeature;
import dev.worldgen.lithostitched.worldgen.feature.LargeDripstoneFeature;
import dev.worldgen.lithostitched.worldgen.feature.OreFeature;
import dev.worldgen.lithostitched.worldgen.feature.SelectFeature;
import dev.worldgen.lithostitched.worldgen.feature.SimplePlacedFeature;
import dev.worldgen.lithostitched.worldgen.feature.StructureTemplateFeature;
import dev.worldgen.lithostitched.worldgen.feature.VinesFeature;
import dev.worldgen.lithostitched.worldgen.feature.WeightedSelectorFeature;
import dev.worldgen.lithostitched.worldgen.feature.WellFeature;
import dev.worldgen.lithostitched.worldgen.modifier.AddProcessorListProcessorsModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddStructureSetEntriesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddStructureTemplatesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddSurfaceRuleModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddTemplatePoolElementsModifier;
import dev.worldgen.lithostitched.worldgen.modifier.NoOpModifier;
import dev.worldgen.lithostitched.worldgen.modifier.RemoveStructureSetEntriesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.SetPoolAliasesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.SetPoolElementProcessorsModifier;
import dev.worldgen.lithostitched.worldgen.modifier.SetStructureSpawnConditionModifier;
import dev.worldgen.lithostitched.worldgen.modifier.StackFeatureModifier;
import dev.worldgen.lithostitched.worldgen.modifier.WrapDensityFunctionModifier;
import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier;
import dev.worldgen.lithostitched.worldgen.modifier.internal.CompileRawTemplatesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.internal.RereferenceNoiseSettingsModifier;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.AnyOfPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.GridPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.HeightFilterPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.InBiomePlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.MultipleOfPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.NotPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.OffsetPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.SampleDensityPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.SampleNoiseRouterPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.TruePlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementmodifier.ConditionPlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.NoiseSlopePlacement;
import dev.worldgen.lithostitched.worldgen.placementmodifier.OffsetPlacement;
import dev.worldgen.lithostitched.worldgen.poolalias.RandomEntries;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.LithostitchedFeaturePoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.legacy.GuaranteedPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.legacy.LimitedPoolElement;
import dev.worldgen.lithostitched.worldgen.processor.condition.AllOf;
import dev.worldgen.lithostitched.worldgen.processor.condition.AnyOf;
import dev.worldgen.lithostitched.worldgen.processor.condition.MatchingBiomes;
import dev.worldgen.lithostitched.worldgen.processor.condition.MatchingBlocks;
import dev.worldgen.lithostitched.worldgen.processor.condition.Not;
import dev.worldgen.lithostitched.worldgen.processor.condition.Position;
import dev.worldgen.lithostitched.worldgen.processor.condition.RandomChance;
import dev.worldgen.lithostitched.worldgen.processor.condition.True;
import dev.worldgen.lithostitched.worldgen.stateprovider.RandomBlockProvider;
import dev.worldgen.lithostitched.worldgen.stateprovider.WeightedProvider;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class LithostitchedBuiltInRegistries {
   public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = create(LithostitchedRegistries.MODIFIER_TYPE);
   public static final Registry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = create(LithostitchedRegistries.PLACEMENT_CONDITION_TYPE);
   public static final Registry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = create(LithostitchedRegistries.PROCESSOR_CONDITION_TYPE);
   public static final Registry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = create(LithostitchedRegistries.BANDLANDS_BAND_TYPE);
   public static final Registry<MapCodec<? extends BiomeInjector>> BIOME_INJECTOR_TYPE = create(LithostitchedRegistries.BIOME_INJECTOR_TYPE);
   public static final Registry<MapCodec<? extends FastNoiseConfig>> FAST_NOISE_CONFIG_TYPE = create(LithostitchedRegistries.FAST_NOISE_CONFIG_TYPE);
   public static final Registry<MapCodec<? extends LoadPredicate>> LOAD_PREDICATE_TYPE = create(LithostitchedRegistries.LOAD_PREDICATE_TYPE);

   private static <T> Registry<T> create(ResourceKey<Registry<T>> key) {
      DeferredRegister<T> register = LithostitchedRegistrations.createDeferredRegister(key);
      return register.makeRegistry(b -> {});
   }

   public static void init() {
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.BANDLANDS, Bandlands.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.BIOME_INJECTOR, BiomeInjector.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.REGION, Region.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.SURFACE_RULE, RuleSource.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.TEMPLATE_LIST, TemplateList.CODEC);
      Lithostitched.REGISTRAR.registerRegistry(LithostitchedRegistries.WORLDGEN_MODIFIER, WorldgenModifier.CODEC);
      LithostitchedRegistrar.register(
         BANDLANDS_BAND_TYPE,
         Map.ofEntries(Map.entry("base", BaseBand.CODEC), Map.entry("repeating", RepeatingBand.CODEC), Map.entry("wrapped", WrappedBand.CODEC))
      );
      LithostitchedRegistrar.register(
         BIOME_INJECTOR_TYPE,
         Map.ofEntries(
            Map.entry("add_points", AddPoints.CODEC),
            Map.entry("dispatch_alternate_layout", DispatchAlternateLayout.CODEC),
            Map.entry("force_placement", ForcePlacement.CODEC),
            Map.entry("replace_fully", ReplaceFully.CODEC),
            Map.entry("replace_partially", ReplacePartially.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         MODIFIER_TYPE,
         Map.ofEntries(
            Map.entry("internal/compile_raw_templates", CompileRawTemplatesModifier.CODEC),
            Map.entry("internal/rereference_noise_settings", RereferenceNoiseSettingsModifier.CODEC),
            Map.entry("add_biome_spawns", AddBiomeSpawnsModifier.CODEC),
            Map.entry("add_features", AddFeaturesModifier.CODEC),
            Map.entry("add_processor_list_processors", AddProcessorListProcessorsModifier.CODEC),
            Map.entry("add_spawn_costs", AddSpawnCostsModifier.CODEC),
            Map.entry("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC),
            Map.entry("add_structure_templates", AddStructureTemplatesModifier.CODEC),
            Map.entry("add_surface_rule", AddSurfaceRuleModifier.CODEC),
            Map.entry("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC),
            Map.entry("no_op", NoOpModifier.CODEC),
            Map.entry("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC),
            Map.entry("remove_features", RemoveFeaturesModifier.CODEC),
            Map.entry("remove_structure_set_entries", RemoveStructureSetEntriesModifier.CODEC),
            Map.entry("replace_climate", ReplaceClimateModifier.CODEC),
            Map.entry("replace_effects", ReplaceEffectsModifier.CODEC),
            Map.entry("set_pool_aliases", SetPoolAliasesModifier.CODEC),
            Map.entry("set_pool_element_processors", SetPoolElementProcessorsModifier.CODEC),
            Map.entry("set_structure_spawn_condition", SetStructureSpawnConditionModifier.CODEC),
            Map.entry("stack_feature", StackFeatureModifier.CODEC),
            Map.entry("wrap_density_function", WrapDensityFunctionModifier.CODEC),
            Map.entry("wrap_noise_router", WrapNoiseRouterModifier.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         LOAD_PREDICATE_TYPE,
         Map.ofEntries(
            Map.entry("all_of", AllOfPredicate.CODEC),
            Map.entry("any_of", AnyOfPredicate.CODEC),
            Map.entry("loader", LoaderPredicate.CODEC),
            Map.entry("mod_loaded", ModLoadedPredicate.CODEC),
            Map.entry("not", NotPredicate.CODEC),
            Map.entry("pack_format", PackFormatPredicate.CODEC),
            Map.entry("true", TruePredicate.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         PLACEMENT_CONDITION_TYPE,
         Map.ofEntries(
            Map.entry("any_of", AnyOfPlacementCondition.CODEC),
            Map.entry("all_of", AllOfPlacementCondition.CODEC),
            Map.entry("grid", GridPlacementCondition.CODEC),
            Map.entry("height_filter", HeightFilterPlacementCondition.CODEC),
            Map.entry("in_biome", InBiomePlacementCondition.CODEC),
            Map.entry("multiple_of", MultipleOfPlacementCondition.CODEC),
            Map.entry("not", NotPlacementCondition.CODEC),
            Map.entry("offset", OffsetPlacementCondition.CODEC),
            Map.entry("sample_density", SampleDensityPlacementCondition.CODEC),
            Map.entry("sample_noise_router", SampleNoiseRouterPlacementCondition.CODEC),
            Map.entry("true", TruePlacementCondition.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         PROCESSOR_CONDITION_TYPE,
         Map.ofEntries(
            Map.entry("all_of", AllOf.CODEC),
            Map.entry("any_of", AnyOf.CODEC),
            Map.entry("matching_biomes", MatchingBiomes.CODEC),
            Map.entry("matching_blocks", MatchingBlocks.CODEC),
            Map.entry("not", Not.CODEC),
            Map.entry("position", Position.CODEC),
            Map.entry("random_chance", RandomChance.CODEC),
            Map.entry("true", True.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         FAST_NOISE_CONFIG_TYPE,
         Map.ofEntries(Map.entry("cellular", CellularNoiseType.CODEC), Map.entry("perlin", PerlinNoiseType.CODEC), Map.entry("simplex", SimplexNoiseType.CODEC))
      );
      LithostitchedRegistrar.register(BuiltInRegistries.BIOME_SOURCE, Map.ofEntries(Map.entry("injector", InjectorBiomeSource.CODEC)));
      LithostitchedRegistrar.register(
         BuiltInRegistries.BLOCK_PREDICATE_TYPE,
         Map.ofEntries(
            Map.entry("block_state", BlockStatePredicate.TYPE),
            Map.entry("grid", GridPredicate.TYPE),
            Map.entry("in_structure", InStructurePredicate.TYPE),
            Map.entry("matching_biomes", MatchingBiomesPredicate.TYPE),
            Map.entry("multiple_of", MultipleOfPredicate.TYPE),
            Map.entry("offset", OffsetPredicate.TYPE),
            Map.entry("random_chance", RandomChancePredicate.TYPE)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE,
         Map.ofEntries(Map.entry("weighted", WeightedProvider.TYPE), Map.entry("random_block", RandomBlockProvider.TYPE))
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.DENSITY_FUNCTION_TYPE,
         Map.ofEntries(
            Map.entry("internal/merged", MergedDensityFunction.CODEC.codec()),
            Map.entry("wrapped_marker", WrappedMarkerDensityFunction.CODEC.codec()),
            Map.entry("original_marker", OriginalMarkerDensityFunction.CODEC.codec()),
            Map.entry("fast_noise", FastNoiseDensityFunction.CODEC.codec()),
            Map.entry("axis", AxisDensityFunction.DATA_CODEC),
            Map.entry("ceil", CeilDensityFunction.DATA_CODEC),
            Map.entry("cos", CosDensityFunction.DATA_CODEC),
            Map.entry("floor", FloorDensityFunction.DATA_CODEC),
            Map.entry("mix", MixDensityFunction.DATA_CODEC),
            Map.entry("select", SelectDensityFunction.DATA_CODEC),
            Map.entry("shift", ShiftDensityFunction.DATA_CODEC),
            Map.entry("sin", SinDensityFunction.DATA_CODEC),
            Map.entry("sqrt", SqrtDensityFunction.DATA_CODEC)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.FEATURE,
         Map.ofEntries(
            Map.entry("composite", CompositeFeature.FEATURE),
            Map.entry("dungeon", DungeonFeature.FEATURE),
            Map.entry("large_dripstone", LargeDripstoneFeature.FEATURE),
            Map.entry("ore", OreFeature.FEATURE),
            Map.entry("placed", SimplePlacedFeature.FEATURE),
            Map.entry("select", SelectFeature.FEATURE),
            Map.entry("structure_template", StructureTemplateFeature.FEATURE),
            Map.entry("weighted_selector", WeightedSelectorFeature.FEATURE),
            Map.entry("well", WellFeature.FEATURE),
            Map.entry("vines", VinesFeature.FEATURE)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.MATERIAL_RULE,
         Map.ofEntries(
            Map.entry("transient_merged", TransientMergedRule.CODEC), Map.entry("bandlands", BandlandsRule.CODEC), Map.entry("reference", ReferenceRule.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.MATERIAL_CONDITION,
         Map.ofEntries(
            Map.entry("all_of", AllOfCondition.CODEC),
            Map.entry("any_of", AnyOfCondition.CODEC),
            Map.entry("biome", BiomeCondition.CODEC),
            Map.entry("slope", SlopeCondition.CODEC),
            Map.entry("sample_density", SampleDensityCondition.CODEC)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
         Map.ofEntries(
            Map.entry("condition", ConditionPlacement.TYPE), Map.entry("noise_slope", NoiseSlopePlacement.TYPE), Map.entry("offset", OffsetPlacement.TYPE)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.STRUCTURE_POOL_ELEMENT,
         Map.ofEntries(
            Map.entry("delegating", DelegatingPoolElement.TYPE),
            Map.entry("guaranteed", GuaranteedPoolElement.TYPE),
            Map.entry("limited", LimitedPoolElement.TYPE)
         )
      );
      LithostitchedRegistrar.register(BuiltInRegistries.POOL_ALIAS_BINDING_TYPE, Map.ofEntries(Map.entry("internal/random_entries", RandomEntries.CODEC)));
      LithostitchedRegistrar.register(
         BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, Map.ofEntries(Map.entry("apply_all", ApplyAll.TYPE), Map.entry("apply_random", ApplyRandom.TYPE))
      );
      LithostitchedRegistrar.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, Map.ofEntries(Map.entry("feature", LithostitchedFeaturePoolElement.TYPE)));
      LithostitchedRegistrar.register(
         BuiltInRegistries.STRUCTURE_PROCESSOR,
         Map.ofEntries(
            Map.entry("internal/unbound_reference", UnboundReferenceProcessor.TYPE),
            Map.entry("apply_random", ApplyRandomStructureProcessor.TYPE),
            Map.entry("block_swap", BlockSwapStructureProcessor.TYPE),
            Map.entry("reference", ReferenceStructureProcessor.TYPE),
            Map.entry("condition", ConditionProcessor.TYPE),
            Map.entry("discard_input", DiscardInputProcessor.TYPE),
            Map.entry("schedule_tick", ScheduleTickProcessor.TYPE),
            Map.entry("set_block", SetBlockProcessor.TYPE)
         )
      );
      LithostitchedRegistrar.register(
         BuiltInRegistries.STRUCTURE_TYPE, Map.ofEntries(Map.entry("delegating", DelegatingStructure.TYPE), Map.entry("jigsaw", AlternateJigsawStructure.TYPE))
      );
      LithostitchedRegistrar.register(NeoForgeRegistries.CONDITION_SERIALIZERS, Map.ofEntries(Map.entry("breaks_seed_parity", BreaksSeedParityCondition.CODEC)));
      LithostitchedRegistrar.register(
         NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS,
         Map.ofEntries(
            Map.entry("replace_climate", LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC),
            Map.entry("replace_effects", LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC),
            Map.entry("add_spawn_costs", LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier.CODEC)
         )
      );
   }
}

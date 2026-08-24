package dev.worldgen.lithostitched.api.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddBiomeSpawnsModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.AddFeaturesModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.RemoveBiomeSpawnsModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.RemoveFeaturesModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ReplaceClimateModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ReplaceEffectsModifier;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import dev.worldgen.lithostitched.worldgen.modifier.AddProcessorListProcessorsModifier;
import dev.worldgen.lithostitched.worldgen.modifier.AddStructureSetEntriesModifier;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public interface WorldgenModifier {
   Codec<WorldgenModifier> CODEC = LithostitchedBuiltInRegistries.MODIFIER_TYPE.byNameCodec().dispatch(WorldgenModifier::codec, Function.identity());
   Integer DEFAULT_PRIORITY = 1000;
   Integer REMOVAL_PRIORITY = 2000;
   MapCodec<Integer> PRIORITY_DEFAULT_CODEC = Codec.INT.optionalFieldOf("priority", DEFAULT_PRIORITY);
   MapCodec<Integer> PRIORITY_REMOVE_CODEC = Codec.INT.optionalFieldOf("priority", REMOVAL_PRIORITY);

   Optional<LoadPredicate> predicate();

   int priority();

   void apply(RegistryAccess var1);

   MapCodec<? extends WorldgenModifier> codec();

   default boolean shouldRecompileSortedFeatures() {
      return false;
   }

   static <T> void resetRegistrationInfo(Registry<T> registry, Holder<T> holder) {
      if (holder.unwrapKey().isPresent()) {
         ResourceKey<T> key = (ResourceKey<T>)holder.unwrapKey().get();
         Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
         knownPackInfo.ifPresent(
            registrationInfo -> ((MappedRegistryAccessor)registry)
               .lithostitched$getRegistrationInfos()
               .put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle()))
         );
      }
   }

   static WorldgenModifier.ModifierBuilder builder() {
      return new WorldgenModifier.ModifierBuilder(Optional.empty());
   }

   static WorldgenModifier.ModifierBuilder builder(LoadPredicate predicate) {
      return new WorldgenModifier.ModifierBuilder(Optional.of(predicate));
   }

   public static class ModifierBuilder {
      private final Optional<LoadPredicate> predicate;
      private Optional<Integer> priority = Optional.empty();

      private ModifierBuilder(Optional<LoadPredicate> predicate) {
         this.predicate = predicate;
      }

      public WorldgenModifier.ModifierBuilder priority(int priority) {
         this.priority = Optional.of(priority);
         return this;
      }

      public WorldgenModifier addBiomeSpawns(Holder<Biome> biome, WeightedSpawnerData... spawns) {
         return new AddBiomeSpawnsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{biome}), List.of(spawns)
         );
      }

      public WorldgenModifier addBiomeSpawns(HolderSet<Biome> biomes, WeightedSpawnerData... spawns) {
         return new AddBiomeSpawnsModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), biomes, List.of(spawns));
      }

      public WorldgenModifier addFeatures(Holder<Biome> biome, Holder<PlacedFeature> feature, Decoration step) {
         return new AddFeaturesModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{biome}),
            HolderSet.direct(new Holder[]{feature}),
            step
         );
      }

      public WorldgenModifier addFeatures(HolderSet<Biome> biomes, Holder<PlacedFeature> feature, Decoration step) {
         return new AddFeaturesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), biomes, HolderSet.direct(new Holder[]{feature}), step
         );
      }

      public WorldgenModifier addFeatures(Holder<Biome> biome, HolderSet<PlacedFeature> features, Decoration step) {
         return new AddFeaturesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{biome}), features, step
         );
      }

      public WorldgenModifier addFeatures(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Decoration step) {
         return new AddFeaturesModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), biomes, features, step);
      }

      public WorldgenModifier addProcessorListProcessors(Holder<StructureProcessorList> list, StructureProcessor... processors) {
         return new AddProcessorListProcessorsModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{list}),
            new StructureProcessorList(List.of(processors))
         );
      }

      public WorldgenModifier addProcessorListProcessors(HolderSet<StructureProcessorList> lists, StructureProcessor... processors) {
         return new AddProcessorListProcessorsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), lists, new StructureProcessorList(List.of(processors))
         );
      }

      public WorldgenModifier addStructureSetEntries(Holder<StructureSet> set, StructureSelectionEntry... entries) {
         return new AddStructureSetEntriesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{set}), List.of(entries)
         );
      }

      public WorldgenModifier addStructureSetEntries(HolderSet<StructureSet> sets, StructureSelectionEntry... entries) {
         return new AddStructureSetEntriesModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), sets, List.of(entries));
      }

      @SafeVarargs
      public final WorldgenModifier addTemplatePoolElements(Holder<StructureTemplatePool> pool, Pair<StructurePoolElement, Integer>... elements) {
         return new AddTemplatePoolElementsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{pool}), List.of(elements)
         );
      }

      @SafeVarargs
      public final WorldgenModifier addTemplatePoolElements(HolderSet<StructureTemplatePool> pools, Pair<StructurePoolElement, Integer>... elements) {
         return new AddTemplatePoolElementsModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), pools, List.of(elements));
      }

      public WorldgenModifier addSurfaceRule(ResourceKey<Level> dimension, InjectionType injectionType, RuleSource ruleSource) {
         return new AddSurfaceRuleModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), List.of(Registries.levelToLevelStem(dimension)), injectionType, ruleSource
         );
      }

      public WorldgenModifier addSurfaceRule(List<ResourceKey<Level>> dimensions, InjectionType injectionType, RuleSource ruleSource) {
         return new AddSurfaceRuleModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            dimensions.stream().<ResourceKey<LevelStem>>map(Registries::levelToLevelStem).toList(),
            injectionType,
            ruleSource
         );
      }

      public WorldgenModifier noop() {
         return new NoOpModifier();
      }

      public WorldgenModifier removeBiomeSpawns(Holder<Biome> biome, Holder<EntityType<?>> mob) {
         return new RemoveBiomeSpawnsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), HolderSet.direct(new Holder[]{biome}), HolderSet.direct(new Holder[]{mob})
         );
      }

      public WorldgenModifier removeBiomeSpawns(HolderSet<Biome> biomes, Holder<EntityType<?>> mob) {
         return new RemoveBiomeSpawnsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), biomes, HolderSet.direct(new Holder[]{mob})
         );
      }

      public WorldgenModifier removeBiomeSpawns(Holder<Biome> biome, HolderSet<EntityType<?>> mobs) {
         return new RemoveBiomeSpawnsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), HolderSet.direct(new Holder[]{biome}), mobs
         );
      }

      public WorldgenModifier removeBiomeSpawns(HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) {
         return new RemoveBiomeSpawnsModifier(this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), biomes, mobs);
      }

      public WorldgenModifier removeFeatures(Holder<Biome> biome, Holder<PlacedFeature> feature, Decoration step) {
         return new RemoveFeaturesModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY),
            HolderSet.direct(new Holder[]{biome}),
            HolderSet.direct(new Holder[]{feature}),
            step
         );
      }

      public WorldgenModifier removeFeatures(HolderSet<Biome> biomes, Holder<PlacedFeature> feature, Decoration step) {
         return new RemoveFeaturesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), biomes, HolderSet.direct(new Holder[]{feature}), step
         );
      }

      public WorldgenModifier removeFeatures(Holder<Biome> biome, HolderSet<PlacedFeature> features, Decoration step) {
         return new RemoveFeaturesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), HolderSet.direct(new Holder[]{biome}), features, step
         );
      }

      public WorldgenModifier removeFeatures(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Decoration step) {
         return new RemoveFeaturesModifier(this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), biomes, features, step);
      }

      @SafeVarargs
      public final WorldgenModifier removeStructureSetEntries(Holder<StructureSet> set, Holder<Structure>... holders) {
         return new RemoveStructureSetEntriesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), HolderSet.direct(new Holder[]{set}), List.of(holders)
         );
      }

      @SafeVarargs
      public final WorldgenModifier removeStructureSetEntries(HolderSet<StructureSet> sets, Holder<Structure>... holders) {
         return new RemoveStructureSetEntriesModifier(this.predicate, this.priority.orElse(WorldgenModifier.REMOVAL_PRIORITY), sets, List.of(holders));
      }

      public WorldgenModifier replaceClimate(Holder<Biome> biome, BiomeClimate climate) {
         return new ReplaceClimateModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{biome}), climate
         );
      }

      public WorldgenModifier replaceClimate(HolderSet<Biome> biomes, BiomeClimate climate) {
         return new ReplaceClimateModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), biomes, climate);
      }

      public WorldgenModifier replaceEffects(Holder<Biome> biome, UnaryOperator<BiomeEffectsBuilder> operator) {
         return new ReplaceEffectsModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{biome}),
            operator.apply(BiomeEffectsBuilder.create()).build()
         );
      }

      public WorldgenModifier replaceEffects(HolderSet<Biome> biomes, UnaryOperator<BiomeEffectsBuilder> operator) {
         return new ReplaceEffectsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), biomes, operator.apply(BiomeEffectsBuilder.create()).build()
         );
      }

      public WorldgenModifier setPoolAliases(Holder<Structure> structure, boolean append, PoolAliasBinding... aliases) {
         return new SetPoolAliasesModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{structure}), List.of(aliases), append
         );
      }

      public WorldgenModifier setPoolAliases(HolderSet<Structure> structures, boolean append, PoolAliasBinding... aliases) {
         return new SetPoolAliasesModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), structures, List.of(aliases), append);
      }

      public WorldgenModifier setPoolElementProcessors(Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append) {
         return new SetPoolElementProcessorsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{pool}), Optional.empty(), list, append
         );
      }

      public WorldgenModifier setPoolElementProcessors(
         Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append, ResourceLocation... ids
      ) {
         return new SetPoolElementProcessorsModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{pool}),
            Optional.of(List.of(ids)),
            list,
            append
         );
      }

      public WorldgenModifier setPoolElementProcessors(HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append) {
         return new SetPoolElementProcessorsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), pools, Optional.empty(), list, append
         );
      }

      public WorldgenModifier setPoolElementProcessors(
         HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append, ResourceLocation... ids
      ) {
         return new SetPoolElementProcessorsModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), pools, Optional.of(List.of(ids)), list, append
         );
      }

      public WorldgenModifier setStructureSpawnCondition(Holder<Structure> structure, PlacementCondition spawnCondition, boolean append) {
         return new SetStructureSpawnConditionModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{structure}), spawnCondition, append
         );
      }

      public WorldgenModifier setStructureSpawnCondition(HolderSet<Structure> structures, PlacementCondition spawnCondition, boolean append) {
         return new SetStructureSpawnConditionModifier(
            this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), structures, spawnCondition, append
         );
      }

      public WorldgenModifier stackFeatures(
         Holder<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType
      ) {
         return new StackFeatureModifier(
            this.predicate,
            this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{baseFeatures}),
            stackedFeature,
            placementType
         );
      }

      public WorldgenModifier stackFeatures(
         HolderSet<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType
      ) {
         return new StackFeatureModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), baseFeatures, stackedFeature, placementType);
      }

      public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, DensityFunction wrapper) {
         return WrapDensityFunctionModifier.create(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), target, Holder.direct(wrapper));
      }

      public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, Holder<DensityFunction> wrapper) {
         return WrapDensityFunctionModifier.create(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), target, wrapper);
      }

      public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, DensityFunction wrapper) {
         return new WrapNoiseRouterModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), dimension, target, Holder.direct(wrapper));
      }

      public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, Holder<DensityFunction> wrapper) {
         return new WrapNoiseRouterModifier(this.predicate, this.priority.orElse(WorldgenModifier.DEFAULT_PRIORITY), dimension, target, wrapper);
      }
   }
}

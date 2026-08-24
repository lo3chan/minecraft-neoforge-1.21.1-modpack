package dev.worldgen.lithostitched.api.worldgen.biomeinjector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.AddPoints;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.DispatchAlternateLayout;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ForcePlacement;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplaceFully;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.ReplacePartially;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.dimension.LevelStem;

public interface BiomeInjector {
   Codec<BiomeInjector> CODEC = LithostitchedBuiltInRegistries.BIOME_INJECTOR_TYPE.byNameCodec().dispatch(BiomeInjector::codec, Function.identity());
   Integer DEFAULT_PRIORITY = 1000;
   MapCodec<ResourceKey<LevelStem>> DIMENSION_CODEC = ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension");
   MapCodec<Integer> PRIORITY_CODEC = Codec.INT.optionalFieldOf("priority", DEFAULT_PRIORITY);

   Optional<LoadPredicate> predicate();

   ResourceKey<LevelStem> dimension();

   int priority();

   List<Holder<Biome>> possibleBiomes();

   default void mapAll(DensityFunctionWrapper noiseHelper) {
   }

   MapCodec<? extends BiomeInjector> codec();

   static BiomeInjector.InjectorBuilder builder(ResourceKey<Level> level) {
      return new BiomeInjector.InjectorBuilder(level, Optional.empty());
   }

   static BiomeInjector.InjectorBuilder builder(ResourceKey<Level> level, LoadPredicate predicate) {
      return new BiomeInjector.InjectorBuilder(level, Optional.of(predicate));
   }

   public static enum ClimateParameter implements StringRepresentable {
      CONTINENTALNESS("continentalness", TargetPoint::continentalness),
      EROSION("erosion", TargetPoint::erosion),
      WEIRDNESS("weirdness", TargetPoint::weirdness),
      HUMIDITY("humidity", TargetPoint::humidity),
      TEMPERATURE("temperature", TargetPoint::temperature),
      DEPTH("depth", TargetPoint::depth);

      public static final Codec<BiomeInjector.ClimateParameter> CODEC = StringRepresentable.fromEnum(BiomeInjector.ClimateParameter::values);
      public final String name;
      public final Function<TargetPoint, Long> getter;

      private ClimateParameter(String name, Function<TargetPoint, Long> getter) {
         this.name = name;
         this.getter = getter;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static class InjectorBuilder {
      private final ResourceKey<LevelStem> level;
      private final Optional<LoadPredicate> predicate;
      private Optional<Integer> priority = Optional.empty();

      private InjectorBuilder(ResourceKey<Level> level, Optional<LoadPredicate> predicate) {
         this.level = Registries.levelToLevelStem(level);
         this.predicate = predicate;
      }

      public BiomeInjector.InjectorBuilder priority(int priority) {
         this.priority = Optional.of(priority);
         return this;
      }

      public BiomeInjector addPoints(ParameterList<Holder<Biome>> points) {
         return new AddPoints(this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), points);
      }

      public BiomeInjector dispatchAlternateLayout(ParameterBuilder parameterBuilder, ParameterList<Holder<Biome>> points) {
         return new DispatchAlternateLayout(this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), parameterBuilder.build(), points);
      }

      public BiomeInjector forcePlacement(Holder<Biome> biome, ParameterBuilder parameterBuilder) {
         return new ForcePlacement(this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), biome, parameterBuilder.build());
      }

      public BiomeInjector replaceFully(Holder<Biome> target, Holder<Biome> replacement) {
         return new ReplaceFully(
            this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), HolderSet.direct(new Holder[]{target}), replacement
         );
      }

      public BiomeInjector replaceFully(HolderSet<Biome> targets, Holder<Biome> replacement) {
         return new ReplaceFully(this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), targets, replacement);
      }

      public BiomeInjector replacePartially(Holder<Biome> target, Holder<Biome> replacement, ParameterBuilder parameterBuilder) {
         return new ReplacePartially(
            this.predicate,
            this.level,
            this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY),
            HolderSet.direct(new Holder[]{target}),
            replacement,
            parameterBuilder.build()
         );
      }

      public BiomeInjector replacePartially(HolderSet<Biome> targets, Holder<Biome> replacement, ParameterBuilder parameterBuilder) {
         return new ReplacePartially(
            this.predicate, this.level, this.priority.orElse(BiomeInjector.DEFAULT_PRIORITY), targets, replacement, parameterBuilder.build()
         );
      }
   }
}

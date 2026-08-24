package dev.worldgen.lithostitched.impl.worldgen.biomeinjector;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.dimension.LevelStem;

public record AddPoints(Optional<LoadPredicate> predicate, ResourceKey<LevelStem> dimension, int priority, ParameterList<Holder<Biome>> points)
   implements BiomeInjector {
   public static final MapCodec<AddPoints> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            LoadPredicate.FIELD_CODEC.forGetter(AddPoints::predicate),
            BiomeInjector.DIMENSION_CODEC.forGetter(AddPoints::dimension),
            BiomeInjector.PRIORITY_CODEC.forGetter(AddPoints::priority),
            ParameterList.codec(Biome.CODEC.fieldOf("biome")).fieldOf("points").forGetter(AddPoints::points)
         )
         .apply(i, AddPoints::new)
   );

   public static void apply(ArrayList<Pair<ParameterPoint, Holder<Biome>>> parameters, List<BiomeInjector> injectors) {
      for (BiomeInjector injector : injectors) {
         parameters.addAll(((AddPoints)injector).points().values());
      }
   }

   @Override
   public List<Holder<Biome>> possibleBiomes() {
      return this.points.values().stream().<Holder<Biome>>map(Pair::getSecond).toList();
   }

   @Override
   public MapCodec<? extends BiomeInjector> codec() {
      return CODEC;
   }
}

package com.iafenvoy.origins.data.condition.builtin.biome;

import com.iafenvoy.origins.data.condition.BiomeCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import org.jetbrains.annotations.NotNull;

public record PrecipitationCondition(Precipitation precipitation) implements BiomeCondition {
   public static final MapCodec<PrecipitationCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Precipitation.CODEC.fieldOf("precipitation").forGetter(PrecipitationCondition::precipitation)).apply(i, PrecipitationCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiomeCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Holder<Biome> biome, @NotNull BlockPos pos) {
      return ((Biome)biome.value()).getPrecipitationAt(pos) == this.precipitation;
   }
}

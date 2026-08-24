package com.iafenvoy.origins.data.condition.builtin.block.meta;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ChanceCondition(double chance) implements BlockCondition {
   public static final MapCodec<ChanceCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Codec.doubleRange(0.0, 1.0).fieldOf("chance").forGetter(ChanceCondition::chance)).apply(i, ChanceCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BlockCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
      return Math.random() < this.chance;
   }
}

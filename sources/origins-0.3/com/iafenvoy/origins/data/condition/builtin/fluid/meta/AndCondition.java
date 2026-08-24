package com.iafenvoy.origins.data.condition.builtin.fluid.meta;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record AndCondition(List<FluidCondition> conditions) implements FluidCondition {
   public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(FluidCondition.CODEC.listOf().fieldOf("conditions").forGetter(AndCondition::conditions)).apply(i, AndCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends FluidCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull FluidState state) {
      return this.conditions.stream().allMatch(x -> x.test(state));
   }
}

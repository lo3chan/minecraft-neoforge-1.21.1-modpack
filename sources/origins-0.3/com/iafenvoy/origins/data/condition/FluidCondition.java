package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public interface FluidCondition {
   Codec<FluidCondition> CODEC = DefaultedCodec.registryDispatch(
      ConditionRegistries.FLUID_CONDITION, FluidCondition::codec, Function.identity(), () -> AlwaysTrueCondition.INSTANCE
   );

   static MapCodec<FluidCondition> optionalCodec(String name) {
      return CODEC.optionalFieldOf(name, AlwaysTrueCondition.INSTANCE);
   }

   @NotNull
   MapCodec<? extends FluidCondition> codec();

   boolean test(@NotNull FluidState var1);
}

package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public final class EmptyPower extends Power {
   public static final MapCodec<EmptyPower> CODEC = MapCodec.unit(EmptyPower::new);

   public EmptyPower() {
      super(new Power.BaseSettings(Optional.empty(), Optional.empty(), false, AlwaysTrueCondition.INSTANCE, 0, List.of()));
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

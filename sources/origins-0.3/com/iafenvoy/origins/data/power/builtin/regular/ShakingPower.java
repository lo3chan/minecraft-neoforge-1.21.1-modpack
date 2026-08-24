package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ShakingPower extends Power {
   public static final MapCodec<ShakingPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings)).apply(i, ShakingPower::new)
   );

   public ShakingPower(Power.BaseSettings settings) {
      super(settings);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

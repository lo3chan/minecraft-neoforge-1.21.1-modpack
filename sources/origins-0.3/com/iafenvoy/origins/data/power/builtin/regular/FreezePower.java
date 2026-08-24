package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class FreezePower extends Power {
   public static final MapCodec<FreezePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings)).apply(i, FreezePower::new)
   );

   public FreezePower(Power.BaseSettings settings) {
      super(settings);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

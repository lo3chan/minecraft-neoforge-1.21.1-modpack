package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventSprintingPower extends Power {
   public static final MapCodec<PreventSprintingPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings)).apply(i, PreventSprintingPower::new)
   );

   public PreventSprintingPower(Power.BaseSettings settings) {
      super(settings);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

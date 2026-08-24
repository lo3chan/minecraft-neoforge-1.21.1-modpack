package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventBlockSelectionPower extends Power {
   public static final MapCodec<PreventBlockSelectionPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(PreventBlockSelectionPower::getBlockCondition)
         )
         .apply(i, PreventBlockSelectionPower::new)
   );
   private final BlockCondition blockCondition;

   public PreventBlockSelectionPower(Power.BaseSettings settings, BlockCondition blockCondition) {
      super(settings);
      this.blockCondition = blockCondition;
   }

   public BlockCondition getBlockCondition() {
      return this.blockCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

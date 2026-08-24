package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data._common.BlockPlaceSettings;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOnBlockPlacePower extends Power {
   public static final MapCodec<ActionOnBlockPlacePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings), BlockPlaceSettings.CODEC.forGetter(ActionOnBlockPlacePower::getBlockPlaceSettings))
         .apply(i, ActionOnBlockPlacePower::new)
   );
   private final BlockPlaceSettings blockPlaceSettings;

   public ActionOnBlockPlacePower(Power.BaseSettings settings, BlockPlaceSettings blockPlaceSettings) {
      super(settings);
      this.blockPlaceSettings = blockPlaceSettings;
   }

   public BlockPlaceSettings getBlockPlaceSettings() {
      return this.blockPlaceSettings;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}

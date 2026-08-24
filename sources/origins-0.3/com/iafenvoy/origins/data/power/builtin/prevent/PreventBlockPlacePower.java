package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data._common.BlockPlaceSettings;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PreventBlockPlacePower extends Power {
   public static final MapCodec<PreventBlockPlacePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings), BlockPlaceSettings.CODEC.forGetter(PreventBlockPlacePower::getBlockPlaceSettings))
         .apply(i, PreventBlockPlacePower::new)
   );
   private final BlockPlaceSettings blockPlaceSettings;

   public PreventBlockPlacePower(Power.BaseSettings settings, BlockPlaceSettings blockPlaceSettings) {
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

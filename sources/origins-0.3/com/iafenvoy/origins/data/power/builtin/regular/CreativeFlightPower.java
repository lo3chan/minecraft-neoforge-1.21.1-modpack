package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CreativeFlightPower extends Power {
   public static final MapCodec<CreativeFlightPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings)).apply(i, CreativeFlightPower::new)
   );

   public CreativeFlightPower(Power.BaseSettings settings) {
      super(settings);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void active(@NotNull OriginDataHolder holder) {
      if (holder.getEntity() instanceof Player player) {
         player.getAbilities().mayfly = true;
      }
   }

   @Override
   public void inactive(@NotNull OriginDataHolder holder) {
      if (holder.getEntity() instanceof Player player) {
         player.getAbilities().mayfly = false;
      }
   }
}

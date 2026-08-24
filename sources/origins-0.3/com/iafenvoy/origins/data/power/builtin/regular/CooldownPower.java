package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class CooldownPower extends HasCooldownPower {
   public static final MapCodec<CooldownPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings), HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown))
         .apply(i, CooldownPower::new)
   );

   public CooldownPower(Power.BaseSettings settings, HasCooldownPower.CooldownSettings cooldown) {
      super(settings, cooldown);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public boolean isActive(OriginDataHolder holder) {
      return this.getCooldownComponent(holder).canUse();
   }
}

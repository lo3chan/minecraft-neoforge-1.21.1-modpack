package com.aetherteam.aether.effect;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RemedyEffect extends MobEffect {
   private int effectDuration;

   public RemedyEffect() {
      super(MobEffectCategory.BENEFICIAL, 5031241);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (livingEntity instanceof Player player && player.level().isClientSide()) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if (data.getRemedyStartDuration() <= 0) {
            data.setSynched(player.getId(), Direction.SERVER, "setRemedyStartDuration", this.effectDuration);
         }
      }

      if (livingEntity.hasEffect(AetherEffects.INEBRIATION)) {
         livingEntity.removeEffect(AetherEffects.INEBRIATION);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      this.effectDuration = duration;
      return true;
   }
}

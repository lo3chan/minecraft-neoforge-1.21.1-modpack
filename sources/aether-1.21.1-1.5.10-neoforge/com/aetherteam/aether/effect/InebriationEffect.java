package com.aetherteam.aether.effect;

import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class InebriationEffect extends MobEffect {
   private int effectDuration;
   private double rotationDirection;
   private double motionDirection;

   public InebriationEffect() {
      super(MobEffectCategory.HARMFUL, 5319035);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (this.effectDuration % 50 == 0) {
         livingEntity.hurt(AetherDamageTypes.damageSource(livingEntity.level(), AetherDamageTypes.INEBRIATION), 1.0F);
      }

      this.distractEntity(livingEntity);
      return true;
   }

   private void distractEntity(LivingEntity livingEntity) {
      if (!(livingEntity instanceof Player player && (player.isCreative() || player.isSpectator()) && player.getAbilities().flying)) {
         double gaussian = livingEntity.level().getRandom().nextGaussian();
         double newMotionDirection = 0.1 * gaussian;
         double newRotationDirection = 0.7853981633974483 * gaussian;
         this.motionDirection = 0.2 * newMotionDirection + 0.8 * this.motionDirection;
         livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(this.motionDirection, 0.0, this.motionDirection));
         this.rotationDirection = 0.125 * newRotationDirection + 0.875 * this.rotationDirection;
         livingEntity.setYRot((float)(livingEntity.getYRot() + this.rotationDirection));
         livingEntity.setXRot((float)(livingEntity.getXRot() + this.rotationDirection));
      }

      if (livingEntity.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new ItemParticleOption(ParticleTypes.ITEM, Items.PURPLE_DYE.getDefaultInstance()),
            livingEntity.getX(),
            livingEntity.getY() + livingEntity.getBbHeight() * 0.8,
            livingEntity.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      this.effectDuration = duration;
      return true;
   }
}

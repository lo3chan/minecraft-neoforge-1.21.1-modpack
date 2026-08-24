package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EffectSunbird extends MobEffect {
   public final boolean curse;

   public EffectSunbird(boolean curse) {
      super(curse ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, 16771769);
      this.curse = curse;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (this.curse) {
         if (entity.isFallFlying() && entity instanceof Player) {
            ((Player)entity).stopFallFlying();
         }

         boolean forceFall = false;
         if (entity instanceof Player player && (!player.isCreative() || !player.getAbilities().flying)) {
            forceFall = true;
         }

         if ((forceFall || !(entity instanceof Player)) && !entity.onGround()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -0.20000000298023224, 0.0));
         }
      } else {
         entity.fallDistance = 0.0F;
         if (entity.isFallFlying()) {
            if (entity.getXRot() < -10.0F) {
               float pitchMulti = Math.abs(entity.getXRot()) / 90.0F;
               entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.02 + pitchMulti * 0.02, 0.0));
            }
         } else if (!entity.onGround() && !entity.isCrouching()) {
            Vec3 vector3d = entity.getDeltaMovement();
            if (vector3d.y < 0.0) {
               entity.setDeltaMovement(vector3d.multiply(1.0, 0.6, 1.0));
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return this.curse ? "alexsmobs.potion.sunbird_curse" : "alexsmobs.potion.sunbird_blessing";
   }
}

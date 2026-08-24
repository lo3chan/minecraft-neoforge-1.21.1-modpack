package net.astralya.hexalia.effect.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SlimewalkerEffect extends MobEffect {
   public SlimewalkerEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (livingEntity.onGround()) {
         livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, false, false, true));
      }

      if (livingEntity.onGround() && livingEntity.isSuppressingBounce()) {
         Vec3 movement = livingEntity.getDeltaMovement();
         livingEntity.setDeltaMovement(movement.x, 1.0, movement.z);
         livingEntity.hasImpulse = true;
         livingEntity.playSound(SoundEvents.SLIME_JUMP, 1.0F, 1.0F);

         for (int i = 0; i < 8; i++) {
            float angle = livingEntity.level().random.nextFloat() * 6.2831855F;
            float distance = livingEntity.level().random.nextFloat() * 0.5F + 0.5F;
            float x = Mth.sin(angle) * 0.5F * distance;
            float z = Mth.cos(angle) * 0.5F * distance;
            livingEntity.level().addParticle(ParticleTypes.ITEM_SLIME, livingEntity.getX() + x, livingEntity.getY(), livingEntity.getZ() + z, 0.0, 0.0, 0.0);
         }

         return true;
      } else {
         livingEntity.fallDistance = 0.0F;
         return super.applyEffectTick(livingEntity, amplifier);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}

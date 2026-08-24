package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.BoneBarrierKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BoneBarrierMobEffect extends MobEffect {
   public BoneBarrierMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -6119795);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      BoneBarrierKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

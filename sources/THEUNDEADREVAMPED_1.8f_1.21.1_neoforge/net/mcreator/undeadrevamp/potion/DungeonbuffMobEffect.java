package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.DungeonbuffOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DungeonbuffMobEffect extends MobEffect {
   public DungeonbuffMobEffect() {
      super(MobEffectCategory.HARMFUL, -16777216);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      DungeonbuffOnEffectActiveTickProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

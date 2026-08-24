package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.GazeOfTerrorKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.GazeOfTerrorPriNalozhieniiEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GazeOfTerrorMobEffect extends MobEffect {
   public GazeOfTerrorMobEffect() {
      super(MobEffectCategory.HARMFUL, -16119285);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      GazeOfTerrorPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      GazeOfTerrorKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}

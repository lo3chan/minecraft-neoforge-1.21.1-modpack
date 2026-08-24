package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class GluttonFishPriGibieliOtEtoiSushchnostiDrughoiProcedure {
   public static void execute(Entity sourceentity) {
      if (sourceentity != null) {
         if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 3600, 0, false, false));
         }
      }
   }
}

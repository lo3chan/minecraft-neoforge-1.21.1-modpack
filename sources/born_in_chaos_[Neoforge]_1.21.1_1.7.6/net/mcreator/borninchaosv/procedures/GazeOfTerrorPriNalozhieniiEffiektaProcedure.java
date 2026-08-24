package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class GazeOfTerrorPriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(
               new MobEffectInstance(
                  MobEffects.DARKNESS,
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.GAZE_OF_TERROR)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.GAZE_OF_TERROR).getDuration()
                     : 0,
                  0,
                  false,
                  false
               )
            );
         }
      }
   }
}

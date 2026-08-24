package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.NeocrorinesEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DungeonbuffOnEffectActiveTickProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(entity instanceof NeocrorinesEntity)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false));
            }
         }
      }
   }
}

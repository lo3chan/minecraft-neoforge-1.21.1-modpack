package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.mcreator.undeadrevamp.entity.ThegliterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ThespitterEntityIsHurtxProcedure {
   public static void execute(Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity.getPersistentData().getDouble("spitter_hid") < 100.0) {
            entity.getPersistentData().putDouble("spitter_hid", 100.0);
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST);
            }
         }

         if (sourceentity instanceof LivingEntity && !entity.onGround() && Math.random() < 0.15) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 45, 0, false, false));
            }

            if (entity instanceof ThegliterEntity) {
               ((ThegliterEntity)entity).setAnimation("charge");
            }

            if (entity instanceof TheMoonflowerEntity) {
               ((TheMoonflowerEntity)entity).setAnimation("charge2");
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 20, false, false));
            }
         }
      }
   }
}

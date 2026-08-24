package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThespectreEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThespectreOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.07
            && !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.BROKENTANK))
            && world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY)
            && entity instanceof ThespectreEntity
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.LUCK, 100, 0, false, false));
         }

         if (!world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl6
               && _lvl6.isDay()
               && !entity.isInWaterRainOrBubble()
               && !entity.isInWaterOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if ((world.getLevelData().isRaining() || world.getLevelData().isThundering()) && !world.isClientSide()) {
               entity.clearFire();
            }
         }

         if (Math.random() < 0.7
            && !(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(UndeadRevamp2ModMobEffects.BROKENTANK))
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
         }

         if (Math.random() < 0.003) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 75, 3, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false));
            }
         }

         if (entity.getPersistentData().getBoolean("coldarm") && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 75, 1, false, false));
         }
      }
   }
}

package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class PumpkinPistol2KoghdaSnariadPopadaietVSushchnostProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 240, 0));
         }

         if (world instanceof Level _level && !_level.isClientSide()) {
            _level.explode(null, x, y, z, 1.0F, ExplosionInteraction.NONE);
         }
      }
   }
}

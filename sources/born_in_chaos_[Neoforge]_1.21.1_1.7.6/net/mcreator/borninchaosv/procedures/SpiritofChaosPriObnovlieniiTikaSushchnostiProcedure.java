package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class SpiritofChaosPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.OBSESSION)) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y, z, 3.0F, ExplosionInteraction.NONE);
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y, z, 10, 1.0, 1.0, 1.0, 0.1);
            }
         }
      }
   }
}

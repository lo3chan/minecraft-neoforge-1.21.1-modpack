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

public class PhantomBombEntityPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y + 1.0, z, 1, 0.1, 0.1, 0.1, 0.1);
         }

         if ((
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)
                  ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK).getDuration()
                  : 0
            )
            <= 10) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y, z, 4.0F, ExplosionInteraction.TNT);
            }
         }
      }
   }
}

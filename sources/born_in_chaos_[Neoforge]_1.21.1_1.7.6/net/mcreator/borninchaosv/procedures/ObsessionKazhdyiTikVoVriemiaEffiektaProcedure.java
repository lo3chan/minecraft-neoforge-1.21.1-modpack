package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class ObsessionKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.clearFire();
         if (entity instanceof Player _player) {
            _player.causeFoodExhaustion(0.05F);
         }

         if (entity.getBbHeight() <= 1.0F) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), x, y + 0.2, z, 1, 0.3, 0.3, 0.3, 0.1);
            }

            if (!(entity instanceof Player) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y + 0.2, z, 1, 0.1, 0.1, 0.1, 0.1);
            }
         } else if (entity.getBbHeight() > 1.0F && entity.getBbHeight() <= 1.5) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), x, y + 1.0, z, 1, 0.4, 0.4, 0.4, 0.1);
            }

            if (!(entity instanceof Player) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y + 1.0, z, 1, 0.2, 0.2, 0.2, 0.1);
            }
         } else if (entity.getBbHeight() > 1.5 && entity.getBbHeight() <= 2.0F) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), x, y + 1.0, z, 1, 0.5, 0.5, 0.5, 0.1);
            }

            if (!(entity instanceof Player) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y + 1.5, z, 1, 0.3, 0.3, 0.3, 0.1);
            }
         } else if (entity.getBbHeight() > 2.0F && entity.getBbHeight() <= 4.0F) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), x, y + 1.0, z, 1, 0.6, 0.8, 0.6, 0.1);
            }

            if (!(entity instanceof Player) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y + 2.0, z, 1, 0.5, 0.5, 0.5, 0.1);
            }
         } else if (entity.getBbHeight() > 4.0F) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CHAOSENERGY.get(), x, y + 3.0, z, 1, 0.7, 1.5, 0.7, 0.1);
            }

            if (!(entity instanceof Player) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.OBSESSIONPAR.get(), x, y + 3.0, z, 1, 0.7, 1.5, 0.7, 0.1);
            }
         }
      }
   }
}

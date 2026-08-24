package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class InsipidityKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.REGENERATION);
         }

         if (entity.isInWaterOrBubble() && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.MYIASIS);
         }

         if ((
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.MYIASIS)
                  ? _livEnt.getEffect(BornInChaosV1ModMobEffects.MYIASIS).getDuration()
                  : 0
            )
            <= 20) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.MYIASIS);
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
                  .spawn(_level, BlockPos.containing(x + 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x + 0.5, y + 1.0, z + 0.5, 3, 0.2, 0.2, 0.2, 0.1);
            }
         }
      }
   }
}

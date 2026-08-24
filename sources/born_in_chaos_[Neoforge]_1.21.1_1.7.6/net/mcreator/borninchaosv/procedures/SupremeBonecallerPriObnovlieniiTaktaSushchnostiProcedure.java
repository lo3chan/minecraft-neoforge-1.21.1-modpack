package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SupremeBonecallerPriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 15.0F) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SUPREME_BONESCALLER_STAGE_2.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.totem.use")),
                     SoundSource.NEUTRAL,
                     0.3F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.totem.use")), SoundSource.NEUTRAL, 0.3F, 1.0F, false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y, z, 15, 0.5, 0.5, 0.5, 0.1);
            }
         }
      }
   }
}

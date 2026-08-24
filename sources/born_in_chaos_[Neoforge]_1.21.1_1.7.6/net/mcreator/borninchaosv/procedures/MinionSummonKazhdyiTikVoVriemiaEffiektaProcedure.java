package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.Blocks;

public class MinionSummonKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof KrampusEntity) {
            if (!world.isClientSide() && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.shulker.shoot")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.shulker.shoot")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F,
                     false
                  );
               }
            }

            if (!world.getBlockState(BlockPos.containing(x + 2.0, y, z + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(x + 2.0, y, z + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.KRAMPUS_HENCHMAN.get())
                     .spawn(_levelx, BlockPos.containing(x + 2.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.POOF, x + 2.0, y, z + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
               }
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.MINION_SUMMON);
            }
         }
      }
   }
}

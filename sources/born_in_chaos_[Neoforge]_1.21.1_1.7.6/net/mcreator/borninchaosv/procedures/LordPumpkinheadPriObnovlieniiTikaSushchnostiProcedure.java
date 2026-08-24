package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class LordPumpkinheadPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x, y + 1.6, z, 1, 0.3, 0.3, 0.3, 0.1);
         }

         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.LAVA
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), ((Block)BornInChaosV1ModBlocks.FEL_SOIL.get()).defaultBlockState(), 3);
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 30.0F) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.trident.thunder")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     0.9F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.trident.thunder")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     0.9F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.elder_guardian.ambient")),
                     SoundSource.NEUTRAL,
                     1.6F,
                     0.6F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.elder_guardian.ambient")),
                     SoundSource.NEUTRAL,
                     1.6F,
                     0.6F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y, z, 12, 0.5, 1.0, 0.5, 0.1);
            }

            if (world instanceof ServerLevel _levelxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.LORDS_FELSTEED.get())
                  .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.LORD_PUMPKINHEAD_WITHOUTA_HORSE.get())
                  .spawn(_levelxxx, BlockPos.containing(x, y + 1.3, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_levelxxxx);
               entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x + 2.0, y, z)));
               _levelxxxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxxxx) {
               LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_levelxxxx);
               entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x - 2.0, y, z)));
               _levelxxxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxxxx) {
               LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_levelxxxx);
               entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z + 2.0)));
               _levelxxxx.addFreshEntity(entityToSpawn);
            }

            if (!world.getLevelData().isRaining() || !world.getLevelData().isThundering()) {
               world.getLevelData().setRaining(true);
            }
         } else if (entity.isInWall() || entity.isInLava()) {
            if (Math.random() < 0.5) {
               entity.teleportTo(x + 3.0, y + 3.0, z);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x + 3.0, y + 3.0, z, entity.getYRot(), entity.getXRot());
               }
            } else if (Math.random() < 0.5) {
               entity.teleportTo(x, y + 3.0, z + 3.0);
               if (entity instanceof ServerPlayer _serverPlayer) {
                  _serverPlayer.connection.teleport(x, y + 3.0, z + 3.0, entity.getYRot(), entity.getXRot());
               }
            }
         }
      }
   }
}

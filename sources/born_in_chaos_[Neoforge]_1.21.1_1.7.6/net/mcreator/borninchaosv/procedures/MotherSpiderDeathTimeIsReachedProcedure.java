package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MotherSpiderDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:spider_splash")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:spider_splash")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(), x, y + 1.0, z, 14, 0.5, 0.5, 0.5, 0.2);
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIDER_BLAST.get(), x, y + 1.0, z, 6, 0.7, 0.5, 0.7, 0.1);
         }

         for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 3, 6); index0++) {
            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER.get())
                  .spawn(_levelx, BlockPos.containing(x, y + 1.0, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }
         }

         if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AIR
            || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.CAVE_AIR) {
            world.setBlock(BlockPos.containing(x, y, z), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.AIR
            || world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock() == Blocks.CAVE_AIR) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.AIR
            || world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock() == Blocks.CAVE_AIR) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.AIR
            || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock() == Blocks.CAVE_AIR) {
            world.setBlock(BlockPos.containing(x, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.AIR
            || world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock() == Blocks.CAVE_AIR) {
            world.setBlock(BlockPos.containing(x, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (Math.random() < 0.4
            && (
               world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.AIR
                  || world.getBlockState(BlockPos.containing(x + 1.0, y, z + 1.0)).getBlock() == Blocks.CAVE_AIR
            )) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (Math.random() < 0.4
            && (
               world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.AIR
                  || world.getBlockState(BlockPos.containing(x + 1.0, y, z - 1.0)).getBlock() == Blocks.CAVE_AIR
            )) {
            world.setBlock(BlockPos.containing(x + 1.0, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (Math.random() < 0.4
            && (
               world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.AIR
                  || world.getBlockState(BlockPos.containing(x - 1.0, y, z - 1.0)).getBlock() == Blocks.CAVE_AIR
            )) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z - 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }

         if (Math.random() < 0.4
            && (
               world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.AIR
                  || world.getBlockState(BlockPos.containing(x - 1.0, y, z + 1.0)).getBlock() == Blocks.CAVE_AIR
            )) {
            world.setBlock(BlockPos.containing(x - 1.0, y, z + 1.0), ((Block)BornInChaosV1ModBlocks.COBWEB_COVER.get()).defaultBlockState(), 3);
         }
      }
   }
}

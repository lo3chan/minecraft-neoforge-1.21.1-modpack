package com.yungnickyoung.minecraft.betterendisland.world.util;

import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.mixin.accessor.EndDragonFightAccessor;
import com.yungnickyoung.minecraft.betterendisland.world.IBetterDragonFight;
import com.yungnickyoung.minecraft.betterendisland.world.feature.BetterEndPodiumFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ExitPortalUtils {
   public static void spawnPortal(IBetterDragonFight dragonFight, ServerLevel serverLevel, boolean isActive, boolean isBottomOnly, boolean noCrystalsOverride) {
      boolean useBetterPortal = dragonFight.hasDragonEverSpawned()
         ? BetterEndIslandCommon.CONFIG.spawnCentralTowerOnResummon
         : BetterEndIslandCommon.CONFIG.spawnCentralTowerInitially;
      EndDragonFightAccessor fightAccessor = (EndDragonFightAccessor)dragonFight;
      fightAccessor.setPortalLocation(getAdjustedPortalPos(fightAccessor.getPortalLocation(), serverLevel));
      if (useBetterPortal) {
         BetterEndPodiumFeature endPodiumFeature = new BetterEndPodiumFeature(dragonFight.isFirstExitPortalSpawn(), isBottomOnly, isActive);
         BlockPos spawnPos = fightAccessor.getPortalLocation().below(5);
         endPodiumFeature.place(FeatureConfiguration.NONE, serverLevel, serverLevel.getChunkSource().getGenerator(), RandomSource.create(), spawnPos);
      } else {
         EndPodiumFeature endPodiumFeature = new EndPodiumFeature(isActive);
         BlockPos spawnPos = fightAccessor.getPortalLocation().below(3);
         if (!dragonFight.hasDragonEverSpawned() || !BetterEndIslandCommon.CONFIG.spawnCentralTowerInitially) {
            spawnPos = spawnPos.above(4);
         }

         if (endPodiumFeature.place(FeatureConfiguration.NONE, serverLevel, serverLevel.getChunkSource().getGenerator(), RandomSource.create(), spawnPos)) {
            int $$2 = Mth.positiveCeilDiv(4, 16);
            serverLevel.getChunkSource().chunkMap.waitForLightBeforeSending(new ChunkPos(spawnPos), $$2);
         }

         if (!dragonFight.hasDragonEverSpawned() && !noCrystalsOverride) {
            BlockPos centerPos = spawnPos.above(1);

            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos crystalPos = centerPos.relative(direction, 3);
               EndCrystal crystal = new EndCrystal(serverLevel, crystalPos.getX() + 0.5, crystalPos.getY(), crystalPos.getZ() + 0.5);
               crystal.setShowBottom(false);
               crystal.setInvulnerable(true);
               serverLevel.addFreshEntity(crystal);
            }
         }
      }

      dragonFight.setIsFirstExitPortalSpawn(false);
   }

   public static void spawnPortal(IBetterDragonFight dragonFight, ServerLevel serverLevel, boolean isActive, boolean isBottomOnly) {
      spawnPortal(dragonFight, serverLevel, isActive, isBottomOnly, false);
   }

   private static BlockPos getAdjustedPortalPos(BlockPos portalLocation, ServerLevel serverLevel) {
      BlockPos portalPos = portalLocation;
      if (portalLocation == null || portalLocation.getY() < 5) {
         if (portalLocation == null) {
            BetterEndIslandCommon.LOGGER.info("Portal location is null. Placing manually...");
         } else {
            BetterEndIslandCommon.LOGGER.info("Portal location is too low: {}. Placing manually...", portalLocation.getY());
         }

         portalPos = new BlockPos(0, WorldgenUtils.getSurfacePosAt(serverLevel, 0, 0), 0);

         while (serverLevel.getBlockState(portalPos).is(Blocks.BEDROCK) && portalPos.getY() > serverLevel.getSeaLevel()) {
            portalPos = portalPos.below();
         }

         if (portalPos.getY() < 5) {
            BetterEndIslandCommon.LOGGER.info("Portal was still placed too low! Force placing at y=65...");
            portalPos = new BlockPos(0, 65, 0);
         }
      }

      BetterEndIslandCommon.LOGGER.info("Set the exit portal location to: {}", portalPos);
      return portalPos;
   }
}

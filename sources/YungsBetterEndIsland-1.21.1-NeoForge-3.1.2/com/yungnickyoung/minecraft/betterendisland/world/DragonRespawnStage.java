package com.yungnickyoung.minecraft.betterendisland.world;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.mixin.accessor.EndDragonFightAccessor;
import com.yungnickyoung.minecraft.betterendisland.world.util.ExitPortalUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import org.jetbrains.annotations.NotNull;

public enum DragonRespawnStage implements StringRepresentable {
   START("start") {
      @Override
      public void tick(ServerLevel serverLevel, EndDragonFight dragonFight, List<EndCrystal> summoningCrystals, int phaseTimer) {
         BlockPos beamTargetPos = new BlockPos(0, 128, 0);
         summoningCrystals.forEach(crystal -> crystal.setBeamTarget(beamTargetPos));
         ((IBetterDragonFight)dragonFight).advanceRespawnStage(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS("preparing_to_summon_pillars") {
      @Override
      public void tick(ServerLevel serverLevel, EndDragonFight dragonFight, List<EndCrystal> summoningCrystals, int phaseTimer) {
         int totalPhaseTime = 100;
         if (phaseTimer < totalPhaseTime) {
            if (phaseTimer == 0 || phaseTimer == 50 || phaseTimer == 51 || phaseTimer == 52 || phaseTimer >= 95) {
               DragonRespawnStage.broadcastDragonGrowlSound(serverLevel);
            }
         } else {
            ((IBetterDragonFight)dragonFight).advanceRespawnStage(SUMMONING_PILLARS);
         }
      }
   },
   SUMMONING_PILLARS("summoning_pillars") {
      @Override
      public void tick(ServerLevel serverLevel, EndDragonFight dragonFight, List<EndCrystal> summoningCrystals, int phaseTimer) {
         int ticksPerSpike = 40;
         boolean isFirstTickForSpike = phaseTimer % ticksPerSpike == 0;
         boolean isLastTickForSpike = phaseTimer % ticksPerSpike == 39;
         if (isFirstTickForSpike || isLastTickForSpike) {
            List<EndSpike> allSpikes = SpikeFeature.getSpikesForLevel(serverLevel);
            int spikeIndex = phaseTimer / ticksPerSpike;
            if (spikeIndex < allSpikes.size()) {
               EndSpike spike = allSpikes.get(spikeIndex);
               int pillarHeight = (spike.getHeight() - 73) / 3;
               if (pillarHeight == 10) {
                  pillarHeight = 9;
               }

               ((IEndSpike)spike).setCrystalYOffsetFromPillarHeight(pillarHeight);
               int topY = BetterEndIslandCommon.betterEnd ? 70 : 60;
               int crystalY = topY + ((IEndSpike)spike).getCrystalYOffset() - 1;
               if (isFirstTickForSpike) {
                  for (EndCrystal crystal : summoningCrystals) {
                     crystal.setBeamTarget(new BlockPos(spike.getCenterX(), crystalY, spike.getCenterZ()));
                  }
               } else {
                  serverLevel.explode(null, spike.getCenterX() + 0.5F, crystalY, spike.getCenterZ() + 0.5F, 5.0F, ExplosionInteraction.BLOCK);
                  serverLevel.players()
                     .forEach(
                        player -> {
                           serverLevel.sendParticles(
                              player,
                              ParticleTypes.EXPLOSION_EMITTER,
                              true,
                              spike.getCenterX() - 5.0F,
                              crystalY,
                              spike.getCenterZ() - 5.0F,
                              1,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                           serverLevel.sendParticles(
                              player,
                              ParticleTypes.EXPLOSION_EMITTER,
                              true,
                              spike.getCenterX() - 5.0F,
                              crystalY,
                              spike.getCenterZ() + 5.0F,
                              1,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                           serverLevel.sendParticles(
                              player,
                              ParticleTypes.EXPLOSION_EMITTER,
                              true,
                              spike.getCenterX() + 5.0F,
                              crystalY,
                              spike.getCenterZ() - 5.0F,
                              1,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                           serverLevel.sendParticles(
                              player,
                              ParticleTypes.EXPLOSION_EMITTER,
                              true,
                              spike.getCenterX() + 5.0F,
                              crystalY,
                              spike.getCenterZ() + 5.0F,
                              1,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                           if (player.distanceToSqr(spike.getCenterX(), crystalY, spike.getCenterZ()) > 32.0) {
                              serverLevel.playSound(
                                 null,
                                 new BlockPos(spike.getCenterX(), crystalY, spike.getCenterZ()),
                                 (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(),
                                 SoundSource.NEUTRAL,
                                 24.0F,
                                 1.0F
                              );
                           }
                        }
                     );
                  int resetRadius = 11;
                  int verticalRadius = BetterEndIslandCommon.betterEnd ? 40 : 30;

                  for (BlockPos blockPos : BlockPos.betweenClosed(
                     new BlockPos(spike.getCenterX() - resetRadius, spike.getHeight() - verticalRadius, spike.getCenterZ() - resetRadius),
                     new BlockPos(spike.getCenterX() + resetRadius, spike.getHeight() + verticalRadius, spike.getCenterZ() + resetRadius)
                  )) {
                     if (!serverLevel.getBlockState(blockPos).is(Blocks.END_STONE)) {
                        serverLevel.removeBlock(blockPos, false);
                     }
                  }

                  SpikeConfiguration spikeConfig = new SpikeConfiguration(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE
                     .place(
                        spikeConfig,
                        serverLevel,
                        serverLevel.getChunkSource().getGenerator(),
                        RandomSource.create(),
                        new BlockPos(spike.getCenterX(), 45, spike.getCenterZ())
                     );
               }
            } else if (isFirstTickForSpike) {
               ((IBetterDragonFight)dragonFight).advanceRespawnStage(SUMMONING_DRAGON);
            }
         }
      }
   },
   SUMMONING_DRAGON("summoning_dragon") {
      @Override
      public void tick(ServerLevel serverLevel, EndDragonFight dragonFight, List<EndCrystal> summoningCrystals, int phaseTimer) {
         int totalPhaseTime = 100;
         if (phaseTimer >= totalPhaseTime) {
            ((IBetterDragonFight)dragonFight).advanceRespawnStage(END);
            dragonFight.resetSpikeCrystals();

            for (EndCrystal crystal : summoningCrystals) {
               crystal.setBeamTarget(null);
               serverLevel.explode(crystal, crystal.getX(), crystal.getY(), crystal.getZ(), 6.0F, ExplosionInteraction.NONE);
               crystal.discard();
            }
         } else if (phaseTimer >= 80) {
            DragonRespawnStage.broadcastDragonGrowlSound(serverLevel);
         } else if (phaseTimer == 0) {
            for (EndCrystal crystal : summoningCrystals) {
               crystal.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (phaseTimer < 5) {
            DragonRespawnStage.broadcastDragonGrowlSound(serverLevel);
         }
      }
   },
   END("end") {
      @Override
      public void tick(ServerLevel serverLevel, EndDragonFight dragonFight, List<EndCrystal> summoningCrystals, int phaseTimer) {
      }

      @Override
      public void onStart(ServerLevel serverLevel, IBetterDragonFight dragonFight) {
         BlockPos portalPos = ((EndDragonFightAccessor)dragonFight).getPortalLocation();
         dragonFight.setDragonRespawnStage(null);
         ((EndDragonFightAccessor)dragonFight).setDragonKilled(false);
         EnderDragon newDragon = ((EndDragonFightAccessor)dragonFight).invokeCreateNewDragon();
         if (((EndDragonFightAccessor)dragonFight).getPreviouslyKilled()) {
            for (ServerPlayer serverPlayer : ((EndDragonFightAccessor)dragonFight).getDragonEvent().getPlayers()) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, newDragon);
            }
         }

         ExitPortalUtils.spawnPortal(dragonFight, serverLevel, false, false, true);
         serverLevel.explode(null, portalPos.getX(), portalPos.getY() + 20, portalPos.getZ(), 6.0F, ExplosionInteraction.NONE);
         serverLevel.players()
            .forEach(
               player -> {
                  serverLevel.sendParticles(
                     player, ParticleTypes.EXPLOSION_EMITTER, true, portalPos.getX(), portalPos.getY() + 20, portalPos.getZ(), 1, 0.0, 0.0, 0.0, 0.0
                  );
                  if (player.distanceToSqr(portalPos.getX(), portalPos.getY() + 20, portalPos.getZ()) > 32.0) {
                     serverLevel.playSound(null, portalPos.above(20), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 24.0F, 1.0F);
                  }
               }
            );
         if (dragonFight.hasDragonEverSpawned()) {
            ExitPortalUtils.spawnPortal(dragonFight, serverLevel, false, true, true);
            serverLevel.explode(null, portalPos.getX(), portalPos.getY(), portalPos.getZ(), 6.0F, ExplosionInteraction.NONE);
         }

         int dragonKills = Mth.clamp(dragonFight.getNumTimesDragonKilled(), 0, 10);
         float cryingChance = Mth.lerp(dragonKills / 10.0F, 0.0F, 0.5F);
         List<Integer> existingGateways = new ArrayList<>(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         existingGateways.removeAll(((EndDragonFightAccessor)dragonFight).getGateways());
         existingGateways.forEach(gateway -> {
            int x = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * gateway.intValue())));
            int z = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * gateway.intValue())));
            BlockPos gatewayPos = new BlockPos(x, 75, z);
            RandomSource gatewayRandom = RandomSource.create(Mth.getSeed(gatewayPos));
            BlockPos.betweenClosed(gatewayPos.offset(-1, -4, -1), gatewayPos.offset(1, 4, 1)).forEach(pos -> {
               if (serverLevel.getBlockState(pos).is(Blocks.OBSIDIAN) && gatewayRandom.nextFloat() < cryingChance) {
                  serverLevel.setBlockAndUpdate(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
               }
            });
         });
         BlockPos platformPos = ServerLevel.END_SPAWN_POINT;
         RandomSource platformRandom = RandomSource.create(Mth.getSeed(platformPos));
         BlockPos.betweenClosed(platformPos.offset(-3, -15, -3), platformPos.offset(3, 4, 3)).forEach(pos -> {
            if (serverLevel.getBlockState(pos).is(Blocks.OBSIDIAN) && platformRandom.nextFloat() < cryingChance) {
               serverLevel.setBlockAndUpdate(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
            }
         });
         dragonFight.setHasDragonEverSpawned(true);
      }
   };

   public static final EnumCodec<DragonRespawnStage> CODEC = StringRepresentable.fromEnum(DragonRespawnStage::values);
   private final String name;

   @Nullable
   public static DragonRespawnStage byName(@Nullable String name) {
      return (DragonRespawnStage)CODEC.byName(name);
   }

   private DragonRespawnStage(String name) {
      this.name = name.toLowerCase();
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }

   private static void broadcastDragonGrowlSound(ServerLevel level) {
      level.levelEvent(3001, new BlockPos(0, 128, 0), 0);
   }

   public abstract void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4);

   public void onStart(ServerLevel serverLevel, IBetterDragonFight dragonFight) {
      dragonFight.setDragonRespawnStage(this);
   }
}

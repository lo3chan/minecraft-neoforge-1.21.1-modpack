package com.yungnickyoung.minecraft.betterendisland.mixin;

import com.yungnickyoung.minecraft.betterendisland.world.ExtraFightData;
import com.yungnickyoung.minecraft.betterendisland.world.IBetterDragonFight;
import com.yungnickyoung.minecraft.betterendisland.world.IPrimaryLevelData;
import com.yungnickyoung.minecraft.betterendisland.world.util.WorldgenUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class ServerLevelMixin extends Level {
   @Shadow
   @Nullable
   private EndDragonFight dragonFight;
   @Shadow
   @Final
   private MinecraftServer server;

   protected ServerLevelMixin(
      WritableLevelData $$0,
      ResourceKey<Level> $$1,
      RegistryAccess $$2,
      Holder<DimensionType> $$3,
      Supplier<ProfilerFiller> $$4,
      boolean $$5,
      boolean $$6,
      long $$7,
      int $$8
   ) {
      super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
   }

   @Shadow
   @Nullable
   public abstract EndDragonFight getDragonFight();

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void betterendisland_attachExtraData1(
      MinecraftServer server,
      Executor $$1,
      LevelStorageAccess $$2,
      ServerLevelData $$3,
      ResourceKey $$4,
      LevelStem $$5,
      ChunkProgressListener $$6,
      boolean $$7,
      long $$8,
      List $$9,
      boolean $$10,
      RandomSequences $$11,
      CallbackInfo ci
   ) {
      if (this.dragonFight != null) {
         this.dragonFight = new EndDragonFight((ServerLevel)this, server.getWorldData().worldGenOptions().seed(), server.getWorldData().endDragonFightData());
         ExtraFightData extraFightData = ((IPrimaryLevelData)server.getWorldData()).getExtraEndDragonFightData();
         ((IBetterDragonFight)this.dragonFight).setIsFirstExitPortalSpawn(extraFightData.firstExitPortalSpawn());
         ((IBetterDragonFight)this.dragonFight).setHasDragonEverSpawned(extraFightData.hasDragonEverSpawned());
         ((IBetterDragonFight)this.dragonFight).setNumTimesDragonKilled(extraFightData.numTimesDragonKilled());
      }
   }

   @Inject(
      method = {"saveLevelData"},
      at = {@At("HEAD")}
   )
   private void betterendisland_attachExtraData2(CallbackInfo ci) {
      if (this.dragonFight != null) {
         ExtraFightData extraFightData = new ExtraFightData(
            ((IBetterDragonFight)this.dragonFight).isFirstExitPortalSpawn(),
            ((IBetterDragonFight)this.dragonFight).hasDragonEverSpawned(),
            ((IBetterDragonFight)this.dragonFight).getNumTimesDragonKilled()
         );
         ((IPrimaryLevelData)this.server.getWorldData()).setExtraEndDragonFightData(extraFightData);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void betterendisland_tickInitialDragonSummonTrigger(BooleanSupplier booleanSupplier, CallbackInfo ci) {
      if (this.dimension().location().equals(BuiltinDimensionTypes.END.location()) && this.getDragonFight() != null) {
         IBetterDragonFight betterDragonFight = (IBetterDragonFight)this.getDragonFight();
         if (!betterDragonFight.hasDragonEverSpawned() && betterDragonFight.getDragonRespawnStage() == null && this.levelData.getGameTime() % 5L == 0L) {
            double requiredDist = 25.0;

            for (Player player : this.players()) {
               if (EntitySelector.NO_SPECTATORS.test(player)) {
                  double xzDistSqr = WorldgenUtils.distSqr(0.0, 0.0, player.position().x(), player.position().z());
                  if (xzDistSqr < requiredDist * requiredDist) {
                     betterDragonFight.doInitialDragonSpawn();
                  }
               }
            }
         }

         betterDragonFight.tickBellSound();
      }
   }
}

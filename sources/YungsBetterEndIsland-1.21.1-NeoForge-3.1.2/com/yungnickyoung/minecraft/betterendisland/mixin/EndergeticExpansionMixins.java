package com.yungnickyoung.minecraft.betterendisland.mixin;

import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import java.util.List;
import java.util.concurrent.Executor;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ServerLevel.class},
   priority = 2000
)
public abstract class EndergeticExpansionMixins extends Level {
   @Shadow
   @Nullable
   private EndDragonFight dragonFight;

   protected EndergeticExpansionMixins(
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

   @Inject(
      at = {@At("RETURN")},
      method = {"<init>"}
   )
   private void betterendisland_overwriteModdedDragonFight(
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
      if (BetterEndIslandCommon.endergetic && this.dragonFight != null) {
         this.dragonFight = new EndDragonFight((ServerLevel)this, server.getWorldData().worldGenOptions().seed(), server.getWorldData().endDragonFightData());
      }
   }
}

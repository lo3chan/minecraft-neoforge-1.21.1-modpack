package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.PlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.SyncedPlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import dev.corgitaco.dataanchor.util.TickableBlockEntityAccessor;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class ServerLevelMixin extends Level {
   protected ServerLevelMixin(
      WritableLevelData levelData,
      ResourceKey<Level> dimension,
      RegistryAccess registryAccess,
      Holder<DimensionType> dimensionTypeRegistration,
      Supplier<ProfilerFiller> profiler,
      boolean isClientSide,
      boolean isDebug,
      long biomeZoomSeed,
      int maxChainedNeighborUpdates
   ) {
      super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
   }

   @Inject(
      method = {"tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$onTickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
      if (chunk instanceof TrackedDataContainer access) {
         for (TrackedDataKey<ChunkTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
            access.dataAnchor$getTrackedData(key).ifPresent(data -> {
               if (data instanceof TickableTrackedData tickableData) {
                  tickableData.tick();
               }
            });
         }
      }

      if (chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING) && chunk instanceof TickableBlockEntityAccessor accessor) {
         for (BlockEntity value : accessor.dataAnchor$getTickableBlockEntities()) {
            if (value instanceof TrackedDataContainer access) {
               for (TrackedDataKey<BlockEntityTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
                  access.dataAnchor$getTrackedData(key).ifPresent(data -> {
                     if (data instanceof TickableTrackedData tickableData) {
                        tickableData.tick();
                     }
                  });
               }
            }
         }
      }
   }

   @Inject(
      method = {"addRespawnedPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$addRespawnTeleport(ServerPlayer player, CallbackInfo ci) {
      if (player instanceof TrackedDataContainer access) {
         for (TrackedDataKey<PlayerTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
            access.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
               if (trackedData instanceof PlayerTrackedData data) {
                  data.addRespawnedPlayer();
               }
            });
         }
      }
   }

   @Inject(
      method = {"addDuringTeleport(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$addDuringPortalTeleport(Entity entity, CallbackInfo ci) {
      if (entity instanceof TrackedDataContainer access) {
         for (TrackedDataKey<PlayerTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
            access.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
               if (trackedData instanceof PlayerTrackedData data) {
                  data.addDuringPortalTeleport();
               }
            });
         }
      }
   }

   @Inject(
      method = {"addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$onPlayerAddToWorld(ServerPlayer player, CallbackInfo ci) {
      if (player instanceof TrackedDataContainer access) {
         for (TrackedDataKey<PlayerTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
            access.dataAnchor$getTrackedData(key).ifPresent(data -> {
               if (data instanceof PlayerTrackedData playerTrackedData) {
                  playerTrackedData.playerAddedToWorld();
                  if (data instanceof SyncedPlayerTrackedData syncedData) {
                     syncedData.syncToPlayer(player);
                  }
               }
            });
         }
      }

      if (this instanceof TrackedDataContainer access) {
         for (TrackedDataKey<LevelTrackedData> key : access.dataAnchor$getTrackedDataKeys()) {
            access.dataAnchor$getTrackedData(key).ifPresent(data -> {
               if (data instanceof SyncedLevelTrackedData syncedData) {
                  syncedData.syncToPlayer(player);
               }
            });
         }
      }
   }
}

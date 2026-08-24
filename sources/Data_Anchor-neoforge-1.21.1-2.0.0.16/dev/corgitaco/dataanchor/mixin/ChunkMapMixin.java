package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerChunkSender.class})
public class ChunkMapMixin {
   @Inject(
      method = {"sendChunk(Lnet/minecraft/server/network/ServerGamePacketListenerImpl;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;)V"},
      at = {@At("RETURN")}
   )
   private static void dataAnchor$onPlayerLoadedChunk(ServerGamePacketListenerImpl packetListener, ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
      if (chunk instanceof TrackedDataContainer<?, ?> trackedDataContainer) {
         for (TrackedDataKey key : trackedDataContainer.dataAnchor$getTrackedDataKeys()) {
            trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
               if (trackedData instanceof SyncedTrackedData syncedData) {
                  syncedData.syncToPlayer(packetListener.getPlayer());
               }
            });
         }

         chunk.getBlockEntities().values().forEach(entry -> {
            if (entry instanceof TrackedDataContainer<?, ?> blockEntityContainer) {
               for (TrackedDataKey keyx : blockEntityContainer.dataAnchor$getTrackedDataKeys()) {
                  blockEntityContainer.dataAnchor$getTrackedData(keyx).ifPresent(blockEntityData -> {
                     if (blockEntityData instanceof SyncedTrackedData syncedBlockEntityData) {
                        syncedBlockEntityData.syncToPlayer(packetListener.getPlayer());
                     }
                  });
               }
            }
         });
      }
   }
}

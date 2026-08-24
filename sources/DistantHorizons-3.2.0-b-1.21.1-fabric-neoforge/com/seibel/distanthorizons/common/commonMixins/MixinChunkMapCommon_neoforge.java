package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.api.internal.ServerApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinChunkMapCommon_neoforge {
   public static void onChunkSave(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<Boolean> ci) {
      IServerLevelWrapper levelWrapper = ServerLevelWrapper_neoforge.getWrapper(level);
      int chunkPosX = chunk.getPos().x;
      int chunkPosZ = chunk.getPos().z;
      if (!SharedApi.isChunkAtChunkPosAlreadyUpdating(levelWrapper, chunkPosX, chunkPosZ)) {
         boolean savingChunkToDisk = (Boolean)ci.getReturnValue();
         if (savingChunkToDisk) {
            if (!chunk.isUnsaved() && !chunk.isUpgrading() && chunk.isLightCorrect() && !(chunk instanceof ProtoChunk)) {
               try {
                  chunk.getNoiseBiome(0, 0, 0);
               } catch (Exception var8) {
                  return;
               }

               ServerApi.INSTANCE.serverChunkSaveEvent(new ChunkWrapper_neoforge(chunk, levelWrapper), levelWrapper);
            }
         }
      }
   }
}

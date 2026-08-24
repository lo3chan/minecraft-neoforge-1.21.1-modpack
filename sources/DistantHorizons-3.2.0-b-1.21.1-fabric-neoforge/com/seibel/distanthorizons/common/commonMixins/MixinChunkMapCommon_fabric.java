package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.ServerApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import net.minecraft.class_2791;
import net.minecraft.class_2839;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinChunkMapCommon_fabric {
   public static void onChunkSave(class_3218 level, class_2791 chunk, CallbackInfoReturnable<Boolean> ci) {
      IServerLevelWrapper levelWrapper = ServerLevelWrapper_fabric.getWrapper(level);
      int chunkPosX = chunk.method_12004().field_9181;
      int chunkPosZ = chunk.method_12004().field_9180;
      if (!SharedApi.isChunkAtChunkPosAlreadyUpdating(levelWrapper, chunkPosX, chunkPosZ)) {
         boolean savingChunkToDisk = (Boolean)ci.getReturnValue();
         if (savingChunkToDisk) {
            if (!chunk.method_12044() && !chunk.method_39461() && chunk.method_12038() && !(chunk instanceof class_2839)) {
               try {
                  chunk.method_16359(0, 0, 0);
               } catch (Exception var8) {
                  return;
               }

               ServerApi.INSTANCE.serverChunkSaveEvent(new ChunkWrapper_fabric(chunk, levelWrapper), levelWrapper);
            }
         }
      }
   }
}

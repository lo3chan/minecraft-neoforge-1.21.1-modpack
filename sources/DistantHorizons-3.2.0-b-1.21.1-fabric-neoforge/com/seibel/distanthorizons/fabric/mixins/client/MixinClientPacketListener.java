package com.seibel.distanthorizons.fabric.mixins.client;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.util.concurrent.AbstractExecutorService;
import net.minecraft.class_2818;
import net.minecraft.class_634;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public class MixinClientPacketListener {
   @Shadow
   private class_638 field_3699;

   @Inject(
      method = {"handleLogin"},
      at = {@At("RETURN")}
   )
   void onHandleLoginEnd(CallbackInfo ci) {
      ClientApi.INSTANCE.onClientOnlyConnected();
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")}
   )
   void onCleanupStart(CallbackInfo ci) {
      ClientApi.INSTANCE.onClientOnlyDisconnected();
   }

   @Inject(
      method = {"enableChunkLight"},
      at = {@At("TAIL")}
   )
   void onEnableChunkLight(class_2818 chunk, int x, int z, CallbackInfo ci) {
      if (chunk != null) {
         AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
         if (executor != null) {
            class_638 clientLevel = (class_638)chunk.method_12200();
            executor.execute(() -> {
               IClientLevelWrapper clientLevelWrapper = ClientLevelWrapper_fabric.getWrapper(clientLevel);
               SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_fabric(chunk, clientLevelWrapper), clientLevelWrapper, true);
            });
         }
      }
   }
}

package com.mrcrayfish.configured.network;

import com.mrcrayfish.configured.impl.framework.message.MessageFramework;
import com.mrcrayfish.configured.network.message.MessageSessionData;
import com.mrcrayfish.configured.network.payload.SyncNeoForgeConfigPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "configured",
   bus = Bus.MOD
)
public class NeoForgeNetwork {
   public static final int VERSION = 1;

   @SubscribeEvent
   private static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("configured").optional().versioned(Integer.toString(1));
      registrar.playToClient(
         MessageSessionData.TYPE, MessageSessionData.STREAM_CODEC, (payload, context) -> MessageSessionData.handle(payload, context::enqueueWork)
      );
      registrar.playToServer(SyncNeoForgeConfigPayload.TYPE, SyncNeoForgeConfigPayload.STREAM_CODEC, SyncNeoForgeConfigPayload::handle);
      if (ModList.get().isLoaded("framework")) {
         registrar.playToServer(
            MessageFramework.Sync.TYPE,
            MessageFramework.Sync.STREAM_CODEC,
            (payload, context) -> MessageFramework.Sync.handle(payload, context::enqueueWork, context.player(), context::disconnect)
         );
         registrar.playToServer(
            MessageFramework.Request.TYPE,
            MessageFramework.Request.STREAM_CODEC,
            (payload, context) -> MessageFramework.Request.handle(payload, context::enqueueWork, context.player(), context::disconnect)
         );
         registrar.playToClient(
            MessageFramework.Response.TYPE,
            MessageFramework.Response.STREAM_CODEC,
            (payload, context) -> MessageFramework.Response.handle(payload, context::enqueueWork, context.player(), context::disconnect)
         );
      }
   }
}

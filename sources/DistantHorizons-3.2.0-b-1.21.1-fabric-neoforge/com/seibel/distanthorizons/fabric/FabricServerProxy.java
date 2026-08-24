package com.seibel.distanthorizons.fabric;

import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_fabric;
import com.seibel.distanthorizons.common.AbstractPluginPacketSender_fabric;
import com.seibel.distanthorizons.common.CommonPacketPayload$Codec_fabric;
import com.seibel.distanthorizons.common.CommonPacketPayload_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.misc.ServerPlayerWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.ServerApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AfterPlayerChange;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Unload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_638;

public class FabricServerProxy implements AbstractModInitializer$IEventProxy_fabric {
   private static final ServerApi SERVER_API = ServerApi.INSTANCE;
   private static final AbstractPluginPacketSender_fabric PACKET_SENDER = (AbstractPluginPacketSender_fabric)SingletonInjector.INSTANCE
      .get(IPluginPacketSender.class);
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final boolean isDedicatedServer;

   public FabricServerProxy(boolean isDedicatedServer) {
      this.isDedicatedServer = isDedicatedServer;
   }

   private IClientLevelWrapper getClientLevelWrapper(class_638 level) {
      return ClientLevelWrapper_fabric.getWrapper(level);
   }

   private ServerLevelWrapper_fabric getServerLevelWrapper(class_3218 level) {
      return ServerLevelWrapper_fabric.getWrapper(level);
   }

   private ServerPlayerWrapper_fabric getServerPlayerWrapper(class_3222 player) {
      return ServerPlayerWrapper_fabric.getWrapper(player);
   }

   @Override
   public void registerEvents() {
      LOGGER.info("Registering Fabric Server Events");
      ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)server -> ServerApi.INSTANCE.serverLoadEvent(this.isDedicatedServer));
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)server -> ServerApi.INSTANCE.serverUnloadEvent());
      ServerWorldEvents.LOAD.register((Load)(server, level) -> ServerApi.INSTANCE.serverLevelLoadEvent(this.getServerLevelWrapper(level)));
      ServerWorldEvents.UNLOAD.register((Unload)(server, level) -> ServerApi.INSTANCE.serverLevelUnloadEvent(this.getServerLevelWrapper(level)));
      ServerChunkEvents.CHUNK_LOAD.register((net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.Load)(server, chunk) -> {
         ILevelWrapper level = this.getServerLevelWrapper((class_3218)chunk.method_12200());
         ServerApi.INSTANCE.serverChunkLoadEvent(new ChunkWrapper_fabric(chunk, level), level);
      });
      ServerPlayConnectionEvents.JOIN
         .register((Join)(handler, sender, server) -> ServerApi.INSTANCE.serverPlayerJoinEvent(this.getServerPlayerWrapper(handler.field_14140)));
      ServerPlayConnectionEvents.DISCONNECT
         .register((Disconnect)(handler, server) -> ServerApi.INSTANCE.serverPlayerDisconnectEvent(this.getServerPlayerWrapper(handler.field_14140)));
      ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD
         .register(
            (AfterPlayerChange)(player, originLevel, destinationLevel) -> ServerApi.INSTANCE
               .serverPlayerLevelChangeEvent(
                  this.getServerPlayerWrapper(player), this.getServerLevelWrapper(originLevel), this.getServerLevelWrapper(destinationLevel)
               )
         );
      PayloadTypeRegistry.playC2S().register(CommonPacketPayload_fabric.TYPE, new CommonPacketPayload$Codec_fabric());
      if (this.isDedicatedServer) {
         PayloadTypeRegistry.playS2C().register(CommonPacketPayload_fabric.TYPE, new CommonPacketPayload$Codec_fabric());
      }

      ServerPlayNetworking.registerGlobalReceiver(CommonPacketPayload_fabric.TYPE, (payload, context) -> {
         if (payload.message() != null) {
            ServerApi.INSTANCE.pluginMessageReceived(ServerPlayerWrapper_fabric.getWrapper(context.player()), payload.message());
         }
      });
   }
}

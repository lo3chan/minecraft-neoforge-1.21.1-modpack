package net.mehvahdjukaar.moonlight.core.network;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.core.network.platform.ModNetworkingImpl;

public class ModNetworking {
   public static void init() {
      NetworkHelper.addNetworkRegistration(ModNetworking::registerMessages, 10);
   }

   private static void registerMessages(NetworkHelper.RegisterMessagesEvent event) {
      event.registerClientBound(ClientBoundFinalizeFluidsMessage.TYPE);
      event.registerClientBound(ClientBoundOpenScreenMessage.TYPE);
      event.registerClientBound(ClientBoundOpenConfigScreenMessage.TYPE);
      event.registerClientBound(ClientBoundSendLoginMessage.TYPE);
      event.registerClientBound(ClientBoundOnPistonMovedBlockMessage.TYPE);
      event.registerClientBound(ClientBoundParticleAroundBlockMessage.TYPE);
      event.registerClientBound(ClientBoundSyncWorldDataMessage.TYPE);
      event.registerServerBound(ServerBoundItemLeftClickMessage.TYPE);
      event.registerServerBound(ServerBoundUpdateBoxBlockTileMessage.TYPE);
      event.registerBidirectional(SyncConfigsMessage.TYPE);
      loaderDependent(event);
   }

   public static void loaderDependent(NetworkHelper.RegisterMessagesEvent var0) {
      ModNetworkingImpl.loaderDependent(var0);
   }
}

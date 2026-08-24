package net.mehvahdjukaar.amendments.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper.RegisterMessagesEvent;

public class ModNetwork {
   public static void init() {
      NetworkHelper.addNetworkRegistration(ModNetwork::registerMessages, 2);
   }

   private static void registerMessages(RegisterMessagesEvent event) {
      event.registerServerBound(ServerBoundSyncLecternBookMessage.TYPE);
      event.registerClientBound(ClientBoundEntityHitSwayingBlockMessage.TYPE);
      event.registerClientBound(ClientBoundPlaySplashParticlesMessage.TYPE);
      event.registerClientBound(ClientBoundFireballExplodePacket.TYPE);
   }
}

package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public final class ClientPlayerNetworkEvents {
   public static final EventInvoker<ClientPlayerNetworkEvents.LoggedIn> LOGGED_IN = EventInvoker.lookup(ClientPlayerNetworkEvents.LoggedIn.class);
   public static final EventInvoker<ClientPlayerNetworkEvents.LoggedOut> LOGGED_OUT = EventInvoker.lookup(ClientPlayerNetworkEvents.LoggedOut.class);

   private ClientPlayerNetworkEvents() {
   }

   @FunctionalInterface
   public interface LoggedIn {
      void onLoggedIn(LocalPlayer var1, MultiPlayerGameMode var2, Connection var3);
   }

   @FunctionalInterface
   public interface LoggedOut {
      void onLoggedOut(LocalPlayer var1, MultiPlayerGameMode var2, Connection var3);
   }
}

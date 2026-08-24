package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerNetworkEvents {
   public static final EventInvoker<PlayerNetworkEvents.LoggedIn> LOGGED_IN = EventInvoker.lookup(PlayerNetworkEvents.LoggedIn.class);
   public static final EventInvoker<PlayerNetworkEvents.LoggedOut> LOGGED_OUT = EventInvoker.lookup(PlayerNetworkEvents.LoggedOut.class);

   private PlayerNetworkEvents() {
   }

   @FunctionalInterface
   public interface LoggedIn {
      void onLoggedIn(ServerPlayer var1);
   }

   @FunctionalInterface
   public interface LoggedOut {
      void onLoggedOut(ServerPlayer var1);
   }
}

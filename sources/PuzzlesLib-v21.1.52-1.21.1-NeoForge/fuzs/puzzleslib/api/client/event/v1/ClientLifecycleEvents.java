package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;

public final class ClientLifecycleEvents {
   public static final EventInvoker<ClientLifecycleEvents.Started> STARTED = EventInvoker.lookup(ClientLifecycleEvents.Started.class);
   public static final EventInvoker<ClientLifecycleEvents.Stopping> STOPPING = EventInvoker.lookup(ClientLifecycleEvents.Stopping.class);

   private ClientLifecycleEvents() {
   }

   @FunctionalInterface
   public interface Started {
      void onClientStarted(Minecraft var1);
   }

   @FunctionalInterface
   public interface Stopping {
      void onClientStopping(Minecraft var1);
   }
}

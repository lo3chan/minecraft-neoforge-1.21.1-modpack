package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
   public static final EventInvoker<ServerLifecycleEvents.Starting> STARTING = EventInvoker.lookup(ServerLifecycleEvents.Starting.class);
   public static final EventInvoker<ServerLifecycleEvents.Started> STARTED = EventInvoker.lookup(ServerLifecycleEvents.Started.class);
   public static final EventInvoker<ServerLifecycleEvents.Stopping> STOPPING = EventInvoker.lookup(ServerLifecycleEvents.Stopping.class);
   public static final EventInvoker<ServerLifecycleEvents.Stopped> STOPPED = EventInvoker.lookup(ServerLifecycleEvents.Stopped.class);

   private ServerLifecycleEvents() {
   }

   @FunctionalInterface
   public interface Started {
      void onServerStarted(MinecraftServer var1);
   }

   @FunctionalInterface
   public interface Starting {
      void onServerStarting(MinecraftServer var1);
   }

   @FunctionalInterface
   public interface Stopped {
      void onServerStopped(MinecraftServer var1);
   }

   @FunctionalInterface
   public interface Stopping {
      void onServerStopping(MinecraftServer var1);
   }
}

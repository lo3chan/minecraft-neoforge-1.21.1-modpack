package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class PlayerTrackingEvents {
   public static final EventInvoker<PlayerTrackingEvents.Start> START = EventInvoker.lookup(PlayerTrackingEvents.Start.class);
   public static final EventInvoker<PlayerTrackingEvents.Stop> STOP = EventInvoker.lookup(PlayerTrackingEvents.Stop.class);

   private PlayerTrackingEvents() {
   }

   @FunctionalInterface
   public interface Start {
      void onStartTracking(Entity var1, ServerPlayer var2);
   }

   @FunctionalInterface
   public interface Stop {
      void onStopTracking(Entity var1, ServerPlayer var2);
   }
}

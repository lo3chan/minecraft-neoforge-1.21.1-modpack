package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class ContainerEvents {
   public static final EventInvoker<ContainerEvents.Open> OPEN = EventInvoker.lookup(ContainerEvents.Open.class);
   public static final EventInvoker<ContainerEvents.Close> CLOSE = EventInvoker.lookup(ContainerEvents.Close.class);

   private ContainerEvents() {
   }

   @FunctionalInterface
   public interface Close {
      void onContainerClose(ServerPlayer var1, AbstractContainerMenu var2);
   }

   @FunctionalInterface
   public interface Open {
      void onContainerOpen(ServerPlayer var1, AbstractContainerMenu var2);
   }
}

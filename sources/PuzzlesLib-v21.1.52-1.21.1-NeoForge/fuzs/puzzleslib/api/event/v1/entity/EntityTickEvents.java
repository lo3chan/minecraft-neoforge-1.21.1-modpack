package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;

public final class EntityTickEvents {
   public static final EventInvoker<EntityTickEvents.Start> START = EventInvoker.lookup(EntityTickEvents.Start.class);
   public static final EventInvoker<EntityTickEvents.End> END = EventInvoker.lookup(EntityTickEvents.End.class);

   private EntityTickEvents() {
   }

   @FunctionalInterface
   public interface End {
      void onEndEntityTick(Entity var1);
   }

   @FunctionalInterface
   public interface Start {
      EventResult onStartEntityTick(Entity var1);
   }
}

package dev.architectury.event.forge;

import dev.architectury.event.Event;
import dev.architectury.event.EventActor;
import dev.architectury.event.EventResult;
import java.util.function.Consumer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EventFactoryImpl {
   public static <T> Event<Consumer<T>> attachToForge(Event<Consumer<T>> event) {
      event.register(eventObj -> {
         if (!(eventObj instanceof net.neoforged.bus.api.Event)) {
            throw new ClassCastException(eventObj.getClass() + " is not an instance of forge Event!");
         } else {
            NeoForge.EVENT_BUS.post((net.neoforged.bus.api.Event)eventObj);
         }
      });
      return event;
   }

   @Internal
   public static <T> Event<EventActor<T>> attachToForgeEventActor(Event<EventActor<T>> event) {
      event.register(eventObj -> {
         if (!(eventObj instanceof net.neoforged.bus.api.Event)) {
            throw new ClassCastException(eventObj.getClass() + " is not an instance of forge Event!");
         } else if (!(eventObj instanceof ICancellableEvent)) {
            throw new ClassCastException(eventObj.getClass() + " is not cancellable Event!");
         } else {
            NeoForge.EVENT_BUS.post((net.neoforged.bus.api.Event)eventObj);
            return EventResult.pass();
         }
      });
      return event;
   }

   @Internal
   public static <T> Event<EventActor<T>> attachToForgeEventActorCancellable(Event<EventActor<T>> event) {
      event.register(
         eventObj -> {
            if (!(eventObj instanceof net.neoforged.bus.api.Event)) {
               throw new ClassCastException(eventObj.getClass() + " is not an instance of forge Event!");
            } else if (!(eventObj instanceof ICancellableEvent)) {
               throw new ClassCastException(eventObj.getClass() + " is not cancellable Event!");
            } else {
               return ((ICancellableEvent)NeoForge.EVENT_BUS.post((net.neoforged.bus.api.Event)eventObj)).isCanceled()
                  ? EventResult.interrupt(false)
                  : EventResult.pass();
            }
         }
      );
      return event;
   }
}

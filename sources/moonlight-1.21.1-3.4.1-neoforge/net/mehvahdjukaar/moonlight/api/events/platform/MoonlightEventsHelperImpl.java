package net.mehvahdjukaar.moonlight.api.events.platform;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.events.IDropItemOnDeathEvent;
import net.mehvahdjukaar.moonlight.api.events.IFireConsumeBlockEvent;
import net.mehvahdjukaar.moonlight.api.events.ILightningStruckBlockEvent;
import net.mehvahdjukaar.moonlight.api.events.IVillagerBrainEvent;
import net.mehvahdjukaar.moonlight.api.events.SimpleEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

public class MoonlightEventsHelperImpl {
   private static final Map<Class<? extends SimpleEvent>, Queue<Consumer<? extends SimpleEvent>>> LISTENERS = new ConcurrentHashMap<>();

   public static <T extends SimpleEvent> void addListener(Consumer<T> listener, Class<T> eventClass) {
      if (eventClass == IVillagerBrainEvent.class) {
         Consumer<VillagerBrainEvent> eventConsumer = e -> listener.accept((T)e);
         NeoForge.EVENT_BUS.addListener(eventConsumer);
      } else if (eventClass == IFireConsumeBlockEvent.class) {
         Consumer<FireConsumeBlockEvent> eventConsumer = e -> listener.accept((T)e);
         NeoForge.EVENT_BUS.addListener(eventConsumer);
      } else if (eventClass == ILightningStruckBlockEvent.class) {
         Consumer<LightningStruckBlockEvent> eventConsumer = e -> listener.accept((T)e);
         NeoForge.EVENT_BUS.addListener(eventConsumer);
      } else if (eventClass == IDropItemOnDeathEvent.class) {
         Consumer<DropItemOnDeathEvent> eventConsumer = e -> listener.accept((T)e);
         NeoForge.EVENT_BUS.addListener(eventConsumer);
      } else {
         LISTENERS.computeIfAbsent(eventClass, ev -> new ConcurrentLinkedDeque<>()).add(listener);
      }
   }

   public static <T extends SimpleEvent> void postEvent(T event, Class<T> eventClass) {
      if (event instanceof Event e) {
         NeoForge.EVENT_BUS.post(e);
      } else {
         Queue<Consumer<? extends SimpleEvent>> consumers = LISTENERS.get(eventClass);
         if (consumers != null) {
            consumers.forEach(ex -> ex.accept(event));
         }
      }
   }
}

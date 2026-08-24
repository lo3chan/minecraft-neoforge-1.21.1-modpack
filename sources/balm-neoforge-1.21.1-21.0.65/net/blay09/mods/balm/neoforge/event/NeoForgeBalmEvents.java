package net.blay09.mods.balm.neoforge.event;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBalmEvents implements BalmEvents {
   private final Table<Class<?>, EventPriority, Consumer<EventPriority>> eventInitializers = Tables.synchronizedTable(HashBasedTable.create());
   private final Map<Class<?>, Consumer<?>> eventDispatchers = new ConcurrentHashMap<>();
   private final Table<Class<?>, EventPriority, CopyOnWriteArrayList<Consumer<?>>> eventHandlers = Tables.synchronizedTable(HashBasedTable.create());
   private final Table<TickType<?>, TickPhase, Consumer<?>> tickEventInitializers = Tables.synchronizedTable(HashBasedTable.create());

   public static net.neoforged.bus.api.EventPriority toForge(EventPriority priority) {
      return switch (priority) {
         case Lowest -> net.neoforged.bus.api.EventPriority.LOWEST;
         case Low -> net.neoforged.bus.api.EventPriority.LOW;
         case Normal -> net.neoforged.bus.api.EventPriority.NORMAL;
         case High -> net.neoforged.bus.api.EventPriority.HIGH;
         case Highest -> net.neoforged.bus.api.EventPriority.HIGHEST;
      };
   }

   public void registerEvent(Class<?> eventClass, Consumer<EventPriority> initializer) {
      this.registerEvent(eventClass, initializer, null);
   }

   public void registerEvent(Class<?> eventClass, Consumer<EventPriority> initializer, @Nullable Consumer<?> dispatcher) {
      for (EventPriority priority : EventPriority.values()) {
         this.eventInitializers.put(eventClass, priority, initializer);
      }

      if (dispatcher != null) {
         this.eventDispatchers.put(eventClass, dispatcher);
      }
   }

   public <T> void fireEventHandlers(EventPriority priority, T event) {
      CopyOnWriteArrayList<Consumer<?>> handlers = (CopyOnWriteArrayList<Consumer<?>>)this.eventHandlers.get(event.getClass(), priority);
      if (handlers != null) {
         handlers.forEach(handler -> this.fireEventHandler((Consumer<?>)handler, event));
      }
   }

   private <T> void fireEventHandler(Consumer<T> handler, Object event) {
      handler.accept((T)event);
   }

   @Override
   public <T> void onEvent(Class<T> eventClass, Consumer<T> handler, EventPriority priority) {
      Consumer<EventPriority> initializer = (Consumer<EventPriority>)this.eventInitializers.remove(eventClass, priority);
      if (initializer != null) {
         initializer.accept(priority);
      }

      synchronized (this.eventHandlers) {
         CopyOnWriteArrayList<Consumer<?>> consumers = (CopyOnWriteArrayList<Consumer<?>>)this.eventHandlers.get(eventClass, priority);
         if (consumers == null) {
            consumers = new CopyOnWriteArrayList<>();
            this.eventHandlers.put(eventClass, priority, consumers);
         }

         consumers.add(handler);
      }
   }

   @Override
   public <T> void fireEvent(T event) {
      Consumer<T> handler = (Consumer<T>)this.eventDispatchers.get(event.getClass());
      if (handler != null) {
         handler.accept(event);
      } else {
         for (EventPriority priority : EventPriority.values) {
            this.fireEventHandlers(priority, event);
         }
      }

      if (event instanceof Event forgeEvent) {
         NeoForge.EVENT_BUS.post(forgeEvent);
      }
   }

   @Override
   public <T> void onTickEvent(TickType<T> type, TickPhase phase, T handler) {
      Consumer<T> initializer = (Consumer<T>)this.tickEventInitializers.get(type, phase);
      if (initializer != null) {
         initializer.accept(handler);
      }
   }

   public <T> void registerTickEvent(TickType<?> type, TickPhase phase, Consumer<T> initializer) {
      this.tickEventInitializers.put(type, phase, initializer);
   }
}

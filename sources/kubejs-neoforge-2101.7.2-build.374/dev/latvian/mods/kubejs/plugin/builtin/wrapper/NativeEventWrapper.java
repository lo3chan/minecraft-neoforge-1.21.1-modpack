package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

public interface NativeEventWrapper {
   static void onEvent(Context cx, Class<?> eventClass, Consumer<Event> consumer) {
      onEvent(cx, EventPriority.NORMAL, eventClass, consumer);
   }

   static void onEvent(Context cx, EventPriority priority, Class<?> eventClass, Consumer<Event> consumer) {
      if (!Event.class.isAssignableFrom(eventClass)) {
         throw new IllegalArgumentException("Event class must extend net.neoforged.bus.api.Event!");
      } else {
         ScriptType scriptType = ((KubeJSContext)cx).kjsFactory.manager.scriptType;
         NativeEventWrapper.Listeners.Key key = new NativeEventWrapper.Listeners.Key(eventClass, priority == null ? EventPriority.NORMAL : priority);
         NativeEventWrapper.Listeners listeners = scriptType.nativeEventListeners.get(key);
         if (listeners == null) {
            listeners = new NativeEventWrapper.Listeners(new LinkedList<>());
            scriptType.nativeEventListeners.put(key, listeners);
            IEventBus bus = IModBusEvent.class.isAssignableFrom(eventClass) ? KubeJS.modEventBus : NeoForge.EVENT_BUS;
            bus.addListener(priority, false, eventClass, listeners);
         }

         listeners.listeners.add(consumer);
      }
   }

   @HideFromJS
   public record Listeners(Collection<Consumer<Event>> listeners) implements Consumer<Event> {
      public void accept(Event event) {
         for (Consumer<Event> listener : this.listeners) {
            listener.accept(event);
         }
      }

      public record Key(Class<?> eventClass, EventPriority priority) {
         @Override
         public boolean equals(Object o) {
            if (o != this) {
               if (o instanceof NativeEventWrapper.Listeners.Key(Class var5, EventPriority var11)) {
                  EventPriority var8 = var11;
                  if (var5 == this.eventClass && var8 == this.priority) {
                     return true;
                  }
               }

               return false;
            } else {
               return true;
            }
         }
      }
   }
}

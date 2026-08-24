package com.alonie.brbe.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ConfigEventBus {
   private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();
   private final AtomicBoolean configChangePending = new AtomicBoolean(false);

   public boolean consumeConfigChange() {
      return this.configChangePending.getAndSet(false);
   }

   public void requestConfigRefresh() {
      this.configChangePending.set(true);
   }

   public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
      this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
   }

   public <T> void publish(T event) {
      List<Consumer<?>> subs = this.listeners.get(event.getClass());
      if (subs != null) {
         for (Consumer<?> sub : subs) {
            try {
               ((Consumer<T>)sub).accept(event);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      }
   }

   public void clear() {
      this.listeners.clear();
   }

   public static final class BookVisibilityChanged {
      private final boolean enableBook;

      public BookVisibilityChanged(boolean enableBook) {
         this.enableBook = enableBook;
      }

      public boolean enableBook() {
         return this.enableBook;
      }
   }

   public static final class ConfigChanged {
      private final Config config;

      public ConfigChanged(Config config) {
         this.config = config;
      }

      public Config config() {
         return this.config;
      }
   }

   public static final class PartialCraftingChanged {
      private final boolean enabled;
      private final boolean markingEnabled;

      public PartialCraftingChanged(boolean enabled, boolean markingEnabled) {
         this.enabled = enabled;
         this.markingEnabled = markingEnabled;
      }

      public boolean enabled() {
         return this.enabled;
      }

      public boolean markingEnabled() {
         return this.markingEnabled;
      }
   }

   public static final class PinningChanged {
      private final boolean enabled;

      public PinningChanged(boolean enabled) {
         this.enabled = enabled;
      }

      public boolean enabled() {
         return this.enabled;
      }
   }
}

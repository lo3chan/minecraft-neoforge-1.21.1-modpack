package fuzs.puzzleslib.impl.event.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class EventInvokerImpl {
   private static final Map<Class<?>, EventInvokerImpl.EventInvokerLike<?>> EVENT_INVOKER_LOOKUP = Collections.synchronizedMap(Maps.newIdentityHashMap());
   private static final Queue<Runnable> DEFERRED_INVOKER_REGISTRATIONS = Queues.newConcurrentLinkedQueue();
   private static boolean initialized;

   private EventInvokerImpl() {
   }

   public static void initialize() {
      if (!initialized) {
         ProxyImpl.get().registerAllEventHandlers();
         initialized = true;

         while (!DEFERRED_INVOKER_REGISTRATIONS.isEmpty()) {
            DEFERRED_INVOKER_REGISTRATIONS.poll().run();
         }
      }
   }

   public static <T> EventInvoker<T> softLookup(Class<T> clazz, @Nullable Object context) {
      Objects.requireNonNull(clazz, "type is null");
      Supplier<EventInvoker<T>> invoker = Suppliers.memoize(() -> lookup(clazz, context));
      return (phase, callback) -> {
         if (!initialized && !EVENT_INVOKER_LOOKUP.containsKey(clazz)) {
            DEFERRED_INVOKER_REGISTRATIONS.offer(() -> invoker.get().register(phase, (T)callback));
         } else {
            invoker.get().register(phase, callback);
         }
      };
   }

   private static <T> EventInvoker<T> lookup(Class<T> clazz, @Nullable Object context) {
      Objects.requireNonNull(clazz, "type is null");
      EventInvokerImpl.EventInvokerLike<T> invokerLike = (EventInvokerImpl.EventInvokerLike<T>)EVENT_INVOKER_LOOKUP.get(clazz);
      Objects.requireNonNull(invokerLike, "invoker is null for type " + clazz);
      EventInvoker<T> invoker = invokerLike.asEventInvoker(context);
      Objects.requireNonNull(invoker, "invoker is null for type " + clazz);
      return invoker;
   }

   public static <T> void register(Class<T> clazz, EventInvokerImpl.EventInvokerLike<T> invoker, boolean joinInvokers) {
      if (joinInvokers) {
         EventInvokerImpl.EventInvokerLike<T> other = (EventInvokerImpl.EventInvokerLike<T>)EVENT_INVOKER_LOOKUP.get(clazz);
         if (other != null) {
            invoker = join(invoker, other);
         }
      }

      if (EVENT_INVOKER_LOOKUP.put(clazz, invoker) != null && !joinInvokers) {
         PuzzlesLib.LOGGER.warn("Overriding existing event invoker for type {}", clazz);
      }
   }

   private static <T> EventInvokerImpl.EventInvokerLike<T> join(EventInvokerImpl.EventInvokerLike<T> invoker, EventInvokerImpl.EventInvokerLike<T> other) {
      return context -> (phase, callback) -> {
         invoker.asEventInvoker(context).register(phase, callback);
         other.asEventInvoker(context).register(phase, callback);
      };
   }

   static {
      ProxyImpl.get().registerAllLoadingHandlers();
   }

   @FunctionalInterface
   public interface EventInvokerLike<T> {
      EventInvoker<T> asEventInvoker(@Nullable Object var1);
   }
}

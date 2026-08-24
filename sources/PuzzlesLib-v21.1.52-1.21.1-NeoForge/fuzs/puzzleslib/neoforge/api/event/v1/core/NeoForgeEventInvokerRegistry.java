package fuzs.puzzleslib.neoforge.api.event.v1.core;

import fuzs.puzzleslib.api.event.v1.core.EventInvokerRegistry;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import java.util.function.BiConsumer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public interface NeoForgeEventInvokerRegistry extends EventInvokerRegistry {
   NeoForgeEventInvokerRegistry INSTANCE = new NeoForgeEventInvokerRegistryImpl();

   default <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
      this.register(clazz, event, (callback, evt, context) -> converter.accept(callback, evt), false);
   }

   default <T, E extends Event> void register(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter, boolean joinInvokers) {
      this.register(clazz, event, (callback, evt, context) -> converter.accept(callback, evt), joinInvokers);
   }

   default <T, E extends Event> void register(Class<T> clazz, Class<E> event, NeoForgeEventInvokerRegistry.NeoForgeEventContextConsumer<T, E> converter) {
      this.register(clazz, event, converter, false);
   }

   <T, E extends Event> void register(Class<T> var1, Class<E> var2, NeoForgeEventInvokerRegistry.NeoForgeEventContextConsumer<T, E> var3, boolean var4);

   @FunctionalInterface
   public interface NeoForgeEventContextConsumer<T, E extends Event> {
      void accept(T var1, E var2, @Nullable Object var3);
   }
}

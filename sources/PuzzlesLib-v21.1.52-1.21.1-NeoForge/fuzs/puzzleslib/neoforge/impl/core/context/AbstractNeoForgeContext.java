package fuzs.puzzleslib.neoforge.impl.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.neoforged.bus.api.Event;

public abstract class AbstractNeoForgeContext {
   private final List<Consumer<Event>> listeners = new ArrayList<>();

   public static <T> T computeIfAbsent(T[] storage, Supplier<T> supplier, Consumer<T> consumer) {
      if (storage[0] == null) {
         storage[0] = supplier.get();
         consumer.accept(storage[0]);
      }

      return storage[0];
   }

   protected final <T extends Event> void registerForEvent(Class<T> eventClazz, Consumer<T> consumer) {
      this.listeners.add(event -> {
         if (eventClazz.isInstance(event)) {
            consumer.accept((T)event);
         }
      });
   }

   public final void registerForEvent(Event event) {
      this.listeners.forEach(consumer -> consumer.accept(event));
   }
}

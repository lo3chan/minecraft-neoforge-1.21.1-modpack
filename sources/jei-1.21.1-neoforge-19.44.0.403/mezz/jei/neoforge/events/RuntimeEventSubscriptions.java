package mezz.jei.neoforge.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mezz.jei.common.util.ErrorUtil;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;

public class RuntimeEventSubscriptions {
   private final List<EventSubscription<?>> subscriptions = new ArrayList<>();
   private final IEventBus eventBus;

   public RuntimeEventSubscriptions(IEventBus eventBus) {
      ErrorUtil.checkNotNull(eventBus, "eventBus");
      this.eventBus = eventBus;
   }

   public <T extends Event> void register(Class<T> eventType, Consumer<T> listener) {
      this.register(EventPriority.NORMAL, eventType, listener);
   }

   public <T extends Event> void register(EventPriority priority, Class<T> eventType, Consumer<T> listener) {
      if (IModBusEvent.class.isAssignableFrom(eventType)) {
         throw new IllegalArgumentException(String.format("%s must be registered on the mod event bus", eventType));
      } else {
         EventSubscription<T> subscription = EventSubscription.register(this.eventBus, priority, eventType, listener);
         this.subscriptions.add(subscription);
      }
   }

   public boolean isEmpty() {
      return this.subscriptions.isEmpty();
   }

   public void clear() {
      this.subscriptions.forEach(EventSubscription::unregister);
      this.subscriptions.clear();
   }
}

package dev.architectury.platform.hooks;

import java.util.Optional;
import java.util.function.Consumer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

public final class EventBusesHooks {
   private EventBusesHooks() {
   }

   public static void whenAvailable(String modId, Consumer<IEventBus> busConsumer) {
      IEventBus bus = getModEventBus(modId).orElseThrow(() -> new IllegalStateException("Mod '" + modId + "' is not available!"));
      busConsumer.accept(bus);
   }

   public static Optional<IEventBus> getModEventBus(String modId) {
      return ModList.get().getModContainerById(modId).map(ModContainer::getEventBus);
   }
}

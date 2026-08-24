package fuzs.puzzleslib.neoforge.api.core.v1;

import java.util.Objects;
import java.util.Optional;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;

public final class NeoForgeModContainerHelper {
   private NeoForgeModContainerHelper() {
   }

   public static IEventBus getActiveModEventBus() {
      return getOptionalActiveModEventBus().orElseThrow(() -> new NullPointerException("mod event bus is null"));
   }

   public static Optional<IEventBus> getOptionalActiveModEventBus() {
      return Optional.of(ModLoadingContext.get().getActiveContainer())
         .filter(modContainer -> !modContainer.getNamespace().equals("minecraft"))
         .map(ModContainer::getEventBus);
   }

   public static IEventBus getModEventBus(String modId) {
      return getOptionalModEventBus(modId).orElseThrow(() -> new NullPointerException("mod event bus for %s is null".formatted(modId)));
   }

   public static Optional<IEventBus> getOptionalModEventBus(String modId) {
      return getOptionalModContainer(modId).map(ModContainer::getEventBus);
   }

   public static ModContainer getModContainer(String modId) {
      return getOptionalModContainer(modId).orElseThrow(() -> new NullPointerException("mod container for %s is null".formatted(modId)));
   }

   public static Optional<? extends ModContainer> getOptionalModContainer(String modId) {
      ModList modList = ModList.get();
      Objects.requireNonNull(modList, "mod list is null");
      return modList.getModContainerById(modId);
   }
}

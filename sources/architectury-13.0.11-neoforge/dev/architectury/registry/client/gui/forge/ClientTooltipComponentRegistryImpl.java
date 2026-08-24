package dev.architectury.registry.client.gui.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
@Internal
public class ClientTooltipComponentRegistryImpl {
   @Nullable
   private static List<ClientTooltipComponentRegistryImpl.Entry<?>> entries = new ArrayList<>();

   public static <T extends TooltipComponent> void register(Class<T> clazz, Function<? super T, ? extends ClientTooltipComponent> factory) {
      if (entries == null) {
         throw new IllegalStateException("Cannot register ClientTooltipComponent factory when factories are already aggregated!");
      } else {
         entries.add(new ClientTooltipComponentRegistryImpl.Entry<>(clazz, factory));
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(EventPriority.HIGH, event -> {
         if (entries != null) {
            for (ClientTooltipComponentRegistryImpl.Entry<?> entry : entries) {
               event.register(entry.clazz(), entry.factory());
            }

            entries = null;
         }
      }));
   }

   public record Entry<T extends TooltipComponent>(Class<T> clazz, Function<? super T, ? extends ClientTooltipComponent> factory) {
   }
}

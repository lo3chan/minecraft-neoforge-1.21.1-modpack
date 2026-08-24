package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.KubeEvent;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class AtlasSpriteRegistryKubeEvent implements KubeEvent {
   private final Consumer<ResourceLocation> registry;

   public AtlasSpriteRegistryKubeEvent(Consumer<ResourceLocation> registry) {
      this.registry = registry;
   }

   public void register(ResourceLocation id) {
      this.registry.accept(id);
   }
}

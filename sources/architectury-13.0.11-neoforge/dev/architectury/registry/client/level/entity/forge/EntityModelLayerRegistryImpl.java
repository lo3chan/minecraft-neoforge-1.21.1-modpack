package dev.architectury.registry.client.level.entity.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

public class EntityModelLayerRegistryImpl {
   private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> DEFINITIONS = new ConcurrentHashMap<>();

   public static void register(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
      DEFINITIONS.put(location, definition);
   }

   @SubscribeEvent
   public static void event(RegisterLayerDefinitions event) {
      for (Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : DEFINITIONS.entrySet()) {
         event.registerLayerDefinition(entry.getKey(), entry.getValue());
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.register(EntityModelLayerRegistryImpl.class));
   }
}

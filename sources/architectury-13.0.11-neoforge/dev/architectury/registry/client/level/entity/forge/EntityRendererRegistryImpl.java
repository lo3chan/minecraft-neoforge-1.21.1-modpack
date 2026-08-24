package dev.architectury.registry.client.level.entity.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class EntityRendererRegistryImpl {
   private static final Map<Supplier<EntityType<?>>, EntityRendererProvider<?>> RENDERERS = new ConcurrentHashMap<>();

   public static <T extends Entity> void register(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> factory) {
      RENDERERS.put((Supplier<EntityType<?>>)type, factory);
   }

   @SubscribeEvent
   public static void event(RegisterRenderers event) {
      for (Entry<Supplier<EntityType<?>>, EntityRendererProvider<?>> entry : RENDERERS.entrySet()) {
         event.registerEntityRenderer(entry.getKey().get(), entry.getValue());
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.register(EntityRendererRegistryImpl.class));
   }
}

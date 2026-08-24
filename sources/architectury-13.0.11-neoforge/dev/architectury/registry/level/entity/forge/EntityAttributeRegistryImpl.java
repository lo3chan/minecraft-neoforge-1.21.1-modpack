package dev.architectury.registry.level.entity.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class EntityAttributeRegistryImpl {
   private static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<Builder>> ATTRIBUTES = new ConcurrentHashMap<>();

   public static void register(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<Builder> attribute) {
      ATTRIBUTES.put(type, attribute);
   }

   @SubscribeEvent
   public static void event(EntityAttributeCreationEvent event) {
      for (Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<Builder>> entry : ATTRIBUTES.entrySet()) {
         event.put(entry.getKey().get(), entry.getValue().get().build());
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.register(EntityAttributeRegistryImpl.class));
   }
}

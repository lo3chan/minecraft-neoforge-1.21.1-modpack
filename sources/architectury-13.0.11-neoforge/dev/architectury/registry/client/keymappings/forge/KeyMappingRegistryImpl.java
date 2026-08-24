package dev.architectury.registry.client.keymappings.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyMappingRegistryImpl {
   private static final Logger LOGGER = LogManager.getLogger(KeyMappingRegistryImpl.class);
   private static final List<KeyMapping> MAPPINGS = new ArrayList<>();
   private static boolean eventCalled = false;

   public static void register(KeyMapping mapping) {
      if (eventCalled) {
         Options options = Minecraft.getInstance().options;
         options.keyMappings = (KeyMapping[])ArrayUtils.add(options.keyMappings, mapping);
         LOGGER.warn("Key mapping %s registered after event".formatted(mapping.getName()), new RuntimeException());
      } else {
         MAPPINGS.add(mapping);
      }
   }

   public static void event(RegisterKeyMappingsEvent event) {
      MAPPINGS.forEach(event::register);
      eventCalled = true;
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(KeyMappingRegistryImpl::event));
   }
}

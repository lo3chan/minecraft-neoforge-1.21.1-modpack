package com.aetherteam.aether.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class AetherKeys {
   public static final KeyMapping OPEN_ACCESSORY_INVENTORY = new KeyMapping("key.aether.open_accessories.desc", 73, "key.aether.category");
   public static final KeyMapping GRAVITITE_JUMP_ABILITY = new KeyMapping("key.aether.gravitite_jump_ability.desc", 32, "key.aether.category");
   public static final KeyMapping INVISIBILITY_TOGGLE = new KeyMapping("key.aether.invisibility_toggle.desc", 86, "key.aether.category");

   public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
      event.register(OPEN_ACCESSORY_INVENTORY);
      event.register(GRAVITITE_JUMP_ABILITY);
      event.register(INVISIBILITY_TOGGLE);
   }
}

package net.blay09.mods.balm.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;

public interface BalmKeyMappingRegistrar {
   default KeyMapping register(String name, int keyCode, String category) {
      return this.register(name, Type.KEYSYM, keyCode, category);
   }

   default KeyMapping register(String name, Type type, int keyCode, String category) {
      return this.register(new KeyMapping(name, type, keyCode, category));
   }

   KeyMapping register(KeyMapping var1);
}

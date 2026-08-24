package dev.tr7zw.entityculling;

import dev.tr7zw.transition.loader.ModLoaderUtil;
import dev.tr7zw.transition.mc.GeneralUtil;
import net.minecraft.client.KeyMapping;

public final class KeybindHolder {
   public static final KeybindHolder INSTANCE = new KeybindHolder();
   private boolean initialized = false;
   public final KeyMapping keybind = GeneralUtil.createKeyMapping("key.entityculling.toggle", -1, "text.entityculling.title");
   public final KeyMapping keybindBoxes = GeneralUtil.createKeyMapping("key.entityculling.toggleBoxes", -1, "text.entityculling.title");

   private KeybindHolder() {
   }

   public void registerKeybinds() {
      if (!this.initialized) {
         this.initialized = true;
         ModLoaderUtil.registerKeybind(this.keybind);
      }
   }
}

package cc.cosmetica.cosmetica;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.gui.HomeScreen;
import cc.cosmetica.cosmetica.gui.OutfitWheelScreen;
import cc.cosmetica.cosmetica.gui.SnipeScreen;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.Sniper;
import cc.cosmetica.kupe.api.Screens;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Keybinds {
   public static final String COSMETICA_CATEGORY = "key.categories.cosmetica";
   public static final Map<Key, KeyMapping> SPECIAL_MAP = new HashMap<>();
   public static KeyMapping CUSTOMISE = new KeyMapping("key.cosmetica.customise", Type.KEYSYM, 344, "key.categories.cosmetica");
   public static KeyMapping SNIPE = registerSpecial(Type.MOUSE.getOrCreate(2), "snipe");
   public static KeyMapping SELECT_OUTFIT = new KeyMapping("key.cosmetica.select_outfit", Type.KEYSYM, 96, "key.categories.cosmetica");
   private static boolean rightShiftMenu = false;

   private static KeyMapping registerSpecial(Key defaultKey, String id) {
      KeyMapping mapping = new KeyMapping("key.cosmetica." + id, Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.cosmetica");
      SPECIAL_MAP.put(defaultKey, mapping);
      mapping.setKey(defaultKey);
      return mapping;
   }

   public static void processKeybinds() {
      Screen screen = Minecraft.getInstance().screen;
      boolean set = false;

      while (SELECT_OUTFIT.consumeClick()) {
         set = true;
      }

      if (set) {
         if (screen == null) {
            Minecraft.getInstance().setScreen(new OutfitWheelScreen());
         } else if (CosmeticaSettings.TOGGLE_OUTFIT_WHEEL.get() && screen instanceof OutfitWheelScreen) {
            Minecraft.getInstance().setScreen(null);
         }
      }

      set = false;

      while (CUSTOMISE.consumeClick()) {
         set = true;
      }

      if (screen == null) {
         rightShiftMenu = false;
      }

      if (CUSTOMISE.isDown()) {
         Logging.getInstance().debug(CosmeticaLogCategory.KEYBINDS, "rsm = " + rightShiftMenu + ", consumed click = " + set, new Object[0]);
      }

      if (set) {
         if (screen == null) {
            rightShiftMenu = true;
            Screens.setScreen(HomeScreen.ID);
         } else if (rightShiftMenu) {
            Minecraft.getInstance().setScreen(null);
         }
      }

      set = false;

      while (SNIPE.consumeClick()) {
         set = true;
      }

      if (set && screen == null) {
         LivingEntity entity = Sniper.getTarget();
         if (entity instanceof StateHolder && (entity instanceof Player || Cosmetics.getCosmetics(entity).isPresent())) {
            Screens.setScreen(new SnipeScreen(entity), SnipeScreen.ID);
         }
      }
   }
}

package net.joefoxe.hexerei.config;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@EventBusSubscriber(
   modid = "hexerei",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public final class ModKeyBindings {
   public static final KeyMapping broomDown;
   public static final KeyMapping broomDismount;
   public static final KeyMapping broomUp;
   public static final KeyMapping broomActivate;
   public static final KeyMapping bookJEIShowUses;
   public static final KeyMapping bookJEIShowRecipe;
   public static final KeyMapping glassesZoom;
   private static final String broom_category = "hexerei.key.category.broom";
   private static final String book_hovering_category = "hexerei.key.category.book_hovering";
   private static final String glasses_category = "hexerei.key.category.glasses";
   private static final List<KeyMapping> allBindings = List.of(
      broomDown = new KeyMapping("key.hexerei.broomDown", KeyConflictContext.IN_GAME, getKey(341), "hexerei.key.category.broom"),
      broomDismount = new KeyMapping("key.hexerei.broomDismount", KeyConflictContext.IN_GAME, getKey(340), "hexerei.key.category.broom"),
      broomUp = new KeyMapping("key.hexerei.broomUp", KeyConflictContext.IN_GAME, getKey(32), "hexerei.key.category.broom"),
      broomActivate = new KeyMapping("key.hexerei.broomActivate", KeyConflictContext.IN_GAME, getKey(71), "hexerei.key.category.broom"),
      bookJEIShowUses = new KeyMapping("key.hexerei.book_hovering_uses", KeyConflictContext.IN_GAME, getKey(85), "hexerei.key.category.book_hovering"),
      bookJEIShowRecipe = new KeyMapping("key.hexerei.book_hovering_recipe", KeyConflictContext.IN_GAME, getKey(82), "hexerei.key.category.book_hovering"),
      glassesZoom = new KeyMapping("key.hexerei.glasses_zoom", KeyConflictContext.IN_GAME, getKey(90), "hexerei.key.category.glasses")
   );

   static Key getKey(int key) {
      return Type.KEYSYM.getOrCreate(key);
   }

   private ModKeyBindings() {
   }

   @SubscribeEvent
   public static void registerKeybinds(RegisterKeyMappingsEvent ev) {
      for (KeyMapping binding : allBindings) {
         ev.register(binding);
      }
   }
}

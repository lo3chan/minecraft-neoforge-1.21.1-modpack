package com.alonie.brbe.compat.rei;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.compat.ItemViewCompat;
import net.minecraft.world.item.ItemStack;

public class ReiCompat {
   private static volatile boolean registered;

   public static void register() {
      if (isModLoaded("roughlyenoughitems")) {
         ItemViewCompat.setHandler(new ReiCompat.ReiHandler() {
            @Override
            public boolean openRecipeView(ItemStack stack) {
               return ReiCompat.openView("addRecipesFor", stack);
            }

            @Override
            public boolean openUsageView(ItemStack stack) {
               return ReiCompat.openView("addUsagesFor", stack);
            }

            @Override
            public boolean matchesShowRecipe(int keyCode, int scanCode) {
               return ReiCompat.matchesKeyBind("getRecipeKeybind", keyCode, scanCode);
            }

            @Override
            public boolean matchesShowUses(int keyCode, int scanCode) {
               return ReiCompat.matchesKeyBind("getUsageKeybind", keyCode, scanCode);
            }
         });
         registered = true;
      }
   }

   private static void ensureRegistered() {
      if (!registered) {
         registered = true;
         register();
      }
   }

   private static boolean openView(String methodName, ItemStack stack) {
      try {
         Class<?> clientHelperClass = Class.forName("me.shedaniel.rei.api.client.ClientHelper");
         Object instance = clientHelperClass.getMethod("getInstance").invoke(null);
         Class<?> builderClass = Class.forName("me.shedaniel.rei.api.client.view.ViewSearchBuilder");
         Object builder = builderClass.getMethod("builder").invoke(null);
         Class<?> entryStacksClass = Class.forName("me.shedaniel.rei.api.common.util.EntryStacks");
         Object entryStack = entryStacksClass.getMethod("of", ItemStack.class).invoke(null, stack);
         Class<?> entryStackClass = Class.forName("me.shedaniel.rei.api.common.entry.EntryStack");
         builderClass.getMethod(methodName, entryStackClass).invoke(builder, entryStack);
         return (Boolean)clientHelperClass.getMethod("openView", builderClass).invoke(instance, builder);
      } catch (ReflectiveOperationException var9) {
         BetterRecipeBook.LOGGER.warn("Failed to open REI view via {} for stack {}", methodName, stack, var9);
         return false;
      }
   }

   public static void setHandler(ReiCompat.ReiHandler h) {
      ItemViewCompat.setHandler(h);
   }

   public static boolean isLoaded() {
      ensureRegistered();
      return ItemViewCompat.isLoaded();
   }

   public static boolean openRecipeView(ItemStack stack) {
      return ItemViewCompat.openRecipeView(stack);
   }

   public static boolean openUsageView(ItemStack stack) {
      return ItemViewCompat.openUsageView(stack);
   }

   static boolean matchesKeyBind(String getterName, int keyCode, int scanCode) {
      try {
         Class<?> configObj = Class.forName("me.shedaniel.rei.api.client.config.ConfigObject");
         Object config = configObj.getMethod("getInstance").invoke(null);
         Object keybind = configObj.getMethod(getterName).invoke(config);
         return (Boolean)keybind.getClass().getMethod("matchesKey", int.class, int.class).invoke(keybind, keyCode, scanCode);
      } catch (Exception var6) {
         return false;
      }
   }

   private static boolean isModLoaded(String modId) {
      try {
         Class<?> modList = Class.forName("net.neoforged.fml.ModList");
         Object instance = modList.getMethod("get").invoke(null);
         return (Boolean)instance.getClass().getMethod("isLoaded", String.class).invoke(instance, modId);
      } catch (Throwable var5) {
         try {
            Class<?> fabricLoader = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object instancex = fabricLoader.getMethod("getInstance").invoke(null);
            return (Boolean)instancex.getClass().getMethod("isModLoaded", String.class).invoke(instancex, modId);
         } catch (Throwable var4) {
            return false;
         }
      }
   }

   public interface ReiHandler extends ItemViewCompat.Handler {
   }
}

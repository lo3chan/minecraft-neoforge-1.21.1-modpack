package com.alonie.brbe.compat.recipeviewer;

import net.minecraft.world.item.ItemStack;

public final class ReiViewer implements RecipeViewer {
   private static volatile boolean available;

   public static void markAvailable(boolean avail) {
      available = avail;
   }

   @Override
   public boolean isAvailable() {
      if (!available) {
         return false;
      } else {
         try {
            Class.forName("me.shedaniel.rei.api.client.ClientHelper");
            return true;
         } catch (ClassNotFoundException var2) {
            available = false;
            return false;
         }
      }
   }

   @Override
   public void showRecipe(ItemStack stack) {
      openView("addRecipesFor", stack);
   }

   @Override
   public void showUses(ItemStack stack) {
      openView("addUsagesFor", stack);
   }

   @Override
   public boolean matchesShowRecipe(int keyCode, int scanCode) {
      if (!available) {
         return false;
      } else {
         try {
            Class<?> configObj = Class.forName("me.shedaniel.rei.api.client.config.ConfigObject");
            Object config = configObj.getMethod("getInstance").invoke(null);
            Object keybind = configObj.getMethod("getRecipeKeybind").invoke(config);
            return (Boolean)keybind.getClass().getMethod("matchesKey", int.class, int.class).invoke(keybind, keyCode, scanCode);
         } catch (Exception var6) {
            return false;
         }
      }
   }

   @Override
   public boolean matchesShowUses(int keyCode, int scanCode) {
      if (!available) {
         return false;
      } else {
         try {
            Class<?> configObj = Class.forName("me.shedaniel.rei.api.client.config.ConfigObject");
            Object config = configObj.getMethod("getInstance").invoke(null);
            Object keybind = configObj.getMethod("getUsageKeybind").invoke(config);
            return (Boolean)keybind.getClass().getMethod("matchesKey", int.class, int.class).invoke(keybind, keyCode, scanCode);
         } catch (Exception var6) {
            return false;
         }
      }
   }

   private static void openView(String methodName, ItemStack stack) {
      if (!stack.isEmpty()) {
         try {
            Class<?> clientHelper = Class.forName("me.shedaniel.rei.api.client.ClientHelper");
            Object instance = clientHelper.getMethod("getInstance").invoke(null);
            Class<?> builderClass = Class.forName("me.shedaniel.rei.api.client.view.ViewSearchBuilder");
            Object builder = builderClass.getMethod("builder").invoke(null);
            Class<?> entryStacks = Class.forName("me.shedaniel.rei.api.common.util.EntryStacks");
            Object entryStack = entryStacks.getMethod("of", ItemStack.class).invoke(null, stack);
            Class<?> entryStackClass = Class.forName("me.shedaniel.rei.api.common.entry.EntryStack");
            builderClass.getMethod(methodName, entryStackClass).invoke(builder, entryStack);
            clientHelper.getMethod("openView", builderClass).invoke(instance, builder);
         } catch (Exception var9) {
         }
      }
   }

   public static boolean isReiLoaded() {
      try {
         Class<?> fabricLoader = Class.forName("net.fabricmc.loader.api.FabricLoader");
         Object instance = fabricLoader.getMethod("getInstance").invoke(null);
         return (Boolean)instance.getClass().getMethod("isModLoaded", String.class).invoke(instance, "roughlyenoughitems");
      } catch (Throwable var4) {
         try {
            Class<?> modList = Class.forName("net.neoforged.fml.ModList");
            Object instancex = modList.getMethod("get").invoke(null);
            return (Boolean)instancex.getClass().getMethod("isLoaded", String.class).invoke(instancex, "roughlyenoughitems");
         } catch (Throwable var3) {
            return false;
         }
      }
   }
}

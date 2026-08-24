package com.alonie.brbe.compat.recipeviewer;

import net.minecraft.world.item.ItemStack;

public final class EmiViewer implements RecipeViewer {
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
            Class.forName("dev.emi.emi.api.EmiApi");
            return true;
         } catch (ClassNotFoundException var2) {
            available = false;
            return false;
         }
      }
   }

   @Override
   public void showRecipe(ItemStack stack) {
      openView("displayRecipes", stack);
   }

   @Override
   public void showUses(ItemStack stack) {
      openView("displayUses", stack);
   }

   @Override
   public boolean matchesShowRecipe(int keyCode, int scanCode) {
      if (!available) {
         return false;
      } else {
         try {
            Class<?> emiConfig = Class.forName("dev.emi.emi.config.EmiConfig");
            Object viewRecipes = emiConfig.getField("viewRecipes").get(null);
            return (Boolean)viewRecipes.getClass().getMethod("matchesKey", int.class, int.class).invoke(viewRecipes, keyCode, scanCode);
         } catch (Exception var5) {
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
            Class<?> emiConfig = Class.forName("dev.emi.emi.config.EmiConfig");
            Object viewUses = emiConfig.getField("viewUses").get(null);
            return (Boolean)viewUses.getClass().getMethod("matchesKey", int.class, int.class).invoke(viewUses, keyCode, scanCode);
         } catch (Exception var5) {
            return false;
         }
      }
   }

   private static void openView(String methodName, ItemStack stack) {
      if (!stack.isEmpty()) {
         try {
            Class<?> emiStackClass = Class.forName("dev.emi.emi.api.stack.EmiStack");
            Object emiStack = emiStackClass.getMethod("of", ItemStack.class).invoke(null, stack);
            Class<?> emiApiClass = Class.forName("dev.emi.emi.api.EmiApi");
            emiApiClass.getMethod(methodName, Class.forName("dev.emi.emi.api.stack.EmiIngredient")).invoke(null, emiStack);
         } catch (Exception var5) {
         }
      }
   }

   public static boolean isEmiLoaded() {
      try {
         Class<?> modList = Class.forName("net.neoforged.fml.ModList");
         Object instance = modList.getMethod("get").invoke(null);
         return (Boolean)instance.getClass().getMethod("isLoaded", String.class).invoke(instance, "emi");
      } catch (Throwable var4) {
         try {
            Class<?> fabricLoader = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object instancex = fabricLoader.getMethod("getInstance").invoke(null);
            return (Boolean)instancex.getClass().getMethod("isModLoaded", String.class).invoke(instancex, "emi");
         } catch (Throwable var3) {
            return false;
         }
      }
   }
}

package com.alonie.brbe.compat.emi;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.compat.ItemViewCompat;
import net.minecraft.world.item.ItemStack;

public final class EmiCompat {
   private static volatile boolean registered;

   private EmiCompat() {
   }

   public static void register() {
      if (isModLoaded("emi")) {
         ItemViewCompat.setHandler(new EmiCompat.EmiHandler() {
            @Override
            public boolean openRecipeView(ItemStack stack) {
               return EmiCompat.openView("displayRecipes", stack);
            }

            @Override
            public boolean openUsageView(ItemStack stack) {
               return EmiCompat.openView("displayUses", stack);
            }

            @Override
            public boolean matchesShowRecipe(int keyCode, int scanCode) {
               return EmiCompat.matchesEmiBind("viewRecipes", keyCode, scanCode);
            }

            @Override
            public boolean matchesShowUses(int keyCode, int scanCode) {
               return EmiCompat.matchesEmiBind("viewUses", keyCode, scanCode);
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
      if (stack.isEmpty()) {
         return false;
      } else {
         try {
            Class<?> emiStackClass = Class.forName("dev.emi.emi.api.stack.EmiStack");
            Object emiStack = emiStackClass.getMethod("of", ItemStack.class).invoke(null, stack);
            Class<?> emiApiClass = Class.forName("dev.emi.emi.api.EmiApi");
            emiApiClass.getMethod(methodName, Class.forName("dev.emi.emi.api.stack.EmiIngredient")).invoke(null, emiStack);
            return true;
         } catch (ReflectiveOperationException var5) {
            BetterRecipeBook.LOGGER.debug("[BRBE] EMI view failed via {}: {}", methodName, var5.getMessage());
            return false;
         }
      }
   }

   public static boolean isLoaded() {
      ensureRegistered();
      return ItemViewCompat.isLoaded();
   }

   static boolean matchesEmiBind(String fieldName, int keyCode, int scanCode) {
      try {
         Class<?> emiConfig = Class.forName("dev.emi.emi.config.EmiConfig");
         Object emiBind = emiConfig.getField(fieldName).get(null);
         return (Boolean)emiBind.getClass().getMethod("matchesKey", int.class, int.class).invoke(emiBind, keyCode, scanCode);
      } catch (Exception var5) {
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

   public interface EmiHandler extends ItemViewCompat.Handler {
   }
}

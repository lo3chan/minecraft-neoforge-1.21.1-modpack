package io.wispforest.owo.util;

import io.wispforest.owo.Owo;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class OwoFreezer {
   private static final List<Runnable> FREEZE_CALLBACKS = new ArrayList<>();
   private static boolean IS_FROZEN = false;
   private static String FREEZER_CLASS = null;

   private OwoFreezer() {
   }

   public static void registerFreezeCallback(Runnable callback) {
      FREEZE_CALLBACKS.add(callback);
   }

   public static boolean isFrozen() {
      return IS_FROZEN;
   }

   public static void checkRegister(String pluralName) {
      if (isFrozen()) {
         throw new ServicesFrozenException(pluralName + " may only be registered during mod initialization");
      }
   }

   @Internal
   public static void freeze() {
      if (IS_FROZEN) {
         throw new ServicesFrozenException(
            ReflectionUtils.getCallingClassName(2) + " tried to freeze services after they were already frozen by " + FREEZER_CLASS
         );
      } else {
         IS_FROZEN = true;
         FREEZER_CLASS = ReflectionUtils.getCallingClassName(2);

         for (Runnable callback : FREEZE_CALLBACKS) {
            callback.run();
         }

         if (Owo.DEBUG) {
            Owo.LOGGER.info("Services frozen by '" + FREEZER_CLASS + "'");
         }
      }
   }
}

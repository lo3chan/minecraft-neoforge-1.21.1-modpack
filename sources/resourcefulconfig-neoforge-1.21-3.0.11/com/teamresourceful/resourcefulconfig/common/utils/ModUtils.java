package com.teamresourceful.resourcefulconfig.common.utils;

import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefulconfig.common.utils.neoforge.ModUtilsImpl;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.lang.reflect.Array;
import java.nio.file.Path;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

public final class ModUtils {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Contract(
      pure = true
   )
   @ExpectPlatform
   @Transformed
   public static Path getConfigPath() {
      return ModUtilsImpl.getConfigPath();
   }

   @Contract(
      pure = true
   )
   @ExpectPlatform
   @Transformed
   public static boolean isDev() {
      return ModUtilsImpl.isDev();
   }

   public static void log(String message) {
      LOGGER.info("[ResourcefulConfig] {}", message);
   }

   public static void log(String message, Throwable throwable) {
      LOGGER.info("[ResourcefulConfig] {}", message, throwable);
   }

   public static void debug(String message) {
      LOGGER.debug("[ResourcefulConfig] {}", message);
   }

   public static <T> T[] castArray(Object[] array, Class<T> clazz) {
      T[] newArray = (T[])Array.newInstance(clazz, array.length);

      for (int i = 0; i < array.length; i++) {
         newArray[i] = clazz.cast(array[i]);
      }

      return newArray;
   }
}

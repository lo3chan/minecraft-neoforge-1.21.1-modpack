package me.lucko.spark.common.util.classfinder;

import org.checkerframework.checker.nullness.qual.Nullable;

public enum FallbackClassFinder implements ClassFinder {
   INSTANCE;

   @Nullable
   @Override
   public Class<?> findClass(String className) {
      try {
         return Class.forName(className);
      } catch (Throwable var3) {
         return null;
      }
   }
}

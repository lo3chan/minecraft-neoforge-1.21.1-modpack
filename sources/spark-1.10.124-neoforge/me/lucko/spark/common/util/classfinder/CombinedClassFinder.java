package me.lucko.spark.common.util.classfinder;

import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

class CombinedClassFinder implements ClassFinder {
   private final List<ClassFinder> finders;

   CombinedClassFinder(List<ClassFinder> finders) {
      this.finders = finders;
   }

   @Nullable
   @Override
   public Class<?> findClass(String className) {
      for (ClassFinder finder : this.finders) {
         Class<?> clazz = finder.findClass(className);
         if (clazz != null) {
            return clazz;
         }
      }

      return null;
   }
}

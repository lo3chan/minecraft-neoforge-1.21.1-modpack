package me.lucko.spark.common.util.classfinder;

import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ClassFinder {
   static ClassFinder combining(ClassFinder... finders) {
      return new CombinedClassFinder(ImmutableList.copyOf(finders));
   }

   @Nullable
   Class<?> findClass(String var1);
}

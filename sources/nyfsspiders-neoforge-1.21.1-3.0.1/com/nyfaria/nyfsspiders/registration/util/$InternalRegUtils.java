package com.nyfaria.nyfsspiders.registration.util;

import java.util.Iterator;
import java.util.ServiceLoader;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class $InternalRegUtils {
   public static <T> T getOneAndOnlyService(Class<T> clazz) {
      ServiceLoader<T> loader = ServiceLoader.load(clazz);
      Iterator<T> it = loader.iterator();
      if (!it.hasNext()) {
         throw new RuntimeException("No " + clazz + " was found on the classpath!");
      } else {
         T instance = it.next();
         if (it.hasNext()) {
            throw new RuntimeException("More than one " + clazz + " was found on the classpath!");
         } else {
            return instance;
         }
      }
   }
}

package net.mehvahdjukaar.moonlight.api.misc;

import java.lang.reflect.Method;

public class TMethod<C, T> {
   private final Method method;

   public static <C, T> TMethod<C, T> of(Class<C> clazz, String name, Class<?>... parameterTypes) {
      return new TMethod<>(clazz, name, parameterTypes);
   }

   public TMethod(Class<C> clazz, String name, Class<?>... parameterTypes) {
      try {
         this.method = clazz.getDeclaredMethod(name, parameterTypes);
         this.method.setAccessible(true);
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException(var5);
      }
   }

   public T invoke(C obj, Object... args) {
      try {
         return (T)this.method.invoke(obj, args);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }
}

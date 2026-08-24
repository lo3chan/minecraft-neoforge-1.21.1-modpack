package net.mehvahdjukaar.moonlight.api.misc;

import java.lang.reflect.Field;

public class TField<C, T> {
   private final Field field;

   public static <C, T> TField<C, T> of(Class<C> clazz, String name) {
      return new TField<>(clazz, name);
   }

   public TField(Class<C> field, String name) {
      try {
         this.field = field.getDeclaredField(name);
         this.field.setAccessible(true);
      } catch (NoSuchFieldException var4) {
         throw new RuntimeException(var4);
      }
   }

   public T get(C obj) {
      try {
         return (T)this.field.get(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void set(C obj, T value) {
      try {
         this.field.set(obj, value);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      }
   }
}

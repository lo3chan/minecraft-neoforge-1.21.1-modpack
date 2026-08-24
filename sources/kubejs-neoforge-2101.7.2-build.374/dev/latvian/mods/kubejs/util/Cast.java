package dev.latvian.mods.kubejs.util;

public interface Cast {
   static <T> T to(Object o) {
      return (T)o;
   }
}

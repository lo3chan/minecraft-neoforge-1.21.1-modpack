package dev.latvian.mods.rhino;

public interface Wrapper {
   static Object unwrapped(Object o) {
      return o instanceof Wrapper w ? unwrapped(w.unwrap()) : o;
   }

   Object unwrap();
}

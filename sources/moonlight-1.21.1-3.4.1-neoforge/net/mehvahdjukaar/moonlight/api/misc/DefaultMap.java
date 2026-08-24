package net.mehvahdjukaar.moonlight.api.misc;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DefaultMap<A, B>(B obj) implements Map<A, B> {
   public static <A, B> DefaultMap<A, B> of(B obj) {
      return new DefaultMap<>(obj);
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public boolean containsKey(Object o) {
      return true;
   }

   @Override
   public boolean containsValue(Object o) {
      return o.equals(this.obj);
   }

   @Override
   public B get(Object o) {
      return null;
   }

   @Nullable
   @Override
   public B put(A a, B b) {
      throw new UnsupportedOperationException();
   }

   @Override
   public B remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(@NotNull Map<? extends A, ? extends B> map) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   @Override
   public Set<A> keySet() {
      return Set.of();
   }

   @NotNull
   @Override
   public Collection<B> values() {
      return List.of(this.obj);
   }

   @NotNull
   @Override
   public Set<Entry<A, B>> entrySet() {
      return Set.of();
   }
}

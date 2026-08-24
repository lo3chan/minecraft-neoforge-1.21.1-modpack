package com.teamresourceful.resourcefulconfig.api.types.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Observable<T> implements Supplier<T>, Consumer<T> {
   private final List<BiConsumer<T, T>> listeners = new ArrayList<>();
   private T value;

   private Observable(T value) {
      this.value = value;
   }

   public Class<?> type() {
      return this.value.getClass();
   }

   @Override
   public T get() {
      return this.value;
   }

   @Override
   public void accept(T t) {
      T previous = this.value;
      this.value = t;
      this.listeners.forEach(listener -> listener.accept(previous, this.value));
   }

   @Internal
   public void setAndCast(Object value) {
      this.accept((T)value);
   }

   public void addListener(BiConsumer<T, T> listener) {
      this.listeners.add(listener);
   }

   public static <T> Observable<T> of(T value) {
      return new Observable<>(value);
   }
}

package com.finndog.moogs_structures.events.base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public class ReturnableEventHandler<T, R> {
   private final List<ReturnableEventHandler.ReturnableFunction<T, R>> listeners = new ArrayList<>();

   public void addListener(ReturnableEventHandler.ReturnableFunction<T, R> listener) {
      this.listeners.add(listener);
   }

   public void addListener(Function<T, R> listener) {
      this.listeners.add((value, event) -> listener.apply(event));
   }

   public void addListener(BiConsumer<R, T> listener) {
      this.addListener((value, event) -> {
         listener.accept(value, event);
         return null;
      });
   }

   public void removeListener(ReturnableEventHandler.ReturnableFunction<T, R> listener) {
      this.listeners.remove(listener);
   }

   public R invoke(T event, R defaultValue) {
      R value = defaultValue;

      for (ReturnableEventHandler.ReturnableFunction<T, R> listener : this.listeners) {
         R result = listener.apply(value, event);
         if (result != null) {
            value = result;
         }
      }

      return value;
   }

   public R invoke(T event) {
      return this.invoke(event, null);
   }

   @FunctionalInterface
   public interface ReturnableFunction<T, R> {
      @Nullable
      R apply(R var1, T var2);
   }
}

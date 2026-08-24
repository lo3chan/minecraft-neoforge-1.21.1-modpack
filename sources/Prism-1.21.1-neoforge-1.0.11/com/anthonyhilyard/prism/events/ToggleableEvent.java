package com.anthonyhilyard.prism.events;

import java.lang.reflect.Array;
import java.util.function.Function;

public class ToggleableEvent<T> {
   private T dummyInvoker;
   private boolean disabled = false;
   private Event<T> event;

   private ToggleableEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
      this.event = EventFactory.create(type, invokerFactory);
      this.dummyInvoker = invokerFactory.apply((Object[])Array.newInstance(type, 0));
   }

   public static <T> ToggleableEvent<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
      return new ToggleableEvent<>(type, invokerFactory);
   }

   public void register(T listener) {
      this.event.register(listener);
   }

   public T invoker() {
      return !this.disabled ? this.event.invoker() : this.dummyInvoker;
   }

   public boolean disable() {
      if (this.disabled) {
         return false;
      } else {
         this.disabled = true;
         return true;
      }
   }

   public boolean enable() {
      if (!this.disabled) {
         return false;
      } else {
         this.disabled = false;
         return true;
      }
   }
}

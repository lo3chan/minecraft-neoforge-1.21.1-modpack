package com.anthonyhilyard.prism.events;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class Event<T> {
   protected volatile T invoker;
   private final Function<T[], T> invokerFactory;
   private final Object lock = new Object();
   private T[] listeners;

   public final T invoker() {
      return this.invoker;
   }

   private void addListener(T listener) {
      int oldLength = this.listeners.length;
      this.listeners = (T[])Arrays.copyOf(this.listeners, oldLength + 1);
      this.listeners[oldLength] = listener;
   }

   public Event(Class<? super T> type, Function<T[], T> invokerFactory) {
      this.invokerFactory = invokerFactory;
      this.listeners = (T[])((Object[])Array.newInstance(type, 0));
      this.update();
   }

   void update() {
      this.invoker = this.invokerFactory.apply((T)this.listeners);
   }

   public void register(T listener) {
      Objects.requireNonNull(listener, "Tried to register a null listener!");
      synchronized (this.lock) {
         this.addListener(listener);
         this.update();
      }
   }

   public int listenerCount() {
      return this.listeners.length;
   }
}

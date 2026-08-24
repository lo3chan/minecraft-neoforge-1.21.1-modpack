package net.bettercombat.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Publisher<T> {
   private final List<T> handlers = new ArrayList<>();

   public void register(T listener) {
      this.handlers.add(listener);
   }

   public void invoke(Consumer<T> function) {
      for (T handler : this.handlers) {
         function.accept(handler);
      }
   }
}

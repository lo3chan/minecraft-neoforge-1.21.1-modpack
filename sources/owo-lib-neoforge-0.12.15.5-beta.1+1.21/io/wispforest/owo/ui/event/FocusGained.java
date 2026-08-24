package io.wispforest.owo.ui.event;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.util.EventStream;

public interface FocusGained {
   void onFocusGained(Component.FocusSource var1);

   static EventStream<FocusGained> newStream() {
      return new EventStream<>(subscribers -> source -> {
         for (FocusGained subscriber : subscribers) {
            subscriber.onFocusGained(source);
         }
      });
   }
}

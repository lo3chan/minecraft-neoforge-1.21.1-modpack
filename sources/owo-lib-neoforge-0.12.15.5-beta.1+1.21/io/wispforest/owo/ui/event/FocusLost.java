package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface FocusLost {
   void onFocusLost();

   static EventStream<FocusLost> newStream() {
      return new EventStream<>(subscribers -> () -> {
         for (FocusLost subscriber : subscribers) {
            subscriber.onFocusLost();
         }
      });
   }
}

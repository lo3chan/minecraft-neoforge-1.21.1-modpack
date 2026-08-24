package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface KeyPress {
   boolean onKeyPress(int var1, int var2, int var3);

   static EventStream<KeyPress> newStream() {
      return new EventStream<>(subscribers -> (keyCode, scanCode, modifiers) -> {
         boolean anyTriggered = false;

         for (KeyPress subscriber : subscribers) {
            anyTriggered |= subscriber.onKeyPress(keyCode, scanCode, modifiers);
         }

         return anyTriggered;
      });
   }
}

package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface MouseDown {
   boolean onMouseDown(double var1, double var3, int var5);

   static EventStream<MouseDown> newStream() {
      return new EventStream<>(subscribers -> (mouseX, mouseY, button) -> {
         boolean anyTriggered = false;

         for (MouseDown subscriber : subscribers) {
            anyTriggered |= subscriber.onMouseDown(mouseX, mouseY, button);
         }

         return anyTriggered;
      });
   }
}

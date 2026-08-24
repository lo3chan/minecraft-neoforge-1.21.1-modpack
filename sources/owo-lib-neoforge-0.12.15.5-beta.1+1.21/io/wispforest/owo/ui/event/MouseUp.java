package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface MouseUp {
   boolean onMouseUp(double var1, double var3, int var5);

   static EventStream<MouseUp> newStream() {
      return new EventStream<>(subscribers -> (mouseX, mouseY, button) -> {
         boolean anyTriggered = false;

         for (MouseUp subscriber : subscribers) {
            anyTriggered |= subscriber.onMouseUp(mouseX, mouseY, button);
         }

         return anyTriggered;
      });
   }
}

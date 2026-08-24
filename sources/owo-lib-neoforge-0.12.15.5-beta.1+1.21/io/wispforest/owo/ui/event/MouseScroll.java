package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface MouseScroll {
   boolean onMouseScroll(double var1, double var3, double var5);

   static EventStream<MouseScroll> newStream() {
      return new EventStream<>(subscribers -> (mouseX, mouseY, amount) -> {
         boolean anyTriggered = false;

         for (MouseScroll subscriber : subscribers) {
            anyTriggered |= subscriber.onMouseScroll(mouseX, mouseY, amount);
         }

         return anyTriggered;
      });
   }
}

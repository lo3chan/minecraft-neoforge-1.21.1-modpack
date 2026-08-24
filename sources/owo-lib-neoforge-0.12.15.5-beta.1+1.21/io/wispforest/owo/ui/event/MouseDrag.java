package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface MouseDrag {
   boolean onMouseDrag(double var1, double var3, double var5, double var7, int var9);

   static EventStream<MouseDrag> newStream() {
      return new EventStream<>(subscribers -> (mouseX, mouseY, deltaX, deltaY, button) -> {
         boolean anyTriggered = false;

         for (MouseDrag subscriber : subscribers) {
            anyTriggered |= subscriber.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
         }

         return anyTriggered;
      });
   }
}

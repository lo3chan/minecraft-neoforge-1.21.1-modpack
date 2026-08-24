package io.wispforest.owo.ui.event;

import io.wispforest.owo.util.EventStream;

public interface CharTyped {
   boolean onCharTyped(char var1, int var2);

   static EventStream<CharTyped> newStream() {
      return new EventStream<>(subscribers -> (chr, modifiers) -> {
         boolean anyTriggered = false;

         for (CharTyped subscriber : subscribers) {
            anyTriggered |= subscriber.onCharTyped(chr, modifiers);
         }

         return anyTriggered;
      });
   }
}

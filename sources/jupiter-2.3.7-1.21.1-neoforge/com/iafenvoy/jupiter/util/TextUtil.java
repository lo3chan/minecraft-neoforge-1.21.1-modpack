package com.iafenvoy.jupiter.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface TextUtil {
   static MutableComponent empty() {
      return Component.empty();
   }

   static MutableComponent literal(String text) {
      return Component.literal(text);
   }

   static MutableComponent translatable(String text, Object... args) {
      return Component.translatable(text, args);
   }

   static MutableComponent translatableWithFallback(String text, String fallback, Object... args) {
      return Component.translatableWithFallback(text, fallback, args);
   }
}

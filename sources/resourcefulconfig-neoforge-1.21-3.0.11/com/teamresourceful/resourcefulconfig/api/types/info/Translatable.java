package com.teamresourceful.resourcefulconfig.api.types.info;

import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public interface Translatable {
   String getTranslationKey();

   static Component toComponent(Object value) {
      return toComponent(value, Component.empty());
   }

   static Component toComponent(Object value, Component defaultValue) {
      if (value instanceof Translatable translatable) {
         return Component.translatable(translatable.getTranslationKey());
      } else if (value instanceof StringRepresentable string) {
         return Component.literal(string.getSerializedName());
      } else {
         return (Component)(value == null ? defaultValue : Component.literal(Objects.toString(value)));
      }
   }

   static Component toSpeifiedComponent(Object value, Component specified) {
      if (value instanceof Translatable translatable) {
         return Component.translatable(translatable.getTranslationKey());
      } else {
         return (Component)(value instanceof StringRepresentable string ? Component.literal(string.getSerializedName()) : specified);
      }
   }
}

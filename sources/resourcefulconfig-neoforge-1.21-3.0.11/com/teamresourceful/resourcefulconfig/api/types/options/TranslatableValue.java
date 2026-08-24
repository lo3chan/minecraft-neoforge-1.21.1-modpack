package com.teamresourceful.resourcefulconfig.api.types.options;

import java.util.function.BiConsumer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record TranslatableValue(String value, String translation) {
   public static final TranslatableValue EMPTY = new TranslatableValue("", "");

   public TranslatableValue(String value) {
      this(value, "");
   }

   public void ifPresent(BiConsumer<String, String> value) {
      if (!this.value.isBlank()) {
         value.accept(this.value, this.translation);
      }
   }

   public MutableComponent toComponent() {
      return this.translation().isBlank() ? Component.literal(this.value()) : Component.translatable(this.translation());
   }

   public String toLocalizedString() {
      return this.translation().isBlank() ? this.value() : Language.getInstance().getOrDefault(this.translation());
   }

   public boolean hasTranslation() {
      return !this.translation().isBlank();
   }
}

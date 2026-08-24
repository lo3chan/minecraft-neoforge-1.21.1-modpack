package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.TextFieldConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public class StringEntry extends BaseEntry<String> implements TextFieldConfigEntry {
   protected StringEntry(StringEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public StringEntry(String nameKey, String defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public ConfigType<String> getType() {
      return ConfigTypes.STRING;
   }

   @Override
   public ConfigEntry<String> newInstance() {
      return new StringEntry.Builder(this).build();
   }

   @Override
   public Codec<String> getCodec() {
      return Codec.STRING;
   }

   @Override
   public String valueAsString() {
      return this.getValue();
   }

   @Override
   public void setValueFromString(String s) {
      this.setValue(s);
   }

   public static StringEntry.Builder builder(Component name, String defaultValue) {
      return new StringEntry.Builder(name, defaultValue);
   }

   public static StringEntry.Builder builder(String nameKey, String defaultValue) {
      return new StringEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<String, StringEntry, StringEntry.Builder> {
      public Builder(Component name, String defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, String defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(StringEntry parent) {
         super(parent);
      }

      public StringEntry.Builder self() {
         return this;
      }

      protected StringEntry buildInternal() {
         return new StringEntry(this);
      }
   }
}

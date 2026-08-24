package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public class BooleanEntry extends BaseEntry<Boolean> {
   protected BooleanEntry(BooleanEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BooleanEntry(String nameKey, boolean defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public ConfigType<Boolean> getType() {
      return ConfigTypes.BOOLEAN;
   }

   @Override
   public ConfigEntry<Boolean> newInstance() {
      return new BooleanEntry.Builder(this).build();
   }

   @Override
   public Codec<Boolean> getCodec() {
      return Codec.BOOL;
   }

   public static BooleanEntry.Builder builder(Component name, boolean defaultValue) {
      return new BooleanEntry.Builder(name, defaultValue);
   }

   public static BooleanEntry.Builder builder(String nameKey, boolean defaultValue) {
      return new BooleanEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Boolean, BooleanEntry, BooleanEntry.Builder> {
      public Builder(Component name, boolean defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, boolean defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(BooleanEntry parent) {
         super(parent);
      }

      public BooleanEntry.Builder self() {
         return this;
      }

      protected BooleanEntry buildInternal() {
         return new BooleanEntry(this);
      }
   }
}

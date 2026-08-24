package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.interfaces.IConfigEnumEntry;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.EnumHelper;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class EnumEntry<T extends Enum<T>> extends BaseEntry<T> {
   @Nullable
   protected Function<T, Component> nameProvider;

   protected EnumEntry(EnumEntry.Builder<T> builder) {
      super(builder);
      this.nameProvider = builder.nameProvider;
   }

   @Deprecated(
      forRemoval = true
   )
   public EnumEntry(String nameKey, IConfigEnumEntry defaultValue) {
      this(nameKey, (T)defaultValue);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public EnumEntry(String nameKey, T defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public ConfigType<T> getType() {
      return ConfigTypes.ENUM;
   }

   @Override
   public ConfigEntry<T> newInstance() {
      return new EnumEntry.Builder<>(this).build();
   }

   @Override
   public Codec<T> getCodec() {
      return EnumHelper.getCodec(this.value);
   }

   public static <T extends Enum<T>> EnumEntry.Builder<T> builder(Component name, T defaultValue) {
      return new EnumEntry.Builder<>(name, defaultValue);
   }

   public static <T extends Enum<T>> EnumEntry.Builder<T> builder(String nameKey, T defaultValue) {
      return new EnumEntry.Builder<>(nameKey, defaultValue);
   }

   public static class Builder<T extends Enum<T>> extends BaseEntry.Builder<T, EnumEntry<T>, EnumEntry.Builder<T>> {
      protected Function<T, Component> nameProvider;

      public Builder(Component name, T defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, T defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(EnumEntry<T> parent) {
         super(parent);
      }

      public EnumEntry.Builder<T> nameProvider(Function<T, Component> nameProvider) {
         this.nameProvider = nameProvider;
         return this;
      }

      public EnumEntry.Builder<T> self() {
         return this;
      }

      protected EnumEntry<T> buildInternal() {
         return new EnumEntry<>(this);
      }
   }
}

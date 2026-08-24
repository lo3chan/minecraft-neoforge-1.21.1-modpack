package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.EnumHelper;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListEnumEntry<T extends Enum<T>> extends ListBaseEntry<T> {
   private final T newValue;

   protected ListEnumEntry(ListEnumEntry.Builder<T> builder) {
      super(builder);
      this.newValue = builder.newValue;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListEnumEntry(String nameKey, List<T> defaultValue, T newValue) {
      super(nameKey, defaultValue);
      this.newValue = newValue;
   }

   @Override
   public Codec<T> getValueCodec() {
      return EnumHelper.getCodec(this.newValue);
   }

   public ConfigEntry<T> newSingleInstance(T value, int index, Runnable reload) {
      return EnumEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            this.getValue().remove(index);
            reload.run();
         } else {
            this.getValue().set(index, v);
         }

         this.setValue(this.getValue());
      }).build();
   }

   public T newValue() {
      return this.newValue;
   }

   @Override
   public ConfigType<List<T>> getType() {
      return ConfigTypes.LIST_ENUM;
   }

   @Override
   public ConfigEntry<List<T>> newInstance() {
      return new ListEnumEntry.Builder<>(this).build();
   }

   public static <T extends Enum<T>> ListEnumEntry.Builder<T> builder(Component name, List<T> defaultValue, T newValue) {
      return new ListEnumEntry.Builder<>(name, defaultValue, newValue);
   }

   public static <T extends Enum<T>> ListEnumEntry.Builder<T> builder(String nameKey, List<T> defaultValue, T newValue) {
      return new ListEnumEntry.Builder<>(nameKey, defaultValue, newValue);
   }

   public static class Builder<T extends Enum<T>> extends BaseEntry.Builder<List<T>, ListEnumEntry<T>, ListEnumEntry.Builder<T>> {
      protected final T newValue;

      public Builder(Component name, List<T> defaultValue, T newValue) {
         super(name, defaultValue);
         this.newValue = newValue;
      }

      public Builder(String nameKey, List<T> defaultValue, T newValue) {
         super(nameKey, defaultValue);
         this.newValue = newValue;
      }

      public Builder(ListEnumEntry<T> parent) {
         super(parent);
         this.newValue = parent.newValue;
      }

      public ListEnumEntry.Builder<T> self() {
         return this;
      }

      protected ListEnumEntry<T> buildInternal() {
         return new ListEnumEntry<>(this);
      }
   }
}

package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListLongEntry extends ListBaseEntry<Long> {
   protected ListLongEntry(ListLongEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListLongEntry(String nameKey, List<Long> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<Long> getValueCodec() {
      return Codec.LONG;
   }

   public ConfigEntry<Long> newSingleInstance(Long value, int index, Runnable reload) {
      return LongEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            ((List)this.getValue()).remove(index);
            reload.run();
         } else {
            ((List)this.getValue()).set(index, v);
         }

         this.setValue((List<Long>)this.getValue());
      }).build();
   }

   public Long newValue() {
      return 0L;
   }

   @Override
   public ConfigType<List<Long>> getType() {
      return ConfigTypes.LIST_LONG;
   }

   @Override
   public ConfigEntry<List<Long>> newInstance() {
      return new ListLongEntry.Builder(this).build();
   }

   public static ListLongEntry.Builder builder(Component name, List<Long> defaultValue) {
      return new ListLongEntry.Builder(name, defaultValue);
   }

   public static ListLongEntry.Builder builder(String nameKey, List<Long> defaultValue) {
      return new ListLongEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<List<Long>, ListLongEntry, ListLongEntry.Builder> {
      public Builder(Component name, List<Long> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, List<Long> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ListLongEntry parent) {
         super(parent);
      }

      public ListLongEntry.Builder self() {
         return this;
      }

      protected ListLongEntry buildInternal() {
         return new ListLongEntry(this);
      }
   }
}

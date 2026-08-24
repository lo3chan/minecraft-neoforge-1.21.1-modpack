package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListIntegerEntry extends ListBaseEntry<Integer> {
   protected ListIntegerEntry(ListIntegerEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListIntegerEntry(String nameKey, List<Integer> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<Integer> getValueCodec() {
      return Codec.INT;
   }

   public ConfigEntry<Integer> newSingleInstance(Integer value, int index, Runnable reload) {
      return IntegerEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            ((List)this.getValue()).remove(index);
            reload.run();
         } else {
            ((List)this.getValue()).set(index, v);
         }

         this.setValue((List<Integer>)this.getValue());
      }).build();
   }

   public Integer newValue() {
      return 0;
   }

   @Override
   public ConfigType<List<Integer>> getType() {
      return ConfigTypes.LIST_INTEGER;
   }

   @Override
   public ConfigEntry<List<Integer>> newInstance() {
      return new ListIntegerEntry.Builder(this).build();
   }

   public static ListIntegerEntry.Builder builder(Component name, List<Integer> defaultValue) {
      return new ListIntegerEntry.Builder(name, defaultValue);
   }

   public static ListIntegerEntry.Builder builder(String nameKey, List<Integer> defaultValue) {
      return new ListIntegerEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<List<Integer>, ListIntegerEntry, ListIntegerEntry.Builder> {
      public Builder(Component name, List<Integer> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, List<Integer> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ListIntegerEntry parent) {
         super(parent);
      }

      public ListIntegerEntry.Builder self() {
         return this;
      }

      protected ListIntegerEntry buildInternal() {
         return new ListIntegerEntry(this);
      }
   }
}

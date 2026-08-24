package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListStringEntry extends ListBaseEntry<String> {
   protected ListStringEntry(ListStringEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListStringEntry(String nameKey, List<String> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<String> getValueCodec() {
      return Codec.STRING;
   }

   public ConfigEntry<String> newSingleInstance(String value, int index, Runnable reload) {
      return StringEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            ((List)this.getValue()).remove(index);
            reload.run();
         } else {
            ((List)this.getValue()).set(index, v);
         }

         this.setValue((List<String>)this.getValue());
      }).build();
   }

   public String newValue() {
      return "";
   }

   @Override
   public ConfigType<List<String>> getType() {
      return ConfigTypes.LIST_STRING;
   }

   @Override
   public ConfigEntry<List<String>> newInstance() {
      return new ListStringEntry.Builder(this).build();
   }

   public static ListStringEntry.Builder builder(Component name, List<String> defaultValue) {
      return new ListStringEntry.Builder(name, defaultValue);
   }

   public static ListStringEntry.Builder builder(String nameKey, List<String> defaultValue) {
      return new ListStringEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<List<String>, ListStringEntry, ListStringEntry.Builder> {
      public Builder(Component name, List<String> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, List<String> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ListStringEntry parent) {
         super(parent);
      }

      public ListStringEntry.Builder self() {
         return this;
      }

      protected ListStringEntry buildInternal() {
         return new ListStringEntry(this);
      }
   }
}

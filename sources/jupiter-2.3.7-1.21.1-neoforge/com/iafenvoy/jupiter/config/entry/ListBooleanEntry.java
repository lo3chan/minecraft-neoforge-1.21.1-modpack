package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListBooleanEntry extends ListBaseEntry<Boolean> {
   protected ListBooleanEntry(ListBooleanEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListBooleanEntry(String nameKey, List<Boolean> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<Boolean> getValueCodec() {
      return Codec.BOOL;
   }

   public ConfigEntry<Boolean> newSingleInstance(Boolean value, int index, Runnable reload) {
      return BooleanEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            ((List)this.getValue()).remove(index);
            reload.run();
         } else {
            ((List)this.getValue()).set(index, v);
         }

         this.setValue((List<Boolean>)this.getValue());
      }).build();
   }

   public Boolean newValue() {
      return false;
   }

   @Override
   public ConfigType<List<Boolean>> getType() {
      return ConfigTypes.LIST_BOOLEAN;
   }

   @Override
   public ConfigEntry<List<Boolean>> newInstance() {
      return new ListBooleanEntry.Builder(this).build();
   }

   public static ListBooleanEntry.Builder builder(Component name, List<Boolean> defaultValue) {
      return new ListBooleanEntry.Builder(name, defaultValue);
   }

   public static ListBooleanEntry.Builder builder(String nameKey, List<Boolean> defaultValue) {
      return new ListBooleanEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<List<Boolean>, ListBooleanEntry, ListBooleanEntry.Builder> {
      public Builder(Component name, List<Boolean> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, List<Boolean> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ListBooleanEntry parent) {
         super(parent);
      }

      public ListBooleanEntry.Builder self() {
         return this;
      }

      protected ListBooleanEntry buildInternal() {
         return new ListBooleanEntry(this);
      }
   }
}

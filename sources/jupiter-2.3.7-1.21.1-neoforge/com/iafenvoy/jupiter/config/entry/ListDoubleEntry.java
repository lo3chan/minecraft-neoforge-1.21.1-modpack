package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ListDoubleEntry extends ListBaseEntry<Double> {
   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ListDoubleEntry(String nameKey, List<Double> defaultValue) {
      super(nameKey, defaultValue);
   }

   protected ListDoubleEntry(ListDoubleEntry.Builder builder) {
      super(builder);
   }

   @Override
   public Codec<Double> getValueCodec() {
      return Codec.DOUBLE;
   }

   public ConfigEntry<Double> newSingleInstance(Double value, int index, Runnable reload) {
      return DoubleEntry.builder(this.name, value).callback((v, r, d) -> {
         if (r) {
            ((List)this.getValue()).remove(index);
            reload.run();
         } else {
            ((List)this.getValue()).set(index, v);
         }

         this.setValue((List<Double>)this.getValue());
      }).build();
   }

   public Double newValue() {
      return 0.0;
   }

   @Override
   public ConfigType<List<Double>> getType() {
      return ConfigTypes.LIST_DOUBLE;
   }

   @Override
   public ConfigEntry<List<Double>> newInstance() {
      return new ListDoubleEntry.Builder(this).build();
   }

   public static ListDoubleEntry.Builder builder(Component name, List<Double> defaultValue) {
      return new ListDoubleEntry.Builder(name, defaultValue);
   }

   public static ListDoubleEntry.Builder builder(String nameKey, List<Double> defaultValue) {
      return new ListDoubleEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<List<Double>, ListDoubleEntry, ListDoubleEntry.Builder> {
      public Builder(Component name, List<Double> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, List<Double> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ListDoubleEntry parent) {
         super(parent);
      }

      public ListDoubleEntry.Builder self() {
         return this;
      }

      protected ListDoubleEntry buildInternal() {
         return new ListDoubleEntry(this);
      }
   }
}

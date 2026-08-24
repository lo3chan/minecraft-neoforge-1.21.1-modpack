package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.network.chat.Component;

public class MapDoubleEntry extends MapBaseEntry<Double> {
   protected MapDoubleEntry(MapDoubleEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public MapDoubleEntry(String nameKey, Map<String, Double> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<Double> getValueCodec() {
      return Codec.DOUBLE;
   }

   public ConfigEntry<Entry<String, Double>> newSingleInstance(Double value, String key, Runnable reload) {
      return EntryDoubleEntry.builder(this.name, new SimpleEntry<>(key, value)).callback((v, r, d) -> {
         if (r) {
            ((Map)this.getValue()).remove(key);
            reload.run();
         } else if (!Objects.equals(key, v.getKey())) {
            ((Map)this.getValue()).remove(key);
            ((Map)this.getValue()).put(v.getKey(), v.getValue());
         } else {
            ((Map)this.getValue()).put(key, v.getValue());
         }

         this.setValue((Map<String, Double>)this.getValue());
      }).build();
   }

   public Double newValue() {
      return 0.0;
   }

   @Override
   public ConfigType<Map<String, Double>> getType() {
      return ConfigTypes.MAP_DOUBLE;
   }

   @Override
   public ConfigEntry<Map<String, Double>> newInstance() {
      return new MapDoubleEntry.Builder(this).build();
   }

   public static MapDoubleEntry.Builder builder(Component name, Map<String, Double> defaultValue) {
      return new MapDoubleEntry.Builder(name, defaultValue);
   }

   public static MapDoubleEntry.Builder builder(String nameKey, Map<String, Double> defaultValue) {
      return new MapDoubleEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Map<String, Double>, MapDoubleEntry, MapDoubleEntry.Builder> {
      public Builder(Component name, Map<String, Double> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Map<String, Double> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(MapDoubleEntry parent) {
         super(parent);
      }

      public MapDoubleEntry.Builder self() {
         return this;
      }

      protected MapDoubleEntry buildInternal() {
         return new MapDoubleEntry(this);
      }
   }
}

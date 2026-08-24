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

public class MapIntegerEntry extends MapBaseEntry<Integer> {
   protected MapIntegerEntry(MapIntegerEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public MapIntegerEntry(String nameKey, Map<String, Integer> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<Integer> getValueCodec() {
      return Codec.INT;
   }

   public ConfigEntry<Entry<String, Integer>> newSingleInstance(Integer value, String key, Runnable reload) {
      return EntryIntegerEntry.builder(this.name, new SimpleEntry<>(key, value)).callback((v, r, d) -> {
         if (r) {
            ((Map)this.getValue()).remove(key);
            reload.run();
         } else if (!Objects.equals(key, v.getKey())) {
            ((Map)this.getValue()).remove(key);
            ((Map)this.getValue()).put(v.getKey(), v.getValue());
         } else {
            ((Map)this.getValue()).put(key, v.getValue());
         }

         this.setValue((Map<String, Integer>)this.getValue());
      }).build();
   }

   public Integer newValue() {
      return 0;
   }

   @Override
   public ConfigType<Map<String, Integer>> getType() {
      return ConfigTypes.MAP_INTEGER;
   }

   @Override
   public ConfigEntry<Map<String, Integer>> newInstance() {
      return new MapIntegerEntry.Builder(this).build();
   }

   public static MapIntegerEntry.Builder builder(Component name, Map<String, Integer> defaultValue) {
      return new MapIntegerEntry.Builder(name, defaultValue);
   }

   public static MapIntegerEntry.Builder builder(String nameKey, Map<String, Integer> defaultValue) {
      return new MapIntegerEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Map<String, Integer>, MapIntegerEntry, MapIntegerEntry.Builder> {
      public Builder(Component name, Map<String, Integer> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Map<String, Integer> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(MapIntegerEntry parent) {
         super(parent);
      }

      public MapIntegerEntry.Builder self() {
         return this;
      }

      protected MapIntegerEntry buildInternal() {
         return new MapIntegerEntry(this);
      }
   }
}

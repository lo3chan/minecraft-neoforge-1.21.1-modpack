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

public class MapStringEntry extends MapBaseEntry<String> {
   protected MapStringEntry(MapStringEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public MapStringEntry(String nameKey, Map<String, String> defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public Codec<String> getValueCodec() {
      return Codec.STRING;
   }

   public ConfigEntry<Entry<String, String>> newSingleInstance(String value, String key, Runnable reload) {
      return EntryStringEntry.builder(this.name, new SimpleEntry<>(key, value)).callback((v, r, d) -> {
         if (r) {
            ((Map)this.getValue()).remove(key);
            reload.run();
         } else if (!Objects.equals(key, v.getKey())) {
            ((Map)this.getValue()).remove(key);
            ((Map)this.getValue()).put(v.getKey(), v.getValue());
         } else {
            ((Map)this.getValue()).put(key, v.getValue());
         }

         this.setValue((Map<String, String>)this.getValue());
      }).build();
   }

   public String newValue() {
      return "";
   }

   @Override
   public ConfigType<Map<String, String>> getType() {
      return ConfigTypes.MAP_STRING;
   }

   @Override
   public ConfigEntry<Map<String, String>> newInstance() {
      return new MapStringEntry.Builder(this).build();
   }

   public static MapStringEntry.Builder builder(Component name, Map<String, String> defaultValue) {
      return new MapStringEntry.Builder(name, defaultValue);
   }

   public static MapStringEntry.Builder builder(String nameKey, Map<String, String> defaultValue) {
      return new MapStringEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Map<String, String>, MapStringEntry, MapStringEntry.Builder> {
      public Builder(Component name, Map<String, String> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Map<String, String> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(MapStringEntry parent) {
         super(parent);
      }

      public MapStringEntry.Builder self() {
         return this;
      }

      protected MapStringEntry buildInternal() {
         return new MapStringEntry(this);
      }
   }
}

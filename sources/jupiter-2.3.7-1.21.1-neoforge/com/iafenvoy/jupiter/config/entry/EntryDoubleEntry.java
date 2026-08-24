package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class EntryDoubleEntry extends EntryBaseEntry<Double> {
   protected EntryDoubleEntry(EntryDoubleEntry.Builder builder) {
      super(builder);
   }

   @Override
   public ConfigEntry<Double> newValueInstance() {
      return DoubleEntry.builder(this.name, (Double)this.defaultValue.getValue()).build();
   }

   @Override
   public ConfigType<Entry<String, Double>> getType() {
      return ConfigTypes.ENTRY_DOUBLE;
   }

   @Override
   public ConfigEntry<Entry<String, Double>> newInstance() {
      return new EntryDoubleEntry.Builder(this).build();
   }

   @Override
   public Codec<Entry<String, Double>> getCodec() {
      return RecordCodecBuilder.create(
         i -> i.group(Codec.STRING.fieldOf("key").forGetter(Entry::getKey), Codec.DOUBLE.fieldOf("value").forGetter(Entry::getValue))
            .apply(i, SimpleEntry::new)
      );
   }

   public static EntryDoubleEntry.Builder builder(Component name, Entry<String, Double> defaultValue) {
      return new EntryDoubleEntry.Builder(name, defaultValue);
   }

   public static EntryDoubleEntry.Builder builder(String nameKey, Entry<String, Double> defaultValue) {
      return new EntryDoubleEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Entry<String, Double>, EntryDoubleEntry, EntryDoubleEntry.Builder> {
      public Builder(Component name, Entry<String, Double> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Entry<String, Double> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(EntryDoubleEntry parent) {
         super(parent);
      }

      public EntryDoubleEntry.Builder self() {
         return this;
      }

      protected EntryDoubleEntry buildInternal() {
         return new EntryDoubleEntry(this);
      }
   }
}

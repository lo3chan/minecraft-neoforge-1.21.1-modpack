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
public class EntryIntegerEntry extends EntryBaseEntry<Integer> {
   protected EntryIntegerEntry(EntryIntegerEntry.Builder builder) {
      super(builder);
   }

   @Override
   public ConfigEntry<Integer> newValueInstance() {
      return IntegerEntry.builder(this.name, (Integer)this.value.getValue()).build();
   }

   @Override
   public ConfigType<Entry<String, Integer>> getType() {
      return ConfigTypes.ENTRY_INTEGER;
   }

   @Override
   public ConfigEntry<Entry<String, Integer>> newInstance() {
      return new EntryIntegerEntry.Builder(this).build();
   }

   @Override
   public Codec<Entry<String, Integer>> getCodec() {
      return RecordCodecBuilder.create(
         i -> i.group(Codec.STRING.fieldOf("key").forGetter(Entry::getKey), Codec.INT.fieldOf("value").forGetter(Entry::getValue)).apply(i, SimpleEntry::new)
      );
   }

   public static EntryIntegerEntry.Builder builder(Component name, Entry<String, Integer> defaultValue) {
      return new EntryIntegerEntry.Builder(name, defaultValue);
   }

   public static EntryIntegerEntry.Builder builder(String nameKey, Entry<String, Integer> defaultValue) {
      return new EntryIntegerEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Entry<String, Integer>, EntryIntegerEntry, EntryIntegerEntry.Builder> {
      public Builder(Component name, Entry<String, Integer> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Entry<String, Integer> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(EntryIntegerEntry parent) {
         super(parent);
      }

      public EntryIntegerEntry.Builder self() {
         return this;
      }

      protected EntryIntegerEntry buildInternal() {
         return new EntryIntegerEntry(this);
      }
   }
}

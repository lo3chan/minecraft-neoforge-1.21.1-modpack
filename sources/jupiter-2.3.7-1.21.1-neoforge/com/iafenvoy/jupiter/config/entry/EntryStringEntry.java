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
public class EntryStringEntry extends EntryBaseEntry<String> {
   protected EntryStringEntry(EntryStringEntry.Builder builder) {
      super(builder);
   }

   @Override
   public ConfigEntry<String> newValueInstance() {
      return StringEntry.builder(this.name, (String)this.value.getValue()).build();
   }

   @Override
   public ConfigType<Entry<String, String>> getType() {
      return ConfigTypes.ENTRY_STRING;
   }

   @Override
   public ConfigEntry<Entry<String, String>> newInstance() {
      return new EntryStringEntry.Builder(this).build();
   }

   @Override
   public Codec<Entry<String, String>> getCodec() {
      return RecordCodecBuilder.create(
         i -> i.group(Codec.STRING.fieldOf("key").forGetter(Entry::getKey), Codec.STRING.fieldOf("value").forGetter(Entry::getValue))
            .apply(i, SimpleEntry::new)
      );
   }

   public static EntryStringEntry.Builder builder(Component name, Entry<String, String> defaultValue) {
      return new EntryStringEntry.Builder(name, defaultValue);
   }

   public static EntryStringEntry.Builder builder(String nameKey, Entry<String, String> defaultValue) {
      return new EntryStringEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Entry<String, String>, EntryStringEntry, EntryStringEntry.Builder> {
      public Builder(Component name, Entry<String, String> defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, Entry<String, String> defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(EntryStringEntry parent) {
         super(parent);
      }

      public EntryStringEntry.Builder self() {
         return this;
      }

      protected EntryStringEntry buildInternal() {
         return new EntryStringEntry(this);
      }
   }
}

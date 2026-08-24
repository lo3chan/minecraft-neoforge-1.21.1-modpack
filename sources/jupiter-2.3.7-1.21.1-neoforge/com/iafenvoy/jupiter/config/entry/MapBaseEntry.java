package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class MapBaseEntry<T> extends BaseEntry<Map<String, T>> {
   protected MapBaseEntry(BaseEntry.Builder<Map<String, T>, ?, ?> builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public MapBaseEntry(String nameKey, Map<String, T> defaultValue) {
      super(nameKey, defaultValue);
   }

   public abstract Codec<T> getValueCodec();

   public abstract ConfigEntry<Entry<String, T>> newSingleInstance(T var1, String var2, Runnable var3);

   public abstract T newValue();

   @Override
   public Codec<Map<String, T>> getCodec() {
      return Codec.unboundedMap(Codec.STRING, this.getValueCodec());
   }

   public void setValue(Map<String, T> value) {
      super.setValue((T)(new HashMap<String, T>(value)));
   }

   protected Map<String, T> newDefaultValue() {
      return new HashMap<>(this.defaultValue);
   }
}

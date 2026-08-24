package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class ListBaseEntry<T> extends BaseEntry<List<T>> {
   protected ListBaseEntry(BaseEntry.Builder<List<T>, ?, ?> builder) {
      super(builder);
   }

   @Comment("Use builder instead")
   @Internal
   public ListBaseEntry(String nameKey, List<T> defaultValue) {
      super(nameKey, defaultValue);
   }

   public abstract Codec<T> getValueCodec();

   public abstract ConfigEntry<T> newSingleInstance(T var1, int var2, Runnable var3);

   public abstract T newValue();

   @Override
   public Codec<List<T>> getCodec() {
      return this.getValueCodec().listOf();
   }

   public void setValue(List<T> value) {
      super.setValue((T)(new ArrayList<T>(value)));
   }

   protected List<T> newDefaultValue() {
      return new ArrayList<>(this.defaultValue);
   }
}

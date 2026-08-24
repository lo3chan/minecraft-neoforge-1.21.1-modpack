package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import java.util.Map.Entry;

public abstract class EntryBaseEntry<T> extends BaseEntry<Entry<String, T>> {
   protected EntryBaseEntry(BaseEntry.Builder<Entry<String, T>, ?, ?> builder) {
      super(builder);
   }

   public abstract ConfigEntry<T> newValueInstance();
}

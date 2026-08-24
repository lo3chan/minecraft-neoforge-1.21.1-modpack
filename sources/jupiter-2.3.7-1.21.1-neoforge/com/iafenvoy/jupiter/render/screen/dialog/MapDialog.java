package com.iafenvoy.jupiter.render.screen.dialog;

import com.iafenvoy.jupiter.config.entry.MapBaseEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ConfigMetaProvider;
import com.iafenvoy.jupiter.render.TitleStack;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.screens.Screen;

public class MapDialog<T> extends AbstractListDialog<Map<String, T>, Entry<String, T>> {
   protected final MapBaseEntry<T> entry;

   public MapDialog(Screen parent, TitleStack titleStack, ConfigMetaProvider provider, MapBaseEntry<T> entry) {
      super(parent, titleStack, provider, entry);
      this.entry = entry;
   }

   @Override
   protected void addNewValue() {
      this.entry.getValue().put("", this.entry.newValue());
   }

   @Override
   protected Collection<Entry<String, T>> getValues() {
      return this.entry.getValue().entrySet();
   }

   protected ConfigEntry<Entry<String, T>> newSingleInstance(Entry<String, T> value, int index, Runnable reload) {
      return this.entry.newSingleInstance(value.getValue(), value.getKey(), reload);
   }
}

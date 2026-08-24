package de.cristelknight.cristellib.config.client.extension;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public abstract class ConfigScreenExtension {
   public final String modId;

   public ConfigScreenExtension(String modId) {
      this.modId = modId;
   }

   public abstract void addToBuilder(ConfigBuilder var1, ConfigEntryBuilder var2);

   public void onSave() {
   }

   public int priority() {
      return 0;
   }
}

package com.seibel.distanthorizons.core.config;

import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import java.util.HashMap;
import java.util.HashSet;

public class ConfigPresetOptions<TQuickEnum, TConfig> {
   public final ConfigEntry<TConfig> configEntry;
   private final HashMap<TQuickEnum, TConfig> configOptionByQualityOption;

   public ConfigPresetOptions(ConfigEntry<TConfig> configEntry, HashMap<TQuickEnum, TConfig> configOptionByQualityOption) {
      this.configEntry = configEntry;
      this.configOptionByQualityOption = configOptionByQualityOption;
   }

   public void updateConfigEntry(TQuickEnum quickQuality) {
      TConfig newValue = this.configOptionByQualityOption.get(quickQuality);
      this.configEntry.set(newValue);
   }

   public HashSet<TQuickEnum> getPossibleQualitiesFromCurrentOptionValue() {
      TConfig inputOptionValue = this.configEntry.getTrueValue();
      HashSet<TQuickEnum> possibleQualities = new HashSet<>();

      for (TQuickEnum key : this.configOptionByQualityOption.keySet()) {
         TConfig optionValue = this.configOptionByQualityOption.get(key);
         if (optionValue.equals(inputOptionValue)) {
            possibleQualities.add(key);
         }
      }

      return possibleQualities;
   }
}

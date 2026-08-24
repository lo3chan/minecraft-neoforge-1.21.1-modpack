package de.cristelknight.cristellib.config.structure.toggle;

import java.util.Map;

public record ToggleConfig(Map<String, Boolean> setStructureInfo) {
   public ToggleConfig(NestedToggleConfig nestedEDConfig) {
      this(ToggleConfigTransformer.stringBooleanMap(nestedEDConfig, ""));
   }

   public boolean containsStructure(String structureName) {
      return this.setStructureInfo.containsKey(structureName);
   }

   public boolean isStructureDisabled(String structureName) {
      return !this.setStructureInfo.get(structureName);
   }

   public boolean hasDisabledStructure() {
      return this.setStructureInfo.containsValue(false);
   }
}

package de.cristelknight.cristellib;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.cristelknight.cristellib.config.ConfigManager;
import de.cristelknight.cristellib.config.ConfigType;
import de.cristelknight.cristellib.config.structure.ReadStructureSets;
import de.cristelknight.cristellib.config.structure.toggle.ToggleConfig;
import de.cristelknight.cristellib.data.codec.StructureSetData;
import de.cristelknight.cristellib.util.FileHelper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class StructureConfigToggle extends StructureConfig {
   private final Supplier<Map<ResourceLocation, List<ResourceLocation>>> structuresForED;
   private Map<ResourceLocation, ToggleConfig> enableDisableConfig = null;

   StructureConfigToggle(Path path) {
      this(path, null, new HashMap<>(), new ArrayList<>());
   }

   StructureConfigToggle(String name, String path, String header, Map<String, String> comments, List<StructureSetData> structureSetHolders) {
      this(FileHelper.janksonPathFromString(path, name), header, comments, structureSetHolders);
   }

   StructureConfigToggle(Path path, String header, Map<String, String> comments, List<StructureSetData> structureSetHolders) {
      super(path, header, comments, structureSetHolders);
      this.structuresForED = Suppliers.memoize(() -> ReadStructureSets.readSetsAndAddStructures(structureSetHolders));
   }

   @Override
   public boolean addChanges(String modId, ResourceLocation setLocation) {
      ToggleConfig setConfig = this.enableDisableConfig.get(setLocation);
      if (!setConfig.hasDisabledStructure()) {
         return false;
      } else if (getStructureSet(setLocation, modId) instanceof JsonObject structureSet) {
         this.removeStructureInSets(structureSet, setConfig, setLocation);
         CristelLib.CONFIG_PACK.addStructureSet(setLocation, structureSet);
         return true;
      } else {
         Constants.LOG.warn("Set for {} {} is not a JsonObject, skipping...", modId, setLocation);
         return true;
      }
   }

   private void removeStructureInSets(JsonObject structureSet, ToggleConfig setConfig, ResourceLocation setLocation) {
      JsonArray array = structureSet.get("structures").getAsJsonArray();
      Iterator<JsonElement> structureIterator = array.iterator();

      while (structureIterator.hasNext()) {
         JsonElement structure = structureIterator.next();
         String structureName = this.toDefaultString(
            Objects.requireNonNull(ResourceLocation.tryParse(structure.getAsJsonObject().get("structure").getAsString()))
         );
         if (!setConfig.containsStructure(structureName)) {
            Constants.LOG.error("{} is not included in: {} for config with path: {}", structureName, setLocation, this.getPath());
         } else if (setConfig.isStructureDisabled(structureName)) {
            structureIterator.remove();
         }
      }
   }

   @Override
   public void writeConfig(boolean override) {
      if (override || !this.getPath().toFile().exists()) {
         ConfigManager.createToggleConfig(this);
      }
   }

   @Override
   public void readConfig(boolean override) {
      if (this.enableDisableConfig == null || override) {
         this.enableDisableConfig = ConfigManager.readToggleConfig(this);
      }
   }

   public Map<ResourceLocation, List<ResourceLocation>> getDefaultStructureToggles() {
      return (Map<ResourceLocation, List<ResourceLocation>>)this.structuresForED.get();
   }

   public Map<ResourceLocation, ToggleConfig> getToggleConfigs() {
      return this.enableDisableConfig;
   }

   @Override
   public Set<ResourceLocation> getConfigKeys() {
      return this.enableDisableConfig.keySet();
   }

   public void updateEDConfig(ResourceLocation key, ToggleConfig config) {
      this.enableDisableConfig.put(key, config);
   }

   @Override
   public void resetConfigs() {
      this.enableDisableConfig = null;
   }

   @Override
   public ConfigType getType() {
      return ConfigType.TOGGLE;
   }
}

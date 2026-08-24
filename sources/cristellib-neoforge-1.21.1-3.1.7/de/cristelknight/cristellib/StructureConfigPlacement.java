package de.cristelknight.cristellib;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import de.cristelknight.cristellib.config.ConfigManager;
import de.cristelknight.cristellib.config.ConfigType;
import de.cristelknight.cristellib.config.structure.ReadStructureSets;
import de.cristelknight.cristellib.config.structure.placement.PlacementConfig;
import de.cristelknight.cristellib.data.codec.StructureSetData;
import de.cristelknight.cristellib.util.FileHelper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class StructureConfigPlacement extends StructureConfig {
   private final Supplier<Map<ResourceLocation, PlacementConfig>> structurePlacement;
   private Map<ResourceLocation, PlacementConfig> placementConfig = null;

   StructureConfigPlacement(Path path) {
      this(path, null, new HashMap<>(), new ArrayList<>());
   }

   StructureConfigPlacement(String name, String path, String header, Map<String, String> comments, List<StructureSetData> structureSetHolders) {
      this(FileHelper.janksonPathFromString(path, name), header, comments, structureSetHolders);
   }

   StructureConfigPlacement(Path path, String header, Map<String, String> comments, List<StructureSetData> structureSetHolders) {
      super(path, header, comments, structureSetHolders);
      this.structurePlacement = Suppliers.memoize(() -> ReadStructureSets.readSetsAndAddPlacements(structureSetHolders));
   }

   StructureConfigPlacement(Path path, String header, Map<String, String> comments, ConfigType type, List<StructureSetData> structureSetHolders) {
      this(path, header, comments, structureSetHolders);
   }

   private void updatePlacementsInSet(JsonObject structureSet, PlacementConfig placementConfig, ResourceLocation setLocation) {
      JsonObject p = structureSet.get("placement").getAsJsonObject();
      p.addProperty("salt", placementConfig.salt());
      p.addProperty("spacing", placementConfig.spacing());
      p.addProperty("separation", placementConfig.separation());
      double newF = placementConfig.frequency();
      if ((p.has("frequency") || newF != 1.0) && (!p.has("frequency") || newF != p.get("frequency").getAsDouble())) {
         p.addProperty("frequency", newF);
      }
   }

   @Override
   public boolean addChanges(String modId, ResourceLocation setLocation) {
      PlacementConfig setConfig = this.placementConfig.get(setLocation);
      if (this.getDefaultStructurePlacements().get(setLocation).equals(setConfig)) {
         return false;
      } else if (getStructureSet(setLocation, modId) instanceof JsonObject structureSet) {
         this.updatePlacementsInSet(structureSet, setConfig, setLocation);
         CristelLib.CONFIG_PACK.addStructureSet(setLocation, structureSet);
         return true;
      } else {
         Constants.LOG.warn("Set for {} {} is not a JsonObject, skipping...", modId, setLocation);
         return true;
      }
   }

   @Override
   public void writeConfig(boolean override) {
      if (override || !this.getPath().toFile().exists()) {
         ConfigManager.createPlacementConfig(this);
      }
   }

   @Override
   public void readConfig(boolean override) {
      if (this.placementConfig == null || override) {
         this.placementConfig = ConfigManager.readPlacementConfig(this);
      }
   }

   public Map<ResourceLocation, PlacementConfig> getDefaultStructurePlacements() {
      return (Map<ResourceLocation, PlacementConfig>)this.structurePlacement.get();
   }

   public Map<ResourceLocation, PlacementConfig> getPlacementConfigs() {
      return this.placementConfig;
   }

   @Override
   public Set<ResourceLocation> getConfigKeys() {
      return this.placementConfig.keySet();
   }

   public void updatePlacement(ResourceLocation key, PlacementConfig config) {
      this.placementConfig.put(key, config);
   }

   @Override
   public void resetConfigs() {
      this.placementConfig = null;
   }

   @Override
   public ConfigType getType() {
      return ConfigType.PLACEMENT;
   }
}

package de.cristelknight.cristellib.config.structure;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.config.FileWriter;
import de.cristelknight.cristellib.config.structure.placement.PlacementConfig;
import de.cristelknight.cristellib.data.codec.StructureSetData;
import de.cristelknight.cristellib.util.JsonHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ReadStructureSets {
   public static Map<ResourceLocation, List<ResourceLocation>> readSetsAndAddStructures(List<StructureSetData> structureSetHolder) {
      Builder<ResourceLocation, List<ResourceLocation>> structures = new Builder();
      structureSetHolder.forEach(holder -> holder.sets().forEach(setLocation -> {
         String modId = holder.modId();
         JsonElement e = JsonHelper.getSetElement(setLocation, modId);
         if (!checkElement(e, modId, setLocation)) {
            JsonArray structureArray = GsonHelper.getAsJsonArray(e.getAsJsonObject(), "structures");
            List<ResourceLocation> structureList = new ArrayList<>();

            for (JsonElement element : structureArray) {
               if (element.isJsonObject()) {
                  structureList.add(ResourceLocation.tryParse(GsonHelper.getAsString(element.getAsJsonObject(), "structure")));
               }
            }

            structures.put(setLocation, structureList);
         }
      }));
      return structures.build();
   }

   public static Map<ResourceLocation, PlacementConfig> readSetsAndAddPlacements(List<StructureSetData> structureSetHolder) {
      Builder<ResourceLocation, PlacementConfig> structurePlacement = new Builder();
      structureSetHolder.forEach(
         holder -> holder.sets()
            .forEach(
               setLocation -> {
                  String modId = holder.modId();
                  JsonElement e = JsonHelper.getSetElement(setLocation, modId);
                  if (!checkElement(e, modId, setLocation)) {
                     JsonObject placement = GsonHelper.getAsJsonObject(e.getAsJsonObject(), "placement");
                     PlacementConfig config = FileWriter.loadFromElement(
                        String.format("Couldn't read %s in %s, crashing instead. Maybe try to delete the config files!", setLocation, modId),
                        PlacementConfig.CODEC,
                        JsonOps.INSTANCE,
                        placement
                     );
                     structurePlacement.put(setLocation, config);
                  }
               }
            )
      );
      return structurePlacement.build();
   }

   private static boolean checkElement(JsonElement element, String modId, ResourceLocation setLocation) {
      if (element != null && element.isJsonObject()) {
         return false;
      } else {
         Constants.LOG.error("Set for {} {} is not a JsonObject", modId, setLocation);
         return true;
      }
   }
}

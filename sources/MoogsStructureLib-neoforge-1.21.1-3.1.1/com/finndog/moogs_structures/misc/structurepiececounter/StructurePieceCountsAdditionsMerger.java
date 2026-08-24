package com.finndog.moogs_structures.misc.structurepiececounter;

import com.finndog.moogs_structures.utils.GeneralUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class StructurePieceCountsAdditionsMerger {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().create();
   private static final String DATA_TYPE = "msl_pieces_spawn_counts_additions";
   private static final int FILE_SUFFIX_LENGTH = ".json".length();

   private StructurePieceCountsAdditionsMerger() {
   }

   static void performCountsAdditionsDetectionAndMerger(ResourceManager resourceManager) {
      Map<ResourceLocation, List<JsonElement>> countsAdditionJSON = GeneralUtils.getAllDatapacksJSONElement(
         resourceManager, GSON, "msl_pieces_spawn_counts_additions", FILE_SUFFIX_LENGTH
      );
      parseCountsAndBeginMerger(countsAdditionJSON);
   }

   private static void parseCountsAndBeginMerger(Map<ResourceLocation, List<JsonElement>> countsAdditionJSON) {
      for (Entry<ResourceLocation, List<JsonElement>> entry : countsAdditionJSON.entrySet()) {
         StructurePieceCountsManager.STRUCTURE_PIECE_COUNTS_MANAGER.parseAndAddCountsJSONObj(entry.getKey(), entry.getValue());
      }
   }
}

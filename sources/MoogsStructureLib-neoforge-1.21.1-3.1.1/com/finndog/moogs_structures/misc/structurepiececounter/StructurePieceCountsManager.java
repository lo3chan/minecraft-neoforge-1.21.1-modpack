package com.finndog.moogs_structures.misc.structurepiececounter;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

public class StructurePieceCountsManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
   public static final StructurePieceCountsManager STRUCTURE_PIECE_COUNTS_MANAGER = new StructurePieceCountsManager();
   private Map<ResourceLocation, List<StructurePieceCountsObj>> StructureToPieceCountsObjs = new HashMap<>();
   private final Map<ResourceLocation, Map<ResourceLocation, StructurePieceCountsManager.RequiredPieceNeeds>> cachedRequirePiecesMap = new HashMap<>();
   private final Map<ResourceLocation, Map<ResourceLocation, Integer>> cachedMaxCountPiecesMap = new HashMap<>();

   public StructurePieceCountsManager() {
      super(GSON, "msl_pieces_spawn_counts");
   }

   @MethodsReturnNonnullByDefault
   private List<StructurePieceCountsObj> getStructurePieceCountsObjs(ResourceLocation fileIdentifier, JsonElement jsonElement) throws Exception {
      List<StructurePieceCountsObj> piecesSpawnCounts = (List<StructurePieceCountsObj>)GSON.fromJson(
         jsonElement.getAsJsonObject().get("pieces_spawn_counts"), (new TypeToken<List<StructurePieceCountsObj>>() {}).getType()
      );

      for (int i = piecesSpawnCounts.size() - 1; i >= 0; i--) {
         StructurePieceCountsObj entry = piecesSpawnCounts.get(i);
         if (entry.alwaysSpawnThisMany != null && entry.neverSpawnMoreThanThisMany != null && entry.alwaysSpawnThisMany > entry.neverSpawnMoreThanThisMany) {
            throw new Exception(
               "Moog's Structure Lib Error: Found "
                  + entry.nbtPieceName
                  + " entry has alwaysSpawnThisMany greater than neverSpawnMoreThanThisMany which is invalid."
            );
         }
      }

      return piecesSpawnCounts;
   }

   protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
      Map<ResourceLocation, List<StructurePieceCountsObj>> mapBuilder = new HashMap<>();
      loader.forEach(
         (fileIdentifier, jsonElement) -> {
            try {
               mapBuilder.put(fileIdentifier, this.getStructurePieceCountsObjs(fileIdentifier, jsonElement));
            } catch (Exception var5) {
               MoogsStructuresCommon.LOGGER
                  .error("Moog's Structure Lib Error: Couldn't parse msl_pieces_spawn_counts file {} - JSON looks like: {}", fileIdentifier, jsonElement, var5);
            }
         }
      );
      this.StructureToPieceCountsObjs = mapBuilder;
      this.cachedRequirePiecesMap.clear();
      this.cachedMaxCountPiecesMap.clear();
      StructurePieceCountsAdditionsMerger.performCountsAdditionsDetectionAndMerger(manager);
   }

   public void parseAndAddCountsJSONObj(ResourceLocation structureRL, List<JsonElement> jsonElements) {
      jsonElements.forEach(
         jsonElement -> {
            try {
               this.StructureToPieceCountsObjs
                  .computeIfAbsent(structureRL, rl -> new ArrayList<>())
                  .addAll(this.getStructurePieceCountsObjs(structureRL, jsonElement));
            } catch (Exception var4) {
               MoogsStructuresCommon.LOGGER
                  .error("Moog's Structure Lib Error: Couldn't parse msl_pieces_spawn_counts file {} - JSON looks like: {}", structureRL, jsonElement, var4);
            }
         }
      );
   }

   @Nullable
   public Map<ResourceLocation, StructurePieceCountsManager.RequiredPieceNeeds> getRequirePieces(ResourceLocation structureRL) {
      if (!this.StructureToPieceCountsObjs.containsKey(structureRL)) {
         return null;
      } else if (this.cachedRequirePiecesMap.containsKey(structureRL)) {
         return this.cachedRequirePiecesMap.get(structureRL);
      } else {
         Map<ResourceLocation, StructurePieceCountsManager.RequiredPieceNeeds> requirePiecesMap = new HashMap<>();
         List<StructurePieceCountsObj> structurePieceCountsObjs = this.StructureToPieceCountsObjs.get(structureRL);
         if (structurePieceCountsObjs != null) {
            structurePieceCountsObjs.forEach(
               entry -> {
                  if (entry.alwaysSpawnThisMany != null) {
                     requirePiecesMap.put(
                        ResourceLocation.tryParse(entry.nbtPieceName),
                        new StructurePieceCountsManager.RequiredPieceNeeds(
                           entry.alwaysSpawnThisMany, entry.minimumDistanceFromCenterPiece != null ? entry.minimumDistanceFromCenterPiece : 0
                        )
                     );
                  }
               }
            );
         }

         this.cachedRequirePiecesMap.put(structureRL, requirePiecesMap);
         return requirePiecesMap;
      }
   }

   @MethodsReturnNonnullByDefault
   public Map<ResourceLocation, Integer> getMaximumCountForPieces(ResourceLocation structureRL) {
      if (this.cachedMaxCountPiecesMap.containsKey(structureRL)) {
         return this.cachedMaxCountPiecesMap.get(structureRL);
      } else {
         Map<ResourceLocation, Integer> maxCountPiecesMap = new HashMap<>();
         List<StructurePieceCountsObj> structurePieceCountsObjs = this.StructureToPieceCountsObjs.get(structureRL);
         if (structurePieceCountsObjs != null) {
            structurePieceCountsObjs.forEach(entry -> {
               if (entry.neverSpawnMoreThanThisMany != null) {
                  maxCountPiecesMap.put(ResourceLocation.tryParse(entry.nbtPieceName), entry.neverSpawnMoreThanThisMany);
               }
            });
         }

         this.cachedMaxCountPiecesMap.put(structureRL, maxCountPiecesMap);
         return maxCountPiecesMap;
      }
   }

   public record RequiredPieceNeeds(int maxLimit, int minDistanceFromCenter) {
      public int getRequiredAmount() {
         return this.maxLimit;
      }

      public int getMinDistanceFromCenter() {
         return this.minDistanceFromCenter;
      }
   }
}

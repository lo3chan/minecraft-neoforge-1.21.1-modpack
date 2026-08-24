package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.generic.pins.PinnableRecipeCollection;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeBookDebugLogger {
   public static boolean enabled = true;
   public static boolean verboseCollections = false;
   private static long lastDumpNanos;
   private static int dumpCount;
   private static final long MIN_DUMP_INTERVAL_MS = 250L;
   private static boolean configDumped;

   private RecipeBookDebugLogger() {
   }

   public static void onUpdateCollectionsStart(
      String screenName, boolean resetPageNumber, int tabOrdinal, String tabName, Object rbipVariant, String searchText
   ) {
      if (enabled) {
         BetterRecipeBook.LOGGER
            .info(
               "[BRBE-DEBUG] ══ updateCollections START ══ screen={} reset={} tab=[{}]{} rbip={} search=\"{}\"",
               screenName,
               resetPageNumber,
               tabOrdinal,
               tabName != null ? " " + tabName : "",
               rbipVariant != null ? "active(" + rbipVariant + ")" : "none",
               searchText != null && !searchText.isEmpty() ? searchText : ""
            );
      }
   }

   public static void onRbipFilterCollections(String searchCategory, int totalBase, int matched, boolean activeTabNonNull) {
      if (enabled) {
         BetterRecipeBook.LOGGER
            .info("[BRBE-DEBUG] RBIP filter: searchCat={} base={} → matched={} activeTab={}", searchCategory, totalBase, matched, activeTabNonNull);
      }
   }

   public static void onDataMarkingStart(int collectionCount, long slotHash, boolean cacheHit, boolean inventoryChanged) {
      if (enabled) {
         BetterRecipeBook.LOGGER
            .info("[BRBE-DEBUG] Data marking: collections={} slotHash={} cacheHit={} invChanged={}", collectionCount, slotHash, cacheHit, inventoryChanged);
      }
   }

   public static void onPartialMarkingDone(
      int totalCollections, int partialCollections, int partialRecipes, int incompatibleCollections, int incompatibleRecipes
   ) {
      if (enabled) {
         BetterRecipeBook.LOGGER
            .info(
               "[BRBE-DEBUG] Partial marking done: {} collections, {} have partials ({} recipes), {} incompatible ({} recipes)",
               totalCollections,
               partialCollections,
               partialRecipes,
               incompatibleCollections,
               incompatibleRecipes
            );
      }
   }

   public static void onPipelineStage(String stage, int inputCount, int outputCount) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] Pipeline [{}]: {} → {} collections", stage, inputCount, outputCount);
      }
   }

   public static void onPipelineDone(int finalCount, int pageNumber, boolean cached) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] Pipeline DONE: {} collections → page {}, cached={}", finalCount, pageNumber, cached);
      }
   }

   public static void onCacheAccess(boolean hit, Class<?> screenClass, long slotHash, Object variant, int resultCount) {
      if (enabled) {
         BetterRecipeBook.LOGGER
            .info(
               "[BRBE-DEBUG] Cache {}: screen={} slotHash={} variant={} resultCount={}",
               hit ? "HIT" : "MISS",
               screenClass.getSimpleName(),
               slotHash,
               variant != null ? variant.hashCode() : "none",
               resultCount
            );
      }
   }

   public static void dumpCollectionSummary(String label, List<RecipeCollection> collections, boolean isFiltering) {
      if (enabled) {
         int craftable = 0;
         int partial = 0;
         int uncraftable = 0;
         int emptyRecipes = 0;
         int totalRecipes = 0;
         int pinned = 0;

         for (RecipeCollection c : collections) {
            List<RecipeHolder<?>> recipes = c.getRecipes();
            totalRecipes += recipes.size();
            if (recipes.isEmpty()) {
               emptyRecipes++;
            }

            if (c.hasCraftable()) {
               craftable++;
            } else if (PartialCraftingUtil.hasPartialMaterials(c)) {
               partial++;
            } else {
               uncraftable++;
            }

            if (BetterRecipeBook.pinnedRecipeManager.has(PinnableRecipeCollection.of(c))) {
               pinned++;
            }
         }

         BetterRecipeBook.LOGGER
            .info(
               "[BRBE-DEBUG] {}: {} total | craftable={} partial={} uncraftable={} emptyRecipeColls={} totalRecipes={} pinned={} filtering={}",
               label,
               collections.size(),
               craftable,
               partial,
               uncraftable,
               emptyRecipes,
               totalRecipes,
               pinned,
               isFiltering
            );
      }
   }

   public static void dumpCollectionDetails(String label, List<RecipeCollection> collections) {
      if (enabled && verboseCollections && !collections.isEmpty()) {
         long now = System.nanoTime();
         if (dumpCount <= 0 || now - lastDumpNanos >= 250000000L) {
            lastDumpNanos = now;
            dumpCount++;
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.ROOT, "[BRBE-DEBUG] %s detail (%d collections, dump #%d):\n", label, collections.size(), dumpCount));
            int limit = Math.min(collections.size(), verboseCollections ? 40 : 10);

            for (int i = 0; i < limit; i++) {
               RecipeCollection c = collections.get(i);
               List<RecipeHolder<?>> recipes = c.getRecipes();
               String result = "?";
               if (!recipes.isEmpty()) {
                  RecipeHolder<?> first = recipes.get(0);
                  ItemStack stack = first.value().getResultItem(Minecraft.getInstance().level.registryAccess());
                  result = stack.isEmpty() ? "(air)" : stack.getHoverName().getString();
               }

               sb.append(
                  String.format(
                     Locale.ROOT,
                     "  [%d] recipes=%d result=%s craftable=%s partial=%s\n",
                     i,
                     recipes.size(),
                     result,
                     c.hasCraftable(),
                     PartialCraftingUtil.hasPartialMaterials(c)
                  )
               );
            }

            if (collections.size() > limit) {
               sb.append(String.format(Locale.ROOT, "  ... and %d more\n", collections.size() - limit));
            }

            BetterRecipeBook.LOGGER.info(sb.toString());
         }
      }
   }

   public static void onRbipTabsBuilt(int tabCount, int craftingCount) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] RBIP tabs built: {} creative tabs, {} in CRAFTING_LIST", tabCount, craftingCount);
      }
   }

   public static void onRbipFilterProgress(int processed, int total, int removed) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] RBIP incremental filter: {}/{} processed, {} removed so far", processed, total, removed);
      }
   }

   public static void onRbipTabSelected(String tabName, String furnaceType) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] RBIP tab selected: \"{}\" furnace={}", tabName, furnaceType != null ? furnaceType : "none");
      }
   }

   public static void onRbipInitComplete(int tabCount, int itemMappingCount, boolean success) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] RBIP init: {} tabs, {} item mappings, success={}", tabCount, itemMappingCount, success);
      }
   }

   public static void onSearchProcessed(String rawText, boolean isAdvanced, String parsedSummary) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] Search: raw=\"{}\" advanced={} parsed={}", rawText, isAdvanced, parsedSummary);
      }
   }

   public static void onFilterToggle(boolean isFiltering) {
      if (enabled) {
         BetterRecipeBook.LOGGER.info("[BRBE-DEBUG] Filter toggle: {}", isFiltering ? "ON (show craftable only)" : "OFF (show all)");
      }
   }

   public static void dumpConfigOnce() {
      if (enabled && !configDumped) {
         configDumped = true;
         BetterRecipeBook.LOGGER
            .info(
               "[BRBE-DEBUG] ══ Config ══ partialCrafting={} partialMarking={} noGrouped={} onHover={} enablePinning={} instantCraft={} showAllSurvival={} keepCentered={} scrolling={} rbip={}",
               BetterRecipeBook.ctx().config().partialCraftingEnabled,
               BetterRecipeBook.ctx().config().partialMarkingEnabled,
               BetterRecipeBook.ctx().config().alternativeRecipes.noGrouped,
               BetterRecipeBook.ctx().config().alternativeRecipes.onHover,
               true,
               BetterRecipeBook.ctx().config().instantCraft.enabled,
               BetterRecipeBook.ctx().config().showAllRecipesInSurvival,
               BetterRecipeBook.ctx().config().keepCentered,
               true,
               BetterRecipeBook.ctx().config().rbip != null ? BetterRecipeBook.ctx().config().rbip.enableRecipeBookIsPain : "null"
            );
      }
   }
}

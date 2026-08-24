package com.alonie.recipebookispain_extended;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.config.ConfigEventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeBookIsPain {
   public static final Logger LOGGER = LogManager.getLogger("RBIP");
   public static PlatformAbstractions PLATFORM;
   public static boolean isOwOLoaded;
   public static final List<CreativeModeTab> CRAFTING_LIST = new ArrayList<>();
   public static final List<CreativeModeTab> CRAFTING_SEARCH_LIST = new ArrayList<>();
   private static boolean initialized;
   private static boolean initAttempted;
   public static int recipeGeneration;
   public static CreativeModeTab activeCreativeTab;
   public static FurnaceVariant activeFurnaceType;
   private static int rbip$pendingScroll;
   private static final Map<CreativeModeTab, Set<Item>> TAB_ITEMS = new HashMap<>();
   private static final Map<Item, CreativeModeTab> ITEM_TO_TAB = new HashMap<>();
   private static final Map<String, CreativeModeTab> namespaceCache = new HashMap<>();
   private static boolean namespaceCacheBuilt;

   public static FurnaceVariant detectFurnaceType(AbstractFurnaceMenu menu) {
      if (menu instanceof SmokerMenu) {
         return FurnaceVariant.SMOKER;
      } else {
         return menu instanceof BlastFurnaceMenu ? FurnaceVariant.BLAST_FURNACE : FurnaceVariant.FURNACE;
      }
   }

   public static int rbip$consumeScroll() {
      int s = rbip$pendingScroll;
      rbip$pendingScroll = 0;
      return s;
   }

   public static void rbip$queueScroll(int direction) {
      rbip$pendingScroll = direction;
   }

   public static String diagnostic() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n══════ RBIP Diagnostic ══════\n");
      sb.append("  PLATFORM         = ").append(PLATFORM).append("\n");
      sb.append("  isOwOLoaded      = ").append(isOwOLoaded).append("\n");
      sb.append("  initialized      = ").append(initialized).append("\n");
      sb.append("  initAttempted    = ").append(initAttempted).append("\n");
      sb.append("  config           = ").append(BetterRecipeBook.ctx().config()).append("\n");
      if (BetterRecipeBook.ctx().config() != null && BetterRecipeBook.ctx().config().rbip != null) {
         sb.append("  config.rbip.enableRecipeBookIsPain = ").append(BetterRecipeBook.ctx().config().rbip.enableRecipeBookIsPain).append("\n");
      }

      sb.append("  enabled()        = ").append(RecipeBookIsPainExtendedConfig.enabled()).append("\n");
      sb.append("  CRAFTING_LIST    = ").append(CRAFTING_LIST.size()).append(" entries\n");

      for (CreativeModeTab tab : CRAFTING_LIST) {
         sb.append("    - ").append(tab.getDisplayName().getString()).append(" (").append(TAB_ITEMS.getOrDefault(tab, Set.of()).size()).append(" items)\n");
      }

      sb.append("  CreativeModeTabs = ").append(CreativeModeTabs.allTabs().size()).append(" tabs\n");
      sb.append("  ITEM_TO_TAB      = ").append(ITEM_TO_TAB.size()).append(" entries\n");
      sb.append("══════════════════════════════\n");
      return sb.toString();
   }

   public static synchronized void ensureInitialized() {
      LOGGER.info("[RBIP] ensureInitialized() — init={}, attempted={}, enabled={}", initialized, initAttempted, RecipeBookIsPainExtendedConfig.enabled());
      if (!RecipeBookIsPainExtendedConfig.enabled()) {
         LOGGER.info("[RBIP] DISABLED — skipping init");
         if (initialized) {
            initialized = false;
            recipeGeneration++;
            CRAFTING_LIST.clear();
            CRAFTING_SEARCH_LIST.clear();
            TAB_ITEMS.clear();
            ITEM_TO_TAB.clear();
         }

         initAttempted = true;
      } else if (!initialized) {
         Minecraft client = Minecraft.getInstance();
         if (client == null) {
            LOGGER.warn("[RBIP] client is null");
         } else if (client.level == null) {
            LOGGER.info("[RBIP] no level yet — defer");
         } else {
            LOGGER.info("[RBIP] Initializing...");

            try {
               CRAFTING_LIST.clear();
               CRAFTING_SEARCH_LIST.clear();
               TAB_ITEMS.clear();
               ITEM_TO_TAB.clear();
               CreativeModeTabs.tryRebuildTabContents(FeatureFlags.DEFAULT_FLAGS, false, client.level.registryAccess());
               int tabCount = 0;

               for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
                  if (shouldMirror(tab)) {
                     try {
                        Set<Item> items = new HashSet<>();

                        for (ItemStack stack : tab.getDisplayItems()) {
                           if (!stack.isEmpty()) {
                              Item item = stack.getItem();
                              items.add(item);
                              ITEM_TO_TAB.putIfAbsent(item, tab);
                           }
                        }

                        if (!items.isEmpty()) {
                           TAB_ITEMS.put(tab, items);
                           CRAFTING_LIST.add(tab);
                           CRAFTING_SEARCH_LIST.add(tab);
                           tabCount++;
                        }
                     } catch (Exception var9) {
                        LOGGER.error("[RBIP] Error processing tab: {}", tab.getDisplayName().getString(), var9);
                     }
                  }
               }

               if (tabCount == 0) {
                  LOGGER.info("[RBIP] getDisplayItems() returned empty — falling back to BuiltInRegistries.ITEM scan");

                  for (Item item : BuiltInRegistries.ITEM) {
                     ItemStack stackx = new ItemStack(item);
                     if (!stackx.isEmpty()) {
                        for (CreativeModeTab tabx : CreativeModeTabs.allTabs()) {
                           if (shouldMirror(tabx)) {
                              try {
                                 if (tabx.contains(stackx)) {
                                    ITEM_TO_TAB.putIfAbsent(item, tabx);
                                    TAB_ITEMS.computeIfAbsent(tabx, k -> new HashSet<>()).add(item);
                                    break;
                                 }
                              } catch (Exception var8) {
                              }
                           }
                        }
                     }
                  }

                  for (CreativeModeTab tabxx : CreativeModeTabs.allTabs()) {
                     if (shouldMirror(tabxx)) {
                        Set<Item> items = TAB_ITEMS.get(tabxx);
                        if (items != null && !items.isEmpty()) {
                           CRAFTING_LIST.add(tabxx);
                           CRAFTING_SEARCH_LIST.add(tabxx);
                        }
                     }
                  }
               }

               recipeGeneration++;
               if (CRAFTING_LIST.isEmpty()) {
                  if (!initAttempted) {
                     LOGGER.warn("[RBIP] 0 tabs mapped (likely recipe-viewer init race) — will retry");
                     initAttempted = true;
                  } else {
                     LOGGER.warn("[RBIP] 0 tabs mapped on retry — giving up");
                     initialized = true;
                  }
               } else {
                  initialized = true;
                  initAttempted = true;
               }

               LOGGER.info(
                  "[RBIP] OK — {} tabs, {} items mapped (strategy: {})",
                  CRAFTING_LIST.size(),
                  ITEM_TO_TAB.size(),
                  tabCount > 0 ? "getDisplayItems" : "registry scan"
               );
            } catch (Exception var10) {
               LOGGER.error("[RBIP] Init failed", var10);
            }
         }
      }
   }

   private static boolean shouldMirror(CreativeModeTab tab) {
      Type type = tab.getType();
      return type != Type.INVENTORY && type != Type.SEARCH;
   }

   public static void init(ConfigEventBus events) {
      events.subscribe(ConfigEventBus.ConfigChanged.class, event -> {
         LOGGER.info("[RBIP] ConfigChanged event received");
         onConfigChanged();
      });
      LOGGER.info("[RBIP] Subscribed to ConfigEventBus");
   }

   public static void onConfigChanged() {
      LOGGER.info("[RBIP] onConfigChanged()");
      initialized = false;
      initAttempted = false;
      recipeGeneration++;
      CRAFTING_LIST.clear();
      CRAFTING_SEARCH_LIST.clear();
      TAB_ITEMS.clear();
      ITEM_TO_TAB.clear();
      namespaceCacheBuilt = false;
      namespaceCache.clear();
      if (RecipeBookIsPainExtendedConfig.enabled()) {
         ensureInitialized();
      }
   }

   public static Set<Item> getItemsForTab(CreativeModeTab tab) {
      return TAB_ITEMS.getOrDefault(tab, Set.of());
   }

   public static CreativeModeTab getCreativeTabForItem(ItemStack stack) {
      return stack.isEmpty() ? null : ITEM_TO_TAB.get(stack.getItem());
   }

   public static boolean isItemInTab(ItemStack stack, CreativeModeTab tab) {
      Set<Item> items = TAB_ITEMS.get(tab);
      return items != null && items.contains(stack.getItem());
   }

   public static void buildNamespaceCache() {
      namespaceCache.clear();

      for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
         Type type = group.getType();
         if (type != Type.INVENTORY && type != Type.SEARCH) {
            ResourceLocation regId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);
            if (regId != null) {
               namespaceCache.put(regId.getPath(), group);
            }

            String displayKey = group.getDisplayName().getString().toLowerCase().replaceAll("[^a-z0-9_]", "");
            if (!displayKey.isEmpty()) {
               namespaceCache.put(displayKey, group);
            }
         }
      }

      namespaceCacheBuilt = true;
   }

   public static CreativeModeTab lookupByNamespace(String itemNamespace) {
      if (!namespaceCacheBuilt) {
         buildNamespaceCache();
      }

      return namespaceCache.get(itemNamespace);
   }

   public static void applyNamespaceOverrides() {
      if (!namespaceCacheBuilt) {
         buildNamespaceCache();
      }
   }

   public static void registerNewGroup(ItemStack stack) {
   }

   public static boolean isOwOLoaded() {
      return isOwOLoaded;
   }

   public static void rbip$renderOwo(GuiGraphics g, RecipeBookTabButton b) {
   }

   public static void rbip$renderOwo(GuiGraphics g, RecipeBookComponent c, RecipeBookTabButton b) {
   }
}

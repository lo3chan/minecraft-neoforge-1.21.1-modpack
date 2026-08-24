package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;

public final class BookStateCache {
   private static final Map<Class<?>, Map<String, List<RecipeCollection>>> CACHE = new HashMap<>();

   private BookStateCache() {
   }

   public static List<RecipeCollection> get(Class<?> screenClass, long slotHash, Object variant) {
      return get(screenClass, slotHash, variant, false);
   }

   public static List<RecipeCollection> get(Class<?> screenClass, long slotHash, Object variant, boolean isFiltering) {
      Map<String, List<RecipeCollection>> screenCache = CACHE.get(screenClass);
      return screenCache == null ? null : screenCache.get(cacheKey(slotHash, variant, isFiltering));
   }

   public static void put(Class<?> screenClass, long slotHash, List<RecipeCollection> collections, Object variant) {
      put(screenClass, slotHash, collections, variant, false);
   }

   public static void put(Class<?> screenClass, long slotHash, List<RecipeCollection> collections, Object variant, boolean isFiltering) {
      CACHE.computeIfAbsent(screenClass, k -> new HashMap<>()).put(cacheKey(slotHash, variant, isFiltering), new ArrayList<>(collections));
   }

   public static void clear() {
      CACHE.clear();
   }

   private static String cacheKey(long slotHash, Object variant) {
      return cacheKey(slotHash, variant, false);
   }

   private static String cacheKey(long slotHash, Object variant, boolean isFiltering) {
      return slotHash
         + "/"
         + (variant != null ? variant.hashCode() : "none")
         + "/filter="
         + isFiltering
         + "/pc="
         + BetterRecipeBook.ctx().config().partialCraftingEnabled
         + "/pm="
         + BetterRecipeBook.ctx().config().partialMarkingEnabled;
   }
}

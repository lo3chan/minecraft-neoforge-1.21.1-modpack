package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeCollectionAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class PartialCraftingUtil {
   private static final WeakHashMap<RecipeCollection, Set<ResourceLocation>> PARTIAL_RECIPES = new WeakHashMap<>();
   private static final WeakHashMap<RecipeCollection, Integer> CHECKED_COLLECTIONS = new WeakHashMap<>();
   private static int filteringGeneration;
   private static boolean filteringActive;
   private static volatile boolean forceFullRefresh = false;

   private PartialCraftingUtil() {
   }

   public static void invalidateCaches() {
      CHECKED_COLLECTIONS.clear();
      filteringGeneration = 0;
      filteringActive = false;
      forceFullRefresh = true;
   }

   private static boolean enabled() {
      return BetterRecipeBook.ctx().config().partialMarkingEnabled;
   }

   public static void beginFilteringUpdate(boolean active) {
      filteringActive = active;
      if (active) {
         if (filteringGeneration == 2147483647) {
            PARTIAL_RECIPES.clear();
            CHECKED_COLLECTIONS.clear();
            filteringGeneration = 0;
         }

         filteringGeneration++;
      }
   }

   public static void requestForceFullRefresh() {
      forceFullRefresh = true;
   }

   public static boolean consumeForceFullRefresh() {
      boolean v = forceFullRefresh;
      forceFullRefresh = false;
      return v;
   }

   public static void clearCaches() {
      PARTIAL_RECIPES.clear();
      CHECKED_COLLECTIONS.clear();
      filteringGeneration = 0;
   }

   public static void markAndInject(RecipeCollection collection, Set<Item> inventoryItems) {
      boolean marked = markPartialMaterials(collection, inventoryItems);
      if (hasPartialMaterials(collection)) {
         int injected = 0;
         RecipeCollectionAccessor ca = (RecipeCollectionAccessor)collection;

         for (RecipeHolder<?> holder : collection.getRecipes()) {
            if (isPartiallyCraftable(collection, holder.id())) {
               ca.brbe$getCraftable().add(holder);
               injected++;
            }
         }

         if (BrbeLogger.isEnabled() && injected > 0) {
            BrbeLogger.log(BrbeLogger.Category.STATE, "markAndInject: marked=%s injected=%d/%d recipes", marked, injected, collection.getRecipes().size());
         }
      }
   }

   public static long slotHash(NonNullList<Slot> slots) {
      long h = 1L;

      for (Slot slot : slots) {
         ItemStack stack = slot.getItem();
         if (!stack.isEmpty()) {
            h = 31L * h + stack.getItem().hashCode();
            h = 31L * h + stack.getCount();
         }
      }

      return h;
   }

   public static Set<Item> hashInventory(NonNullList<Slot> slots) {
      Set<Item> inventoryItems = new HashSet<>();

      for (Slot slot : slots) {
         ItemStack stack = slot.getItem();
         if (!stack.isEmpty()) {
            inventoryItems.add(stack.getItem());
         }
      }

      return inventoryItems;
   }

   public static boolean markPartialMaterials(RecipeCollection collection, NonNullList<Slot> slots) {
      return markPartialMaterials(collection, hashInventory(slots));
   }

   public static boolean markPartialMaterials(RecipeCollection collection, Set<Item> inventoryItems) {
      if (!enabled()) {
         return false;
      } else if (wasCheckedForPartialMaterials(collection)) {
         return hasPartialMaterials(collection);
      } else {
         CHECKED_COLLECTIONS.put(collection, filteringGeneration);
         boolean markedAny = false;
         Set<ResourceLocation> partialRecipes = new HashSet<>();

         for (RecipeHolder<?> recipe : collection.getRecipes()) {
            if (!collection.isCraftable(recipe)) {
               Recipe<?> vanillaRecipe = recipe.value();
               if (hasMatchingIngredientFast(vanillaRecipe.getIngredients(), inventoryItems)) {
                  partialRecipes.add(recipe.id());
                  markedAny = true;
               }
            }
         }

         if (markedAny) {
            PARTIAL_RECIPES.put(collection, partialRecipes);
         } else {
            PARTIAL_RECIPES.remove(collection);
         }

         return markedAny;
      }
   }

   public static void markPartialMaterial(RecipeCollection collection, ResourceLocation recipeId) {
      if (enabled()) {
         CHECKED_COLLECTIONS.put(collection, filteringGeneration);
         PARTIAL_RECIPES.put(collection, new HashSet<>(Collections.singleton(recipeId)));
      }
   }

   public static boolean wasCheckedForPartialMaterials(RecipeCollection collection) {
      if (!enabled()) {
         return false;
      } else {
         Integer generation = CHECKED_COLLECTIONS.get(collection);
         return filteringActive && generation != null && generation == filteringGeneration;
      }
   }

   public static boolean isPartiallyCraftable(RecipeCollection collection, RecipeHolder<?> recipe) {
      return isPartiallyCraftable(collection, recipe.id());
   }

   public static boolean isPartiallyCraftable(RecipeCollection collection, ResourceLocation recipeId) {
      if (!enabled()) {
         return false;
      } else {
         Set<ResourceLocation> partialRecipes = PARTIAL_RECIPES.get(collection);
         return partialRecipes != null && partialRecipes.contains(recipeId);
      }
   }

   public static boolean hasPartialMaterialsRaw(RecipeCollection collection) {
      Set<ResourceLocation> partialRecipes = PARTIAL_RECIPES.get(collection);
      return partialRecipes != null && !partialRecipes.isEmpty();
   }

   public static boolean isPartiallyCraftableRaw(RecipeCollection collection, ResourceLocation recipeId) {
      Set<ResourceLocation> partialRecipes = PARTIAL_RECIPES.get(collection);
      return partialRecipes != null && partialRecipes.contains(recipeId);
   }

   public static boolean hasPartialMaterials(RecipeCollection collection) {
      return !enabled() ? false : hasPartialMaterialsRaw(collection);
   }

   public static CollectionCategory categorize(RecipeCollection c) {
      if (!enabled()) {
         return CollectionCategory.UNASSIGNED;
      } else {
         boolean truly = false;
         boolean partial = false;

         for (RecipeHolder<?> holder : c.getRecipes()) {
            if (isPartiallyCraftable(c, holder.id())) {
               partial = true;
            } else if (c.isCraftable(holder)) {
               truly = true;
            }
         }

         if (truly) {
            return CollectionCategory.TRULY_CRAFTABLE;
         } else {
            return partial ? CollectionCategory.PARTIAL : CollectionCategory.UNASSIGNED;
         }
      }
   }

   public static List<RecipeHolder<?>> getPartiallyCraftableRecipes(RecipeCollection collection) {
      if (!enabled()) {
         return Collections.emptyList();
      } else {
         Set<ResourceLocation> partialRecipes = PARTIAL_RECIPES.get(collection);
         if (partialRecipes != null && !partialRecipes.isEmpty()) {
            List<RecipeHolder<?>> recipes = new ArrayList<>();

            for (RecipeHolder<?> recipe : collection.getRecipes()) {
               if (partialRecipes.contains(recipe.id())) {
                  recipes.add(recipe);
               }
            }

            return recipes;
         } else {
            return Collections.emptyList();
         }
      }
   }

   private static boolean hasMatchingIngredient(List<Ingredient> ingredients, NonNullList<Slot> slots) {
      Set<Item> inventoryItems = hashInventory(slots);
      return hasMatchingIngredientFast(ingredients, inventoryItems);
   }

   private static boolean hasMatchingIngredientFast(List<Ingredient> ingredients, Set<Item> inventoryItems) {
      for (Ingredient ingredient : ingredients) {
         if (!ingredient.isEmpty()) {
            for (ItemStack stack : ingredient.getItems()) {
               if (!stack.isEmpty() && inventoryItems.contains(stack.getItem())) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}

package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.generic.pins.PinnableRecipeCollection;
import com.alonie.brbe.generic.pins.PipelineCollection;
import com.alonie.brbe.mixins.accessors.RecipeCollectionAccessor;
import com.alonie.brbe.search.SearchCache;
import com.alonie.brbe.search.SearchQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public final class CollectionPipeline {
   private CollectionPipeline() {
   }

   public static List<RecipeCollection> applySearch(List<RecipeCollection> collections, SearchQuery query, Level level) {
      if (query != null && level != null) {
         RegistryAccess registryAccess = level.registryAccess();
         SearchCache cache = new SearchCache();
         List<RecipeCollection> filtered = new ArrayList<>();

         for (RecipeCollection coll : collections) {
            for (RecipeHolder<?> holder : coll.getRecipes()) {
               ItemStack result = holder.value().getResultItem(registryAccess);
               if (result != null && !result.isEmpty() && query.matches(result, cache)) {
                  filtered.add(coll);
                  break;
               }
            }
         }

         return filtered;
      } else {
         return collections;
      }
   }

   public static List<RecipeCollection> applyUngroup(List<RecipeCollection> collections) {
      if (!BetterRecipeBook.ctx().config().alternativeRecipes.noGrouped) {
         return collections;
      } else {
         List<RecipeCollection> split = new ArrayList<>(collections.size());

         for (RecipeCollection collection : collections) {
            List<RecipeHolder<?>> recipes = collection.getRecipes();
            if (recipes.size() <= 1) {
               split.add(collection);
            } else {
               RecipeCollectionAccessor source = (RecipeCollectionAccessor)collection;
               boolean restrictToCraftableOrPartial = PartialCraftingUtil.hasPartialMaterials(collection) || collection.hasCraftable();
               boolean addedAny = false;

               for (RecipeHolder<?> recipe : recipes) {
                  boolean fits = false;

                  for (RecipeHolder<?> h : source.getFitsDimensions()) {
                     if (h.id().equals(recipe.id())) {
                        fits = true;
                        break;
                     }
                  }

                  if (fits) {
                     boolean isCraftable = false;

                     for (RecipeHolder<?> hx : source.brbe$getCraftable()) {
                        if (hx.id().equals(recipe.id())) {
                           isCraftable = true;
                           break;
                        }
                     }

                     boolean isPartial = PartialCraftingUtil.isPartiallyCraftable(collection, recipe.id());
                     if (!restrictToCraftableOrPartial || isCraftable || isPartial) {
                        RecipeCollection child = new RecipeCollection(collection.registryAccess(), Collections.singletonList(recipe));
                        if (isCraftable) {
                           RecipeCollectionAccessor ca = (RecipeCollectionAccessor)child;
                           ca.brbe$getCraftable().add(recipe);
                        }

                        if (isPartial) {
                           PartialCraftingUtil.markPartialMaterial(child, recipe.id());
                        }

                        RecipeCollectionAccessor childAcc = (RecipeCollectionAccessor)child;
                        childAcc.getFitsDimensions().add(recipe);
                        split.add(child);
                        addedAny = true;
                     }
                  }
               }

               if (!addedAny && !restrictToCraftableOrPartial) {
                  split.add(collection);
               }
            }
         }

         return split;
      }
   }

   public static void applyPins(List<RecipeCollection> collections) {
      if (collections.size() > 1) {
         for (RecipeCollection coll : new ArrayList<>(collections)) {
            if (BetterRecipeBook.pinnedRecipeManager.has(PinnableRecipeCollection.of(coll))) {
               collections.remove(coll);
               collections.add(0, coll);
            }
         }
      }
   }

   public static List<RecipeCollection> applyPartialSort(List<RecipeCollection> collections, boolean hasPartialData) {
      List<RecipeCollection> pinnedCraftable = new ArrayList<>();
      List<RecipeCollection> pinnedPartial = new ArrayList<>();
      List<RecipeCollection> pinnedUncraftable = new ArrayList<>();
      List<RecipeCollection> unpinnedCraftable = new ArrayList<>();
      List<RecipeCollection> unpinnedPartial = new ArrayList<>();
      List<RecipeCollection> unpinnedUncraftable = new ArrayList<>();

      for (RecipeCollection c : collections) {
         boolean isPinned = BetterRecipeBook.pinnedRecipeManager.has(PinnableRecipeCollection.of(c));
         if (hasPartialData) {
            CollectionCategory cat = PartialCraftingUtil.categorize(c);
            if (isPinned) {
               switch (cat) {
                  case TRULY_CRAFTABLE:
                     pinnedCraftable.add(c);
                     break;
                  case PARTIAL:
                     pinnedPartial.add(c);
                     break;
                  case UNASSIGNED:
                     pinnedUncraftable.add(c);
               }
            } else {
               switch (cat) {
                  case TRULY_CRAFTABLE:
                     unpinnedCraftable.add(c);
                     break;
                  case PARTIAL:
                     unpinnedPartial.add(c);
                     break;
                  case UNASSIGNED:
                     unpinnedUncraftable.add(c);
               }
            }
         } else if (c.hasCraftable()) {
            if (isPinned) {
               pinnedCraftable.add(c);
            } else {
               unpinnedCraftable.add(c);
            }
         } else if (isPinned) {
            pinnedUncraftable.add(c);
         } else {
            unpinnedUncraftable.add(c);
         }
      }

      List<RecipeCollection> result = new ArrayList<>(collections.size());
      result.addAll(pinnedCraftable);
      result.addAll(pinnedPartial);
      result.addAll(pinnedUncraftable);
      result.addAll(unpinnedCraftable);
      result.addAll(unpinnedPartial);
      result.addAll(unpinnedUncraftable);
      return result;
   }

   public static List<RecipeCollection> applyFilterToggle(List<RecipeCollection> collections, boolean isFiltering) {
      if (!isFiltering) {
         return collections;
      } else {
         boolean hasPartial = BetterRecipeBook.ctx().config().partialMarkingEnabled;
         List<RecipeCollection> result = new ArrayList<>();

         for (RecipeCollection coll : collections) {
            boolean keep = hasPartial ? coll.hasCraftable() || PartialCraftingUtil.hasPartialMaterials(coll) : coll.hasCraftable();
            if (keep) {
               result.add(coll);
            }
         }

         return result;
      }
   }

   public static <T extends PipelineCollection> void applyPinsGeneric(List<T> collections) {
      if (collections.size() > 1) {
         for (T coll : new ArrayList<>(collections)) {
            if (BetterRecipeBook.pinnedRecipeManager.has(coll)) {
               collections.remove(coll);
               collections.add(0, coll);
            }
         }
      }
   }

   public static <T extends PipelineCollection> List<T> applyPartialSortGeneric(List<T> collections) {
      List<T> pinnedCraftable = new ArrayList<>();
      List<T> pinnedPartial = new ArrayList<>();
      List<T> pinnedUncraftable = new ArrayList<>();
      List<T> unpinnedCraftable = new ArrayList<>();
      List<T> unpinnedPartial = new ArrayList<>();
      List<T> unpinnedUncraftable = new ArrayList<>();

      for (T c : collections) {
         boolean isPinned = BetterRecipeBook.pinnedRecipeManager.has(c);
         boolean craftable = c.hasAnyCraftable();
         boolean partial = c.hasAnyPartiallyCraftable();
         if (isPinned) {
            if (craftable) {
               pinnedCraftable.add(c);
            } else if (partial) {
               pinnedPartial.add(c);
            } else {
               pinnedUncraftable.add(c);
            }
         } else if (craftable) {
            unpinnedCraftable.add(c);
         } else if (partial) {
            unpinnedPartial.add(c);
         } else {
            unpinnedUncraftable.add(c);
         }
      }

      List<T> result = new ArrayList<>(collections.size());
      result.addAll(pinnedCraftable);
      result.addAll(pinnedPartial);
      result.addAll(pinnedUncraftable);
      result.addAll(unpinnedCraftable);
      result.addAll(unpinnedPartial);
      result.addAll(unpinnedUncraftable);
      return result;
   }

   public static <T extends PipelineCollection> List<T> applyFilterToggleGeneric(List<T> collections, boolean isFiltering) {
      if (!isFiltering) {
         return collections;
      } else {
         boolean hasPartial = BetterRecipeBook.ctx().config().partialMarkingEnabled;
         List<T> result = new ArrayList<>();

         for (T coll : collections) {
            boolean keep = hasPartial ? coll.hasAnyCraftable() || coll.hasAnyPartiallyCraftable() : coll.hasAnyCraftable();
            if (keep) {
               result.add(coll);
            }
         }

         return result;
      }
   }
}

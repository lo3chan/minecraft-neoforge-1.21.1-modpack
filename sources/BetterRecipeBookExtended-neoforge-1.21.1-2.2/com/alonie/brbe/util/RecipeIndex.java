package com.alonie.brbe.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeIndex {
   private static final Map<Item, Set<RecipeCollection>> INDEX = new HashMap<>();
   private static volatile boolean built;

   private RecipeIndex() {
   }

   public static boolean isBuilt() {
      return built;
   }

   public static void build(Collection<RecipeCollection> allCollections) {
      if (!built) {
         synchronized (INDEX) {
            if (!built) {
               INDEX.clear();

               for (RecipeCollection coll : allCollections) {
                  for (RecipeHolder<?> holder : coll.getRecipes()) {
                     Recipe<?> recipe = holder.value();

                     for (Ingredient ingredient : recipe.getIngredients()) {
                        for (ItemStack stack : ingredient.getItems()) {
                           Item item = stack.getItem();
                           INDEX.computeIfAbsent(item, k -> new HashSet<>()).add(coll);
                        }
                     }
                  }
               }

               built = true;
            }
         }
      }
   }

   public static Set<RecipeCollection> getAffected(Set<Item> inventoryItems) {
      if (!built) {
         return Collections.emptySet();
      } else {
         Set<RecipeCollection> result = new HashSet<>();
         synchronized (INDEX) {
            for (Item item : inventoryItems) {
               Set<RecipeCollection> colls = INDEX.get(item);
               if (colls != null) {
                  result.addAll(colls);
               }
            }

            return result;
         }
      }
   }

   public static void clear() {
      synchronized (INDEX) {
         INDEX.clear();
         built = false;
      }
   }
}

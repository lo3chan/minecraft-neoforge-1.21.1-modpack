package com.alonie.brbe.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;

public final class RecipeCollectionTagger<T> {
   private final WeakHashMap<RecipeCollection, Set<T>> tags = new WeakHashMap<>();
   private final WeakHashMap<RecipeCollection, Integer> checkedGenerations = new WeakHashMap<>();
   private int currentGeneration;

   public void beginFiltering(boolean active) {
      if (active) {
         this.currentGeneration++;
      }
   }

   public boolean wasChecked(RecipeCollection collection) {
      Integer gen = this.checkedGenerations.get(collection);
      return gen != null && gen == this.currentGeneration;
   }

   public void markAsChecked(RecipeCollection collection) {
      this.checkedGenerations.put(collection, this.currentGeneration);
   }

   public boolean hasAnyTag(RecipeCollection collection) {
      if (!this.wasChecked(collection)) {
         return false;
      } else {
         Set<T> set = this.tags.get(collection);
         return set != null && !set.isEmpty();
      }
   }

   public boolean hasTag(RecipeCollection collection, T tag) {
      if (!this.wasChecked(collection)) {
         return false;
      } else {
         Set<T> set = this.tags.get(collection);
         return set != null && set.contains(tag);
      }
   }

   public Set<T> getTags(RecipeCollection collection) {
      if (!this.wasChecked(collection)) {
         return Collections.emptySet();
      } else {
         Set<T> set = this.tags.get(collection);
         return set != null ? Collections.unmodifiableSet(set) : Collections.emptySet();
      }
   }

   public boolean hasAnyTagEvenIfStale(RecipeCollection collection) {
      Set<T> set = this.tags.get(collection);
      return set != null && !set.isEmpty();
   }

   public boolean hasTagEvenIfStale(RecipeCollection collection, T tag) {
      Set<T> set = this.tags.get(collection);
      return set != null && set.contains(tag);
   }

   public Set<T> getTagsEvenIfStale(RecipeCollection collection) {
      Set<T> set = this.tags.get(collection);
      return set != null ? Collections.unmodifiableSet(set) : Collections.emptySet();
   }

   public void addTag(RecipeCollection collection, T tag) {
      this.tags.computeIfAbsent(collection, k -> new HashSet<>()).add(tag);
   }

   public void setAllTags(RecipeCollection collection, Set<T> newTags) {
      this.tags.put(collection, new HashSet<>(newTags));
   }

   public void removeTag(RecipeCollection collection, T tag) {
      Set<T> set = this.tags.get(collection);
      if (set != null) {
         set.remove(tag);
         if (set.isEmpty()) {
            this.tags.remove(collection);
            this.checkedGenerations.remove(collection);
         }
      }
   }

   public void clearTags(RecipeCollection collection) {
      this.tags.remove(collection);
      this.checkedGenerations.remove(collection);
   }

   public void clearAll(RecipeCollection collection) {
      this.tags.remove(collection);
      this.checkedGenerations.remove(collection);
   }

   public void clearAll() {
      this.tags.clear();
      this.checkedGenerations.clear();
   }
}

package dev.latvian.mods.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagEntry.Lookup;
import net.minecraft.tags.TagLoader.EntryWithSource;
import net.minecraft.util.DependencySorter;
import net.minecraft.world.item.Items;

public class CachedTagLookup<T> {
   public final Registry<T> registry;
   public final Map<ResourceLocation, List<EntryWithSource>> originalMap;
   private Map<ResourceLocation, Collection<Holder<T>>> tagMap;
   private Map<TagKey<T>, Set<T>> keyToValue;
   private Map<T, Set<TagKey<T>>> valueToKey;

   public CachedTagLookup(Registry<T> registry, Map<ResourceLocation, List<EntryWithSource>> originalMap) {
      this.registry = registry;
      this.originalMap = originalMap;
   }

   private Either<Collection<EntryWithSource>, Collection<T>> build(Lookup<T> lookup, List<EntryWithSource> entries) {
      LinkedHashSet<T> builder = new LinkedHashSet<>();
      ArrayList<EntryWithSource> list = new ArrayList<>();

      for (EntryWithSource tagloader$entrywithsource : entries) {
         if (!tagloader$entrywithsource.entry().build(lookup, tagloader$entrywithsource.remove() ? builder::remove : builder::add)
            && !tagloader$entrywithsource.remove()) {
            list.add(tagloader$entrywithsource);
         }
      }

      return list.isEmpty() ? Either.right(List.copyOf(builder)) : Either.left(list);
   }

   public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<EntryWithSource>> builders) {
      final HashMap<ResourceLocation, Collection<T>> map = new HashMap<>();
      var lookup = new Lookup<T>() {
         @Nullable
         public T element(ResourceLocation id) {
            return (T)CachedTagLookup.this.registry.getOptional(id).orElse(null);
         }

         @Nullable
         public Collection<T> tag(ResourceLocation id) {
            return map.get(id);
         }
      };
      DependencySorter<ResourceLocation, CachedTagLookup.SortingEntry> dependencysorter = new DependencySorter();
      builders.forEach((arg, list) -> dependencysorter.addEntry(arg, new CachedTagLookup.SortingEntry((List<EntryWithSource>)list)));
      dependencysorter.orderByDependencies(
         (arg, arg2) -> this.build(lookup, arg2.entries)
            .ifLeft(
               collection -> KubeJS.LOGGER
                  .error(
                     "Couldn't load tag {} as it is missing following references: {}",
                     arg,
                     collection.stream().map(Objects::toString).collect(Collectors.joining("\n\t", "\n\t", ""))
                  )
            )
            .ifRight(collection -> map.put(arg, collection))
      );
      return map;
   }

   public Map<TagKey<T>, Set<T>> keyToValue() {
      if (this.keyToValue == null) {
         Map<ResourceLocation, Collection<T>> map = this.build(this.originalMap);
         this.keyToValue = new Reference2ObjectOpenHashMap(map.size());

         for (Map.Entry<ResourceLocation, Collection<T>> entry : map.entrySet()) {
            TagKey<T> k = TagKey.create(this.registry.key(), entry.getKey());
            this.keyToValue.put(k, new LinkedHashSet<>(entry.getValue()));
         }
      }

      return this.keyToValue;
   }

   public Set<T> values(TagKey<T> key) {
      return this.keyToValue().getOrDefault(key, Set.of());
   }

   public boolean isEmpty(TagKey<T> key) {
      Set<T> set = this.values(key);
      return set.size() - (this.registry.key() == Registries.ITEM ? (set.contains(Items.AIR) ? 1 : 0) + (set.contains(Items.BARRIER) ? 1 : 0) : 0) <= 0;
   }

   public Set<TagKey<T>> keys(T value) {
      if (this.valueToKey == null) {
         this.valueToKey = new Reference2ObjectOpenHashMap();

         for (Map.Entry<TagKey<T>, Set<T>> entry : this.keyToValue().entrySet()) {
            for (T v : entry.getValue()) {
               this.valueToKey.computeIfAbsent(v, k -> new HashSet<>()).add(entry.getKey());
            }
         }
      }

      return this.valueToKey.getOrDefault(value, Set.of());
   }

   public Map<TagKey<T>, List<Holder<T>>> bindingMap() {
      Map<TagKey<T>, Set<T>> k2v = this.keyToValue();
      Reference2ObjectOpenHashMap<TagKey<T>, List<Holder<T>>> map = new Reference2ObjectOpenHashMap(k2v.size());

      for (Map.Entry<TagKey<T>, Set<T>> entry : k2v.entrySet()) {
         ArrayList<Holder<T>> list = new ArrayList<>(entry.getValue().size());

         for (T value : entry.getValue()) {
            list.add(this.registry.wrapAsHolder(value));
         }

         map.put(entry.getKey(), list);
      }

      return map;
   }

   public Map<ResourceLocation, Collection<Holder<T>>> tagMap() {
      if (this.tagMap == null) {
         this.tagMap = new HashMap<>();
         Map<TagKey<T>, Set<T>> k2v = this.keyToValue();

         for (Map.Entry<TagKey<T>, Set<T>> entry : k2v.entrySet()) {
            ArrayList<Holder<T>> list = new ArrayList<>(entry.getValue().size());

            for (T value : entry.getValue()) {
               list.add(this.registry.wrapAsHolder(value));
            }

            this.tagMap.put(entry.getKey().location(), list);
         }

         this.tagMap = Map.copyOf(this.tagMap);
      }

      return this.tagMap;
   }

   public record Entry<T>(ResourceKey<T> key, Registry<T> registry, CachedTagLookup<T> lookup) {
   }

   record SortingEntry(List<EntryWithSource> entries) implements net.minecraft.util.DependencySorter.Entry<ResourceLocation> {
      public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
         this.entries.forEach(arg -> arg.entry().visitRequiredDependencies(visitor));
      }

      public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
         this.entries.forEach(arg -> arg.entry().visitOptionalDependencies(visitor));
      }
   }
}

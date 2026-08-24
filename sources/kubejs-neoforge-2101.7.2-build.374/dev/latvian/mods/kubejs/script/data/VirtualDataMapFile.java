package dev.latvian.mods.kubejs.script.data;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.registries.datamaps.DataMapEntry;
import net.neoforged.neoforge.registries.datamaps.DataMapFile;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapEntry.Removal;

public class VirtualDataMapFile<RT, DT> implements BiConsumer<ResourceLocation, DT> {
   public final KubeDataGenerator pack;
   public final RegistryAccessContainer registryAccess;
   public final Registry<RT> registry;
   private boolean replace = false;
   private final Map<TagKey<RT>, DataMapEntry<DT>> tagAdditions = new LinkedHashMap<>();
   private final Set<TagKey<RT>> tagRemovals = new LinkedHashSet<>();
   private final Map<Holder<RT>, DataMapEntry<DT>> additions = new LinkedHashMap<>();
   private final Set<Holder<RT>> removals = new LinkedHashSet<>();

   public VirtualDataMapFile(DataMapType<RT, DT> type, VirtualDataPack pack) {
      this.pack = pack;
      this.registryAccess = pack.getRegistries();
      this.registry = this.registryAccess.access().registryOrThrow(type.registryKey());
   }

   public void replaceAll() {
      this.replace = true;
   }

   public void add(HolderSet<RT> holders, DT value) {
      this.add(holders, value, false);
   }

   public void add(HolderSet<RT> holders, DT value, boolean replace) {
      for (Holder<RT> holder : holders) {
         this.add(holder, value, replace);
      }
   }

   public void remove(HolderSet<RT> holders) {
      holders.forEach(this.removals::add);
   }

   public void add(Holder<RT> holder, DT value) {
      this.add(holder, value, false);
   }

   public void add(Holder<RT> holder, DT value, boolean replace) {
      this.additions.put(holder, new DataMapEntry(value, replace));
   }

   public void remove(Holder<RT> holder) {
      this.removals.add(holder);
   }

   public void add(RT holder, DT value) {
      this.add(holder, value, false);
   }

   public void add(RT holder, DT value, boolean replace) {
      this.additions.put(this.registry.wrapAsHolder(holder), new DataMapEntry(value, replace));
   }

   public void remove(RT holder) {
      this.removals.add(this.registry.wrapAsHolder(holder));
   }

   public void addTag(TagKey<RT> tag, DT value) {
      this.addTag(tag, value, false);
   }

   public void addTag(TagKey<RT> tag, DT value, boolean replace) {
      this.tagAdditions.put(tag, new DataMapEntry(value, replace));
   }

   public void removeTag(TagKey<RT> tag) {
      this.tagRemovals.add(tag);
   }

   public void clear() {
      this.tagAdditions.clear();
      this.tagRemovals.clear();
      this.additions.clear();
      this.removals.clear();
   }

   private void buildValues(Map<Either<TagKey<RT>, ResourceKey<RT>>, Optional<WithConditions<DataMapEntry<DT>>>> map) {
      for (Entry<TagKey<RT>, DataMapEntry<DT>> tagEntry : this.tagAdditions.entrySet()) {
         TagKey<RT> key = tagEntry.getKey();
         DataMapEntry<DT> entry = tagEntry.getValue();
         map.put(Either.left(key), Optional.of(new WithConditions(List.of(), entry)));
      }

      for (Entry<Holder<RT>, DataMapEntry<DT>> holderEntry : this.additions.entrySet()) {
         Holder<RT> holder = holderEntry.getKey();
         DataMapEntry<DT> entry = holderEntry.getValue();
         map.put(Either.right(holder.getKey()), Optional.of(new WithConditions(List.of(), entry)));
      }
   }

   private void buildRemovals(ArrayList<Removal<DT, RT>> list) {
      for (TagKey<RT> key : this.tagRemovals) {
         list.add(new Removal(Either.left(key), Optional.empty()));
      }

      for (Holder<RT> removal : this.removals) {
         list.add(new Removal(Either.right(removal.getKey()), Optional.empty()));
      }
   }

   DataMapFile<DT, RT> toFile() {
      return new DataMapFile(this.replace, (Map)Util.make(new HashMap(), this::buildValues), (List)Util.make(new ArrayList(), this::buildRemovals));
   }

   @HideFromJS
   public void accept(ResourceLocation id, DT data) {
      this.add(this.registry.getHolderOrThrow(ResourceKey.create(this.registry.key(), id)), data);
   }
}

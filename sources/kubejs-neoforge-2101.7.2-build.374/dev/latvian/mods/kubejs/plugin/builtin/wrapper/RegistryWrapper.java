package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RegistryWrapper<T>(Registry<T> registry, ResourceKey<T> unknownKey) implements Iterable<T> {
   public static RegistryWrapper<?> of(Context cx, ResourceLocation id) {
      return RegistryAccessContainer.of(cx).wrapRegistry(id);
   }

   public static RegistryAccessContainer access() {
      return RegistryAccessContainer.current;
   }

   public T get(ResourceLocation id) {
      return (T)this.registry.get(id);
   }

   public boolean contains(ResourceLocation id) {
      return this.registry.containsKey(id);
   }

   public boolean containsValue(T value) {
      return this.registry.containsValue(value);
   }

   public Set<Entry<ResourceLocation, T>> getEntrySet() {
      return this.registry.entrySet().stream().map(e -> Map.entry(((ResourceKey)e.getKey()).location(), e.getValue())).collect(Collectors.toSet());
   }

   public Map<ResourceLocation, T> getValueMap() {
      return this.registry.entrySet().stream().collect(Collectors.toMap(e -> ((ResourceKey)e.getKey()).location(), Entry::getValue));
   }

   public HolderSetWrapper<T> getValues(Object filter) {
      HolderSet<T> holderSet = HolderWrapper.wrapSimpleSet(this.registry, filter);
      return new HolderSetWrapper<>(this.registry, Objects.requireNonNullElseGet(holderSet, HolderSet::empty));
   }

   <A> DataMapWrapper<T, A> getDataMap(DataMapType<T, A> type) {
      return new DataMapWrapper<>(this.registry, type);
   }

   public DataMapWrapper<T, ?> getDataMap(ResourceLocation id) {
      return DataMapWrapper.of(this, id);
   }

   public List<T> getValues() {
      return this.registry.stream().collect(Collectors.toList());
   }

   public Set<ResourceLocation> getKeys() {
      return this.registry.keySet();
   }

   @Nullable
   public T getRandom() {
      return this.getRandom(UtilsJS.RANDOM);
   }

   @Nullable
   public T getRandom(RandomSource random) {
      return this.registry.getRandom(random).<T>map(Holder::value).orElse(null);
   }

   @Nullable
   public ResourceLocation getId(T value) {
      return this.registry.getKey(value);
   }

   @Nullable
   public ResourceKey<T> getKey(T value) {
      return this.registry.getResourceKey(value).orElse(this.unknownKey);
   }

   @NotNull
   public ListIterator<T> iterator() {
      return this.getValues().listIterator();
   }
}

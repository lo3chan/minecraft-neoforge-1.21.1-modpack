package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DataMapWrapper<T, A>(Registry<T> registry, DataMapType<T, A> type) implements Iterable<DataMapWrapper.Data<T, A>> {
   public static DataMapWrapper<?, ?> of(Context cx, ResourceLocation registry, ResourceLocation id) {
      RegistryWrapper<?> reg = RegistryAccessContainer.of(cx).wrapRegistry(registry);
      return of(reg, id);
   }

   public static DataMapType<?, ?> typeOf(Context cx, ResourceLocation registry, ResourceLocation id) {
      RegistryWrapper<?> reg = RegistryAccessContainer.of(cx).wrapRegistry(registry);
      return typeOf(reg, id);
   }

   public static <T> DataMapWrapper<T, ?> of(RegistryWrapper<T> registry, ResourceLocation id) {
      DataMapType<T, ?> type = typeOf(registry, id);
      return new DataMapWrapper<>(registry.registry(), (DataMapType<T, A>)type);
   }

   public static <T> DataMapType<T, ?> typeOf(RegistryWrapper<T> registry, ResourceLocation id) {
      return RegistryManager.getDataMap(registry.registry().key(), id);
   }

   @Nullable
   public A get(T item) {
      return (A)this.registry.getData(this.type, (ResourceKey)this.registry.getResourceKey(item).orElseThrow());
   }

   public Stream<T> keys() {
      return this.byKey().keySet().stream().map(this.registry::get);
   }

   @NotNull
   @Override
   public Iterator<DataMapWrapper.Data<T, A>> iterator() {
      return new Iterator<DataMapWrapper.Data<T, A>>() {
         final Iterator<Entry<ResourceKey<T>, A>> backing = DataMapWrapper.this.byKey().entrySet().iterator();

         @Override
         public boolean hasNext() {
            return this.backing.hasNext();
         }

         public DataMapWrapper.Data<T, A> next() {
            Entry<ResourceKey<T>, A> entry = this.backing.next();
            return new DataMapWrapper.Data<>((T)DataMapWrapper.this.registry.get(entry.getKey()), entry.getValue());
         }
      };
   }

   @NotNull
   public Map<ResourceKey<T>, A> byKey() {
      return this.registry.getDataMap(this.type);
   }

   public record Data<T, A>(T element, A data) {
   }
}

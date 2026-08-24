package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.common.collect.Iterators;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record HolderSetWrapper<T>(Registry<T> registry, HolderSet<T> holders) implements Iterable<T> {
   public int size() {
      return this.holders.size();
   }

   public boolean isEmpty() {
      return this.holders.size() == 0;
   }

   public boolean contains(ResourceLocation id) {
      return this.registry.getHolder(id).filter(this.holders::contains).isPresent();
   }

   public boolean containsValue(T value) {
      return this.holders.contains(this.registry.wrapAsHolder(value));
   }

   public List<T> getValues() {
      return this.holders.stream().<T>map(Holder::value).toList();
   }

   public Set<ResourceLocation> getKeys() {
      return this.holders.stream().map(holder -> {
         ResourceKey<T> key = holder.getKey();
         return key == null ? null : key.location();
      }).filter(Objects::nonNull).collect(Collectors.toSet());
   }

   @Nullable
   public T getRandom() {
      return this.getRandom(UtilsJS.RANDOM);
   }

   @Nullable
   public T getRandom(RandomSource random) {
      return this.holders.getRandomElement(random).<T>map(Holder::value).orElse(null);
   }

   @NotNull
   @Override
   public Iterator<T> iterator() {
      return Iterators.transform(this.holders.iterator(), Holder::value);
   }
}

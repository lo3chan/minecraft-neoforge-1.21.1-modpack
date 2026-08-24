package fuzs.puzzleslib.impl.config.serialization;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfigDataSetImpl<T> implements ConfigDataSet<T> {
   private static final Set<Class<?>> SUPPORTED_DATA_TYPES = ImmutableSet.of(
      boolean.class, Boolean.class, int.class, Integer.class, double.class, Double.class, new Class[]{String.class}
   );
   private final KeyedValueProvider<T> valueProvider;
   private final List<ConfigDataSetImpl.EntryHolder<?, T>> values = new ArrayList<>();
   private final BiPredicate<Integer, Object> filter;
   private final int dataSize;
   private Map<T, Object[]> dissolved;

   public ConfigDataSetImpl(KeyedValueProvider<T> valueProvider, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
      this.valueProvider = valueProvider;
      this.filter = filter;

      for (Class<?> clazz : types) {
         if (!SUPPORTED_DATA_TYPES.contains(clazz)) {
            throw new IllegalArgumentException("Data type of clazz %s is not supported".formatted(clazz));
         }
      }

      this.dataSize = types.length;

      for (String value : values) {
         this.deserialize(value, types).ifPresent(this.values::add);
      }

      TagsUpdatedCallback.EVENT.register((registryAccess, client) -> this.dissolved = null);
   }

   private static Object deserializeData(Class<?> clazz, String source) throws RuntimeException {
      if (clazz != boolean.class && clazz != Boolean.class) {
         if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(source);
         } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(source);
         } else if (clazz == String.class) {
            return source;
         } else {
            throw new IllegalArgumentException("Data type of clazz %s is not supported".formatted(clazz));
         }
      } else if (source.equals("true")) {
         return true;
      } else if (source.equals("false")) {
         return false;
      } else {
         throw new IllegalArgumentException("%s is not a boolean value".formatted(source));
      }
   }

   @Override
   public Map<T, Object[]> toMap() {
      return this.dissolve();
   }

   @Override
   public Set<T> toSet() {
      return this.toMap().keySet();
   }

   @Override
   public Iterator<T> iterator() {
      return this.toSet().iterator();
   }

   @Override
   public int size() {
      return this.toMap().size();
   }

   @Override
   public boolean isEmpty() {
      return this.toMap().isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.toSet().contains(o);
   }

   @NotNull
   @Override
   public Object[] toArray() {
      return this.toSet().toArray();
   }

   @NotNull
   @Override
   public <T1> T1[] toArray(@NotNull T1[] a) {
      return this.toSet().toArray(a);
   }

   @Override
   public boolean add(T t) {
      return this.toSet().add(t);
   }

   @Override
   public boolean remove(Object o) {
      return this.toSet().remove(o);
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.toSet().containsAll(c);
   }

   @Override
   public boolean addAll(@NotNull Collection<? extends T> c) {
      return this.toSet().addAll(c);
   }

   @Override
   public boolean removeAll(@NotNull Collection<?> c) {
      return this.toSet().removeAll(c);
   }

   @Override
   public boolean retainAll(@NotNull Collection<?> c) {
      return this.toSet().retainAll(c);
   }

   @Override
   public void clear() {
      this.toMap().clear();
   }

   @Nullable
   @Override
   public Object[] get(T entry) {
      return this.toMap().get(entry);
   }

   @Override
   public <V> V get(T entry, int index) {
      Objects.checkIndex(index, this.dataSize);
      Object[] data = this.get(entry);
      Objects.requireNonNull(data, "data is null");
      return (V)data[index];
   }

   @Override
   public <V> Optional<V> getOptional(T entry, int index) {
      return index >= 0 && index < this.dataSize ? Optional.ofNullable(this.get(entry)).map(data -> (V)data[index]) : Optional.empty();
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof ConfigDataSet<?> impl && this.toMap().equals(impl.toMap());
   }

   @Override
   public int hashCode() {
      return this.toMap().hashCode();
   }

   private Map<T, Object[]> dissolve() {
      Map<T, Object[]> dissolved = this.dissolved;
      if (dissolved == null) {
         Map<T, Object[]> entries = new IdentityHashMap<>();
         Set<T> toRemove = Sets.newIdentityHashSet();

         for (ConfigDataSetImpl.EntryHolder<?, T> holder : this.values) {
            if (holder instanceof ConfigDataSetImpl.TagEntryHolder) {
               holder.dissolve(holder.inverted ? (t, objects) -> toRemove.add(t) : entries::put);
            }
         }

         for (ConfigDataSetImpl.EntryHolder<?, T> holderx : this.values) {
            if (holderx instanceof ConfigDataSetImpl.RegistryEntryHolder) {
               holderx.dissolve(holderx.inverted ? (t, objects) -> toRemove.add(t) : entries::put);
            }
         }

         if (entries.isEmpty() && !toRemove.isEmpty()) {
            entries = this.valueProvider
               .streamValues()
               .collect(Collectors.toMap(Function.identity(), t -> ConfigDataSetImpl.EntryHolder.EMPTY_DATA, (o1, o2) -> o1, Maps::newIdentityHashMap));
         }

         entries.keySet().removeIf(t -> !this.filter.test(0, t) || toRemove.contains(t));
         this.dissolved = dissolved = Collections.unmodifiableMap(entries);
      }

      return dissolved;
   }

   private Optional<ConfigDataSetImpl.EntryHolder<?, T>> deserialize(String source, Class<?>[] types) {
      String[] sources = source.trim().split(",");

      try {
         String newSource = sources[0].trim();
         if (!newSource.startsWith("!")) {
            Object[] data = new Object[types.length];

            for (int i = 0; i < types.length; i++) {
               if (sources.length - 1 <= i) {
                  throw new IllegalArgumentException("Data index out of bounds, index was %s, but length is %s".formatted(i + 1, sources.length));
               }

               data[i] = deserializeData(types[i], sources[i + 1].trim());
               if (!this.filter.test(i + 1, data[i])) {
                  throw new IllegalStateException("Data %s at index %s from source entry %s does not conform to filter".formatted(data[i], i, source));
               }
            }

            return Optional.of(this.deserialize(newSource).withData(data));
         } else {
            return Optional.of(this.deserialize(newSource));
         }
      } catch (Exception var7) {
         PuzzlesLib.LOGGER.warn("Unable to parse entry {}", source, var7);
         return Optional.empty();
      }
   }

   private ConfigDataSetImpl.EntryHolder<?, T> deserialize(String source) throws RuntimeException {
      boolean inverted = source.startsWith("!");
      if (inverted) {
         source = source.substring(1);
      }

      boolean tagHolder = source.startsWith("#");
      if (tagHolder) {
         source = source.substring(1);
      }

      if (!source.contains(":")) {
         source = "minecraft:" + source;
      }

      if (tagHolder) {
         if (this.valueProvider instanceof RegistryProvider<T> registryProvider) {
            return new ConfigDataSetImpl.TagEntryHolder<>(registryProvider, source, inverted);
         } else {
            throw new IllegalArgumentException("Value provider %s does not support tags!".formatted(this.valueProvider.name()));
         }
      } else {
         return new ConfigDataSetImpl.RegistryEntryHolder<>(this.valueProvider, source, inverted);
      }
   }

   private abstract static class EntryHolder<D, E> {
      public static final Object[] EMPTY_DATA = new Object[0];
      private final String providerName;
      public final boolean inverted;
      private final String input;
      private Object[] data = EMPTY_DATA;

      protected EntryHolder(String providerName, String input, boolean inverted) {
         this.providerName = providerName;
         this.input = input;
         this.inverted = inverted;
      }

      public ConfigDataSetImpl.EntryHolder<D, E> withData(Object[] data) {
         this.data = data;
         return this;
      }

      public final void dissolve(BiConsumer<E, Object[]> builder) {
         this.findRegistryMatches(this.input).stream().flatMap(this::dissolveValue).forEach(value -> builder.accept((E)value, this.data));
      }

      private Collection<D> findRegistryMatches(String s) {
         Collection<D> matches = Sets.newHashSet();
         if (!s.contains("*")) {
            Optional.ofNullable(ResourceLocationHelper.tryParse(s)).flatMap(this::toValue).ifPresent(matches::add);
         } else {
            String regexSource = s.replace("*", "[a-z0-9/._-]*");
            this.allValues().filter(entry -> entry.getKey().toString().matches(regexSource)).map(Entry::getValue).forEach(matches::add);
         }

         if (matches.isEmpty()) {
            PuzzlesLib.LOGGER.warn("Unable to parse entry {}: No matches found in {}", s, this.providerName);
         }

         return matches;
      }

      protected abstract Stream<E> dissolveValue(D var1);

      protected abstract Optional<D> toValue(ResourceLocation var1);

      protected abstract Stream<Entry<ResourceLocation, D>> allValues();
   }

   private static class RegistryEntryHolder<T> extends ConfigDataSetImpl.EntryHolder<T, T> {
      private final KeyedValueProvider<T> valueProvider;

      RegistryEntryHolder(KeyedValueProvider<T> valueProvider, String source, boolean inverted) {
         super(valueProvider.name(), source, inverted);
         this.valueProvider = valueProvider;
      }

      @Override
      protected Stream<T> dissolveValue(T entry) {
         return Stream.of(entry);
      }

      @Override
      protected Optional<T> toValue(ResourceLocation identifier) {
         return this.valueProvider.getValue(identifier);
      }

      @Override
      protected Stream<Entry<ResourceLocation, T>> allValues() {
         return this.valueProvider.stream();
      }
   }

   private static class TagEntryHolder<T> extends ConfigDataSetImpl.EntryHolder<TagKey<T>, T> {
      private final Registry<T> registry;

      TagEntryHolder(RegistryProvider<T> registryProvider, String source, boolean inverted) {
         super(registryProvider.name(), source, inverted);
         this.registry = registryProvider.registry();
      }

      public Stream<T> dissolveValue(TagKey<T> entry) {
         return StreamSupport.stream(this.registry.getTagOrEmpty(entry).spliterator(), false).map(Holder::value);
      }

      @Override
      protected Optional<TagKey<T>> toValue(ResourceLocation identifier) {
         TagKey<T> tag = TagKey.create(this.registry.key(), identifier);
         return this.registry.getTag(tag).isEmpty() ? Optional.empty() : Optional.of(tag);
      }

      @Override
      protected Stream<Entry<ResourceLocation, TagKey<T>>> allValues() {
         return this.registry.getTagNames().map(tagKey -> Map.entry(tagKey.location(), (TagKey<T>)tagKey));
      }
   }
}

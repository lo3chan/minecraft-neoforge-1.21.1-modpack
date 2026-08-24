package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.impl.config.serialization.ConfigDataSetImpl;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigDataSet<T> extends Collection<T> {
   String CONFIG_DESCRIPTION = "Format for every entry is \"<namespace>:<path>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color. Begin an entry with \"!\" to make sure it is excluded, useful e.g. when it has already been matched by another pattern.";
   String CONFIG_DESCRIPTION_WITHOUT_TAGS = "Format for every entry is \"<namespace>:<path>\". Tags are not supported. Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color. Begin an entry with \"!\" to make sure it is excluded, useful e.g. when it has already been matched by another pattern.";

   static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, String... values) {
      return from(KeyedValueProvider.registryEntries(registryKey), values);
   }

   static <T> ConfigDataSet<T> from(ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, Class<?>... types) {
      return from(KeyedValueProvider.registryEntries(registryKey), values, types);
   }

   static <T> ConfigDataSet<T> from(
      ResourceKey<? extends Registry<? super T>> registryKey, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types
   ) {
      return from(KeyedValueProvider.registryEntries(registryKey), values, filter, types);
   }

   static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, String... values) {
      return from(valueProvider, Arrays.asList(values));
   }

   static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, Class<?>... types) {
      return from(valueProvider, values, (index, value) -> true, types);
   }

   static <T> ConfigDataSet<T> from(KeyedValueProvider<T> valueProvider, List<String> values, BiPredicate<Integer, Object> filter, Class<?>... types) {
      return new ConfigDataSetImpl<>(valueProvider, values, filter, types);
   }

   Map<T, Object[]> toMap();

   Set<T> toSet();

   @Nullable
   Object[] get(T var1);

   <V> V get(T var1, int var2);

   <V> Optional<V> getOptional(T var1, int var2);

   @Deprecated
   @Override
   boolean add(T var1);

   @Deprecated
   @Override
   boolean remove(Object var1);

   @Deprecated
   @Override
   boolean addAll(@NotNull Collection<? extends T> var1);

   @Deprecated
   @Override
   boolean removeAll(@NotNull Collection<?> var1);

   @Deprecated
   @Override
   boolean retainAll(@NotNull Collection<?> var1);

   @Deprecated
   @Override
   void clear();
}

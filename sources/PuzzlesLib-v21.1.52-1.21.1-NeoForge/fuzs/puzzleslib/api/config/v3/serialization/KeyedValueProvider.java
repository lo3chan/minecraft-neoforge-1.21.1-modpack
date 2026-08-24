package fuzs.puzzleslib.api.config.v3.serialization;

import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.impl.config.serialization.EnumProvider;
import fuzs.puzzleslib.impl.config.serialization.RegistryProvider;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.data.SortingTagBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

public interface KeyedValueProvider<T> {
   static <T> KeyedValueProvider<T> registryEntries(ResourceKey<? extends Registry<? super T>> registryKey) {
      return new RegistryProvider<>(registryKey);
   }

   static <T extends Enum<T>> KeyedValueProvider<T> enumConstants(Class<T> enumClazz) {
      return enumConstants(enumClazz, "minecraft");
   }

   static <T extends Enum<T>> KeyedValueProvider<T> enumConstants(Class<T> enumClazz, String modId) {
      return new EnumProvider<>(enumClazz, modId);
   }

   @SafeVarargs
   static <T> List<String> toString(ResourceKey<? extends Registry<? super T>> registryKey, T... entries) {
      return toString(registryEntries(registryKey), entries);
   }

   @Deprecated
   static <T> AbstractTagAppender<T> tagAppender(ResourceKey<? extends Registry<? super T>> registryKey) {
      Registry<T> registry = RegistryHelper.findNullableBuiltInRegistry(registryKey);
      Function<T, ResourceKey<T>> keyExtractor = registry != null ? t -> RegistryHelper.getResourceKeyOrThrow(registry, t) : null;
      return ProxyImpl.get().getTagAppenderV2(new SortingTagBuilder(), keyExtractor);
   }

   static <T> fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tags(ResourceKey<? extends Registry<? super T>> registryKey) {
      return tags(new SortingTagBuilder(), registryKey);
   }

   static <T> fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tags(TagBuilder tagBuilder, ResourceKey<? extends Registry<? super T>> registryKey) {
      Optional<Registry<T>> optional = LookupHelper.getRegistry(registryKey);
      Function<T, ResourceKey<T>> keyExtractor = optional.isPresent()
         ? t -> optional.<ResourceKey<T>>flatMap(registry -> registry.getResourceKey(t))
            .orElseThrow(() -> new IllegalStateException("Missing value in " + registryKey + ": " + t))
         : null;
      return ProxyImpl.get().getTagAppenderV3(tagBuilder, keyExtractor);
   }

   @SafeVarargs
   static <T extends Enum<T>> List<String> toString(Class<T> enumClazz, T... entries) {
      return toString(enumClazz, "minecraft", entries);
   }

   @SafeVarargs
   static <T extends Enum<T>> List<String> toString(Class<T> enumClazz, String modId, T... entries) {
      return toString(enumConstants(enumClazz, modId), entries);
   }

   @SafeVarargs
   static <T> List<String> toString(KeyedValueProvider<T> valueProvider, T... entries) {
      return Stream.<T>of(entries)
         .peek(Objects::requireNonNull)
         .map(valueProvider::getKey)
         .filter(Objects::nonNull)
         .<String>map(ResourceLocation::toString)
         .collect(Collectors.toList());
   }

   Optional<T> getValue(ResourceLocation var1);

   ResourceLocation getKey(T var1);

   Stream<Entry<ResourceLocation, T>> stream();

   Stream<T> streamValues();

   String name();
}

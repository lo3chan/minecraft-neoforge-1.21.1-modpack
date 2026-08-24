package fuzs.puzzleslib.impl.config.serialization;

import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record RegistryProvider<T>(Registry<T> registry) implements KeyedValueProvider<T> {
   public RegistryProvider(ResourceKey<? extends Registry<? super T>> registryKey) {
      this(RegistryHelper.findBuiltInRegistry(registryKey));
   }

   @Override
   public Optional<T> getValue(ResourceLocation name) {
      return this.registry.getOptional(name);
   }

   @Override
   public ResourceLocation getKey(T value) {
      return this.registry.getKey(value);
   }

   @Override
   public Stream<Entry<ResourceLocation, T>> stream() {
      return this.registry.entrySet().stream().map(entry -> Map.entry(((ResourceKey)entry.getKey()).location(), (T)entry.getValue()));
   }

   @Override
   public Stream<T> streamValues() {
      return this.registry.stream();
   }

   @Override
   public String name() {
      return this.registry.key().location().toString();
   }
}

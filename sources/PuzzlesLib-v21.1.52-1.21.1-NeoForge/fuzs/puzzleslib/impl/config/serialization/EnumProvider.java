package fuzs.puzzleslib.impl.config.serialization;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public final class EnumProvider<T extends Enum<T>> implements KeyedValueProvider<T> {
   private final Class<T> clazz;
   private final BiMap<ResourceLocation, T> values;

   public EnumProvider(Class<T> enumClazz, String modId) {
      this.clazz = enumClazz;
      this.values = Stream.of(enumClazz.getEnumConstants())
         .collect(
            ImmutableBiMap.toImmutableBiMap(
               t -> ResourceLocationHelper.fromNamespaceAndPath(modId, Util.sanitizeName(t.name(), ResourceLocation::validPathChar)), Function.identity()
            )
         );
   }

   @Override
   public Optional<T> getValue(ResourceLocation name) {
      return Optional.ofNullable((T)this.values.get(name));
   }

   public ResourceLocation getKey(T value) {
      ResourceLocation resourceLocation = (ResourceLocation)this.values.inverse().get(value);
      Objects.requireNonNull(resourceLocation, "resource location is null");
      return resourceLocation;
   }

   @Override
   public Stream<Entry<ResourceLocation, T>> stream() {
      return this.values.entrySet().stream();
   }

   @Override
   public Stream<T> streamValues() {
      return this.values.values().stream();
   }

   @Override
   public String name() {
      return this.clazz.getSimpleName();
   }
}

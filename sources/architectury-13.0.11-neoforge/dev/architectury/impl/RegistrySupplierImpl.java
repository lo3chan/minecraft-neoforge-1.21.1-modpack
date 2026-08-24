package dev.architectury.impl;

import com.mojang.datafixers.util.Either;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface RegistrySupplierImpl<T> extends RegistrySupplier<T> {
   @Nullable
   Holder<T> getHolder();

   default T value() {
      return this.get();
   }

   default boolean isBound() {
      return this.isPresent();
   }

   default boolean is(ResourceLocation resourceLocation) {
      return this.getId().equals(resourceLocation);
   }

   default boolean is(ResourceKey<T> resourceKey) {
      return this.getKey().equals(resourceKey);
   }

   default boolean is(Predicate<ResourceKey<T>> predicate) {
      return predicate.test(this.getKey());
   }

   default boolean is(TagKey<T> tagKey) {
      Holder<T> holder = this.getHolder();
      return holder != null && holder.is(tagKey);
   }

   default boolean is(Holder<T> holder) {
      return holder.is(this.getKey());
   }

   default Stream<TagKey<T>> tags() {
      Holder<T> holder = this.getHolder();
      return holder != null ? holder.tags() : Stream.empty();
   }

   default Either<ResourceKey<T>, T> unwrap() {
      return Either.left(this.getKey());
   }

   default Optional<ResourceKey<T>> unwrapKey() {
      return Optional.of(this.getKey());
   }

   default Kind kind() {
      return Kind.REFERENCE;
   }

   default boolean canSerializeIn(HolderOwner<T> holderOwner) {
      Holder<T> holder = this.getHolder();
      return holder != null && holder.canSerializeIn(holderOwner);
   }
}

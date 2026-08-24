package net.blay09.mods.balm.core;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class DeferredHolder<T> implements Holder<T> {
   private final ResourceKey<T> resourceKey;
   private Holder<T> delegate;

   public DeferredHolder(ResourceKey<T> resourceKey) {
      this.resourceKey = resourceKey;
   }

   public T value() {
      this.tryBind();
      if (this.delegate == null) {
         throw new IllegalStateException("Tried to access " + this.resourceKey + " before it was bound");
      } else {
         return (T)this.delegate.value();
      }
   }

   public boolean isBound() {
      this.tryBind();
      return this.delegate != null && this.delegate.isBound();
   }

   public boolean is(ResourceLocation identifier) {
      return this.resourceKey.location().equals(identifier);
   }

   public boolean is(ResourceKey<T> resourceKey) {
      return this.resourceKey.equals(resourceKey);
   }

   public boolean is(Predicate<ResourceKey<T>> predicate) {
      return predicate.test(this.resourceKey);
   }

   public boolean is(TagKey<T> tagKey) {
      this.tryBind();
      return this.delegate != null && this.delegate.is(tagKey);
   }

   public boolean is(Holder<T> holder) {
      this.tryBind();
      return this.delegate != null ? this.delegate.is(holder) : this.equals(holder);
   }

   public Stream<TagKey<T>> tags() {
      this.tryBind();
      return this.delegate != null ? this.delegate.tags() : Stream.empty();
   }

   public Either<ResourceKey<T>, T> unwrap() {
      return Either.left(this.resourceKey);
   }

   public Optional<ResourceKey<T>> unwrapKey() {
      return Optional.of(this.resourceKey);
   }

   public Kind kind() {
      return Kind.REFERENCE;
   }

   public boolean canSerializeIn(HolderOwner<T> holderOwner) {
      this.tryBind();
      return this.delegate != null && this.delegate.canSerializeIn(holderOwner);
   }

   private void tryBind() {
      if (this.delegate == null) {
         Registry<T> registry = (Registry<T>)BuiltInRegistries.REGISTRY.get(this.resourceKey.registry());
         if (registry != null) {
            this.delegate = (Holder<T>)registry.getHolder(this.resourceKey).orElse(null);
         }
      }
   }
}

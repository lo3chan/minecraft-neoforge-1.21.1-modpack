package net.mehvahdjukaar.moonlight.api.misc;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public class OptRegSupplier<A> implements RegSupplier<A> {
   private final Supplier<A> supp;
   private final Supplier<Holder<A>> holderSupplier;
   private final ResourceLocation id;
   private final ResourceKey<A> key;

   protected OptRegSupplier(Registry<A> reg, ResourceLocation loc) {
      this.supp = Suppliers.memoize(() -> reg.getOptional(loc).orElse(null));
      this.holderSupplier = Suppliers.memoize(() -> (Holder)reg.getHolder(loc).orElse(null));
      this.id = loc;
      this.key = ResourceKey.create(reg.key(), loc);
   }

   public static <A> OptRegSupplier<A> of(ResourceLocation location, Registry<A> registry) {
      return new OptRegSupplier<>(registry, location);
   }

   public static <A> OptRegSupplier<A> of(ResourceLocation location, ResourceKey<Registry<A>> registry) {
      Registry<A> reg = (Registry<A>)BuiltInRegistries.REGISTRY.getOrThrow(registry);
      return new OptRegSupplier<>(reg, location);
   }

   public static <A> OptRegSupplier<A> wrap(A obj, ResourceKey<Registry<A>> registry) {
      Registry<A> reg = (Registry<A>)BuiltInRegistries.REGISTRY.getOrThrow(registry);
      return wrap(obj, reg);
   }

   public static <A> OptRegSupplier<A> wrap(A obj, Registry<A> registry) {
      return of(Utils.getID(obj), registry);
   }

   public Optional<Holder<A>> asOptionalHolder() {
      return Optional.ofNullable(this.holderSupplier.get());
   }

   @Nullable
   public A value() {
      return this.get();
   }

   public boolean isBound() {
      return this.isPresent() ? this.holderSupplier.get().isBound() : false;
   }

   public boolean is(ResourceLocation location) {
      return this.id.equals(location);
   }

   public boolean is(ResourceKey<A> resourceKey) {
      return this.key.equals(resourceKey);
   }

   public boolean is(Predicate<ResourceKey<A>> predicate) {
      return predicate.test(this.key);
   }

   public boolean is(TagKey<A> tag) {
      Holder<A> h = this.holderSupplier.get();
      return h != null && h.is(tag);
   }

   public boolean is(Holder<A> holder) {
      Holder<A> h = this.holderSupplier.get();
      return h != null && h.equals(holder);
   }

   public Stream<TagKey<A>> tags() {
      Holder<A> h = this.holderSupplier.get();
      return h != null ? h.tags() : Stream.empty();
   }

   public Either<ResourceKey<A>, A> unwrap() {
      return Either.right(this.get());
   }

   public Optional<ResourceKey<A>> unwrapKey() {
      return Optional.ofNullable(this.key);
   }

   public Kind kind() {
      return this.isPresent() ? this.holderSupplier.get().kind() : Kind.DIRECT;
   }

   public boolean canSerializeIn(HolderOwner<A> owner) {
      return this.isPresent() ? this.holderSupplier.get().canSerializeIn(owner) : false;
   }

   public boolean isPresent() {
      return this.get() != null;
   }

   public void ifPresent(Consumer<A> consumer) {
      A a = this.get();
      if (a != null) {
         consumer.accept(a);
      }
   }

   @Nullable
   @Override
   public A get() {
      return this.supp.get();
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Nullable
   @Override
   public ResourceKey<A> getKey() {
      return this.key;
   }

   @Nullable
   @Override
   public Holder<A> getHolder() {
      return this.holderSupplier.get();
   }
}

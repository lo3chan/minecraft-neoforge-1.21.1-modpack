package me.lucko.spark.lib.adventure.pointer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PointersSupplierImpl<T> implements PointersSupplier<T> {
   private final PointersSupplier<? super T> parent;
   private final Map<Pointer<?>, Function<T, ?>> resolvers;

   PointersSupplierImpl(@NotNull final PointersSupplierImpl.BuilderImpl<T> builder) {
      this.parent = builder.parent;
      this.resolvers = new HashMap<>(builder.resolvers);
   }

   @NotNull
   @Override
   public Pointers view(@NotNull final T instance) {
      return new PointersSupplierImpl.ForwardingPointers<>(instance, this);
   }

   @Override
   public <P> boolean supports(@NotNull final Pointer<P> pointer) {
      if (this.resolvers.containsKey(Objects.requireNonNull(pointer, "pointer"))) {
         return true;
      } else {
         return this.parent == null ? false : this.parent.supports(pointer);
      }
   }

   @Nullable
   @Override
   public <P> Function<? super T, P> resolver(@NotNull final Pointer<P> pointer) {
      Function<? super T, ?> resolver = this.resolvers.get(Objects.requireNonNull(pointer, "pointer"));
      if (resolver != null) {
         return (Function<? super T, P>)resolver;
      } else {
         return this.parent == null ? null : this.parent.resolver(pointer);
      }
   }

   static final class BuilderImpl<T> implements PointersSupplier.Builder<T> {
      private PointersSupplier<? super T> parent = null;
      private final Map<Pointer<?>, Function<T, ?>> resolvers = new HashMap<>();

      @NotNull
      @Override
      public PointersSupplier.Builder<T> parent(@Nullable final PointersSupplier<? super T> parent) {
         this.parent = parent;
         return this;
      }

      @NotNull
      @Override
      public <P> PointersSupplier.Builder<T> resolving(@NotNull final Pointer<P> pointer, @NotNull final Function<T, P> resolver) {
         this.resolvers.put(pointer, resolver);
         return this;
      }

      @NotNull
      public PointersSupplier<T> build() {
         return new PointersSupplierImpl<>(this);
      }
   }

   static final class ForwardingPointers<U> implements Pointers {
      private final U instance;
      private final PointersSupplierImpl<U> supplier;

      ForwardingPointers(@NotNull final U instance, @NotNull final PointersSupplierImpl<U> supplier) {
         this.instance = instance;
         this.supplier = supplier;
      }

      @NotNull
      @Override
      public <T> Optional<T> get(@NotNull final Pointer<T> pointer) {
         Function<? super U, ?> resolver = this.supplier.resolvers.get(Objects.requireNonNull(pointer, "pointer"));
         if (resolver == null) {
            resolver = this.supplier.parent.resolver(pointer);
         }

         return resolver == null ? Optional.empty() : Optional.ofNullable((T)resolver.apply(this.instance));
      }

      @Override
      public <T> boolean supports(@NotNull final Pointer<T> pointer) {
         return this.supplier.supports(pointer);
      }

      @NotNull
      public Pointers.Builder toBuilder() {
         Pointers.Builder builder = this.supplier.parent == null ? Pointers.builder() : this.supplier.parent.view(this.instance).toBuilder();

         for (Entry<Pointer<?>, Function<U, ?>> entry : this.supplier.resolvers.entrySet()) {
            builder.withDynamic(entry.getKey(), () -> entry.getValue().apply(this.instance));
         }

         return builder;
      }
   }
}

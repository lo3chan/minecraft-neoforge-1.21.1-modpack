package me.lucko.spark.lib.adventure.pointer;

import java.util.function.Function;
import me.lucko.spark.lib.adventure.builder.AbstractBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PointersSupplier<T> {
   @NotNull
   static <T> PointersSupplier.Builder<T> builder() {
      return new PointersSupplierImpl.BuilderImpl<>();
   }

   @NotNull
   Pointers view(@NotNull final T instance);

   <P> boolean supports(@NotNull final Pointer<P> pointer);

   @Nullable
   <P> Function<? super T, P> resolver(@NotNull final Pointer<P> pointer);

   public interface Builder<T> extends AbstractBuilder<PointersSupplier<T>> {
      @Contract("_ -> this")
      @NotNull
      PointersSupplier.Builder<T> parent(@Nullable final PointersSupplier<? super T> parent);

      @Contract("_, _ -> this")
      @NotNull
      <P> PointersSupplier.Builder<T> resolving(@NotNull final Pointer<P> pointer, @NotNull final Function<T, P> resolver);
   }
}

package net.mehvahdjukaar.moonlight.api.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INamedSupplier<T> extends Supplier<T> {
   @Nullable
   ResourceLocation getId();

   @Nullable
   @Override
   T get();

   @NotNull
   default T getOrThrow() {
      return this.get();
   }

   static <T> INamedSupplier<T> memoize(final ResourceLocation id, Supplier<T> supp) {
      final com.google.common.base.Supplier<T> instance = Suppliers.memoize(supp::get);
      return new INamedSupplier<T>() {
         @Override
         public ResourceLocation getId() {
            return id;
         }

         @Override
         public T get() {
            return (T)instance.get();
         }
      };
   }
}

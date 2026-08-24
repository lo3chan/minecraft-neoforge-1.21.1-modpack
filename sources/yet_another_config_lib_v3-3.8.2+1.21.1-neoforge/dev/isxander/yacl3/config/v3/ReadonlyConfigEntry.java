package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface ReadonlyConfigEntry<T> {
   String fieldName();

   T get();

   ReadonlyConfigEntry<T> modifyGet(UnaryOperator<T> var1);

   default ReadonlyConfigEntry<T> onGet(Consumer<T> consumer) {
      return this.modifyGet(v -> {
         consumer.accept((T)v);
         return v;
      });
   }

   <R> RecordBuilder<R> encode(DynamicOps<R> var1, RecordBuilder<R> var2);

   <R> boolean decode(R var1, DynamicOps<R> var2);
}

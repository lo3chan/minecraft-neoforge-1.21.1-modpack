package org.dimdev.limlib.api.util.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
   R apply(T var1, U var2, V var3);

   default <W> TriFunction<T, U, V, W> andThen(Function<R, W> after) {
      Objects.requireNonNull(after);
      return (t, u, v) -> after.apply(this.apply(t, u, v));
   }
}

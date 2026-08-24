package org.dimdev.limlib.util;

import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

public interface DataValue<T> {
   @Nullable
   T get(Object var1);

   T getOrCreate(Object var1);

   void set(Object var1, T var2);

   void update(Object var1, T var2, UnaryOperator<T> var3);

   void update(Object var1, UnaryOperator<T> var2);

   void remove(Object var1);

   boolean has(Object var1);
}

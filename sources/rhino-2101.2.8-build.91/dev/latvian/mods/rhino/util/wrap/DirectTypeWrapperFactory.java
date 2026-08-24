package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

@FunctionalInterface
public interface DirectTypeWrapperFactory<T> extends TypeWrapperFactory<T> {
   T wrap(Object var1);

   @Override
   default T wrap(Context cx, Object from, TypeInfo target) {
      return this.wrap(from);
   }
}

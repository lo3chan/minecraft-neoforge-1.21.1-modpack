package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

@FunctionalInterface
public interface TypeWrapperFactory<T> {
   T wrap(Context var1, Object var2, TypeInfo var3);
}

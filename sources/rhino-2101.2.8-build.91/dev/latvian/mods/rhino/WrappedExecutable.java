package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

public interface WrappedExecutable {
   Object invoke(Context var1, Scriptable var2, Object var3, Object[] var4) throws Throwable;

   default Object construct(Context cx, Scriptable scope, Object[] args) throws Throwable {
      throw new UnsupportedOperationException();
   }

   default boolean isStatic() {
      return false;
   }

   default TypeInfo getReturnType() {
      return TypeInfo.PRIMITIVE_VOID;
   }

   @Nullable
   default CachedExecutableInfo unwrap() {
      return null;
   }
}

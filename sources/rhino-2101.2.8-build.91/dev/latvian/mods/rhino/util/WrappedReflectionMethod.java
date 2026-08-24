package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.CachedExecutableInfo;
import dev.latvian.mods.rhino.CachedMethodInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.WrappedExecutable;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

public record WrappedReflectionMethod(CachedMethodInfo method) implements WrappedExecutable {
   public static WrappedExecutable of(@Nullable CachedMethodInfo method) {
      return method == null ? null : new WrappedReflectionMethod(method);
   }

   @Override
   public Object invoke(Context cx, Scriptable scope, Object self, Object[] args) throws Throwable {
      return this.method.invoke(cx, scope, self, args);
   }

   @Override
   public boolean isStatic() {
      return this.method.isStatic;
   }

   @Override
   public TypeInfo getReturnType() {
      return this.method.getReturnType();
   }

   @Nullable
   @Override
   public CachedExecutableInfo unwrap() {
      return this.method;
   }
}

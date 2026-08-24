package dev.latvian.mods.rhino;

import java.lang.reflect.Executable;
import java.util.Arrays;

public record MethodSignature(String name, Class<?>[] args) {
   private static final Class<?>[] NO_ARGS = new Class[0];

   public MethodSignature(Executable method) {
      this(method.getName(), method.getParameterCount() == 0 ? NO_ARGS : method.getParameterTypes());
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof MethodSignature(String var5, Class[] var8) ? var5.equals(this.name) && Arrays.equals((Object[])this.args, (Object[])var8) : false;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode() ^ this.args.length;
   }
}

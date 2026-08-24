package dev.latvian.mods.rhino.type;

import dev.latvian.mods.rhino.ScriptRuntime;
import java.util.Collection;
import java.util.Set;

public record JSStringConstantTypeInfo(String constant) implements TypeInfo {
   public static final JSStringConstantTypeInfo EMPTY = new JSStringConstantTypeInfo("");

   @Override
   public Class<?> asClass() {
      return TypeInfo.class;
   }

   @Override
   public String toString() {
      return ScriptRuntime.escapeAndWrapString(this.constant);
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      sb.append(ScriptRuntime.escapeAndWrapString(this.constant));
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
   }

   @Override
   public Set<Class<?>> getContainedComponentClasses() {
      return Set.of();
   }
}

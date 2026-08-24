package dev.latvian.mods.rhino.type;

import java.util.Collection;
import java.util.Set;

public record JSNumberConstantTypeInfo(Number number) implements TypeInfo {
   @Override
   public Class<?> asClass() {
      return TypeInfo.class;
   }

   @Override
   public String toString() {
      return this.number.toString();
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      sb.append(this.number);
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
   }

   @Override
   public Set<Class<?>> getContainedComponentClasses() {
      return Set.of();
   }
}

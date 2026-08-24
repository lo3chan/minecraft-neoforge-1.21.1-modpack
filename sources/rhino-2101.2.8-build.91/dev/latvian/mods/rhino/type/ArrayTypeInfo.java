package dev.latvian.mods.rhino.type;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class ArrayTypeInfo extends TypeInfoBase.OptionallyConsolidatable {
   private final TypeInfo component;
   private Class<?> asClass;

   ArrayTypeInfo(TypeInfo component) {
      this.component = component;
   }

   @Override
   public Class<?> asClass() {
      if (this.asClass == null) {
         this.asClass = this.component.newArray(0).getClass();
      }

      return this.asClass;
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof ArrayTypeInfo t && this.component.equals(t.component);
   }

   @Override
   public int hashCode() {
      return this.component.hashCode();
   }

   @Override
   public String toString() {
      return this.component + "[]";
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      ctx.append(sb, this.component);
      sb.append('[');
      sb.append(']');
   }

   @Override
   public String signature() {
      return this.component.signature() + "[]";
   }

   @Override
   public TypeInfo componentType() {
      return this.component;
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
      this.component.collectContainedComponentClasses(classes);
   }

   @Override
   public Set<Class<?>> getContainedComponentClasses() {
      return this.component.getContainedComponentClasses();
   }

   @Override
   protected TypeInfo consolidateImpl(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
      TypeInfo consolidatedComponent = this.component.consolidate(mapping);
      return consolidatedComponent == this.component ? this : new ArrayTypeInfo(consolidatedComponent);
   }
}

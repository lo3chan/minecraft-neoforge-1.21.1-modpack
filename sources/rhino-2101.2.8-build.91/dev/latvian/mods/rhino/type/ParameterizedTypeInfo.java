package dev.latvian.mods.rhino.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ParameterizedTypeInfo extends TypeInfoBase.OptionallyConsolidatable {
   private final TypeInfo rawType;
   private final TypeInfo[] params;
   private int hashCode;

   ParameterizedTypeInfo(TypeInfo rawType, TypeInfo[] params) {
      this.rawType = rawType;
      this.params = params;
   }

   @Override
   public Class<?> asClass() {
      return this.rawType.asClass();
   }

   @Override
   public boolean is(TypeInfo info) {
      return this.rawType.is(info);
   }

   @Override
   public TypeInfo param(int index) {
      return index >= 0 && index < this.params.length && this.params[index] != TypeInfo.OBJECT ? this.params[index] : TypeInfo.NONE;
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = Objects.hash(this.rawType, Arrays.hashCode((Object[])this.params));
         if (this.hashCode == 0) {
            this.hashCode = 1;
         }
      }

      return this.hashCode;
   }

   @Override
   public boolean equals(Object object) {
      return this == object
         || object instanceof ParameterizedTypeInfo that
            && this.params.length == that.params.length
            && this.rawType.equals(that.rawType)
            && Arrays.deepEquals(this.params, that.params);
   }

   @Override
   public String toString() {
      return TypeStringContext.DEFAULT.toString(this);
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      ctx.append(sb, this.rawType);
      sb.append('<');

      for (int i = 0; i < this.params.length; i++) {
         if (i > 0) {
            sb.append(',');
            ctx.appendSpace(sb);
         }

         ctx.append(sb, this.params[i]);
      }

      sb.append('>');
   }

   @Override
   public String signature() {
      return this.rawType.signature();
   }

   public TypeInfo rawType() {
      return this.rawType;
   }

   public TypeInfo[] params() {
      return this.params;
   }

   @Override
   public Object newArray(int length) {
      return this.rawType.newArray(length);
   }

   @Override
   public TypeInfo withParams(TypeInfo... params) {
      return this.rawType.withParams(params);
   }

   @Override
   public boolean isFunctionalInterface() {
      return this.rawType.isFunctionalInterface();
   }

   @Override
   public Map<String, RecordTypeInfo.Component> recordComponents() {
      return this.rawType.recordComponents();
   }

   @Override
   public List<Object> enumConstants() {
      return this.rawType.enumConstants();
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
      this.rawType.collectContainedComponentClasses(classes);

      for (TypeInfo param : this.params) {
         param.collectContainedComponentClasses(classes);
      }
   }

   @Override
   protected TypeInfo consolidateImpl(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
      TypeInfo[] consolidatedParams = TypeConsolidator.consolidateAll(this.params, mapping);
      return consolidatedParams == this.params ? this : new ParameterizedTypeInfo(this.rawType, consolidatedParams);
   }
}

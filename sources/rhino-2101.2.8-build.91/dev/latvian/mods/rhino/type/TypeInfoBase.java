package dev.latvian.mods.rhino.type;

import java.lang.reflect.Array;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class TypeInfoBase implements TypeInfo {
   private TypeInfo asArray;
   private Object emptyArray;

   @Override
   public TypeInfo asArray() {
      if (this.asArray == null) {
         this.asArray = new ArrayTypeInfo(this);
      }

      return this.asArray;
   }

   @Override
   public Object newArray(int length) {
      if (length == 0) {
         if (this.emptyArray == null) {
            this.emptyArray = Array.newInstance(this.asClass(), 0);
         }

         return this.emptyArray;
      } else {
         return Array.newInstance(this.asClass(), length);
      }
   }

   public abstract static class OptionallyConsolidatable extends TypeInfoBase {
      private Boolean consolidatable = null;

      @NotNull
      @Override
      public TypeInfo consolidate(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
         if (this.consolidatable == null) {
            TypeInfo consolidated = this.consolidateImpl(mapping);
            this.consolidatable = consolidated == this;
            return consolidated;
         } else {
            return (TypeInfo)(this.consolidatable ? this.consolidateImpl(mapping) : this);
         }
      }

      protected abstract TypeInfo consolidateImpl(@NotNull Map<VariableTypeInfo, TypeInfo> var1);
   }
}

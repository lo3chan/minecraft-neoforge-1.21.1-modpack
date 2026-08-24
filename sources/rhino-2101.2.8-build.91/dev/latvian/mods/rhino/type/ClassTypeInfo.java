package dev.latvian.mods.rhino.type;

import java.util.Set;

public abstract class ClassTypeInfo extends TypeInfoBase {
   private final Class<?> type;
   private Set<Class<?>> typeSet;

   ClassTypeInfo(Class<?> type) {
      this.type = type;
   }

   @Override
   public Class<?> asClass() {
      return this.type;
   }

   @Override
   public boolean shouldConvert() {
      return this.type != Object.class;
   }

   @Override
   public int hashCode() {
      return this.type.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return o == this || o instanceof ClassTypeInfo t && this.type == t.type;
   }

   @Override
   public String toString() {
      return this.type.getName();
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      ctx.appendClassName(sb, this);
   }

   @Override
   public boolean isVoid() {
      return this.type == Void.class || this.type == void.class;
   }

   @Override
   public boolean isBoolean() {
      return this.type == Boolean.class || this.type == boolean.class;
   }

   @Override
   public boolean isByte() {
      return this.type == Byte.class || this.type == byte.class;
   }

   @Override
   public boolean isShort() {
      return this.type == Short.class || this.type == short.class;
   }

   @Override
   public boolean isInt() {
      return this.type == Integer.class || this.type == int.class;
   }

   @Override
   public boolean isLong() {
      return this.type == Long.class || this.type == long.class;
   }

   @Override
   public boolean isFloat() {
      return this.type == Float.class || this.type == float.class;
   }

   @Override
   public boolean isDouble() {
      return this.type == Double.class || this.type == double.class;
   }

   @Override
   public boolean isCharacter() {
      return this.type == Character.class || this.type == char.class;
   }

   @Override
   public Set<Class<?>> getContainedComponentClasses() {
      if (this.typeSet == null) {
         this.typeSet = Set.of(this.type);
      }

      return this.typeSet;
   }
}

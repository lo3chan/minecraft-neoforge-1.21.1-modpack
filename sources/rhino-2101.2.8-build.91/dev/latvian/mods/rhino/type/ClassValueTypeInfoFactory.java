package dev.latvian.mods.rhino.type;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

final class ClassValueTypeInfoFactory implements TypeInfoFactory {
   private final ClassValue<TypeInfo> classCache = new ClassValue<TypeInfo>() {
      protected TypeInfo computeValue(Class<?> c) {
         TypeInfo preset = TypeUtils.IMMUTABLE_CACHE.get(c);
         if (preset != null) {
            return preset;
         } else if (c.isArray()) {
            return ClassValueTypeInfoFactory.this.create(c.getComponentType()).asArray();
         } else if (c.isEnum()) {
            return new EnumTypeInfo(c);
         } else if (c.isRecord()) {
            return new RecordTypeInfo(c);
         } else {
            return (TypeInfo)(c.isInterface() ? new InterfaceTypeInfo(c) : new BasicClassTypeInfo(c));
         }
      }
   };
   private final Map<TypeVariable<?>, VariableTypeInfo> variableCache = new HashMap<>();

   @Override
   public TypeInfo create(Class<?> c) {
      if (c == null || c == Object.class) {
         return TypeInfo.OBJECT;
      } else {
         return c == void.class ? TypeInfo.PRIMITIVE_VOID : this.classCache.get(c);
      }
   }

   @Override
   public VariableTypeInfo create(TypeVariable<?> typeVariable) {
      synchronized (this.variableCache) {
         return this.variableCache.computeIfAbsent(typeVariable, VariableTypeInfo::new);
      }
   }
}

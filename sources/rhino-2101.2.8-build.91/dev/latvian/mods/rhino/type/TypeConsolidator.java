package dev.latvian.mods.rhino.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TypeConsolidator {
   private static final Map<Class<?>, Map<VariableTypeInfo, TypeInfo>> MAPPINGS = new ConcurrentHashMap<>();
   private static final boolean DEBUG = false;

   private TypeConsolidator() {
   }

   @NotNull
   public static Map<VariableTypeInfo, TypeInfo> getMapping(Class<?> type) {
      Map<VariableTypeInfo, TypeInfo> got = getImpl(type);
      return got == null ? Collections.emptyMap() : got;
   }

   @NotNull
   public static TypeInfo consolidateOrNone(VariableTypeInfo variable, Map<VariableTypeInfo, TypeInfo> mapping) {
      return mapping.getOrDefault(variable, TypeInfo.NONE);
   }

   @NotNull
   public static TypeInfo[] consolidateAll(@NotNull TypeInfo[] original, @NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
      int len = original.length;
      if (len == 0) {
         return original;
      } else if (len == 1) {
         TypeInfo consolidated = original[0].consolidate(mapping);
         return consolidated != original[0] ? new TypeInfo[]{consolidated} : original;
      } else {
         TypeInfo[] transformed = original;

         for (int i = 0; i < original.length; i++) {
            TypeInfo type = original[i];
            TypeInfo consolidated = type.consolidate(mapping);
            if (consolidated != type) {
               if (transformed == original) {
                  transformed = (TypeInfo[])original.clone();
               }

               transformed[i] = consolidated;
            }
         }

         return transformed;
      }
   }

   @NotNull
   public static List<TypeInfo> consolidateAll(@NotNull List<TypeInfo> original, @NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
      int len = original.size();
      if (len == 0) {
         return original;
      } else if (len == 1) {
         TypeInfo consolidated = ((TypeInfo)original.getFirst()).consolidate(mapping);
         return consolidated != original.getFirst() ? List.of(consolidated) : original;
      } else {
         List<TypeInfo> transformed = original;

         for (int i = 0; i < len; i++) {
            TypeInfo type = original.get(i);
            TypeInfo consolidated = type.consolidate(mapping);
            if (consolidated != type) {
               if (transformed == original) {
                  transformed = new ArrayList<>(original);
               }

               transformed.set(i, consolidated);
            }
         }

         return transformed;
      }
   }

   @Nullable
   private static Map<VariableTypeInfo, TypeInfo> getImpl(Class<?> type) {
      if (type != null && !type.isPrimitive() && type != Object.class) {
         Map<VariableTypeInfo, TypeInfo> got = MAPPINGS.get(type);
         if (got == null) {
            got = collect(type);
            MAPPINGS.put(type, got);
         }

         return got;
      } else {
         return null;
      }
   }

   @NotNull
   private static Map<VariableTypeInfo, TypeInfo> collect(Class<?> type) {
      HashMap<VariableTypeInfo, TypeInfo> mapping = new HashMap<>();
      Class<?> parent = type.getSuperclass();
      extractSuperMapping(type.getGenericSuperclass(), mapping);

      for (Type genericInterface : type.getGenericInterfaces()) {
         extractSuperMapping(genericInterface, mapping);
      }

      Map<VariableTypeInfo, TypeInfo> superMapping = getMapping(parent);
      Class<?>[] interfaces = type.getInterfaces();
      ArrayList<Map<VariableTypeInfo, TypeInfo>> interfaceMappings = new ArrayList<>(interfaces.length);

      for (Class<?> anInterface : interfaces) {
         interfaceMappings.add(getMapping(anInterface));
      }

      if (!superMapping.isEmpty() && !interfaceMappings.stream().allMatch(Map::isEmpty)) {
         HashMap<VariableTypeInfo, TypeInfo> merged = new HashMap<>(transformMapping(superMapping, mapping));

         for (Map<VariableTypeInfo, TypeInfo> interfaceMapping : interfaceMappings) {
            merged.putAll(transformMapping(interfaceMapping, mapping));
         }

         merged.putAll(mapping);
         return postMapping(merged);
      } else {
         return postMapping(mapping);
      }
   }

   private static Map<VariableTypeInfo, TypeInfo> transformMapping(Map<VariableTypeInfo, TypeInfo> mapping, Map<VariableTypeInfo, TypeInfo> transformer) {
      if (mapping.isEmpty()) {
         return Map.of();
      } else if (mapping.size() == 1) {
         Entry<VariableTypeInfo, TypeInfo> entry = mapping.entrySet().iterator().next();
         return Map.of(entry.getKey(), entry.getValue().consolidate(transformer));
      } else {
         HashMap<VariableTypeInfo, TypeInfo> transformed = new HashMap<>(mapping);

         for (Entry<VariableTypeInfo, TypeInfo> entry : transformed.entrySet()) {
            entry.setValue(entry.getValue().consolidate(transformer));
         }

         return transformed;
      }
   }

   private static void extractSuperMapping(Type superType, Map<VariableTypeInfo, TypeInfo> pushTo) {
      if (superType instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> parent) {
         TypeVariable<? extends Class<?>>[] params = parent.getTypeParameters();
         Type[] args = parameterized.getActualTypeArguments();

         for (int i = 0; i < args.length; i++) {
            pushTo.put(TypeInfo.of(params[i]), TypeInfo.of(args[i]));
         }
      }
   }

   private static Map<VariableTypeInfo, TypeInfo> postMapping(Map<VariableTypeInfo, TypeInfo> mapping) {
      switch (mapping.size()) {
         case 0:
            return Collections.emptyMap();
         case 1:
            Entry<VariableTypeInfo, TypeInfo> entry = mapping.entrySet().iterator().next();
            return Collections.singletonMap(entry.getKey(), entry.getValue());
         default:
            return Collections.unmodifiableMap(mapping);
      }
   }
}

package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.EnumGetMethod;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

final class StandardDeserializers {
   private StandardDeserializers() {
   }

   static final class CollectionDeserializer implements ValueDeserializer<Collection<?>, Collection<?>> {
      public Collection<?> deserialize(Collection<?> collectionValue, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         int size = collectionValue.size();
         Collection<Object> res;
         Optional<TypeConstraint> valueType;
         if (resultType.isPresent()) {
            TypeConstraint collectionType = resultType.get();
            res = this.createCollectionInstance(collectionType.getSatisfyingRawType().get(), size);
            valueType = extractCollectionValueType(collectionType);
         } else {
            res = new ArrayList<>(size);
            valueType = Optional.empty();
         }

         for (Object v : collectionValue) {
            Object deserialized = ctx.deserializeValue(v, valueType);
            res.add(deserialized);
         }

         return res;
      }

      private Collection<Object> createCollectionInstance(Class<?> cls, int sizeHint) {
         if (cls.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>(sizeHint);
         } else if (cls.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>();
         } else if (cls.isAssignableFrom(ArrayDeque.class)) {
            return new ArrayDeque<>(sizeHint);
         } else {
            try {
               return (Collection<Object>)cls.getDeclaredConstructor().newInstance();
            } catch (Exception var4) {
               throw new SerdeException("Failed to create an instance of " + cls, var4);
            }
         }
      }

      private static Optional<TypeConstraint> extractCollectionValueType(TypeConstraint collType) {
         return collType.resolveTypeArgumentsFor(Collection.class).map(c -> (TypeConstraint)c[0]);
      }
   }

   static final class CollectionToArrayDeserializer implements ValueDeserializer<Collection<?>, Object> {
      public Object deserialize(Collection<?> collectionValue, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         int size = collectionValue.size();
         Object res;
         Optional<TypeConstraint> valueType;
         if (resultType.isPresent()) {
            TypeConstraint arrayType = resultType.get();
            Class<?> componentType = ((Class)arrayType.getFullType()).getComponentType();

            assert componentType != null;

            res = Array.newInstance(componentType, size);
            valueType = Optional.of(new TypeConstraint(componentType));
         } else {
            res = new Object[size];
            valueType = Optional.empty();
         }

         int i = 0;

         for (Object v : collectionValue) {
            Object deserialized = ctx.deserializeValue(v, valueType);
            Array.set(res, i, deserialized);
            i++;
         }

         return res;
      }
   }

   static final class EnumDeserializer implements ValueDeserializer<String, Enum<?>> {
      public Enum<?> deserialize(String value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         TypeConstraint enumType = resultType.orElseThrow(() -> new SerdeException("Cannot deserialize a value to an enum without knowing the enum type"));
         Class<?> cls = enumType.getSatisfyingRawType()
            .orElseThrow(() -> new SerdeException("Could not find a concrete enum type that can satisfy the constraint " + enumType));
         return EnumGetMethod.NAME.get(value, (Class<Enum<?>>)cls);
      }
   }

   static final class MapDeserializer implements ValueDeserializer<Object, Map<String, ?>> {
      public Map<String, ?> deserialize(Object mapValue, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         int size;
         if (mapValue instanceof UnmodifiableConfig) {
            size = ((UnmodifiableConfig)mapValue).size();
         } else {
            size = ((Map)mapValue).size();
         }

         Optional<TypeConstraint[]> mapKVType;
         Map<String, Object> res;
         if (resultType.isPresent()) {
            TypeConstraint mapType = resultType.get();
            res = createMapInstance(mapType.getSatisfyingRawType().get(), size);
            mapKVType = extractMapKVType(mapType);
         } else {
            mapKVType = Optional.empty();
            res = (Map<String, Object>)(Config.isInsertionOrderPreserved() ? new LinkedHashMap<>(size) : new HashMap<>(size));
         }

         Optional<TypeConstraint> mapKeyType = mapKVType.map(arr -> (TypeConstraint)arr[0]);
         Optional<TypeConstraint> mapValueType = mapKVType.map(arr -> (TypeConstraint)arr[1]);
         if (mapKeyType.isPresent() && !mapKeyType.get().getSatisfyingRawType().equals(Optional.of(String.class))) {
            throw new SerdeException(
               "Invalid map type for deserialization, the keys should be of type String instead of "
                  + mapKeyType.get()
                  + ". Full map type: "
                  + resultType.get()
            );
         } else {
            if (mapValue instanceof UnmodifiableConfig) {
               for (UnmodifiableConfig.Entry entry : ((UnmodifiableConfig)mapValue).entrySet()) {
                  String key = entry.getKey();
                  Object value = entry.getValue();
                  Object deserialized = ctx.deserializeValue(value, mapValueType);
                  res.put(key, deserialized);
               }
            } else {
               for (Entry<?, ?> entry : ((Map)mapValue).entrySet()) {
                  Object key = entry.getKey();
                  if (!(key instanceof String)) {
                     String keyClassStr = key == null ? "null" : key.getClass().toString();
                     throw new SerdeException(
                        "Invalid map type for deserialization, the keys should be of type String instead of "
                           + keyClassStr
                           + ". Full map type: "
                           + resultType.get()
                     );
                  }

                  Object value = entry.getValue();
                  Object deserialized = ctx.deserializeValue(value, mapValueType);
                  res.put((String)key, deserialized);
               }
            }

            return res;
         }
      }

      private static Optional<TypeConstraint[]> extractMapKVType(TypeConstraint mapTypeC) {
         return mapTypeC.resolveTypeArgumentsFor(Map.class);
      }

      private static Map<String, Object> createMapInstance(Class<?> cls, int sizeHint) {
         if (cls == Map.class) {
            return (Map<String, Object>)(Config.isInsertionOrderPreserved() ? new LinkedHashMap<>(sizeHint) : new HashMap<>(sizeHint));
         } else if (cls == LinkedHashMap.class) {
            return new LinkedHashMap<>(sizeHint);
         } else if (cls == HashMap.class) {
            return new HashMap<>(sizeHint);
         } else if (cls == IdentityHashMap.class) {
            return new IdentityHashMap<>(sizeHint);
         } else if (cls.isAssignableFrom(HashMap.class)) {
            return (Map<String, Object>)(Config.isInsertionOrderPreserved() && cls.isAssignableFrom(LinkedHashMap.class)
               ? new LinkedHashMap<>(sizeHint)
               : new HashMap<>(sizeHint));
         } else if (cls.isAssignableFrom(ConcurrentHashMap.class)) {
            return new ConcurrentHashMap<>(sizeHint);
         } else {
            try {
               return (Map<String, Object>)cls.getDeclaredConstructor().newInstance();
            } catch (Exception var3) {
               throw new SerdeException("Failed to create an instance of " + cls, var3);
            }
         }
      }
   }

   static final class RiskyNumberDeserializer implements ValueDeserializer<Number, Number> {
      public static boolean isNumberTypeSupported(Class<?> t) {
         return t == Integer.class || t == int.class || t == Long.class || t == long.class;
      }

      public Number deserialize(Number value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         TypeConstraint numberType = resultType.orElseThrow(
            () -> new SerdeException("Cannot deserialize a value with a risky number conversion without knowing the number type")
         );
         Class<?> resultCls = numberType.getSatisfyingRawType()
            .orElseThrow(() -> new SerdeException("Could not find a concrete number type that can satisfy the constraint " + numberType));
         Class<?> valueCls = value.getClass();
         if (valueCls == Long.class) {
            long l = value.longValue();
            if (resultCls == Integer.class || resultCls == int.class) {
               int i = (int)l;
               if (i == l) {
                  return i;
               }
            } else if (resultCls != Short.class && resultCls != short.class) {
               if (resultCls != Byte.class && resultCls != byte.class) {
                  throw new SerdeException(
                     String.format("Cannot deserialize from %s to %s: risky conversion not implemented, you should change your types.", valueCls, resultCls)
                  );
               }

               byte b = (byte)l;
               if (b == l) {
                  return b;
               }
            } else {
               short s = (short)l;
               if (s == l) {
                  return s;
               }
            }
         } else if (valueCls == Integer.class) {
            int i = value.intValue();
            if (resultCls != Short.class && resultCls != short.class) {
               if (resultCls != Byte.class && resultCls != byte.class) {
                  throw new SerdeException(
                     String.format("Cannot deserialize from %s to %s: risky conversion not implemented, you should change your types.", valueCls, resultCls)
                  );
               }

               byte b = (byte)i;
               if (b == i) {
                  return b;
               }
            } else {
               short s = (short)i;
               if (s == i) {
                  return s;
               }
            }
         }

         throw new SerdeException(String.format("Cannot deserialize %s to %s: the conversion would be lossy", value, resultCls));
      }
   }

   static final class TrivialDeserializer implements ValueDeserializer<Object, Object> {
      @Override
      public Object deserialize(Object value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         return value;
      }
   }

   static final class UuidDeserializer implements ValueDeserializer<String, UUID> {
      public UUID deserialize(String value, Optional<TypeConstraint> resultType, DeserializerContext ctx) {
         return UUID.fromString(value);
      }
   }
}

package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

final class StandardSerializers {
   private StandardSerializers() {
   }

   static final class ArraySerializer implements ValueSerializer<Object, List<?>> {
      public List<?> serialize(Object arrayValue, SerializerContext ctx) {
         int size = Array.getLength(arrayValue);
         List<Object> res = new ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            Object element = Array.get(arrayValue, i);
            Object serialized = ctx.serializeValue(element);
            res.add(serialized);
         }

         return res;
      }
   }

   static final class CollectionSerializer implements ValueSerializer<Collection<?>, List<?>> {
      public List<?> serialize(Collection<?> value, SerializerContext ctx) {
         List<Object> res = new ArrayList<>(value.size());

         for (Object v : value) {
            Object serialized = ctx.serializeValue(v);
            res.add(serialized);
         }

         return res;
      }
   }

   static final class EnumSerializer implements ValueSerializer<Enum<?>, String> {
      public String serialize(Enum<?> value, SerializerContext ctx) {
         return value.name();
      }
   }

   static final class FieldsToConfigSerializer implements ValueSerializer<Object, Config> {
      public Config serialize(Object value, SerializerContext ctx) {
         Config sub = ctx.createConfig();
         ctx.serializeFields(value, sub);
         return sub;
      }
   }

   static final class IterableSerializer implements ValueSerializer<Iterable<?>, List<?>> {
      public List<?> serialize(Iterable<?> value, SerializerContext ctx) {
         List<Object> res = new ArrayList<>();

         for (Object v : value) {
            Object serialized = ctx.serializeValue(v);
            res.add(serialized);
         }

         return res;
      }
   }

   static final class MapSerializer implements ValueSerializer<Map<?, ?>, Config> {
      public Config serialize(Map<?, ?> value, SerializerContext ctx) {
         Config res = ctx.createConfig();

         for (Entry<?, ?> entry : value.entrySet()) {
            Object key = entry.getKey();
            if (!(key instanceof String)) {
               String keyTypeString = key == null ? "null" : key.getClass().toString();
               throw new SerdeException("Map keys must be strings, invalid key type " + keyTypeString + " in value.");
            }

            List<String> path = Collections.singletonList((String)key);
            Object serialized = ctx.serializeValue(entry.getValue());
            res.set(path, serialized);
         }

         return res;
      }
   }

   static final class TrivialSerializer implements ValueSerializer<Object, Object> {
      @Override
      public Object serialize(Object value, SerializerContext ctx) {
         return value;
      }
   }

   static final class UuidSerializer implements ValueSerializer<UUID, String> {
      public String serialize(UUID value, SerializerContext ctx) {
         return value.toString();
      }
   }
}

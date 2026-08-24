package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ObjectSerializerBuilder {
   final IdentityHashMap<Class<?>, ValueSerializer<?, ?>> classBasedSerializers = new IdentityHashMap<>(7);
   final List<ValueSerializerProvider<?, ?>> generalProviders = new ArrayList<>();
   ValueSerializerProvider<?, ?> defaultProvider = ObjectSerializerBuilder.NoProvider.INSTANCE;
   boolean applyTransientModifier = true;

   ObjectSerializerBuilder(boolean standards) {
      if (standards) {
         this.registerStandardSerializers();
      }
   }

   public ObjectSerializer build() {
      return new ObjectSerializer(this);
   }

   public <V, R> void withSerializerForExactClass(Class<V> cls, ValueSerializer<? super V, ? extends R> serializer) {
      this.classBasedSerializers.put(cls, serializer);
   }

   public <V, R> void withSerializerForClass(Class<V> cls, ValueSerializer<? super V, ? extends R> serializer) {
      this.generalProviders.add((valueClass, ctx) -> valueClass != null && Util.canAssign(cls, valueClass) ? serializer : null);
   }

   public <V, R> void withSerializerProvider(ValueSerializerProvider<V, R> provider) {
      this.generalProviders.add(provider);
   }

   public <V, R> void withDefaultSerializerProvider(ValueSerializerProvider<V, R> provider) {
      this.defaultProvider = provider;
   }

   public void withDefaultSerializerProvider() {
      ValueSerializer trivialSer = new StandardSerializers.TrivialSerializer();
      ValueSerializer fieldsSer = new StandardSerializers.FieldsToConfigSerializer();
      ValueSerializer numberToIntSer = (value, ctx) -> ((Number)value).intValue();
      ValueSerializer charToIntSer = (value, ctx) -> Integer.valueOf((Character)value);
      this.defaultProvider = (valueClass, ctx) -> {
         ConfigFormat<?> format = ctx.configFormat();
         if (format != null && !format.supportsType(valueClass)) {
            if (valueClass != null && (Util.isPrimitiveOrWrapper(valueClass) || valueClass == String.class || valueClass.isArray())) {
               if (!format.supportsType(int.class) || !Util.canAssign(int.class, valueClass)) {
                  return null;
               } else {
                  return valueClass != Character.class && valueClass != char.class ? numberToIntSer : charToIntSer;
               }
            } else {
               return fieldsSer;
            }
         } else {
            return trivialSer;
         }
      };
   }

   public void serializeTransientFields() {
      this.applyTransientModifier = false;
   }

   private void registerStandardSerializers() {
      this.withDefaultSerializerProvider();
      ValueSerializer mapSer = new StandardSerializers.MapSerializer();
      ValueSerializer collSer = new StandardSerializers.CollectionSerializer();
      ValueSerializer iterSer = new StandardSerializers.IterableSerializer();
      ValueSerializer arraySer = new StandardSerializers.ArraySerializer();
      ValueSerializer enumSer = new StandardSerializers.EnumSerializer();
      ValueSerializer trivialSer = new StandardSerializers.TrivialSerializer();
      ValueSerializer uuidSer = new StandardSerializers.UuidSerializer();
      this.withSerializerProvider((valueClass, ctx) -> {
         if (valueClass == null) {
            ConfigFormat<?> format = ctx.configFormat();
            return format != null && !format.supportsType(null) ? null : trivialSer;
         } else if (Map.class.isAssignableFrom(valueClass)) {
            return mapSer;
         } else if (Collection.class.isAssignableFrom(valueClass)) {
            return collSer;
         } else if (Iterable.class.isAssignableFrom(valueClass)) {
            return iterSer;
         } else if (UnmodifiableConfig.class.isAssignableFrom(valueClass)) {
            return trivialSer;
         } else if (Enum.class.isAssignableFrom(valueClass)) {
            return enumSer;
         } else if (valueClass.isArray()) {
            return arraySer;
         } else {
            return valueClass == UUID.class ? uuidSer : null;
         }
      });
   }

   static final class NoProvider implements ValueSerializerProvider<Object, Object> {
      static final ObjectSerializerBuilder.NoProvider INSTANCE = new ObjectSerializerBuilder.NoProvider();

      @Override
      public ValueSerializer<Object, Object> provide(Class<?> valueClass, SerializerContext ctx) {
         return null;
      }
   }
}

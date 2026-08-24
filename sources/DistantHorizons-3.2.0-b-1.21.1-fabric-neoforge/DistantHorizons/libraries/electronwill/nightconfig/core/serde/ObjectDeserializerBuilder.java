package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ObjectDeserializerBuilder {
   final List<ValueDeserializerProvider<?, ?>> deserializerProviders = new ArrayList<>();
   ValueDeserializerProvider<?, ?> defaultProvider = ObjectDeserializerBuilder.NoProvider.INSTANCE;
   boolean applyTransientModifier = true;

   ObjectDeserializerBuilder(boolean standards) {
      if (standards) {
         this.registerStandardDeserializers();
      }
   }

   public ObjectDeserializer build() {
      return new ObjectDeserializer(this);
   }

   public void deserializeTransientFields() {
      this.applyTransientModifier = false;
   }

   public <V, R> void withDeserializerForClass(Class<V> valueClass, Class<R> resultClass, ValueDeserializer<? super V, ? extends R> deserializer) {
      this.withDeserializerProvider(
         (valueCls, resultType) -> resultType.getSatisfyingRawType()
            .map(resultCls -> (ValueDeserializer<V, R>)(valueCls.isAssignableFrom(valueClass) && resultCls.isAssignableFrom(resultClass) ? deserializer : null))
            .orElse(null)
      );
   }

   public <V, R> void withDeserializerProvider(ValueDeserializerProvider<V, R> provider) {
      this.deserializerProviders.add(provider);
   }

   public <V, R> void withDefaultDeserializerProvider(ValueDeserializerProvider<V, R> provider) {
      this.defaultProvider = provider;
   }

   public void withDefaultDeserializerProvider() {
      ValueDeserializer pojoDe = new ConfigToPojoDeserializer();
      this.defaultProvider = (valueClass, resultType) -> UnmodifiableConfig.class.isAssignableFrom(valueClass) ? pojoDe : null;
   }

   private void registerStandardDeserializers() {
      this.withDefaultDeserializerProvider();
      ValueDeserializer trivialDe = new StandardDeserializers.TrivialDeserializer();
      ValueDeserializer mapDe = new StandardDeserializers.MapDeserializer();
      ValueDeserializer collDe = new StandardDeserializers.CollectionDeserializer();
      ValueDeserializer arrDe = new StandardDeserializers.CollectionToArrayDeserializer();
      ValueDeserializer enumDe = new StandardDeserializers.EnumDeserializer();
      ValueDeserializer uuidDe = new StandardDeserializers.UuidDeserializer();
      ValueDeserializer numberDe = new StandardDeserializers.RiskyNumberDeserializer();
      this.withDeserializerProvider(
         (valueClass, resultType) -> {
            Type fullType = resultType.getFullType();
            return resultType.getSatisfyingRawType()
               .map(
                  resultClass -> {
                     if (!Util.canAssign((Class<?>)resultClass, valueClass) || valueClass != null && !(fullType instanceof Class)) {
                        if (Collection.class.isAssignableFrom(valueClass)) {
                           if (Collection.class.isAssignableFrom((Class<?>)resultClass)) {
                              return collDe;
                           }

                           if (resultClass.isArray()) {
                              return arrDe;
                           }
                        }

                        if ((UnmodifiableConfig.class.isAssignableFrom(valueClass) || Map.class.isAssignableFrom(valueClass))
                           && Map.class.isAssignableFrom((Class<?>)resultClass)) {
                           return mapDe;
                        } else if (resultClass == UUID.class && valueClass == String.class) {
                           return uuidDe;
                        } else if (valueClass == String.class && Enum.class.isAssignableFrom((Class<?>)resultClass)) {
                           return enumDe;
                        } else {
                           return StandardDeserializers.RiskyNumberDeserializer.isNumberTypeSupported(valueClass)
                                 && Util.isPrimitiveOrWrapperNumber((Class<?>)resultClass)
                              ? numberDe
                              : null;
                        }
                     } else {
                        return trivialDe;
                     }
                  }
               )
               .orElse(null);
         }
      );
   }

   static final class NoProvider implements ValueDeserializerProvider<Object, Object> {
      static final ObjectDeserializerBuilder.NoProvider INSTANCE = new ObjectDeserializerBuilder.NoProvider();

      @Override
      public ValueDeserializer<Object, Object> provide(Class<?> valueClass, TypeConstraint resultType) {
         return null;
      }
   }
}

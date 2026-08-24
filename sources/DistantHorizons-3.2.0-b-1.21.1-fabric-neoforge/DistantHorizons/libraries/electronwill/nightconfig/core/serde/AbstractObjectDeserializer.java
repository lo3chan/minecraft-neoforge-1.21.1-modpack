package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdePhase;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

class AbstractObjectDeserializer {
   protected final List<ValueDeserializerProvider<?, ?>> generalProviders;
   protected ValueDeserializerProvider<?, ?> defaultProvider;
   protected final boolean applyTransientModifier;

   protected AbstractObjectDeserializer(ObjectDeserializerBuilder builder) {
      this.generalProviders = builder.deserializerProviders;
      this.defaultProvider = builder.defaultProvider;
      this.applyTransientModifier = builder.applyTransientModifier;

      assert this.generalProviders != null && this.defaultProvider != null;
   }

   protected <C extends Collection<V>, V> C deserializeToCollection(Object configValue, Class<C> collectionClass, Class<V> valueClass) {
      DeserializerContext ctx = new DeserializerContext(this);
      TypeConstraint t = new TypeConstraint(new TypeConstraint.ManuallyParameterized(collectionClass, valueClass));
      return (C)ctx.deserializeValue(configValue, Optional.of(t));
   }

   protected <M extends Map<String, V>, V> M deserializeToMap(Object configValue, Class<M> mapClass, Class<V> valueClass) {
      DeserializerContext ctx = new DeserializerContext(this);
      TypeConstraint t = new TypeConstraint(new TypeConstraint.ManuallyParameterized(mapClass, String.class, valueClass));
      return (M)ctx.deserializeValue(configValue, Optional.of(t));
   }

   protected void deserializeFields(UnmodifiableConfig source, Object destination) {
      DeserializerContext ctx = new DeserializerContext(this);
      ctx.deserializeFields(source, destination);
   }

   protected <R> R deserializeFields(UnmodifiableConfig source, Supplier<? extends R> destinationSupplier) {
      R dest = (R)destinationSupplier.get();
      this.deserializeFields(source, dest);
      return dest;
   }

   protected <T, R> ValueDeserializer<T, R> findValueDeserializer(T value, TypeConstraint resultType) {
      Class<?> valueClass = value == null ? null : value.getClass();

      for (ValueDeserializerProvider<?, ?> provider : this.generalProviders) {
         ValueDeserializer<?, ?> maybeDe = provider.provide(valueClass, resultType);
         if (maybeDe != null) {
            return (ValueDeserializer<T, R>)maybeDe;
         }
      }

      ValueDeserializer<?, ?> maybeDe = this.defaultProvider.provide(valueClass, resultType);
      if (maybeDe != null) {
         return (ValueDeserializer<T, R>)maybeDe;
      } else {
         String ofTypeStr = valueClass == null ? "" : " of type " + valueClass;
         throw new SerdeException("No suitable deserializer found for value" + ofTypeStr + ": " + value + " and result constraint " + resultType);
      }
   }

   protected Supplier<?> findDefaultValueSupplier(Object rawConfigValue, Field field, Object instance) {
      EnumMap<SerdeDefault.WhenValue, SerdeDefault> defaultForDeserializing = AnnotationProcessor.getConfigDefaultAnnotations(field)
         .get(SerdePhase.DESERIALIZING);
      if (defaultForDeserializing == null) {
         return null;
      } else {
         SerdeDefault applicableDefault = null;
         if (rawConfigValue == null) {
            applicableDefault = defaultForDeserializing.get(SerdeDefault.WhenValue.IS_MISSING);
         } else if (rawConfigValue == NullObject.NULL_OBJECT) {
            applicableDefault = defaultForDeserializing.get(SerdeDefault.WhenValue.IS_NULL);
         } else {
            SerdeDefault forEmpty = defaultForDeserializing.get(SerdeDefault.WhenValue.IS_EMPTY);
            if (forEmpty != null && Util.isEmpty(rawConfigValue)) {
               applicableDefault = forEmpty;
            }
         }

         return applicableDefault == null ? null : AnnotationProcessor.resolveConfigDefaultProvider(applicableDefault, instance);
      }
   }
}

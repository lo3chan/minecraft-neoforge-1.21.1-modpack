package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdePhase;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Supplier;

public final class ObjectSerializer {
   private final IdentityHashMap<Class<?>, ValueSerializer<?, ?>> classBasedSerializers;
   private final List<ValueSerializerProvider<?, ?>> generalProviders;
   private final ValueSerializerProvider<?, ?> defaultProvider;
   final boolean applyTransientModifier;

   public static ObjectSerializerBuilder builder() {
      return new ObjectSerializerBuilder(true);
   }

   public static ObjectSerializerBuilder blankBuilder() {
      return new ObjectSerializerBuilder(false);
   }

   public static ObjectSerializer standard() {
      return builder().build();
   }

   ObjectSerializer(ObjectSerializerBuilder builder) {
      this.classBasedSerializers = builder.classBasedSerializers;
      this.generalProviders = builder.generalProviders;
      this.defaultProvider = builder.defaultProvider;
      this.applyTransientModifier = builder.applyTransientModifier;

      assert this.classBasedSerializers != null && this.generalProviders != null && this.defaultProvider != null;
   }

   public Object serialize(Object value, Supplier<? extends Config> configSupplier) {
      SerializerContext ctx = new SerializerContext(this, () -> configSupplier.get().configFormat(), configSupplier);
      return ctx.serializeValue(value);
   }

   public <C extends Config> C serializeFields(Object source, Supplier<C> configSupplier) {
      C dest = (C)configSupplier.get();
      this.serializeFields(source, dest);
      return dest;
   }

   public void serializeFields(Object source, Config destination) {
      SerializerContext ctx = new SerializerContext(this, () -> destination.configFormat(), () -> destination.createSubConfig());
      ctx.serializeFields(source, destination);
   }

   <T, R> ValueSerializer<T, R> findValueSerializer(Object value, SerializerContext ctx) {
      Class<?> valueClass = value == null ? null : value.getClass();
      ValueSerializer<?, ?> maybeSe = null;

      for (ValueSerializerProvider<?, ?> provider : this.generalProviders) {
         maybeSe = provider.provide(valueClass, ctx);
         if (maybeSe != null) {
            return (ValueSerializer<T, R>)maybeSe;
         }
      }

      maybeSe = this.classBasedSerializers.get(valueClass);
      if (maybeSe != null) {
         return (ValueSerializer<T, R>)maybeSe;
      } else {
         maybeSe = this.defaultProvider.provide(valueClass, ctx);
         if (maybeSe != null) {
            return (ValueSerializer<T, R>)maybeSe;
         } else {
            throw noSerializerFound(value, valueClass, ctx);
         }
      }
   }

   Supplier<?> findDefaultValueSupplier(Object fieldValue, Field field, Object instance) {
      EnumMap<SerdeDefault.WhenValue, SerdeDefault> defaultForSerializing = AnnotationProcessor.getConfigDefaultAnnotations(field).get(SerdePhase.SERIALIZING);
      if (defaultForSerializing == null) {
         return null;
      } else {
         SerdeDefault applicableDefault = null;
         if (fieldValue == null) {
            applicableDefault = defaultForSerializing.get(SerdeDefault.WhenValue.IS_NULL);
         } else {
            SerdeDefault forEmpty = defaultForSerializing.get(SerdeDefault.WhenValue.IS_EMPTY);
            if (forEmpty != null && Util.isEmpty(fieldValue)) {
               applicableDefault = forEmpty;
            }
         }

         return applicableDefault == null ? null : AnnotationProcessor.resolveConfigDefaultProvider(applicableDefault, instance);
      }
   }

   static SerdeException noSerializerFound(Object value, Class<?> valueClass, SerializerContext ctx) {
      ConfigFormat<?> format = ctx.configFormat();
      String supportedStr;
      if (format == null) {
         supportedStr = "The current SerializerContext has no ConfigFormat. Is there a bug in the implementation of the chosen Config type?";
      } else if (format.supportsType(valueClass)) {
         supportedStr = "The value's type is supported by the ConfigFormat of the current SerializerContext.";
      } else {
         supportedStr = "The value's type is NOT supported by the ConfigFormat of the current SerializerContext.";
      }

      String ofTypeStr = valueClass == null ? "" : " of type " + valueClass;
      return new SerdeException("No suitable serializer found for value" + ofTypeStr + ": " + value + ". " + supportedStr);
   }
}

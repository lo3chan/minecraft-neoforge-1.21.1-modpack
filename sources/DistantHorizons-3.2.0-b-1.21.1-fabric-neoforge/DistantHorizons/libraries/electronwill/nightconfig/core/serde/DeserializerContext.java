package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeAssert;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeKey;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdePhase;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeSkipDeserializingIf;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class DeserializerContext {
   final AbstractObjectDeserializer settings;

   DeserializerContext(AbstractObjectDeserializer settings) {
      this.settings = settings;
   }

   public Object deserializeValue(Object value, Optional<TypeConstraint> typeConstraint) {
      TypeConstraint t = typeConstraint.orElse(new TypeConstraint(Object.class));
      ValueDeserializer<Object, ?> deserializer = this.settings.findValueDeserializer(value, t);
      return deserializer.deserialize(value, typeConstraint, this);
   }

   public void deserializeFields(UnmodifiableConfig source, Object destination) {
      for (Class<?> cls = destination.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
         for (Field field : cls.getDeclaredFields()) {
            if (this.preCheck(field)) {
               List<String> path = Collections.singletonList(this.configKey(field));
               Object value = source.getRaw(path);
               if (!this.skipField(field, destination, value)) {
                  Supplier<?> defaultValueSupplier = this.settings.findDefaultValueSupplier(value, field, destination);
                  Object deserialized;
                  if (defaultValueSupplier != null) {
                     try {
                        deserialized = defaultValueSupplier.get();
                     } catch (Exception var16) {
                        throw new SerdeException("Error in default value provider for field " + field, var16);
                     }
                  } else {
                     value = this.normalizeForDeserialization(value, path, field);
                     TypeConstraint resultType = new TypeConstraint(field.getGenericType());
                     ValueDeserializer<Object, ?> deserializer = this.settings.findValueDeserializer(value, resultType);

                     try {
                        Optional<TypeConstraint> type = Optional.of(resultType);
                        deserialized = deserializer.deserialize(value, type, this);
                     } catch (Exception var15) {
                        throw new SerdeException(
                           "Error during deserialization of value `" + value + "` to field `" + field + "` with deserializer " + deserializer, var15
                        );
                     }
                  }

                  if (!this.assertField(field, destination, value)) {
                     throw new SerdeAssertException("Field `" + field + "` has an invalid value: " + value);
                  }

                  try {
                     field.set(destination, deserialized);
                  } catch (Exception var17) {
                     throw new SerdeException(
                        "Could not assign the deserialized value `" + deserialized + "` to the field " + field + ". The original config value was " + value
                     );
                  }
               }
            }
         }
      }
   }

   private Object normalizeForDeserialization(Object configValue, List<String> path, Field field) {
      if (configValue == null) {
         throw new SerdeException("Missing configuration entry " + path + " for field `" + field + "` declared in " + field.getDeclaringClass());
      } else {
         return configValue == NullObject.NULL_OBJECT ? null : configValue;
      }
   }

   private String configKey(Field field) {
      SerdeKey keyAnnot = field.getAnnotation(SerdeKey.class);
      return keyAnnot == null ? field.getName() : keyAnnot.value();
   }

   private boolean skipField(Field field, Object fieldContainer, Object rawConfigValue) {
      SerdeSkipDeserializingIf annot = field.getAnnotation(SerdeSkipDeserializingIf.class);
      if (annot == null) {
         return false;
      } else {
         try {
            Predicate<?> skipPredicate = AnnotationProcessor.resolveSkipDeserializingIfPredicate(annot, fieldContainer);
            return ((Predicate<Object>)skipPredicate).test(rawConfigValue);
         } catch (Exception var7) {
            String msg = "Failed to resolve or apply skip predicate for deserialization of field " + field;
            throw new SerdeException(msg, var7);
         }
      }
   }

   private boolean assertField(Field field, Object fieldContainer, Object fieldValue) {
      SerdeAssert[] annot = field.getAnnotationsByType(SerdeAssert.class);
      if (annot != null && annot.length != 0) {
         try {
            Predicate<?> assertPredicate = AnnotationProcessor.resolveAssertPredicate(annot, fieldContainer, SerdePhase.DESERIALIZING, field);
            return assertPredicate == null ? true : ((Predicate<Object>)assertPredicate).test(fieldValue);
         } catch (Exception var7) {
            String msg = "Failed to resolve or apply assertion for deserialization of field " + field;
            throw new SerdeException(msg, var7);
         }
      } else {
         return true;
      }
   }

   private boolean preCheck(Field field) {
      int mods = field.getModifiers();
      if (Modifier.isStatic(mods) || field.isSynthetic()) {
         return false;
      } else if (Modifier.isTransient(mods) && this.settings.applyTransientModifier) {
         return false;
      } else {
         if (Modifier.isFinal(mods) || !Modifier.isPublic(mods)) {
            field.setAccessible(true);
         }

         return true;
      }
   }
}

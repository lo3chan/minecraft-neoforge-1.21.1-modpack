package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeAssert;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeComment;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeKey;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdePhase;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeSkipSerializingIf;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SerializerContext {
   final ObjectSerializer settings;
   final Supplier<? extends ConfigFormat<?>> formatSupplier;
   final Supplier<? extends Config> configSupplier;

   SerializerContext(ObjectSerializer settings, Supplier<? extends ConfigFormat<?>> formatSupplier, Supplier<? extends Config> configSupplier) {
      this.settings = settings;
      this.formatSupplier = formatSupplier;
      this.configSupplier = configSupplier;
   }

   public ConfigFormat<?> configFormat() {
      return (ConfigFormat<?>)this.formatSupplier.get();
   }

   public Config createConfig() {
      return this.configSupplier.get();
   }

   public CommentedConfig createCommentedConfig() {
      return CommentedConfig.fake(this.createConfig());
   }

   public Object serializeValue(Object value) {
      ValueSerializer<Object, ?> serializer = this.settings.findValueSerializer(value, this);
      return serializer.serialize(value, this);
   }

   public void serializeFields(Object source, Config destination) {
      for (Class<?> cls = source.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
         for (Field field : cls.getDeclaredFields()) {
            if (this.preCheck(field)) {
               Object value;
               try {
                  value = field.get(source);
               } catch (Exception var15) {
                  throw new SerdeException("Failed to read field `" + field + "`", var15);
               }

               if (!this.skipField(field, source, value)) {
                  List<String> path = Collections.singletonList(this.configKey(field));
                  String comment = this.configComment(field);
                  Supplier<?> defaultValueSupplier = this.settings.findDefaultValueSupplier(value, field, source);
                  if (defaultValueSupplier != null) {
                     try {
                        value = defaultValueSupplier.get();
                     } catch (Exception var14) {
                        throw new SerdeException("Error in default value provider for field " + field);
                     }
                  }

                  if (!this.assertField(field, source, value)) {
                     throw new SerdeAssertException("Field `" + field + "` has an invalid value: " + value);
                  }

                  ValueSerializer<Object, ?> serializer = this.settings.findValueSerializer(value, this);

                  try {
                     Object serialized = serializer.serialize(value, this);
                     destination.set(path, serialized);
                     if (comment != null && destination instanceof CommentedConfig) {
                        ((CommentedConfig)destination).setComment(path, comment);
                     }
                  } catch (Exception var16) {
                     throw new SerdeException("Error during serialization of field `" + field + "` with serializer " + serializer, var16);
                  }
               }
            }
         }
      }
   }

   private String configKey(Field field) {
      SerdeKey keyAnnot = field.getAnnotation(SerdeKey.class);
      return keyAnnot == null ? field.getName() : keyAnnot.value();
   }

   private String configComment(Field field) {
      SerdeComment[] commentAnnots = field.getDeclaredAnnotationsByType(SerdeComment.class);
      if (commentAnnots.length == 0) {
         return null;
      } else {
         String comment = commentAnnots[0].value();

         for (int i = 1; i < commentAnnots.length; i++) {
            comment = comment + "\n";
            comment = comment + commentAnnots[i].value();
         }

         return comment;
      }
   }

   private boolean skipField(Field field, Object fieldContainer, Object fieldValue) {
      SerdeSkipSerializingIf annot = field.getAnnotation(SerdeSkipSerializingIf.class);
      if (annot == null) {
         return false;
      } else {
         try {
            Predicate<?> skipPredicate = AnnotationProcessor.resolveSkipSerializingIfPredicate(annot, fieldContainer, field);
            return ((Predicate<Object>)skipPredicate).test(fieldValue);
         } catch (Exception var7) {
            String msg = "Failed to resolve or apply skip predicate for serialization of field " + field;
            throw new SerdeException(msg, var7);
         }
      }
   }

   private boolean assertField(Field field, Object fieldContainer, Object fieldValue) {
      SerdeAssert[] annot = field.getAnnotationsByType(SerdeAssert.class);
      if (annot != null && annot.length != 0) {
         try {
            Predicate<?> assertPredicate = AnnotationProcessor.resolveAssertPredicate(annot, fieldContainer, SerdePhase.SERIALIZING, field);
            return assertPredicate == null ? true : ((Predicate<Object>)assertPredicate).test(fieldValue);
         } catch (Exception var7) {
            String msg = "Failed to resolve or apply assertion for serialization of field " + field;
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

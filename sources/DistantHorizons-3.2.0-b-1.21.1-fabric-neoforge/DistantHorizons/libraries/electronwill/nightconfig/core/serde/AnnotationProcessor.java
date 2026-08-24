package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeAssert;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeDefault;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdePhase;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeSkipDeserializingIf;
import DistantHorizons.libraries.electronwill.nightconfig.core.serde.annotations.SerdeSkipSerializingIf;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class AnnotationProcessor {
   static Predicate<?> resolveAssertPredicate(SerdeAssert[] annotations, Object currentInstance, SerdePhase currentPhase, Field field) {
      List<Predicate<Object>> predicates = new ArrayList<>(annotations.length);

      for (SerdeAssert annot : annotations) {
         SerdePhase annotPhase = annot.phase();
         SerdeAssert.AssertThat[] conditions = annot.value();
         AnnotationProcessor.SerdeAssertSanityCheck sanityCheck = new AnnotationProcessor.SerdeAssertSanityCheck();
         if (annotPhase == currentPhase || annotPhase == SerdePhase.BOTH) {
            for (int i = 0; i < conditions.length; i++) {
               SerdeAssert.AssertThat condition = conditions[i];
               predicates.add((Predicate<Object>)resolveAssertPredicate1(condition, annot, currentInstance, currentPhase, field.getType(), sanityCheck));
            }
         }

         sanityCheck.check(annot);
      }

      return combineAnd(predicates);
   }

   private static Predicate<?> resolveAssertPredicate1(
      SerdeAssert.AssertThat assertThat,
      SerdeAssert annotation,
      Object currentInstance,
      SerdePhase currentPhase,
      Class<?> fieldType,
      AnnotationProcessor.SerdeAssertSanityCheck sanityCheck
   ) {
      Class<?> cls = annotation.customClass();
      String methodOrFieldName = annotation.customCheck();
      if (assertThat == SerdeAssert.AssertThat.CUSTOM) {
         sanityCheck.hasCustomAssert = true;
         if (methodOrFieldName.isEmpty()) {
            throw new SerdeException(
               String.format(
                  "Invalid annotation %s: with AssertThat.CUSTOM, parameter `customCheck` must be provided and non-empty.", annotToString(annotation)
               )
            );
         } else {
            return findCustomPredicate("assert predicate", annotation, cls, methodOrFieldName, currentInstance, fieldType);
         }
      } else {
         sanityCheck.hasCustomParam = !methodOrFieldName.isEmpty() || cls != Object.class;
         if (assertThat == SerdeAssert.AssertThat.NOT_NULL) {
            return v -> v != null;
         } else if (assertThat == SerdeAssert.AssertThat.NOT_EMPTY) {
            return v -> v == null || !Util.isEmpty(v);
         } else {
            assert false : "missing case";

            return null;
         }
      }
   }

   static Predicate<?> resolveSkipDeserializingIfPredicate(SerdeSkipDeserializingIf annotation, Object currentInstance) {
      SerdeSkipDeserializingIf.SkipDeIf[] conditions = annotation.value();
      Predicate[] predicates = new Predicate[conditions.length];

      for (int i = 0; i < predicates.length; i++) {
         SerdeSkipDeserializingIf.SkipDeIf condition = conditions[i];
         predicates[i] = resolveSkipDeserializingIfPredicate1(condition, annotation, currentInstance, Object.class);
      }

      return combineOr(predicates);
   }

   private static Predicate<?> resolveSkipDeserializingIfPredicate1(
      SerdeSkipDeserializingIf.SkipDeIf skipIf, SerdeSkipDeserializingIf annotation, Object currentInstance, Class<?> configValueType
   ) {
      Class<?> cls = annotation.customClass();
      String methodOrFieldName = annotation.customCheck();
      if (skipIf == SerdeSkipDeserializingIf.SkipDeIf.CUSTOM) {
         if (methodOrFieldName.isEmpty()) {
            throw new SerdeException(
               String.format("Invalid annotation %s: with SkipDeIf.CUSTOM, parameter `customCheck` must be provided and non-empty.", annotToString(annotation))
            );
         } else {
            return findCustomPredicate("skip predicate", annotation, cls, methodOrFieldName, currentInstance, configValueType);
         }
      } else if (!methodOrFieldName.isEmpty() || cls != Object.class) {
         throw new SerdeException(
            String.format("Invalid annotation %s: with SkipDeIf.%s, no additional parameter must be specified.", annotToString(annotation), skipIf.name())
         );
      } else if (skipIf == SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING) {
         return v -> v == null;
      } else if (skipIf == SerdeSkipDeserializingIf.SkipDeIf.IS_NULL) {
         return v -> v == NullObject.NULL_OBJECT;
      } else if (skipIf == SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY) {
         return v -> v != null && Util.isEmpty(v);
      } else {
         assert false : "missing case";

         return null;
      }
   }

   static Predicate<?> resolveSkipSerializingIfPredicate(SerdeSkipSerializingIf annotation, Object currentInstance, Field field) {
      SerdeSkipSerializingIf.SkipSerIf[] conditions = annotation.value();
      Predicate[] predicates = new Predicate[conditions.length];

      for (int i = 0; i < predicates.length; i++) {
         SerdeSkipSerializingIf.SkipSerIf condition = conditions[i];
         predicates[i] = resolveSkipSerializingIfPredicate1(condition, annotation, currentInstance, field.getType());
      }

      return combineOr(predicates);
   }

   private static Predicate<?> resolveSkipSerializingIfPredicate1(
      SerdeSkipSerializingIf.SkipSerIf skipIf, SerdeSkipSerializingIf annotation, Object currentInstance, Class<?> fieldType
   ) {
      Class<?> cls = annotation.customClass();
      String methodOrFieldName = annotation.customCheck();
      if (skipIf == SerdeSkipSerializingIf.SkipSerIf.CUSTOM) {
         if (methodOrFieldName.isEmpty()) {
            throw new SerdeException(
               String.format("Invalid annotation %s: with SkipSerIf.CUSTOM, parameter `customCheck` must be provided and non-empty.", annotToString(annotation))
            );
         } else {
            return findCustomPredicate("skip predicate", annotation, cls, methodOrFieldName, currentInstance, fieldType);
         }
      } else if (!methodOrFieldName.isEmpty() || cls != Object.class) {
         throw new SerdeException(
            String.format("Invalid annotation %s: with SkipSerIf.%s, no additional parameter must be specified.", annotToString(annotation), skipIf.name())
         );
      } else if (skipIf == SerdeSkipSerializingIf.SkipSerIf.IS_NULL) {
         return v -> v == null;
      } else if (skipIf == SerdeSkipSerializingIf.SkipSerIf.IS_EMPTY) {
         return v -> v != null && Util.isEmpty(v);
      } else {
         assert false : "missing case";

         return null;
      }
   }

   private static Predicate<?> pedicateFromField(String label, Field field, Object instance, boolean mustBeStatic) {
      return anyFromField(Predicate.class, label, field, instance, mustBeStatic);
   }

   static EnumMap<SerdePhase, EnumMap<SerdeDefault.WhenValue, SerdeDefault>> getConfigDefaultAnnotations(Field field) {
      EnumMap<SerdePhase, EnumMap<SerdeDefault.WhenValue, SerdeDefault>> byPhase = new EnumMap<>(SerdePhase.class);

      for (SerdeDefault annot : field.getAnnotationsByType(SerdeDefault.class)) {
         SerdePhase[] phases;
         if (annot.phase() == SerdePhase.BOTH) {
            phases = new SerdePhase[]{SerdePhase.BOTH, SerdePhase.SERIALIZING, SerdePhase.DESERIALIZING};
         } else {
            phases = new SerdePhase[]{annot.phase()};
         }

         for (SerdePhase phase : phases) {
            EnumMap<SerdeDefault.WhenValue, SerdeDefault> byWhen = byPhase.computeIfAbsent(phase, p -> new EnumMap<>(SerdeDefault.WhenValue.class));

            for (SerdeDefault.WhenValue when : annot.whenValue()) {
               SerdeDefault conflict = byWhen.put(when, annot);
               if (conflict != null) {
                  String msg = String.format(
                     "Annotation %s is conflicting with annotation %s on field `%s`. Only one @SerdeDefault must be applicable in a given situation.",
                     annotToString(annot),
                     conflict,
                     field
                  );
                  throw new SerdeException(msg);
               }
            }
         }
      }

      return byPhase;
   }

   static Supplier<?> resolveConfigDefaultProvider(SerdeDefault annotation, Object currentInstance) {
      Class<?> cls = annotation.cls();
      String methodOrFieldName = annotation.provider();
      Class<?>[] noParameters = new Class[0];
      Object methodOrField;
      if (cls == Object.class) {
         methodOrField = findFieldOrMethodIn(currentInstance.getClass(), methodOrFieldName, true, noParameters);
      } else {
         methodOrField = findFieldOrMethodIn(cls, methodOrFieldName, false, noParameters);
      }

      if (methodOrField == null) {
         String msg = String.format("Default value provider `%s` not found for annotation %s", methodOrFieldName, annotToString(annotation));
         throw new SerdeException(msg);
      } else {
         return methodOrField instanceof Field
            ? defaultSupplierFromField((Field)methodOrField, currentInstance, cls != Object.class)
            : defaultSupplierFromMethod((Method)methodOrField, currentInstance, cls != Object.class);
      }
   }

   private static <T> Predicate<T> combineOr(Predicate<T>[] predicates) {
      return predicates.length == 1 ? predicates[0] : o -> {
         for (Predicate<T> p : predicates) {
            if (p.test(o)) {
               return true;
            }
         }

         return false;
      };
   }

   private static <T> Predicate<T> combineAnd(List<Predicate<T>> predicates) {
      if (predicates.isEmpty()) {
         return null;
      } else {
         return predicates.size() == 1 ? predicates.get(0) : o -> {
            for (Predicate<T> p : predicates) {
               if (!p.test(o)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   private static Predicate<?> findCustomPredicate(
      String label, Annotation annotation, Class<?> cls, String methodOrFieldName, Object currentInstance, Class<?> predicateParameter
   ) {
      Class<?>[] methodParameters = new Class[]{predicateParameter};
      Object methodOrField;
      if (cls == Object.class) {
         methodOrField = findFieldOrMethodIn(currentInstance.getClass(), methodOrFieldName, true, methodParameters);
      } else {
         methodOrField = findFieldOrMethodIn(cls, methodOrFieldName, false, methodParameters);
      }

      if (methodOrField == null) {
         String msg = String.format("Custom %s `%s` not found for annotation %s", label, methodOrFieldName, annotToString(annotation));
         throw new SerdeException(msg);
      } else {
         return methodOrField instanceof Field
            ? pedicateFromField(label, (Field)methodOrField, currentInstance, cls != Object.class)
            : predicateFromMethod(label, (Method)methodOrField, currentInstance, cls != Object.class, predicateParameter);
      }
   }

   private static Object findFieldOrMethodIn(Class<?> cls, String name, boolean recurse, Class<?>[] methodParameters) {
      boolean methodOnly = false;
      if (name.endsWith("()")) {
         methodOnly = true;
         name = name.substring(0, name.length() - 2);
      }

      while (true) {
         if (!methodOnly) {
            try {
               return cls.getDeclaredField(name);
            } catch (NoSuchFieldException var7) {
            }
         }

         try {
            return cls.getDeclaredMethod(name, methodParameters);
         } catch (NoSuchMethodException var6) {
            if (recurse) {
               cls = cls.getSuperclass();
               if (cls != Object.class) {
                  continue;
               }
            }

            return null;
         }
      }
   }

   private static Supplier<?> defaultSupplierFromField(Field field, Object instance, boolean mustBeStatic) {
      return anyFromField(Supplier.class, "default value provider", field, instance, mustBeStatic);
   }

   private static Supplier<?> defaultSupplierFromMethod(Method method, Object instance, boolean mustBeStatic) {
      return supplierFromMethod("default value provider", method, instance, mustBeStatic);
   }

   private static <T> T anyFromField(Class<T> t, String label, Field field, Object instance, boolean mustBeStatic) {
      int mods = field.getModifiers();
      if (!Modifier.isPublic(mods)) {
         field.setAccessible(true);
      }

      if (mustBeStatic && !Modifier.isStatic(mods)) {
         String msg = String.format("Invalid %s: field %s should be declared as static.", label, field);
         throw new SerdeException(msg);
      } else {
         Object value;
         try {
            value = field.get(instance);
         } catch (IllegalAccessException | IllegalArgumentException var9) {
            String msg = String.format("Could not read the %s `%s` on object `%s`.", label, field, instance);
            throw new SerdeException(msg, var9);
         }

         if (value == null) {
            throw new SerdeException(String.format("Invalid %s: field `%s` is null in object `%s`.", label, field, instance));
         } else if (!t.isAssignableFrom(value.getClass())) {
            throw new SerdeException(String.format("Invalid %s: field `%s` must be of type `%s`.", label, field.getName(), t));
         } else {
            return (T)value;
         }
      }
   }

   private static Supplier<?> supplierFromMethod(String label, Method method, Object instance, boolean mustBeStatic) {
      if (method.getParameterCount() > 0) {
         throw new SerdeException(String.format("Invalid %s: method %s should take no parameter.", label, method));
      } else {
         int mods = method.getModifiers();
         if (!Modifier.isPublic(mods)) {
            method.setAccessible(true);
         }

         if (Modifier.isStatic(mods)) {
            return () -> {
               try {
                  return method.invoke(null);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
                  throw new SerdeException(String.format("Could not invoke the %s `%s`", label, var3));
               }
            };
         } else if (mustBeStatic) {
            String msg = String.format("Invalid %s: method %s should be declared as static.", label, method);
            throw new SerdeException(msg);
         } else {
            return () -> {
               try {
                  return method.invoke(instance);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4x) {
                  throw new SerdeException(String.format("Could not invoke the %s `%s` on object %s", label, method, instance), var4x);
               }
            };
         }
      }
   }

   private static Predicate<?> predicateFromMethod(String label, Method method, Object instance, boolean mustBeStatic, Class<?> parameterType) {
      if (method.getParameterCount() != 1) {
         throw new SerdeException(String.format("Invalid %s: method %s should take exactly one parameter of type %s.", label, method, parameterType));
      } else if (method.getReturnType() != boolean.class) {
         throw new SerdeException(String.format("Invalid %s: method %s should return a boolean.", label, method));
      } else {
         int mods = method.getModifiers();
         if (!Modifier.isPublic(mods)) {
            method.setAccessible(true);
         }

         if (Modifier.isStatic(mods)) {
            return x -> {
               try {
                  return (Boolean)method.invoke(null, x);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
                  throw new SerdeException(String.format("Could not invoke the %s `%s`", label, var4));
               }
            };
         } else if (mustBeStatic) {
            String msg = String.format("Invalid %s: method %s should be declared as static.", label, method);
            throw new SerdeException(msg);
         } else {
            return x -> {
               try {
                  return (Boolean)method.invoke(instance, x);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5x) {
                  throw new SerdeException(String.format("Could not invoke the %s `%s` on object %s", label, method, instance), var5x);
               }
            };
         }
      }
   }

   static String annotToString(Annotation annotation) {
      return annotation.toString().replace("@com.electronwill.nightconfig.core.serde.annotations.", "@");
   }

   private static class SerdeAssertSanityCheck {
      boolean hasCustomAssert;
      boolean hasCustomParam;

      private SerdeAssertSanityCheck() {
      }

      void check(SerdeAssert annotation) {
         if (this.hasCustomParam && !this.hasCustomAssert) {
            throw new SerdeException(
               String.format(
                  "Invalid annotation %s: without AssertThat.CUSTOM, no additional parameter must be specified.", AnnotationProcessor.annotToString(annotation)
               )
            );
         }
      }
   }
}

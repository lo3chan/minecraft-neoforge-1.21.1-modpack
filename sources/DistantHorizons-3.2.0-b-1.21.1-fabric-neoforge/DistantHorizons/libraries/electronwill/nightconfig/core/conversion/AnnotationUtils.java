package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.EnumGetMethod;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.StringUtils;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

final class AnnotationUtils {
   private AnnotationUtils() {
   }

   static boolean hasPreserveNotNull(AnnotatedElement annotatedElement) {
      return annotatedElement.isAnnotationPresent(PreserveNotNull.class);
   }

   static boolean mustPreserve(Field field, Class<?> fieldClass) {
      return hasPreserveNotNull(field) || hasPreserveNotNull(fieldClass);
   }

   static Converter<Object, Object> getConverter(Field field) {
      Conversion conversion = field.getAnnotation(Conversion.class);
      if (conversion != null) {
         try {
            Constructor<? extends Converter<?, ?>> constructor = conversion.value().getDeclaredConstructor();
            if (!constructor.isAccessible()) {
               constructor.setAccessible(true);
            }

            return (Converter<Object, Object>)constructor.newInstance();
         } catch (ReflectiveOperationException var3) {
            throw new ReflectionException("Cannot create a converter for field " + field, var3);
         }
      } else {
         return null;
      }
   }

   static List<String> getPath(Field field) {
      List<String> annotatedPath = getPath((AnnotatedElement)field);
      return annotatedPath == null ? Collections.singletonList(field.getName()) : annotatedPath;
   }

   static List<String> getPath(AnnotatedElement annotatedElement) {
      Path path = annotatedElement.getDeclaredAnnotation(Path.class);
      if (path != null) {
         return StringUtils.split(path.value(), '.');
      } else {
         AdvancedPath advancedPath = annotatedElement.getDeclaredAnnotation(AdvancedPath.class);
         return advancedPath != null ? Arrays.asList(advancedPath.value()) : null;
      }
   }

   static void checkField(Field field, Object value) {
      SpecNotNull specNotNull = field.getDeclaredAnnotation(SpecNotNull.class);
      if (specNotNull != null) {
         checkNotNull(field, value);
      }

      SpecClassInArray specClassInArray = field.getDeclaredAnnotation(SpecClassInArray.class);
      if (specClassInArray != null) {
         checkFieldSpec(field, value, specClassInArray);
      }

      SpecStringInArray specStringInArray = field.getDeclaredAnnotation(SpecStringInArray.class);
      if (specStringInArray != null) {
         checkFieldSpec(field, value, specStringInArray);
      }

      SpecStringInRange specStringInRange = field.getDeclaredAnnotation(SpecStringInRange.class);
      if (specStringInRange != null) {
         checkFieldSpec(field, value, specStringInRange);
      }

      SpecDoubleInRange specDoubleInRange = field.getDeclaredAnnotation(SpecDoubleInRange.class);
      if (specDoubleInRange != null) {
         checkFieldSpec(field, value, specDoubleInRange);
      }

      SpecFloatInRange specFloatInRange = field.getDeclaredAnnotation(SpecFloatInRange.class);
      if (specFloatInRange != null) {
         checkFieldSpec(field, value, specFloatInRange);
      }

      SpecLongInRange specLongInRange = field.getDeclaredAnnotation(SpecLongInRange.class);
      if (specLongInRange != null) {
         checkFieldSpec(field, value, specLongInRange);
      }

      SpecIntInRange specIntInRange = field.getDeclaredAnnotation(SpecIntInRange.class);
      if (specIntInRange != null) {
         checkFieldSpec(field, value, specIntInRange);
      }

      SpecEnum specEnum = field.getDeclaredAnnotation(SpecEnum.class);
      if (specEnum != null) {
         checkFieldSpec(field, value, specEnum);
      }

      SpecValidator specValidator = field.getDeclaredAnnotation(SpecValidator.class);
      if (specValidator != null) {
         checkFieldSpec(field, value, specValidator);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecValidator spec) {
      Predicate<Object> validatorInstance;
      try {
         Constructor<? extends Predicate<Object>> constructor = spec.value().getDeclaredConstructor();
         constructor.setAccessible(true);
         validatorInstance = (Predicate<Object>)constructor.newInstance();
      } catch (ReflectiveOperationException var5) {
         throw new ReflectionException("Cannot create a converter for field " + field, var5);
      }

      if (!validatorInstance.test(value)) {
         throw new InvalidValueException("Invalid value \"%s\" for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecClassInArray spec) {
      checkNotNull(field, value);
      Class<?> valueClass = value.getClass();
      if (spec.strict()) {
         for (Class<?> aClass : spec.value()) {
            if (aClass.isAssignableFrom(valueClass)) {
               return;
            }
         }
      } else {
         for (Class<?> aClassx : spec.value()) {
            if (aClassx.equals(valueClass)) {
               return;
            }
         }
      }

      throw new InvalidValueException("Invalid value \"%s\" for field %s: it doesn't conform to %s", value, field, spec);
   }

   private static void checkFieldSpec(Field field, Object value, SpecStringInRange spec) {
      checkClass(field, value, String.class);
      String s = (String)value;
      if (s.compareTo(spec.min()) < 0 || s.compareTo(spec.max()) > 0) {
         throw new InvalidValueException("Invalid value \"%s\" for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecEnum spec) {
      EnumGetMethod m = spec.method();
      Class<?> fieldType = field.getType();
      if (!fieldType.isEnum()) {
         throw new InvalidValueException("Field %s is annotated with @SpecEnum but isn't of type enum", field);
      } else if (!m.validate(value, fieldType)) {
         throw new InvalidValueException("Invalid value \"%s\" for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecStringInArray spec) {
      checkClass(field, value, String.class);
      String s = (String)value;
      if (spec.ignoreCase()) {
         for (String acceptable : spec.value()) {
            if (s.equalsIgnoreCase(acceptable)) {
               return;
            }
         }
      } else {
         for (String acceptablex : spec.value()) {
            if (s.equals(acceptablex)) {
               return;
            }
         }
      }

      throw new InvalidValueException("Invalid value \"%s\" for field %s: it doesn't conform to %s", value, field, spec);
   }

   private static void checkFieldSpec(Field field, Object value, SpecDoubleInRange spec) {
      checkClass(field, value, Double.class);
      double d = (Double)value;
      if (d < spec.min() || d > spec.max()) {
         throw new InvalidValueException("Invalid value %f for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecFloatInRange spec) {
      checkClass(field, value, Float.class);
      float d = (Float)value;
      if (d < spec.min() || d > spec.max()) {
         throw new InvalidValueException("Invalid value %f for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecLongInRange spec) {
      checkClass(field, value, Long.class);
      long d = (Long)value;
      if (d < spec.min() || d > spec.max()) {
         throw new InvalidValueException("Invalid value %d for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkFieldSpec(Field field, Object value, SpecIntInRange spec) {
      checkClass(field, value, Integer.class);
      int d = (Integer)value;
      if (d < spec.min() || d > spec.max()) {
         throw new InvalidValueException("Invalid value %d for field %s: it doesn't conform to %s", value, field, spec);
      }
   }

   private static void checkNotNull(Field field, Object value) {
      if (value == null) {
         throw new InvalidValueException("Invalid null value for field %s", field);
      }
   }

   private static void checkClass(Field field, Object value, Class<?> expectedClass) {
      checkNotNull(field, value);
      Class<?> valueClass = value.getClass();
      if (valueClass != expectedClass) {
         throw new InvalidValueException("Invalid type %s for field %s, expected %s", valueClass, field, expectedClass);
      }
   }
}

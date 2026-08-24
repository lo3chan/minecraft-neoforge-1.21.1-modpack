package io.wispforest.owo.util;

import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public final class NumberReflection {
   private NumberReflection() {
   }

   public static boolean isNumberType(Class<?> clazz) {
      return clazz.isPrimitive() && clazz != boolean.class && clazz != char.class
         || clazz == Byte.class
         || clazz == Short.class
         || clazz == Integer.class
         || clazz == Long.class
         || clazz == Double.class
         || clazz == Float.class;
   }

   public static boolean isFloatingPointType(Class<?> clazz) {
      return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
   }

   public static <T extends Number> T convert(Number in, Class<T> targetClass) {
      if (!isNumberType(targetClass)) {
         throw new IllegalArgumentException("Cannot convert to non-number target class");
      } else if (targetClass == Float.class || targetClass == float.class) {
         return (T)in.floatValue();
      } else if (targetClass == Double.class || targetClass == double.class) {
         return (T)in.doubleValue();
      } else if (targetClass == Byte.class || targetClass == byte.class) {
         return (T)in.byteValue();
      } else if (targetClass == Short.class || targetClass == short.class) {
         return (T)in.shortValue();
      } else if (targetClass == Integer.class || targetClass == int.class) {
         return (T)in.intValue();
      } else if (targetClass != Long.class && targetClass != long.class) {
         throw new IllegalStateException("Target class does not correspond to a supported number type - this should be unreachable");
      } else {
         return (T)in.longValue();
      }
   }

   public static <T extends Number> T maxValue(Class<T> numberType) {
      if (!isNumberType(numberType)) {
         throw new IllegalArgumentException("Cannot get maximum value of non-number class");
      } else if (numberType == Float.class || numberType == float.class) {
         return (T)3.4028235E38F;
      } else if (numberType == Double.class || numberType == double.class) {
         return (T)1.7976931348623157E308;
      } else if (numberType == Byte.class || numberType == byte.class) {
         return (T)(byte)127;
      } else if (numberType == Short.class || numberType == short.class) {
         return (T)(short)32767;
      } else if (numberType == Integer.class || numberType == int.class) {
         return (T)2147483647;
      } else if (numberType != Long.class && numberType != long.class) {
         throw new IllegalStateException("Target class does not correspond to a supported number type - this should be unreachable");
      } else {
         return (T)9223372036854775807L;
      }
   }

   public static <T extends Number> T minValue(Class<T> numberType) {
      if (!isNumberType(numberType)) {
         throw new IllegalArgumentException("Cannot get minimum value of non-number class");
      } else if (numberType == Float.class || numberType == float.class) {
         return (T)-3.4028235E38F;
      } else if (numberType == Double.class || numberType == double.class) {
         return (T)-1.7976931348623157E308;
      } else if (numberType == Byte.class || numberType == byte.class) {
         return (T)(byte)-128;
      } else if (numberType == Short.class || numberType == short.class) {
         return (T)(short)-32768;
      } else if (numberType == Integer.class || numberType == int.class) {
         return (T)-2147483648;
      } else if (numberType != Long.class && numberType != long.class) {
         throw new IllegalStateException("Target class does not correspond to a supported number type - this should be unreachable");
      } else {
         return (T)-9223372036854775808L;
      }
   }
}

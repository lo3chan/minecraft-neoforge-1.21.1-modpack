package com.seibel.distanthorizons.core.util;

import java.util.HashMap;
import java.util.Map;

public class NumberUtil {
   public static Map<Class<?>, Number> minValues = new HashMap<Class<?>, Number>() {
      {
         this.put(Byte.class, -128);
         this.put(Short.class, -32768);
         this.put(Integer.class, -2147483648);
         this.put(Long.class, -9223372036854775808L);
         this.put(Double.class, 5.0E-324);
         this.put(Float.class, 1.0E-45F);
      }
   };
   public static Map<Class<?>, Number> maxValues = new HashMap<Class<?>, Number>() {
      {
         this.put(Byte.class, (byte)127);
         this.put(Short.class, (short)32767);
         this.put(Integer.class, 2147483647);
         this.put(Long.class, 9223372036854775807L);
         this.put(Double.class, 1.7976931348623157E308);
         this.put(Float.class, 3.4028235E38F);
      }
   };

   public static Number getMinimum(Class<?> c) {
      return minValues.get(c);
   }

   public static Number getMaximum(Class<?> c) {
      return maxValues.get(c);
   }

   public static boolean greaterThan(Number a, Number b) {
      if (a.getClass() != b.getClass()) {
         return false;
      } else {
         Class<?> typeClass = a.getClass();
         if (typeClass == Byte.class) {
            return a.byteValue() > b.byteValue();
         } else if (typeClass == Short.class) {
            return a.shortValue() > b.shortValue();
         } else if (typeClass == Integer.class) {
            return a.intValue() > b.intValue();
         } else if (typeClass == Long.class) {
            return a.longValue() > b.longValue();
         } else if (typeClass == Double.class) {
            return a.doubleValue() > b.doubleValue();
         } else {
            return typeClass == Float.class ? a.floatValue() > b.floatValue() : false;
         }
      }
   }

   public static boolean lessThan(Number a, Number b) {
      if (a.getClass() != b.getClass()) {
         return false;
      } else {
         Class<?> typeClass = a.getClass();
         if (typeClass == Byte.class) {
            return a.byteValue() < b.byteValue();
         } else if (typeClass == Short.class) {
            return a.shortValue() < b.shortValue();
         } else if (typeClass == Integer.class) {
            return a.intValue() < b.intValue();
         } else if (typeClass == Long.class) {
            return a.longValue() < b.longValue();
         } else if (typeClass == Double.class) {
            return a.doubleValue() < b.doubleValue();
         } else {
            return typeClass == Float.class ? a.floatValue() < b.floatValue() : false;
         }
      }
   }
}

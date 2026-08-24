package com.seibel.distanthorizons.core.util;

public class BoolUtil {
   public static boolean falseIfNull(Boolean value) {
      return value == null ? false : value;
   }
}

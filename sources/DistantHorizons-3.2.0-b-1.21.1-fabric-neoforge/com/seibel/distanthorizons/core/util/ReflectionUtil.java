package com.seibel.distanthorizons.core.util;

import java.lang.reflect.Field;

public class ReflectionUtil {
   public static String getAllFieldValuesAsString(Object obj) {
      StringBuilder stringBuilder = new StringBuilder();
      Field[] fields = obj.getClass().getDeclaredFields();

      for (Field field : fields) {
         String fieldName = field.getName();

         String fieldStringValue;
         try {
            field.setAccessible(true);
            fieldStringValue = field.get(obj) + "";
         } catch (Exception var10) {
            fieldStringValue = "ERROR:[" + var10.getMessage() + "]";
         }

         stringBuilder.append(fieldName + " - " + fieldStringValue + "\n");
      }

      return stringBuilder.toString();
   }
}

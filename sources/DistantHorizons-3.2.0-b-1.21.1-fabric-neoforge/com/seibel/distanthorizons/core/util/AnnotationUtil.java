package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationUtil {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static <TEnum extends Enum<?>, TAnno extends Annotation> boolean doesEnumHaveAnnotation(TEnum enumValue, Class<TAnno> annotationToSearchFor) {
      try {
         Field[] fields = enumValue.getClass().getFields();

         for (Field field : fields) {
            TEnum testEnumValue = (TEnum)field.get(enumValue);
            if (testEnumValue == enumValue) {
               return field.<TAnno>getAnnotation(annotationToSearchFor) != null;
            }
         }

         throw new IllegalStateException(
            "Enum missing expected value. Enum: [" + enumValue.getClass() + "] doesn't contain the value: [" + enumValue.name() + "]."
         );
      } catch (IllegalArgumentException | ClassCastException | IllegalAccessException var8) {
         LOGGER.error(
            "Unable to get annotation for enum: [" + enumValue.getClass() + "]. Unexpected exception: [" + var8 + "], message: [" + var8.getMessage() + "].",
            var8
         );
         return false;
      }
   }
}

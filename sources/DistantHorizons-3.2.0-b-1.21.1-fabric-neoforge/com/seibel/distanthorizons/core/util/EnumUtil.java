package com.seibel.distanthorizons.core.util;

import java.io.InvalidObjectException;

public class EnumUtil {
   public static <T extends Enum<T>> T parseEnumIgnoreCase(String enumName, Class<T> enumType) throws InvalidObjectException {
      for (T enumValue : (Enum[])enumType.getEnumConstants()) {
         if (enumValue.name().equalsIgnoreCase(enumName)) {
            return enumValue;
         }
      }

      throw new InvalidObjectException(
         "No Enum of type ["
            + enumType.getSimpleName()
            + "] exists with the name ["
            + enumName
            + "]. Possible enum values are: ["
            + createEnumCsv(enumType)
            + "]"
      );
   }

   public static String createEnumCsv(Class<? extends Enum<?>> enumType) {
      StringBuilder str = new StringBuilder();
      Enum<?>[] enumValues = (Enum<?>[])enumType.getEnumConstants();

      for (int i = 0; i < enumValues.length; i++) {
         if (i == 0) {
            str.append(enumValues[i].name());
         } else {
            str.append(", ").append(enumValues[i].name());
         }
      }

      return str.toString();
   }

   public static EnumUtil.EnumComparisonResult compareEnumClassesByValues(Class<? extends Enum<?>> alphaEnum, Class<? extends Enum<?>> betaEnum) {
      Enum<?>[] alphaValues = (Enum<?>[])alphaEnum.getEnumConstants();
      Enum<?>[] betaValues = (Enum<?>[])betaEnum.getEnumConstants();
      if (alphaValues.length != betaValues.length) {
         return new EnumUtil.EnumComparisonResult(
            false,
            createFailMessageHeader(alphaEnum, betaEnum) + "the enums have [" + alphaValues.length + "] and [" + betaValues.length + "] values respectively."
         );
      } else {
         for (Enum<?> alphaVal : alphaValues) {
            boolean valueFoundInBothEnums = false;

            for (Enum<?> betaVal : betaValues) {
               if (alphaVal.name().equals(betaVal.name())) {
                  valueFoundInBothEnums = true;
                  break;
               }
            }

            if (!valueFoundInBothEnums) {
               return new EnumUtil.EnumComparisonResult(
                  false,
                  createFailMessageHeader(alphaEnum, betaEnum) + "the enum value [" + alphaVal.name() + "] wasn't found in [" + betaEnum.getSimpleName() + "]."
               );
            }
         }

         return new EnumUtil.EnumComparisonResult(true, "");
      }
   }

   public static String createFailMessageHeader(Class<? extends Enum<?>> alphaEnum, Class<? extends Enum<?>> betaEnum) {
      return "The enums [" + alphaEnum.getSimpleName() + "] and [" + betaEnum.getSimpleName() + "] aren't equal: ";
   }

   public static class EnumComparisonResult {
      public final boolean success;
      public final String failMessage;

      public EnumComparisonResult(boolean newSuccess, String newFailMessage) {
         this.success = newSuccess;
         this.failMessage = newFailMessage;
      }
   }
}

package com.seibel.distanthorizons.core.config.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.json.JsonFormat;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.HashMap;
import java.util.Map;

public class ConfigTypeConverters {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final Map<Class<?>, ConfigTypeConverters.ConverterBase> convertObjects = new HashMap<Class<?>, ConfigTypeConverters.ConverterBase>() {
      {
         this.put(Short.class, new ConfigTypeConverters.ShortConverter());
         this.put(Long.class, new ConfigTypeConverters.LongConverter());
         this.put(Float.class, new ConfigTypeConverters.FloatConverter());
         this.put(Double.class, new ConfigTypeConverters.DoubleConverter());
         this.put(Byte.class, new ConfigTypeConverters.ByteConverter());
         this.put(Map.class, new ConfigTypeConverters.MapConverter());
      }
   };

   public static Class<?> isClassConvertable(Class<?> clazz) {
      for (int i = 0; i < convertObjects.size(); i++) {
         Class<?> selectedClass = (Class<?>)convertObjects.keySet().toArray()[i];
         if (selectedClass.isAssignableFrom(clazz)) {
            return selectedClass;
         }
      }

      return null;
   }

   public static Object attemptToConvertToString(Object value) {
      return attemptToConvertToString(value.getClass(), value);
   }

   public static Object attemptToConvertToString(Class<?> clazz, Object value) {
      Class<?> convertablClass = isClassConvertable(clazz);
      return convertablClass != null ? convertToString(convertablClass, value) : value;
   }

   public static Object attemptToConvertFromString(Object value) {
      return attemptToConvertFromString(value.getClass(), value);
   }

   public static Object attemptToConvertFromString(Class<?> outputClass, Object value) {
      boolean valueNeedsConverting = value == null || value.getClass().equals(String.class);
      Class<?> convertablClass = isClassConvertable(outputClass);
      return valueNeedsConverting && convertablClass != null ? convertFromString(convertablClass, (String)value) : value;
   }

   public static String convertToString(Class<?> clazz, Object value) {
      try {
         return convertObjects.get(clazz).convertToString(value);
      } catch (Exception var3) {
         System.out.println("Type [" + clazz.toString() + "] isn't a convertible value in the config file handler");
         return null;
      }
   }

   public static Object convertFromString(Class<?> clazz, String value) {
      try {
         return convertObjects.get(clazz).convertFromString(value);
      } catch (Exception var3) {
         System.out.println("Type [" + clazz.toString() + "] isn't a convertible value in the config file handler");
         return null;
      }
   }

   public static class ByteConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         return ((Byte)item).toString();
      }

      public Byte convertFromString(String s) {
         return Byte.valueOf(s);
      }
   }

   public abstract static class ConverterBase {
      public abstract String convertToString(Object object);

      public abstract Object convertFromString(String string);
   }

   public static class DoubleConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         return ((Double)item).toString();
      }

      public Double convertFromString(String s) {
         return Double.valueOf(s);
      }
   }

   public static class FloatConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         return ((Float)item).toString();
      }

      public Float convertFromString(String s) {
         return Float.valueOf(s);
      }
   }

   public static class LongConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         return ((Long)item).toString();
      }

      public Long convertFromString(String s) {
         return Long.valueOf(s);
      }
   }

   public static class MapConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         Map<String, Object> mapObject = (Map<String, Object>)item;
         Config jsonObject = Config.inMemory();
         Object[] keyArray = mapObject.keySet().toArray();

         for (int i = 0; i < mapObject.size(); i++) {
            jsonObject.add(keyArray[i].toString(), mapObject.get(keyArray[i]));
         }

         return JsonFormat.minimalInstance().createWriter().writeToString(jsonObject);
      }

      public Map<String, Object> convertFromString(String str) {
         new HashMap();
         Config jsonObject = Config.inMemory();

         try {
            JsonFormat.minimalInstance().createParser().parse(str, jsonObject, ParsingMode.REPLACE);
         } catch (Exception var5) {
            ConfigTypeConverters.LOGGER.error("Unable to convert config string value [" + str + "] to a Map, error: [" + var5.getMessage() + "].", var5);
         }

         return jsonObject.valueMap();
      }
   }

   public static class ShortConverter extends ConfigTypeConverters.ConverterBase {
      @Override
      public String convertToString(Object item) {
         return ((Short)item).toString();
      }

      public Short convertFromString(String s) {
         return Short.valueOf(s);
      }
   }
}

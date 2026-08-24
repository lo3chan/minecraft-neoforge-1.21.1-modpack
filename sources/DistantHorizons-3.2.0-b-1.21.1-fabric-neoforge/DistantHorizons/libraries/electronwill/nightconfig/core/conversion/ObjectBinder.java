package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.EnumGetMethod;
import DistantHorizons.libraries.electronwill.nightconfig.core.InMemoryFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingMap;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingSet;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;

public final class ObjectBinder {
   private final boolean bypassTransient;
   private final boolean bypassFinal;

   public ObjectBinder(boolean bypassTransient, boolean bypassFinal) {
      this.bypassTransient = bypassTransient;
      this.bypassFinal = bypassFinal;
   }

   public ObjectBinder() {
      this(false, true);
   }

   public Config bind(Class<?> clazz) {
      return this.bind(clazz, InMemoryFormat.defaultInstance());
   }

   public Config bind(Class<?> clazz, ConfigFormat<?> configFormat) {
      return this.bind(null, clazz, configFormat);
   }

   public Config bind(Object object) {
      return this.bind(object, InMemoryFormat.defaultInstance());
   }

   public Config bind(Object object, ConfigFormat<?> configFormat) {
      return this.bind(object, object.getClass(), configFormat);
   }

   private Config bind(Object object, Class<?> clazz, ConfigFormat<?> configFormat) {
      ObjectBinder.BoundConfig boundConfig = this.createBoundConfig(object, clazz, configFormat);
      List<String> annotatedPath = AnnotationUtils.getPath(clazz);
      if (annotatedPath != null) {
         Config parentConfig = configFormat.createConfig();
         parentConfig.set(annotatedPath, boundConfig);
         return parentConfig;
      } else {
         return boundConfig;
      }
   }

   private ObjectBinder.BoundConfig createBoundConfig(Object object, Class<?> clazz, ConfigFormat<?> configFormat) {
      ObjectBinder.BoundConfig boundConfig = new ObjectBinder.BoundConfig(object, configFormat, this.bypassFinal);

      for (Field field : clazz.getDeclaredFields()) {
         int fieldModifiers = field.getModifiers();
         if ((object != null || !Modifier.isStatic(fieldModifiers)) && (this.bypassTransient || !Modifier.isTransient(fieldModifiers))) {
            if (!field.isAccessible()) {
               field.setAccessible(true);
            }

            List<String> path = AnnotationUtils.getPath(field);
            Converter<Object, Object> converter = AnnotationUtils.getConverter(field);
            boolean isEnum = Enum.class.isAssignableFrom(field.getType());
            if (converter == null) {
               if (isEnum) {
                  SpecEnum spec = field.getAnnotation(SpecEnum.class);
                  EnumGetMethod method = spec == null ? EnumGetMethod.NAME_IGNORECASE : spec.method();
                  converter = new ObjectBinder.EnumValueConverter<>(field.getType(), method);
               } else {
                  converter = ObjectBinder.NoOpConverter.INSTANCE;
               }
            }

            ObjectBinder.FieldInfos fieldInfos;
            try {
               Object value = converter.convertFromField(field.get(object));
               if (value != null && !isEnum && !configFormat.supportsType(value.getClass())) {
                  ObjectBinder.BoundConfig subConfig = this.createBoundConfig(value, field.getType(), configFormat);
                  fieldInfos = new ObjectBinder.FieldInfos(field, subConfig, converter);
               } else {
                  fieldInfos = new ObjectBinder.FieldInfos(field, null, converter);
               }
            } catch (IllegalAccessException var16) {
               throw new ReflectionException("Failed to bind field " + field, var16);
            }

            boundConfig.registerField(fieldInfos, path);
         }
      }

      return boundConfig;
   }

   private static final class BoundConfig implements Config {
      private Object object;
      private final Map<String, Object> dataMap;
      private final ConfigFormat<?> configFormat;
      private final boolean bypassFinal;

      private BoundConfig(Object object, Map<String, Object> dataMap, ConfigFormat<?> configFormat, boolean bypassFinal) {
         this.object = object;
         this.dataMap = dataMap;
         this.configFormat = configFormat;
         this.bypassFinal = bypassFinal;
      }

      private BoundConfig(Object object, ConfigFormat<?> configFormat, boolean bypassFinal) {
         this(object, new HashMap<>(), configFormat, bypassFinal);
      }

      private void registerField(ObjectBinder.FieldInfos fieldInfos, List<String> path) {
         int lastIndex = path.size() - 1;
         Map<String, Object> currentMap = this.dataMap;

         for (String currentKey : path.subList(0, lastIndex)) {
            Object currentValue = currentMap.get(currentKey);
            ObjectBinder.BoundConfig config;
            if (currentValue == null) {
               config = new ObjectBinder.BoundConfig(null, new HashMap<>(1), this.configFormat, this.bypassFinal);
               currentMap.put(currentKey, config);
            } else {
               if (!(currentValue instanceof ObjectBinder.BoundConfig)) {
                  throw new IllegalArgumentException("Cannot add an element to an intermediary value of type: " + currentValue.getClass());
               }

               config = (ObjectBinder.BoundConfig)currentValue;
            }

            currentMap = config.dataMap;
         }

         String lastKey = path.get(lastIndex);
         currentMap.put(lastKey, fieldInfos);
      }

      private ObjectBinder.BoundSearchResult searchInfosOrConfig(List<String> path) {
         int lastIndex = path.size() - 1;
         ObjectBinder.BoundConfig currentConfig = this;

         for (String key : path.subList(0, lastIndex)) {
            Object v = currentConfig.dataMap.get(key);
            if (v == null) {
               return null;
            }

            if (v instanceof ObjectBinder.BoundConfig) {
               currentConfig = (ObjectBinder.BoundConfig)v;
            } else {
               ObjectBinder.FieldInfos fieldInfos = (ObjectBinder.FieldInfos)v;
               currentConfig = fieldInfos.getUpdatedConfig(currentConfig.object);
            }
         }

         String lastKey = path.get(lastIndex);
         Object data = currentConfig.dataMap.get(lastKey);
         return new ObjectBinder.BoundSearchResult(currentConfig, data);
      }

      @Override
      public <T> T getRaw(List<String> path) {
         ObjectBinder.BoundSearchResult searchResult = this.searchInfosOrConfig(path);
         if (searchResult == null) {
            return null;
         } else {
            return (T)(searchResult.hasSubConfig() ? searchResult.subConfig : searchResult.fieldInfos.getValue(searchResult.parentConfig.object));
         }
      }

      @Override
      public boolean contains(List<String> path) {
         return this.searchInfosOrConfig(path) != null;
      }

      @Override
      public <T> T set(List<String> path, Object value) {
         ObjectBinder.BoundSearchResult searchResult = this.searchInfosOrConfig(path);
         if (searchResult == null) {
            throw new UnsupportedOperationException("Cannot add elements to a bound config");
         } else if (searchResult.hasFieldInfos()) {
            return (T)searchResult.fieldInfos.setValue(searchResult.parentConfig.object, value, this.bypassFinal);
         } else {
            throw new UnsupportedOperationException("Cannot modify non-field elements of a bound config");
         }
      }

      @Override
      public boolean add(List<String> path, Object value) {
         throw new UnsupportedOperationException("Cannot add elements to a bound config");
      }

      @Override
      public <T> T remove(List<String> path) {
         ObjectBinder.BoundSearchResult searchResult = this.searchInfosOrConfig(path);
         if (searchResult == null) {
            return null;
         } else if (searchResult.hasFieldInfos()) {
            return (T)searchResult.fieldInfos.removeValue(searchResult.parentConfig.object, this.bypassFinal);
         } else {
            Config copy = Config.copy(searchResult.subConfig);
            searchResult.subConfig.clear();
            return (T)copy;
         }
      }

      @Override
      public void clear() {
         for (Map.Entry<String, Object> dataEntry : this.dataMap.entrySet()) {
            Object value = dataEntry.getValue();
            if (value instanceof ObjectBinder.FieldInfos) {
               ((ObjectBinder.FieldInfos)value).removeValue(this.object, this.bypassFinal);
            } else if (value instanceof ObjectBinder.BoundConfig) {
               ((ObjectBinder.BoundConfig)value).clear();
            }
         }

         this.dataMap.clear();
      }

      @Override
      public ConfigFormat<?> configFormat() {
         return this.configFormat;
      }

      @Override
      public Config createSubConfig() {
         return new ObjectBinder.BoundConfig(null, new HashMap<>(1), this.configFormat, this.bypassFinal);
      }

      @Override
      public Map<String, Object> valueMap() {
         Function<Object, Object> readConversion = o -> {
            if (o instanceof ObjectBinder.FieldInfos) {
               ObjectBinder.FieldInfos fieldInfos = (ObjectBinder.FieldInfos)o;
               return fieldInfos.boundConfig != null ? fieldInfos.getUpdatedConfig(this.object) : fieldInfos.getValue(this.object);
            } else {
               return o;
            }
         };
         return new TransformingMap<>(this.dataMap, readConversion, o -> (Object)o, o -> o);
      }

      @Override
      public Set<? extends Config.Entry> entrySet() {
         Function<Map.Entry<String, Object>, Config.Entry> readTransfo = entry -> new Config.Entry() {
            @Override
            public <T> T setValue(Object value) {
               return BoundConfig.this.set((String)entry.getKey(), value);
            }

            @Override
            public String getKey() {
               return (String)entry.getKey();
            }

            @Override
            public <T> T getRawValue() {
               return (T)entry.getValue();
            }
         };
         return new TransformingSet<>(this.dataMap.entrySet(), readTransfo, o -> null, o -> o);
      }

      @Override
      public int size() {
         return this.dataMap.size();
      }

      @Override
      public String toString() {
         return "BoundConfig{object=" + this.object + ", dataMap=" + this.dataMap + '}';
      }
   }

   private static final class BoundSearchResult {
      final ObjectBinder.BoundConfig parentConfig;
      final ObjectBinder.FieldInfos fieldInfos;
      final ObjectBinder.BoundConfig subConfig;

      BoundSearchResult(ObjectBinder.BoundConfig parentConfig, Object data) {
         this.parentConfig = parentConfig;
         if (data instanceof ObjectBinder.FieldInfos) {
            this.fieldInfos = (ObjectBinder.FieldInfos)data;
            if (this.fieldInfos.boundConfig == null) {
               this.subConfig = null;
            } else {
               this.subConfig = this.fieldInfos.getUpdatedConfig(parentConfig.object);
            }
         } else {
            this.fieldInfos = null;
            this.subConfig = (ObjectBinder.BoundConfig)data;
         }
      }

      boolean hasFieldInfos() {
         return this.fieldInfos != null;
      }

      boolean hasSubConfig() {
         return this.subConfig != null;
      }
   }

   private static final class EnumValueConverter<T extends Enum<T>> implements Converter<T, Object> {
      private final Class<T> enumType;
      private final EnumGetMethod method;

      EnumValueConverter(Class<T> enumType, EnumGetMethod method) {
         this.enumType = enumType;
         this.method = method;
      }

      public T convertToField(Object value) {
         return this.method.get(value, this.enumType);
      }

      public String convertFromField(T value) {
         return value == null ? null : value.toString();
      }
   }

   private static final class FieldInfos {
      final Field field;
      final ObjectBinder.BoundConfig boundConfig;
      final Converter<Object, Object> converter;

      FieldInfos(Field field, ObjectBinder.BoundConfig boundConfig, Converter<Object, Object> converter) {
         this.field = field;
         this.boundConfig = boundConfig;
         this.converter = converter;
      }

      Object setValue(Object fieldObject, Object value, boolean bypassFinal) {
         if (!bypassFinal && Modifier.isFinal(this.field.getModifiers())) {
            throw new UnsupportedOperationException("Cannot modify the field " + this.field);
         } else {
            try {
               Object previousValue = this.converter.convertFromField(this.field.get(fieldObject));
               Object newValue = this.converter.convertToField(value);
               AnnotationUtils.checkField(this.field, newValue);
               this.field.set(fieldObject, newValue);
               return previousValue;
            } catch (IllegalAccessException var6) {
               throw new ReflectionException("Failed to set field " + this.field, var6);
            }
         }
      }

      Object removeValue(Object fieldObject, boolean bypassFinal) {
         Object previousValue = this.getValue(fieldObject);
         if (this.field.getType().isPrimitive()) {
            this.setValue(fieldObject, (byte)0, bypassFinal);
         } else {
            this.setValue(fieldObject, null, bypassFinal);
            if (this.boundConfig != null) {
               this.boundConfig.clear();
            }
         }

         return previousValue;
      }

      Object getValue(Object fieldObject) {
         try {
            return this.converter.convertFromField(this.field.get(fieldObject));
         } catch (IllegalAccessException var3) {
            throw new ReflectionException("Failed to get field " + this.field, var3);
         }
      }

      ObjectBinder.BoundConfig getUpdatedConfig(Object fieldObject) {
         this.boundConfig.object = this.getValue(fieldObject);
         return this.boundConfig;
      }

      @Override
      public String toString() {
         return "FieldInfos{field=" + this.field + ", boundConfig=" + this.boundConfig + '}';
      }
   }

   private static final class NoOpConverter implements Converter<Object, Object> {
      static final ObjectBinder.NoOpConverter INSTANCE = new ObjectBinder.NoOpConverter();

      @Override
      public Object convertToField(Object value) {
         return value;
      }

      @Override
      public Object convertFromField(Object value) {
         return value;
      }
   }
}

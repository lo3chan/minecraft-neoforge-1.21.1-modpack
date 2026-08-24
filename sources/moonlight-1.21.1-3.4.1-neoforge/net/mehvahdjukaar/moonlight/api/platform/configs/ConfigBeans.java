package net.mehvahdjukaar.moonlight.api.platform.configs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class ConfigBeans {
   static <T> Supplier<T> define(ConfigBuilder builder, Class<T> type, T defaultValue) {
      return type.isRecord() ? defineRecord(builder, type, defaultValue) : definePojo(builder, type, defaultValue);
   }

   private static <T> Supplier<T> definePojo(ConfigBuilder builder, Class<T> type, T defaultValue) {
      List<Field> fields = new ArrayList<>();
      List<Supplier<?>> readers = new ArrayList<>();

      try {
         for (Field f : type.getDeclaredFields()) {
            int mods = f.getModifiers();
            if (!Modifier.isStatic(mods) && !Modifier.isTransient(mods)) {
               f.setAccessible(true);
               fields.add(f);
               readers.add(defineField(builder, f.getName(), f.getType(), f.get(defaultValue)));
            }
         }

         Constructor<T> ctor = type.getDeclaredConstructor();
         ctor.setAccessible(true);
         return () -> {
            try {
               T instance = ctor.newInstance();

               for (int i = 0; i < fields.size(); i++) {
                  fields.get(i).set(instance, readers.get(i).get());
               }

               return instance;
            } catch (ReflectiveOperationException var6) {
               throw new RuntimeException("Failed to build bean " + type.getName(), var6);
            }
         };
      } catch (ReflectiveOperationException var10) {
         throw new RuntimeException("defineBean: " + type.getName() + " needs a no-arg constructor and readable fields", var10);
      }
   }

   private static <T> Supplier<T> defineRecord(ConfigBuilder builder, Class<T> type, T defaultValue) {
      RecordComponent[] comps = type.getRecordComponents();
      List<Supplier<?>> readers = new ArrayList<>();
      Class<?>[] paramTypes = new Class[comps.length];

      try {
         for (int i = 0; i < comps.length; i++) {
            paramTypes[i] = comps[i].getType();
            readers.add(defineField(builder, comps[i].getName(), comps[i].getType(), comps[i].getAccessor().invoke(defaultValue)));
         }

         Constructor<T> ctor = type.getDeclaredConstructor(paramTypes);
         ctor.setAccessible(true);
         return () -> {
            try {
               Object[] args = new Object[readers.size()];

               for (int i = 0; i < args.length; i++) {
                  args[i] = readers.get(i).get();
               }

               return ctor.newInstance(args);
            } catch (ReflectiveOperationException var5x) {
               throw new RuntimeException("Failed to build record " + type.getName(), var5x);
            }
         };
      } catch (ReflectiveOperationException var7) {
         throw new RuntimeException("defineBean: failed to read record " + type.getName(), var7);
      }
   }

   private static Supplier<?> defineField(ConfigBuilder builder, String name, Class<?> type, Object current) {
      if (type == boolean.class || type == Boolean.class) {
         return builder.define(name, (Boolean)current);
      } else if (type == int.class || type == Integer.class) {
         return builder.define(name, (Integer)current, -2147483648, 2147483647);
      } else if (type == double.class || type == Double.class) {
         return builder.define(name, (Double)current, -1.7976931348623157E308, 1.7976931348623157E308);
      } else if (type == float.class || type == Float.class) {
         return builder.define(name, (Float)current, -3.4028235E38F, 3.4028235E38F);
      } else if (type == String.class) {
         return builder.define(name, (String)current);
      } else if (type.isEnum()) {
         return builder.define(name, (Enum)current);
      } else {
         throw new IllegalArgumentException("defineBean: unsupported field type " + type.getName() + " for field '" + name + "'");
      }
   }
}

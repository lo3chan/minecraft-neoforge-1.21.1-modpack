package io.wispforest.owo.util;

import io.wispforest.owo.registration.annotations.AssignedName;
import io.wispforest.owo.registration.annotations.IterationIgnored;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public final class ReflectionUtils {
   private ReflectionUtils() {
   }

   public static <C> C tryInstantiateWithNoArgs(Class<C> clazz) {
      try {
         return clazz.getConstructor().newInstance();
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException var2) {
         throw new RuntimeException(
            (var2 instanceof NoSuchMethodException ? "No zero-args constructor defined on class " : "Could not instantiate class ") + clazz, var2
         );
      }
   }

   public static <C> C instantiate(Constructor<C> constructor, Object... args) {
      try {
         return constructor.newInstance(args);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new RuntimeException("Wrapped object creation failure, look below for reason", var3);
      }
   }

   public static <C> Constructor<C> getNoArgsConstructor(Class<C> clazz) {
      try {
         return clazz.getConstructor();
      } catch (NoSuchMethodException var2) {
         throw new IllegalStateException("Class " + clazz.getName() + " does not declare a zero-args constructor", var2);
      }
   }

   public static <C, F> void iterateAccessibleStaticFields(Class<C> clazz, Class<F> targetFieldType, ReflectionUtils.FieldConsumer<F> fieldConsumer) {
      for (Field field : clazz.getDeclaredFields()) {
         if (Modifier.isStatic(field.getModifiers())) {
            F value;
            try {
               value = (F)field.get(null);
            } catch (IllegalAccessException var9) {
               continue;
            }

            if (value != null && targetFieldType.isAssignableFrom(value.getClass()) && !field.isAnnotationPresent(IterationIgnored.class)) {
               fieldConsumer.accept(value, getFieldName(field), field);
            }
         }
      }
   }

   public static String getFieldName(Field field) {
      String fieldId = field.getName().toLowerCase(Locale.ROOT);
      if (field.isAnnotationPresent(AssignedName.class)) {
         fieldId = field.getAnnotation(AssignedName.class).value();
      }

      return fieldId;
   }

   public static void forApplicableSubclasses(Class<?> parent, Class<?> targetType, Consumer<Class<?>> action) {
      for (Class<?> subclass : parent.getDeclaredClasses()) {
         if (targetType.isAssignableFrom(subclass)) {
            action.accept(subclass);
         }
      }
   }

   public static void requireZeroArgsConstructor(Class<?> clazz, Function<String, String> reasonFormatter) {
      boolean found = false;

      for (Constructor<?> constructor : clazz.getConstructors()) {
         if (constructor.getParameterCount() == 0) {
            found = true;
            break;
         }
      }

      if (!found) {
         throw new IllegalStateException(reasonFormatter.apply(clazz.getName()));
      }
   }

   public static String getCallingClassName(int depth) {
      return StackWalker.getInstance().walk(s -> s.skip(depth).map(StackFrame::getClassName).findFirst()).orElse("<unknown>");
   }

   @Nullable
   public static Class<?> getTypeArgument(Type type, int index) {
      if (type instanceof ParameterizedType parameterizedType) {
         Type[] typeArgs = parameterizedType.getActualTypeArguments();
         if (index > typeArgs.length - 1) {
            return null;
         } else {
            return typeArgs[index] instanceof Class<?> typeClass ? typeClass : null;
         }
      } else {
         return null;
      }
   }

   @FunctionalInterface
   public interface FieldConsumer<F> {
      void accept(F var1, String var2, Field var3);
   }
}

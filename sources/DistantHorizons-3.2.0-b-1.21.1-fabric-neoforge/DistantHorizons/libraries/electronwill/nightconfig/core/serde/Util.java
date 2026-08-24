package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

final class Util {
   private static final IdentityHashMap<Class<?>, Util.TypeAndOrder> PRIMITIVE_TO_WRAPPER = new IdentityHashMap<>();
   private static final IdentityHashMap<Class<?>, Util.TypeAndOrder> WRAPPER_TO_PRIMITIVE = new IdentityHashMap<>();

   static boolean isEmpty(Object configValue) {
      if (configValue instanceof Collection) {
         return ((Collection)configValue).isEmpty();
      } else if (configValue instanceof Map) {
         return ((Map)configValue).isEmpty();
      } else if (configValue instanceof UnmodifiableConfig) {
         return ((UnmodifiableConfig)configValue).isEmpty();
      } else if (configValue instanceof CharSequence) {
         return ((CharSequence)configValue).length() == 0;
      } else if (configValue instanceof Optional) {
         return !((Optional)configValue).isPresent();
      } else {
         if (configValue instanceof Buffer) {
            ((Buffer)configValue).hasRemaining();
         }

         Class<?> cls = configValue.getClass();
         return cls.isArray() ? Array.getLength(configValue) == 0 : isEmptyWithReflection(cls, configValue);
      }
   }

   private static boolean isEmptyWithReflection(Class<?> cls, Object configValue) {
      Util.AdditionalEmptyables.EmptyableClass scalaIterableOnce = Util.AdditionalEmptyables.scalaIterableOnce;
      if (scalaIterableOnce != null && scalaIterableOnce.isInstance(cls)) {
         return scalaIterableOnce.isEmpty(configValue);
      } else {
         Util.AdditionalEmptyables.EmptyableClass kotlinCollection = Util.AdditionalEmptyables.kotlinCollection;
         if (kotlinCollection != null && kotlinCollection.isInstance(cls)) {
            return kotlinCollection.isEmpty(configValue);
         } else {
            Method isEmptyMethod;
            try {
               isEmptyMethod = cls.getMethod("isEmpty");
            } catch (SecurityException | NoSuchMethodException var7) {
               return false;
            }

            if (isEmptyMethod.getReturnType() != boolean.class) {
               return false;
            } else {
               try {
                  return (Boolean)isEmptyMethod.invoke(configValue);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var6) {
                  throw new SerdeException("Exception during call to isEmpty() on " + configValue, var6);
               }
            }
         }
      }
   }

   static boolean canAssign(Class<?> fieldType, Class<?> valueType) {
      if (valueType == null) {
         return !fieldType.isPrimitive();
      } else if (!fieldType.isPrimitive() && !valueType.isPrimitive()) {
         return fieldType.isAssignableFrom(valueType);
      } else {
         Util.TypeAndOrder a = fieldType.isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(fieldType) : WRAPPER_TO_PRIMITIVE.get(fieldType);
         Util.TypeAndOrder b = valueType.isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(valueType) : WRAPPER_TO_PRIMITIVE.get(valueType);
         return a != null && b != null && a.canAssignValue(b);
      }
   }

   static boolean isPrimitiveOrWrapper(Class<?> type) {
      return type.isPrimitive() || WRAPPER_TO_PRIMITIVE.get(type) != null;
   }

   static boolean isPrimitiveOrWrapperNumber(Class<?> type) {
      return isPrimitiveOrWrapper(type) && type != Boolean.class && type != boolean.class && type != Character.class && type != char.class;
   }

   static void addPrimitiveAndWrapper(Class<?> primitiveType, Class<?> wrapperType) {
      PRIMITIVE_TO_WRAPPER.put(primitiveType, new Util.TypeAndOrder(PRIMITIVE_TO_WRAPPER.size(), wrapperType));
      WRAPPER_TO_PRIMITIVE.put(wrapperType, new Util.TypeAndOrder(WRAPPER_TO_PRIMITIVE.size(), primitiveType));
   }

   static {
      addPrimitiveAndWrapper(boolean.class, Boolean.class);
      addPrimitiveAndWrapper(byte.class, Byte.class);
      addPrimitiveAndWrapper(short.class, Short.class);
      addPrimitiveAndWrapper(char.class, Character.class);
      addPrimitiveAndWrapper(int.class, Integer.class);
      addPrimitiveAndWrapper(long.class, Long.class);
      addPrimitiveAndWrapper(float.class, Float.class);
      addPrimitiveAndWrapper(double.class, Double.class);
   }

   private static final class AdditionalEmptyables {
      static final Util.AdditionalEmptyables.EmptyableClass scalaIterableOnce = classOrNull("scala.collection.IterableOnce");
      static final Util.AdditionalEmptyables.EmptyableClass kotlinCollection = classOrNull("kotlin.collections.Collection");

      private static Util.AdditionalEmptyables.EmptyableClass classOrNull(String fullName) {
         Class<?> cls;
         try {
            cls = Class.forName(fullName);
         } catch (ClassNotFoundException var5) {
            return null;
         }

         Method m;
         try {
            m = cls.getMethod("isEmpty");
         } catch (SecurityException | NoSuchMethodException var4) {
            return null;
         }

         return m.getReturnType() != boolean.class ? null : new Util.AdditionalEmptyables.EmptyableClass(cls, m);
      }

      private static class EmptyableClass {
         final Class<?> cls;
         final Method isEmptyMethod;

         EmptyableClass(Class<?> cls, Method isEmptyMethod) {
            this.cls = cls;
            this.isEmptyMethod = isEmptyMethod;
         }

         boolean isInstance(Class<?> instanceClass) {
            return this.cls.isAssignableFrom(instanceClass);
         }

         boolean isEmpty(Object instance) {
            try {
               return (Boolean)this.isEmptyMethod.invoke(instance);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
               throw new SerdeException("Exception during call to isEmpty() on " + instance, var3);
            }
         }
      }
   }

   private static final class TypeAndOrder {
      final int order;
      final Class<?> type;

      TypeAndOrder(int order, Class<?> type) {
         this.order = order;
         this.type = type;
      }

      boolean canAssignValue(Util.TypeAndOrder valueType) {
         if (this.order == 0) {
            return valueType.order == 0;
         } else {
            return valueType.order == 0 ? false : this.order >= valueType.order;
         }
      }

      @Override
      public String toString() {
         return "TypeAndOrder [order=" + this.order + ", type=" + this.type + "]";
      }
   }
}

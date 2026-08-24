package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.EnumGetMethod;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Deprecated
public final class ObjectConverter {
   private final boolean bypassTransient;
   private final boolean bypassFinal;

   public ObjectConverter(boolean bypassTransient, boolean bypassFinal) {
      this.bypassTransient = bypassTransient;
      this.bypassFinal = bypassFinal;
   }

   /** @deprecated */
   public ObjectConverter() {
      this(false, true);
   }

   public void toConfig(Object o, Config destination) {
      Objects.requireNonNull(o, "The object must not be null.");
      Objects.requireNonNull(destination, "The config must not be null.");
      Class<?> clazz = o.getClass();
      List<String> annotatedPath = AnnotationUtils.getPath(clazz);
      if (annotatedPath != null) {
         destination = destination.getRaw(annotatedPath);
      }

      this.convertToConfig(o, clazz, destination);
   }

   public void toConfig(Class<?> clazz, Config destination) {
      Objects.requireNonNull(destination, "The config must not be null.");
      List<String> annotatedPath = AnnotationUtils.getPath(clazz);
      if (annotatedPath != null) {
         destination = destination.getRaw(annotatedPath);
      }

      this.convertToConfig(null, clazz, destination);
   }

   /** @deprecated */
   public <C extends Config> C toConfig(Object o, Supplier<C> destinationSupplier) {
      C destination = (C)destinationSupplier.get();
      this.toConfig(o, destination);
      return destination;
   }

   public <C extends Config> C toConfig(Class<?> clazz, Supplier<C> destinationSupplier) {
      C destination = (C)destinationSupplier.get();
      this.toConfig(clazz, destination);
      return destination;
   }

   public void toObject(UnmodifiableConfig config, Object destination) {
      Objects.requireNonNull(config, "The config must not be null.");
      Objects.requireNonNull(destination, "The object must not be null.");
      Class<?> clazz = destination.getClass();
      List<String> annotatedPath = AnnotationUtils.getPath(clazz);
      if (annotatedPath != null) {
         config = config.getRaw(annotatedPath);
      }

      this.convertToObject(config, destination, clazz);
   }

   /** @deprecated */
   public <O> O toObject(UnmodifiableConfig config, Supplier<O> destinationSupplier) {
      O destination = destinationSupplier.get();
      this.toObject(config, destination);
      return destination;
   }

   private void convertToConfig(Object object, Class<?> clazz, Config destination) {
      while (clazz != Object.class) {
         for (Field field : clazz.getDeclaredFields()) {
            int fieldModifiers = field.getModifiers();
            if ((object == null || !Modifier.isStatic(fieldModifiers)) && (this.bypassTransient || !Modifier.isTransient(fieldModifiers))) {
               if (!field.isAccessible()) {
                  field.setAccessible(true);
               }

               Object value;
               try {
                  value = field.get(object);
               } catch (IllegalAccessException var17) {
                  throw new ReflectionException("Unable to parse the field " + field, var17);
               }

               AnnotationUtils.checkField(field, value);
               Converter<Object, Object> converter = AnnotationUtils.getConverter(field);
               if (converter != null) {
                  value = converter.convertFromField(value);
               }

               List<String> path = AnnotationUtils.getPath(field);
               ConfigFormat<?> format = destination.configFormat();
               if (value == null) {
                  destination.set(path, null);
               } else {
                  Class<?> valueType = value.getClass();
                  if (Enum.class.isAssignableFrom(valueType)) {
                     if (destination.configFormat().supportsType(Enum.class)) {
                        destination.set(path, value);
                     } else {
                        destination.set(path, value.toString());
                     }
                  } else if (field.isAnnotationPresent(ForceBreakdown.class) || !format.supportsType(valueType)) {
                     destination.set(path, value);
                     Config converted = destination.createSubConfig();
                     this.convertToConfig(value, valueType, converted);
                     destination.set(path, converted);
                  } else if (value instanceof Collection) {
                     Collection<?> src = (Collection<?>)value;
                     Class<?> bottomType = this.bottomElementType(src);
                     if (format.supportsType(bottomType)) {
                        destination.set(path, value);
                     } else {
                        Collection<Object> dst = new ArrayList<>(src.size());
                        this.convertObjectsToConfigs(src, bottomType, dst, destination);
                        destination.set(path, dst);
                     }
                  } else {
                     destination.set(path, value);
                  }
               }
            }
         }

         clazz = clazz.getSuperclass();
      }
   }

   private void convertToObject(UnmodifiableConfig config, Object object, Class<?> clazz) {
      while (clazz != Object.class) {
         for (Field field : clazz.getDeclaredFields()) {
            int fieldModifiers = field.getModifiers();
            if ((object != null || !Modifier.isStatic(fieldModifiers)) && (this.bypassFinal || !Modifier.isFinal(fieldModifiers))) {
               field.setAccessible(true);
               if (this.bypassTransient || !Modifier.isTransient(fieldModifiers)) {
                  List<String> path = AnnotationUtils.getPath(field);
                  Object value = config.get(path);
                  Converter<Object, Object> converter = AnnotationUtils.getConverter(field);
                  if (converter != null) {
                     value = converter.convertToField(value);
                  }

                  Class<?> fieldType = field.getType();

                  try {
                     if (value instanceof UnmodifiableConfig && !fieldType.isAssignableFrom(value.getClass())) {
                        UnmodifiableConfig cfg = (UnmodifiableConfig)value;
                        Object fieldValue = field.get(object);
                        if (fieldValue == null) {
                           fieldValue = this.createInstance(fieldType);
                           field.set(object, fieldValue);
                           this.convertToObject(cfg, fieldValue, field.getType());
                        } else {
                           this.convertToObject(cfg, fieldValue, field.getType());
                        }
                     } else if (value instanceof Collection && Collection.class.isAssignableFrom(fieldType)) {
                        Collection<?> src = (Collection<?>)value;
                        Class<?> srcBottomType = this.bottomElementType(src);
                        ParameterizedType genericType = (ParameterizedType)field.getGenericType();
                        List<Class<?>> dstTypes = this.elementTypes(genericType);
                        Class<?> dstBottomType = dstTypes.get(dstTypes.size() - 1);
                        if (srcBottomType != null && dstBottomType != null && !dstBottomType.isAssignableFrom(srcBottomType)) {
                           Collection<Object> dst = (Collection<Object>)field.get(object);
                           if (dst == null) {
                              if (fieldType != ArrayList.class && !fieldType.isInterface() && !Modifier.isAbstract(fieldType.getModifiers())) {
                                 dst = this.createInstance((Class<Collection<Object>>)fieldType);
                              } else {
                                 dst = new ArrayList<>(src.size());
                              }

                              field.set(object, dst);
                           }

                           this.convertConfigsToObject(src, dst, dstTypes, 0);
                           AnnotationUtils.checkField(field, dst);
                        } else {
                           AnnotationUtils.checkField(field, value);
                           field.set(object, value);
                        }
                     } else if (value == null && AnnotationUtils.mustPreserve(field, clazz)) {
                        AnnotationUtils.checkField(field, field.get(object));
                     } else {
                        AnnotationUtils.checkField(field, value);
                        if (field.getType().isEnum()) {
                           Class<? extends Enum> enumType = (Class<? extends Enum>)field.getType();
                           SpecEnum specEnum = field.getAnnotation(SpecEnum.class);
                           EnumGetMethod method = specEnum == null ? EnumGetMethod.NAME_IGNORECASE : specEnum.method();
                           field.set(object, method.get(value, enumType));
                        } else {
                           field.set(object, value);
                        }
                     }
                  } catch (ReflectiveOperationException var19) {
                     throw new ReflectionException("Unable to work with field " + field, var19);
                  }
               }
            }
         }

         clazz = clazz.getSuperclass();
      }
   }

   private Class<?> bottomElementType(ParameterizedType genericType) {
      if (genericType != null && genericType.getActualTypeArguments().length > 0) {
         Type parameter = genericType.getActualTypeArguments()[0];
         if (parameter instanceof ParameterizedType) {
            ParameterizedType genericParameter = (ParameterizedType)parameter;
            Class<?> paramClass = (Class<?>)genericParameter.getRawType();
            if (paramClass.isAssignableFrom(Collection.class)) {
               return this.bottomElementType(genericParameter);
            }

            return paramClass;
         }

         if (parameter instanceof Class) {
            return (Class<?>)parameter;
         }
      }

      return null;
   }

   private void detectElementTypes(ParameterizedType genericType, List<Class<?>> storage) {
      if (genericType != null && genericType.getActualTypeArguments().length > 0) {
         Type parameter = genericType.getActualTypeArguments()[0];
         if (parameter instanceof ParameterizedType) {
            ParameterizedType genericParameter = (ParameterizedType)parameter;
            Class<?> paramClass = (Class<?>)genericParameter.getRawType();
            storage.add(paramClass);
            if (Collection.class.isAssignableFrom(paramClass)) {
               this.detectElementTypes(genericParameter, storage);
            }
         } else if (parameter instanceof Class) {
            storage.add((Class<?>)parameter);
         }
      }
   }

   private List<Class<?>> elementTypes(ParameterizedType genericType) {
      List<Class<?>> storage = new ArrayList<>();
      this.detectElementTypes(genericType, storage);
      return storage;
   }

   private Class<?> bottomElementType(Collection<?> list) {
      for (Object elem : list) {
         if (elem instanceof Collection) {
            return this.bottomElementType((Collection<?>)elem);
         }

         if (elem != null) {
            return elem.getClass();
         }
      }

      return null;
   }

   private void convertConfigsToObject(Collection<?> src, Collection<Object> dst, List<Class<?>> dstElementTypes, int currentLevel) {
      Class<?> currentType = dstElementTypes.get(currentLevel);

      for (Object elem : src) {
         if (elem == null) {
            dst.add(null);
         } else if (!(elem instanceof Collection)) {
            if (!(elem instanceof UnmodifiableConfig)) {
               String elemType = elem.getClass().toString();
               throw new InvalidValueException("Unexpected element of type " + elemType + " in collection of objects");
            }

            Object elementObj = this.createInstance(currentType);
            this.convertToObject((UnmodifiableConfig)elem, elementObj, currentType);
            dst.add(elementObj);
         } else {
            Collection<?> subSrc = (Collection<?>)elem;
            Collection<Object> subDst;
            if (currentType != ArrayList.class && !currentType.isInterface() && !Modifier.isAbstract(currentType.getModifiers())) {
               subDst = this.createInstance((Class<Collection<Object>>)currentType);
            } else {
               subDst = new ArrayList<>();
            }

            this.convertConfigsToObject(subSrc, subDst, dstElementTypes, currentLevel + 1);
            dst.add(subDst);
         }
      }
   }

   private void convertObjectsToConfigs(Collection<?> src, Class<?> srcBottomType, Collection<Object> dst, Config parentConfig) {
      for (Object elem : src) {
         if (elem == null) {
            dst.add(null);
         } else if (srcBottomType.isAssignableFrom(elem.getClass())) {
            Config elementConfig = parentConfig.createSubConfig();
            this.convertToConfig(elem, elem.getClass(), elementConfig);
            dst.add(elementConfig);
         } else {
            if (!(elem instanceof Collection)) {
               String elemType = elem.getClass().toString();
               throw new InvalidValueException("Unexpected element of type " + elemType + " in collection of " + srcBottomType);
            }

            ArrayList<Object> subList = new ArrayList<>();
            this.convertObjectsToConfigs((Collection<?>)elem, srcBottomType, subList, parentConfig);
            subList.trimToSize();
            dst.add(subList);
         }
      }
   }

   private <T> T createInstance(Class<T> tClass) {
      try {
         Constructor<T> ctor = tClass.getDeclaredConstructor();
         if (!ctor.isAccessible()) {
            ctor.setAccessible(true);
         }

         return ctor.newInstance();
      } catch (ReflectiveOperationException var3) {
         throw new ReflectionException("Unable to create an instance of " + tClass, var3);
      }
   }
}

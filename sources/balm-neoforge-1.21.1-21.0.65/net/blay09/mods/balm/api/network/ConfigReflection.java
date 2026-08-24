package net.blay09.mods.balm.api.network;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.ExpectedType;
import net.blay09.mods.balm.api.config.IgnoreConfig;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.LoadedReflectionConfig;
import net.blay09.mods.balm.api.config.reflection.NestedType;
import net.blay09.mods.balm.api.config.reflection.Synced;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategoryBuilder;
import net.blay09.mods.balm.api.config.schema.builder.ConfigPropertyBuilder;
import net.blay09.mods.balm.api.config.schema.builder.PropertyHolderBuilder;
import net.blay09.mods.balm.api.config.schema.impl.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;

public class ConfigReflection {
   public static BalmConfigSchema schemaOf(Class<?> configDataClass) {
      List<Field> rootFields = getAllFields(configDataClass);
      List<Field> rootDataFields = rootFields.stream().filter(it -> !isCategoryField(it)).toList();
      ResourceLocation identifier = getIdentifier(configDataClass);
      ConfigSchemaImpl schema = BalmConfigSchema.create(identifier);
      buildFieldsIntoSchema(schema, configDataClass, rootDataFields);

      for (Field categoryField : rootFields.stream().filter(ConfigReflection::isCategoryField).toList()) {
         List<Field> fields = getAllFields(categoryField.getType());
         ConfigCategoryBuilder category = schema.category(categoryField.getName());
         Comment commentAnnotation = categoryField.getAnnotation(Comment.class);
         if (commentAnnotation != null) {
            category.comment(commentAnnotation.value());
         } else {
            net.blay09.mods.balm.api.config.Comment legacyCommentAnnotation = categoryField.getAnnotation(net.blay09.mods.balm.api.config.Comment.class);
            if (legacyCommentAnnotation != null) {
               category.comment(legacyCommentAnnotation.value());
            }
         }

         buildFieldsIntoSchema(category, categoryField.getType(), fields);
      }

      return schema;
   }

   private static void buildFieldsIntoSchema(PropertyHolderBuilder builder, Class<?> clazz, List<Field> fields) {
      Object defaults = createInstance(clazz);

      for (Field field : fields) {
         ConfigPropertyBuilder property = builder.property(field.getName());
         Comment commentAnnotation = field.getAnnotation(Comment.class);
         if (commentAnnotation != null) {
            property.comment(commentAnnotation.value());
         } else {
            net.blay09.mods.balm.api.config.Comment legacyCommentAnnotation = field.getAnnotation(net.blay09.mods.balm.api.config.Comment.class);
            if (legacyCommentAnnotation != null) {
               property.comment(legacyCommentAnnotation.value());
            }
         }

         if (field.getAnnotation(Synced.class) != null || field.getAnnotation(net.blay09.mods.balm.api.config.Synced.class) != null) {
            property.synced();
         }

         Class<?> type = field.getType();
         Class<?> nestedType = null;
         NestedType nestedTypeAnnotation = field.getAnnotation(NestedType.class);
         if (nestedTypeAnnotation != null) {
            nestedType = nestedTypeAnnotation.value();
         } else {
            ExpectedType legacyNestedTypeAnnotation = field.getAnnotation(ExpectedType.class);
            if (legacyNestedTypeAnnotation != null) {
               nestedType = legacyNestedTypeAnnotation.value();
            }
         }

         try {
            Object defaultValue = field.get(defaults);
            if (type == String.class) {
               property.stringOf((String)defaultValue);
            } else if (type == ResourceLocation.class) {
               property.resourceLocationOf((ResourceLocation)defaultValue);
            } else if (type == Integer.class || type == int.class) {
               property.intOf((Integer)defaultValue);
            } else if (type == Long.class || type == long.class) {
               property.longOf((Long)defaultValue);
            } else if (type == Float.class || type == float.class) {
               property.floatOf((Float)defaultValue);
            } else if (type == Double.class || type == double.class) {
               property.doubleOf((Double)defaultValue);
            } else if (type == Boolean.class || type == boolean.class) {
               property.boolOf((Boolean)defaultValue);
            } else if (type.isEnum()) {
               propertyOfEnum(property, defaultValue);
            } else if (List.class.isAssignableFrom(type)) {
               if (nestedType == null) {
                  throw new IllegalArgumentException("List field " + field.getName() + " in class " + clazz.getName() + " is missing @NestedType annotation");
               }

               List listValue = (List)defaultValue;
               property.listOf(nestedType, listValue);
            } else {
               if (!Set.class.isAssignableFrom(type)) {
                  throw new IllegalArgumentException("Unsupported config field type " + type.getName() + " in class " + clazz.getName());
               }

               if (nestedType == null) {
                  throw new IllegalArgumentException("Set field " + field.getName() + " in class " + clazz.getName() + " is missing @NestedType annotation");
               }

               Set setValue = (Set)defaultValue;
               property.setOf(nestedType, setValue);
            }
         } catch (IllegalAccessException var13) {
            throw new RuntimeException("Error accessing config field " + field.getName() + " in class " + clazz.getName(), var13);
         }
      }
   }

   private static <T extends Enum<T>> void propertyOfEnum(ConfigPropertyBuilder property, Object obj) {
      if (obj == null) {
         throw new IllegalArgumentException("Object cannot be null");
      } else if (!(obj instanceof T enumValue)) {
         throw new IllegalArgumentException("Object must be an Enum");
      } else {
         property.enumOf(enumValue);
      }
   }

   private static <T> T createInstance(Class<T> clazz) {
      try {
         return clazz.getConstructor().newInstance();
      } catch (IllegalAccessException | NoSuchMethodException | InstantiationException var2) {
         throw new IllegalArgumentException("Config class " + clazz.getName() + " must have a public no-arg constructor.", var2);
      } catch (InvocationTargetException var3) {
         throw new RuntimeException("Error instantiating config class " + clazz.getName(), var3);
      }
   }

   private static boolean isConfigDataField(Field field) {
      return !Modifier.isFinal(field.getModifiers())
         && !Modifier.isStatic(field.getModifiers())
         && field.getAnnotation(IgnoreConfig.class) == null
         && field.getAnnotation(net.blay09.mods.balm.api.config.reflection.IgnoreConfig.class) == null;
   }

   public static List<Field> getAllFields(Class<?> clazz) {
      return Arrays.stream(clazz.getFields()).filter(ConfigReflection::isConfigDataField).toList();
   }

   private static boolean isCategoryField(Field field) {
      return !field.getType().isPrimitive()
         && !field.getType().isEnum()
         && field.getType() != String.class
         && field.getType() != List.class
         && field.getType() != Set.class
         && field.getType() != ResourceLocation.class;
   }

   public static ResourceLocation getIdentifier(Class<?> configDataClass) {
      Config configAnnotation = configDataClass.getAnnotation(Config.class);
      if (configAnnotation == null) {
         net.blay09.mods.balm.api.config.Config legacyConfigAnnotation = configDataClass.getAnnotation(net.blay09.mods.balm.api.config.Config.class);
         if (legacyConfigAnnotation == null) {
            throw new IllegalArgumentException("Class " + configDataClass.getName() + " is missing a @Config annotation");
         } else {
            return ResourceLocation.fromNamespaceAndPath(legacyConfigAnnotation.value(), "common");
         }
      } else {
         return ResourceLocation.fromNamespaceAndPath(configAnnotation.value(), configAnnotation.type());
      }
   }

   public static <T> LoadedReflectionConfig<T> of(Class<T> configDataClass, LoadedConfig loadedConfig) {
      T instance = createInstance(configDataClass);
      BalmConfigSchema schema = Balm.getConfig().getSchema(configDataClass);
      LoadedReflectionConfig<T> config = new LoadedReflectionConfig<>(instance);
      config.applyFrom(schema, loadedConfig);
      return config;
   }

   @Deprecated
   public static List<Field> getSyncedFields(Class<?> clazz) {
      List<Field> syncedFields = new ArrayList<>();
      Field[] fields = clazz.getFields();

      for (Field field : fields) {
         if (isSyncedFieldOrObject(field) && isConfigDataField(field)) {
            syncedFields.add(field);
         }
      }

      return syncedFields;
   }

   @Deprecated
   public static boolean isSyncedFieldOrObject(Field field) {
      boolean hasSyncedAnnotation = field.getAnnotation(Synced.class) != null;
      boolean isObject = !field.getType().isPrimitive()
         && !field.getType().isEnum()
         && field.getType() != String.class
         && field.getType() != List.class
         && field.getType() != Set.class
         && field.getType() != ResourceLocation.class;
      return hasSyncedAnnotation || isObject;
   }

   @Deprecated
   public static Object deepCopy(Object from, Object to) {
      Field[] fields = from.getClass().getFields();

      for (Field field : fields) {
         if (isConfigDataField(field)) {
            Class<?> type = field.getType();

            try {
               if (String.class.isAssignableFrom(type)
                  || ResourceLocation.class.isAssignableFrom(type)
                  || Enum.class.isAssignableFrom(type)
                  || type.isPrimitive()) {
                  field.set(to, field.get(from));
               } else if (List.class.isAssignableFrom(type)) {
                  field.set(to, new ArrayList((Collection)field.get(from)));
               } else if (Set.class.isAssignableFrom(type)) {
                  field.set(to, new HashSet((Collection)field.get(from)));
               } else {
                  field.set(to, deepCopy(field.get(from), field.get(to)));
               }
            } catch (IllegalAccessException var9) {
               throw new IllegalStateException(var9);
            }
         }
      }

      return to;
   }
}

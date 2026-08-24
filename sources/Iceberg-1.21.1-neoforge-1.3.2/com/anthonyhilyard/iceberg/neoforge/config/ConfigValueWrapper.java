package com.anthonyhilyard.iceberg.neoforge.config;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.util.UnsafeUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ConfigValueWrapper<T, S extends ConfigValue<T>> implements Supplier<T> {
   private final S configValue;
   private static Map<Class<?>, Field> pathFields = Maps.newHashMap();
   private static Map<Class<?>, Field> defaultSupplierFields = Maps.newHashMap();
   private static Map<Class<?>, Field> cachedValueFields = Maps.newHashMap();
   private static Map<Class<?>, Field> specFields = Maps.newHashMap();

   public ConfigValueWrapper(S configValue) {
      this.configValue = configValue;
   }

   private static Field findField(Class<?> startClass, String fieldName) {
      for (Class<?> currentClass = startClass; currentClass != null; currentClass = currentClass.getSuperclass()) {
         try {
            Field field = currentClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException var4) {
         }
      }

      return null;
   }

   private <U extends Enum<U>> U getRawEnum(Config config, List<String> path, Class<U> clazz, Supplier<U> defaultSupplier) {
      return (U)config.getEnumOrElse(path, clazz, EnumGetMethod.NAME_IGNORECASE, defaultSupplier);
   }

   @Override
   public T get() {
      Class<?> configValueClass = this.configValue.getClass();
      if (!pathFields.containsKey(configValueClass)) {
         try {
            pathFields.put(configValueClass, findField(configValueClass, "path"));
            defaultSupplierFields.put(configValueClass, findField(configValueClass, "defaultSupplier"));
            cachedValueFields.put(configValueClass, findField(configValueClass, "cachedValue"));
            specFields.put(configValueClass, findField(configValueClass, "spec"));
         } catch (Exception var8) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var8));
         }
      }

      if (pathFields.containsKey(configValueClass)) {
         try {
            T cachedValue = (T)cachedValueFields.get(configValueClass).get(this.configValue);
            if (cachedValue == null) {
               NeoForgeIcebergConfigSpec spec = UnsafeUtil.getField(specFields.get(configValueClass), this.configValue);
               Preconditions.checkState(spec != null, "Cannot get config value before spec is built.");
               Preconditions.checkState(spec.loadedConfig() != null, "Cannot get config value before config is loaded.");
               List<String> path = UnsafeUtil.getField(pathFields.get(configValueClass), this.configValue);
               Supplier<T> defaultSupplier = UnsafeUtil.getField(defaultSupplierFields.get(configValueClass), this.configValue);
               Object value = spec.loadedConfig().config().getOrElse(path, defaultSupplier);
               if (configValueClass == EnumValue.class) {
                  Field clazzField = EnumValue.class.getDeclaredField("clazz");
                  clazzField.setAccessible(true);
                  value = this.getRawEnum(spec.loadedConfig().config(), path, UnsafeUtil.getField(clazzField, this.configValue), defaultSupplier);
               }

               if (value instanceof Number number) {
                  if (configValueClass == IntValue.class) {
                     value = number.intValue();
                  } else if (configValueClass == LongValue.class) {
                     value = number.longValue();
                  } else if (configValueClass == DoubleValue.class) {
                     value = number.doubleValue();
                  }
               }

               cachedValue = (T)value;
               UnsafeUtil.setField(cachedValueFields.get(configValueClass), this.configValue, value);
            }

            return cachedValue;
         } catch (Exception var9) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var9));
         }
      }

      Preconditions.checkState(false);
      return null;
   }
}

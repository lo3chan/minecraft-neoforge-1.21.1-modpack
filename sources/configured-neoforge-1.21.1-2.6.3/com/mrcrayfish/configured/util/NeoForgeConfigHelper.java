package com.mrcrayfish.configured.util;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.mrcrayfish.configured.Constants;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class NeoForgeConfigHelper {
   private static final Method MOD_CONFIG_OPEN_CONFIG = ObfuscationReflectionHelper.findMethod(
      ConfigTracker.class, "openConfig", new Class[]{ModConfig.class, Path.class, Path.class}
   );
   private static final Method MOD_CONFIG_CLOSE_CONFIG = ObfuscationReflectionHelper.findMethod(
      ConfigTracker.class, "unloadConfig", new Class[]{ModConfig.class}
   );
   private static final Field MOD_CONFIG_LOADED_CONFIG = ObfuscationReflectionHelper.findField(ModConfig.class, "loadedConfig");
   private static final Field MOD_CONFIG_LOCK = ObfuscationReflectionHelper.findField(ModConfig.class, "lock");

   public static List<Pair<ConfigValue<?>, ValueSpec>> gatherAllConfigValues(UnmodifiableConfig config, ModConfigSpec spec) {
      List<Pair<ConfigValue<?>, ValueSpec>> values = new ArrayList<>();
      gatherValuesFromModConfig(config, spec, values);
      return ImmutableList.copyOf(values);
   }

   private static void gatherValuesFromModConfig(UnmodifiableConfig config, ModConfigSpec spec, List<Pair<ConfigValue<?>, ValueSpec>> values) {
      config.valueMap().forEach((s, o) -> {
         if (o instanceof AbstractConfig) {
            gatherValuesFromModConfig((UnmodifiableConfig)o, spec, values);
         } else if (o instanceof ConfigValue<?> configValue) {
            ValueSpec valueSpec = (ValueSpec)spec.getSpec().getRaw(configValue.getPath());
            values.add(Pair.of(configValue, valueSpec));
         }
      });
   }

   @Nullable
   public static ILoadedConfig getLoadedConfig(ModConfig config) {
      try {
         return (ILoadedConfig)MOD_CONFIG_LOADED_CONFIG.get(config);
      } catch (IllegalAccessException var2) {
         throw new RuntimeException(var2);
      }
   }

   @Nullable
   public static CommentedConfig getConfigData(ModConfig config) {
      ILoadedConfig data = getLoadedConfig(config);
      return data != null ? data.config() : null;
   }

   private static void wrapLock(ModConfig config, Runnable task) {
      Lock lock;
      try {
         lock = (Lock)MOD_CONFIG_LOCK.get(config);
      } catch (IllegalAccessException var8) {
         Constants.LOG.error("Failed to acquire lock when setting config data");
         return;
      }

      lock.lock();

      try {
         task.run();
      } finally {
         lock.unlock();
      }
   }

   public static void setConfigData(ModConfig config, @Nullable CommentedConfig configData) {
      ILoadedConfig loadedConfig = getLoadedConfig(config);
      if (loadedConfig != null) {
         CommentedConfig data = loadedConfig.config();
         wrapLock(config, () -> {
            data.putAll(configData);
            correctConfig(config, data);
         });
         resetConfigCache(config);
         loadedConfig.save();
      }
   }

   @Nullable
   public static ModConfig getModConfig(String fileName) {
      ConcurrentHashMap<String, ModConfig> configMap = (ConcurrentHashMap<String, ModConfig>)ObfuscationReflectionHelper.getPrivateValue(
         ConfigTracker.class, ConfigTracker.INSTANCE, "fileMap"
      );
      return configMap != null ? configMap.get(fileName) : null;
   }

   public static List<Pair<ConfigValue<?>, ValueSpec>> gatherAllConfigValues(ModConfig config) {
      return gatherAllConfigValues(((ModConfigSpec)config.getSpec()).getValues(), (ModConfigSpec)config.getSpec());
   }

   public static void closeConfig(ModConfig config) {
      try {
         MOD_CONFIG_CLOSE_CONFIG.invoke(ConfigTracker.INSTANCE, config);
      } catch (IllegalAccessException | InvocationTargetException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void openConfig(ModConfig config, Path path) {
      try {
         MOD_CONFIG_OPEN_CONFIG.invoke(ConfigTracker.INSTANCE, config, path, null);
      } catch (IllegalAccessException | InvocationTargetException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void saveConfig(ModConfig config) {
      ILoadedConfig loadedConfig = getLoadedConfig(config);
      if (loadedConfig != null) {
         loadedConfig.save();
      }
   }

   public static void resetConfigCache(ModConfig config) {
      if (config.getSpec() instanceof ModConfigSpec spec) {
         spec.afterReload();
      }
   }

   public static void correctConfig(ModConfig config, CommentedConfig data) {
      IConfigSpec spec = config.getSpec();
      if (!spec.isCorrect(data)) {
         spec.correct(data);
      }
   }

   static {
      MOD_CONFIG_OPEN_CONFIG.setAccessible(true);
      MOD_CONFIG_CLOSE_CONFIG.setAccessible(true);
      MOD_CONFIG_LOADED_CONFIG.setAccessible(true);
      MOD_CONFIG_LOCK.setAccessible(true);
   }
}

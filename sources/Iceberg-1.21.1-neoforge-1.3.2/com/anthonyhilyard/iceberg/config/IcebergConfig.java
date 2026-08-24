package com.anthonyhilyard.iceberg.config;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.events.common.ConfigEvents;
import com.anthonyhilyard.iceberg.services.IIcebergConfigSpecBuilder;
import com.anthonyhilyard.iceberg.services.Services;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public abstract class IcebergConfig<T extends IcebergConfig<?>> {
   private static final Map<String, IIcebergConfigSpec> configSpecs = Maps.newHashMap();
   protected static final Map<String, IcebergConfig<?>> configInstances = Maps.newHashMap();
   private static final Set<Class<?>> registeredClasses = Sets.newHashSet();
   private String modId = null;
   private static Method registerConfig = null;

   protected void onLoad() {
   }

   protected void onReload() {
   }

   protected final void onLoadEvent(String loadingModId) {
      if (this.modId != null && configInstances.containsKey(loadingModId) && loadingModId.contentEquals(this.modId)) {
         this.onLoad();
      }
   }

   protected final void onReloadEvent(String reloadingModId) {
      if (this.modId != null && configInstances.containsKey(reloadingModId) && reloadingModId.contentEquals(this.modId)) {
         this.onReload();
      }
   }

   public boolean isLoaded() {
      return configSpecs.containsKey(this.modId) && configSpecs.get(this.modId).isLoaded();
   }

   public static final synchronized boolean register(Class<? extends IcebergConfig<?>> subClass, @NotNull String modId) {
      if (registeredClasses.contains(subClass)) {
         Iceberg.LOGGER.warn("Failed to register configuration: " + subClass.getName() + " is already registered!");
         return false;
      } else {
         if (ConfigEvents.REGISTER.listenerCount() == 0) {
            String spec = Services.getPlatformHelper().getPlatformName();

            String specPair = switch (spec) {
               case "Fabric" -> "com.anthonyhilyard.iceberg.fabric.config.ConfigRegistrar";
               case "Forge" -> "com.anthonyhilyard.iceberg.forge.IcebergForge";
               case "NeoForge" -> "com.anthonyhilyard.iceberg.neoforge.IcebergNeoForge";
               default -> throw new IllegalStateException("Unable to determine modloader when registering config!");
            };

            try {
               registerConfig = Class.forName(specPair).getDeclaredMethod("registerConfig", Class.class, IIcebergConfigSpec.class, String.class);
               registerConfig.setAccessible(true);
            } catch (Exception var5) {
               Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var5));
            }

            Preconditions.checkNotNull(registerConfig);
            ConfigEvents.REGISTER.register((clazz, specx, modid) -> {
               if (registerConfig != null) {
                  try {
                     registerConfig.invoke(null, clazz, specx, modid);
                  } catch (Exception var4) {
                     Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var4));
                  }
               }
            });
         }

         Pair<IcebergConfig<?>, IIcebergConfigSpec> specPair = Services.getConfigSpecBuilder().finish(builder -> {
            IcebergConfig<?> result = null;

            try {
               Constructor<?> constructor = subClass.getDeclaredConstructor(IIcebergConfigSpecBuilder.class);
               constructor.setAccessible(true);
               result = (IcebergConfig<?>)constructor.newInstance(builder);
            } catch (Exception var4) {
               Iceberg.LOGGER.warn("Failed to register configuration:", var4);
            }

            return result;
         });
         IIcebergConfigSpec spec = (IIcebergConfigSpec)specPair.getRight();
         IcebergConfig<?> config = (IcebergConfig<?>)specPair.getLeft();
         if (spec == null) {
            Iceberg.LOGGER.warn("Failed to register configuration: Generated spec was null!");
            return false;
         } else if (config == null) {
            Iceberg.LOGGER.warn("Failed to register configuration: Generated configuration instance was null!");
            return false;
         } else {
            config.modId = modId;
            configInstances.put(modId, config);
            configSpecs.put(modId, spec);
            ConfigEvents.LOAD.register(config::onLoadEvent);
            ConfigEvents.RELOAD.register(config::onReloadEvent);
            ConfigEvents.REGISTER.invoker().onRegister(subClass, spec, modId);
            registeredClasses.add(subClass);
            Services.getConfigSpecBuilder().reset();
            return true;
         }
      }
   }
}

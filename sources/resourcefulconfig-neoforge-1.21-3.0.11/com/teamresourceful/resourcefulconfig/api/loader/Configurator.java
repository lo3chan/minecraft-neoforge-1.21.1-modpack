package com.teamresourceful.resourcefulconfig.api.loader;

import com.teamresourceful.resourcefulconfig.api.patching.ConfigPatchEvent;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.common.config.Configurations;
import com.teamresourceful.resourcefulconfig.common.utils.ModUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.Optionull;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Configurator {
   private final Map<String, ResourcefulConfig> configs = new ConcurrentHashMap<>();
   private final Map<String, Consumer<ConfigPatchEvent>> patchHandlers = new ConcurrentHashMap<>();
   private final Map<Object, String> configClasses = new ConcurrentHashMap<>();
   private final String modid;

   public Configurator(String modid) {
      this.modid = modid;
   }

   public void register(Class<?> clazz) {
      this.register(clazz, event -> {});
   }

   public void register(Class<?> clazz, Consumer<ConfigPatchEvent> handler) {
      ResourcefulConfig config = this.loadConfigClass(clazz, handler);
      if (config != null) {
         this.patchHandlers.put(config.id(), handler);
         this.configClasses.put(clazz, config.id());
         this.configs.put(config.id(), config);
         Configurations.INSTANCE.addConfig(config, this.modid);
      }
   }

   public void register(ResourcefulConfig config) {
      this.register(config, event -> {});
   }

   public void register(ResourcefulConfig config, Consumer<ConfigPatchEvent> handler) {
      config.load(handler);
      config.save();
      this.patchHandlers.put(config.id(), handler);
      this.configs.put(config.id(), config);
      this.configClasses.put(config, config.id());
      Configurations.INSTANCE.addConfig(config, this.modid);
   }

   @Internal
   private ResourcefulConfig loadConfigClass(Class<?> clazz, Consumer<ConfigPatchEvent> handler) {
      try {
         ResourcefulConfig config = ConfigParser.tryParse(clazz);
         config.load(handler);
         config.save();
         return config;
      } catch (Exception var4) {
         if (ModUtils.isDev()) {
            throw new RuntimeException("Failed to create config for " + clazz.getName(), var4);
         } else {
            ModUtils.log("Failed to create config for " + clazz.getName(), var4);
            return null;
         }
      }
   }

   public boolean saveConfig(ResourcefulConfig config) {
      config.save();
      return true;
   }

   public boolean saveConfig(Class<?> config) {
      return this.configClasses.containsKey(config) && this.saveConfig(this.configClasses.get(config));
   }

   public boolean saveConfig(String fileName) {
      ResourcefulConfig config = this.getConfig(fileName);
      if (config != null) {
         config.save();
         return true;
      } else {
         return false;
      }
   }

   public boolean loadConfig(ResourcefulConfig config) {
      return this.configClasses.containsKey(config) && this.loadConfig(this.configClasses.get(config));
   }

   public boolean loadConfig(Class<?> config) {
      return this.configClasses.containsKey(config) && this.loadConfig(this.configClasses.get(config));
   }

   public boolean loadConfig(String fileName) {
      ResourcefulConfig config = this.getConfig(fileName);
      if (config != null) {
         config.load(this.patchHandlers.getOrDefault(fileName, event -> {}));
         return true;
      } else {
         return false;
      }
   }

   public ResourcefulConfig getConfig(String fileName) {
      return this.configs.get(fileName);
   }

   public ResourcefulConfig getConfig(ResourcefulConfig config) {
      return (ResourcefulConfig)Optionull.map(this.configClasses.get(config), this::getConfig);
   }

   public ResourcefulConfig getConfig(Class<?> config) {
      return (ResourcefulConfig)Optionull.map(this.configClasses.get(config), this::getConfig);
   }
}

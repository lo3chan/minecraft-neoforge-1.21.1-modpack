package dev.isxander.yacl3.config;

import java.lang.reflect.InvocationTargetException;

@Deprecated
public abstract class ConfigInstance<T> {
   private final Class<T> configClass;
   private final T defaultInstance;
   private T instance;

   public ConfigInstance(Class<T> configClass) {
      this.configClass = configClass;

      try {
         this.defaultInstance = this.instance = configClass.getConstructor().newInstance();
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var3) {
         throw new IllegalStateException(
            String.format("Could not create default instance of config for %s. Make sure there is a default constructor!", this.configClass.getSimpleName())
         );
      }
   }

   public abstract void save();

   public abstract void load();

   public T getConfig() {
      return this.instance;
   }

   protected void setConfig(T instance) {
      this.instance = instance;
   }

   public T getDefaults() {
      return this.defaultInstance;
   }

   public Class<T> getConfigClass() {
      return this.configClass;
   }
}

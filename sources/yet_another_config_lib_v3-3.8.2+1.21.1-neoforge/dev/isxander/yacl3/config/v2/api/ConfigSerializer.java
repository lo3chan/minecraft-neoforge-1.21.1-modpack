package dev.isxander.yacl3.config.v2.api;

import java.util.Map;

public abstract class ConfigSerializer<T> {
   protected final ConfigClassHandler<T> config;

   public ConfigSerializer(ConfigClassHandler<T> config) {
      this.config = config;
   }

   public abstract void save();

   public ConfigSerializer.LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
      this.load();
      return ConfigSerializer.LoadResult.NO_CHANGE;
   }

   @Deprecated
   public void load() {
      throw new IllegalArgumentException("load() is deprecated, use loadSafely() instead.");
   }

   public static enum LoadResult {
      SUCCESS,
      FAILURE,
      NO_CHANGE,
      DIRTY;
   }
}

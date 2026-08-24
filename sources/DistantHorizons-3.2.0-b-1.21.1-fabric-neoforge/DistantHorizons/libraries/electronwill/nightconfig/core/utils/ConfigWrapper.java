package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import java.util.List;
import java.util.Set;

public abstract class ConfigWrapper<C extends Config> extends UnmodifiableConfigWrapper<C> implements Config {
   protected ConfigWrapper(C config) {
      super(config);
   }

   @Override
   public Set<? extends Config.Entry> entrySet() {
      return this.config.entrySet();
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      return this.config.set(path, value);
   }

   @Override
   public boolean add(List<String> path, Object value) {
      return this.config.add(path, value);
   }

   @Override
   public <T> T remove(List<String> path) {
      return this.config.remove(path);
   }

   @Override
   public void clear() {
      this.config.clear();
   }

   @Override
   public Config createSubConfig() {
      return this.config.createSubConfig();
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + ':' + this.config;
   }
}

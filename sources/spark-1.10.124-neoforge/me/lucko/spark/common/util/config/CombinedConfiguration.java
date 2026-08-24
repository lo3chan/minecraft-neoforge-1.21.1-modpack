package me.lucko.spark.common.util.config;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;

class CombinedConfiguration implements Configuration {
   private final List<Configuration> configurations;

   CombinedConfiguration(Configuration... configurations) {
      this.configurations = ImmutableList.copyOf(configurations).reverse();
   }

   @Override
   public void load() {
      for (Configuration configuration : this.configurations) {
         configuration.load();
      }
   }

   @Override
   public void save() {
      for (Configuration configuration : this.configurations) {
         configuration.save();
      }
   }

   @Override
   public String getString(String path, String def) {
      String result = def;

      for (Configuration configuration : this.configurations) {
         result = configuration.getString(path, result);
      }

      return result;
   }

   @Override
   public boolean getBoolean(String path, boolean def) {
      boolean result = def;

      for (Configuration configuration : this.configurations) {
         result = configuration.getBoolean(path, result);
      }

      return result;
   }

   @Override
   public int getInteger(String path, int def) {
      int result = def;

      for (Configuration configuration : this.configurations) {
         result = configuration.getInteger(path, result);
      }

      return result;
   }

   @Override
   public List<String> getStringList(String path) {
      for (Configuration configuration : this.configurations) {
         List<String> result = configuration.getStringList(path);
         if (!result.isEmpty()) {
            return result;
         }
      }

      return Collections.emptyList();
   }

   @Override
   public void setString(String path, String value) {
      for (Configuration configuration : this.configurations) {
         configuration.setString(path, value);
      }
   }

   @Override
   public void setBoolean(String path, boolean value) {
      for (Configuration configuration : this.configurations) {
         configuration.setBoolean(path, value);
      }
   }

   @Override
   public void setInteger(String path, int value) {
      for (Configuration configuration : this.configurations) {
         configuration.setInteger(path, value);
      }
   }

   @Override
   public void setStringList(String path, List<String> value) {
      for (Configuration configuration : this.configurations) {
         configuration.setStringList(path, value);
      }
   }

   @Override
   public boolean contains(String path) {
      for (Configuration configuration : this.configurations) {
         if (configuration.contains(path)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void remove(String path) {
      for (Configuration configuration : this.configurations) {
         configuration.remove(path);
      }
   }
}

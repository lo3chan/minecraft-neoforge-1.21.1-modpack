package me.lucko.spark.common.util.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum RuntimeConfiguration implements Configuration {
   SYSTEM_PROPERTIES {
      @Override
      public String getString(String path, String def) {
         return System.getProperty("spark." + path, def);
      }
   },
   ENVIRONMENT_VARIABLES {
      @Override
      public String getString(String path, String def) {
         String name = "SPARK_" + path.replace(".", "_").replace("-", "_").toUpperCase();
         String value = System.getenv(name);
         return value != null ? value : def;
      }
   };

   private RuntimeConfiguration() {
   }

   @Override
   public boolean getBoolean(String path, boolean def) {
      return Boolean.parseBoolean(this.getString(path, Boolean.toString(def)));
   }

   @Override
   public int getInteger(String path, int def) {
      try {
         return Integer.parseInt(this.getString(path, Integer.toString(def)));
      } catch (NumberFormatException var4) {
         return def;
      }
   }

   @Override
   public List<String> getStringList(String path) {
      String value = this.getString(path, "");
      return value.isEmpty() ? Collections.emptyList() : Arrays.asList(value.split(","));
   }

   @Override
   public boolean contains(String path) {
      return this.getString(path, null) != null;
   }

   @Override
   public void load() {
   }

   @Override
   public void save() {
   }

   @Override
   public void setString(String path, String value) {
   }

   @Override
   public void setBoolean(String path, boolean value) {
   }

   @Override
   public void setInteger(String path, int value) {
   }

   @Override
   public void setStringList(String path, List<String> value) {
   }

   @Override
   public void remove(String path) {
   }
}

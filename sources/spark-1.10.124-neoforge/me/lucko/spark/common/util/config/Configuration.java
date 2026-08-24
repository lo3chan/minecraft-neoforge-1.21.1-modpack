package me.lucko.spark.common.util.config;

import java.util.List;

public interface Configuration {
   static Configuration combining(Configuration... configurations) {
      return new CombinedConfiguration(configurations);
   }

   void load();

   void save();

   String getString(String var1, String var2);

   boolean getBoolean(String var1, boolean var2);

   int getInteger(String var1, int var2);

   List<String> getStringList(String var1);

   void setString(String var1, String var2);

   void setBoolean(String var1, boolean var2);

   void setInteger(String var1, int var2);

   void setStringList(String var1, List<String> var2);

   boolean contains(String var1);

   void remove(String var1);
}

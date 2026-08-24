package com.aetherteam.aether.data;

import com.aetherteam.aether.AetherConfig;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public final class ConfigSerializationUtil {
   public static String serialize(ConfigValue<Boolean> config) {
      try {
         return config.getPath().toString();
      } catch (NullPointerException var2) {
         throw new JsonSyntaxException("Error loading config entry from JSON! Maybe the config key is incorrect?");
      }
   }

   public static ConfigValue<Boolean> deserialize(String string) {
      List<String> path = Arrays.asList(string.replace("[", "").replace("]", "").split(", "));
      ConfigValue<Boolean> config = (ConfigValue<Boolean>)AetherConfig.SERVER_SPEC.getValues().get(path);
      if (config == null) {
         config = (ConfigValue<Boolean>)AetherConfig.COMMON_SPEC.getValues().get(path);
      }

      return config;
   }
}

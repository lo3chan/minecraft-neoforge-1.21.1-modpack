package com.github.alexthe666.citadel.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
   public static final ModConfigSpec SERVER_SPEC;
   public static final ServerConfig SERVER;

   static {
      Pair<ServerConfig, ModConfigSpec> specPair = new Builder().configure(ServerConfig::new);
      SERVER = (ServerConfig)specPair.getLeft();
      SERVER_SPEC = (ModConfigSpec)specPair.getRight();
   }
}

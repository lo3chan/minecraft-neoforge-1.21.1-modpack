package com.github.alexthe666.alexsmobs.config;

import codx.codxlib.api.settings.CodxSettings;
import codx.codxlib.api.settings.CodxSettings.Configured;

public final class ConfigHolder {
   public static final CodxSettings COMMON_SPEC;
   public static final CommonConfig COMMON;

   private ConfigHolder() {
   }

   public static void load() {
      COMMON_SPEC.load();
   }

   public static boolean save() {
      return COMMON_SPEC.apply();
   }

   static {
      Configured<CommonConfig> configured = CodxSettings.builder("alexsmobs")
         .fileName("amc.json")
         .legacyFiles(new String[]{"alexsmobs.toml", "alexsmobs.json"})
         .onChange(AMConfig::bake)
         .configure(CommonConfig::new);
      COMMON = (CommonConfig)configured.holder();
      COMMON_SPEC = configured.settings();
   }
}

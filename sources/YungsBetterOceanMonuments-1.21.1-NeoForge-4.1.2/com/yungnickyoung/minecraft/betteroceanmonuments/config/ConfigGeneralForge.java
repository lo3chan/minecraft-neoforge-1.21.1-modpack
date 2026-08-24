package com.yungnickyoung.minecraft.betteroceanmonuments.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralForge {
   public final ConfigValue<Boolean> disableVanillaMonuments;

   public ConfigGeneralForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# General settings.\n##########################################################################################################"
         )
         .push("General");
      this.disableVanillaMonuments = BUILDER.comment("Whether or not vanilla ocean monuments should be disabled.\nDefault: true".indent(1))
         .worldRestart()
         .define("Disable Vanilla Ocean Monuments", true);
      BUILDER.pop();
   }
}

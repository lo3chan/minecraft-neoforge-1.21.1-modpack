package com.yungnickyoung.minecraft.betterfortresses.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralNeoForge {
   public final ConfigValue<Boolean> disableVanillaFortresses;

   public ConfigGeneralNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# General settings.\n##########################################################################################################"
         )
         .push("General");
      this.disableVanillaFortresses = BUILDER.comment("Whether or not vanilla Nether Fortresses should be disabled.\nDefault: true".indent(1))
         .worldRestart()
         .define("Disable Vanilla Nether Fortresses", true);
      BUILDER.pop();
   }
}

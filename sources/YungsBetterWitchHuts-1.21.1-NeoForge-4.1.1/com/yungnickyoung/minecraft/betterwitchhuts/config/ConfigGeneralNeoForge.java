package com.yungnickyoung.minecraft.betterwitchhuts.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralNeoForge {
   public final ConfigValue<Boolean> disableVanillaWitchHuts;

   public ConfigGeneralNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# General settings.\n##########################################################################################################"
         )
         .push("General");
      this.disableVanillaWitchHuts = BUILDER.comment("Whether or not vanilla witch huts should be disabled.\nDefault: true".indent(1))
         .worldRestart()
         .define("Disable Vanilla Witch Huts", true);
      BUILDER.pop();
   }
}

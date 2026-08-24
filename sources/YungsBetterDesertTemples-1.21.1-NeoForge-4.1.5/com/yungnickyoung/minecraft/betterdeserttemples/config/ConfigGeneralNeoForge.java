package com.yungnickyoung.minecraft.betterdeserttemples.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralNeoForge {
   public final ConfigValue<Boolean> disableVanillaPyramids;
   public final ConfigValue<Boolean> applyMiningFatigue;

   public ConfigGeneralNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# General settings.\n##########################################################################################################"
         )
         .push("General");
      this.disableVanillaPyramids = BUILDER.comment("Whether or not vanilla desert pyramids should be disabled.\nDefault: true".indent(1))
         .worldRestart()
         .define("Disable Vanilla Pyramids", true);
      this.applyMiningFatigue = BUILDER.comment(
            "Whether or not mining fatigue is applied to players in the desert temple if it has not yet been cleared.\nDefault: true".indent(1)
         )
         .worldRestart()
         .define("Apply Mining Fatigue", true);
      BUILDER.pop();
   }
}

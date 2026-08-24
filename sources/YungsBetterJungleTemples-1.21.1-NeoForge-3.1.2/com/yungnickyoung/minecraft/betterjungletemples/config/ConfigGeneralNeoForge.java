package com.yungnickyoung.minecraft.betterjungletemples.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralNeoForge {
   public final ConfigValue<Boolean> disableVanillaJungleTemples;

   public ConfigGeneralNeoForge(Builder BUILDER) {
      BUILDER.push("General");
      this.disableVanillaJungleTemples = BUILDER.worldRestart().define("Disable Vanilla Jungle Temples", true);
      BUILDER.pop();
   }
}

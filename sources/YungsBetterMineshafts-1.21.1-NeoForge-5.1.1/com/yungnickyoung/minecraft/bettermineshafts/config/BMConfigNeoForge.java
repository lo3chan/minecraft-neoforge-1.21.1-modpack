package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public final class BMConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigValue<Boolean> disableVanillaMineshafts = BUILDER.worldRestart()
      .comment(" Whether or not vanilla mineshafts should be disabled.\n Default: true")
      .define("Disable Vanilla Mineshafts", true);
   public static final ConfigValue<Integer> minY = BUILDER.worldRestart()
      .comment(" The lowest a mineshaft can spawn.\n Default: -55")
      .define("Minimum y-coordinate", -55);
   public static final ConfigValue<Integer> maxY = BUILDER.worldRestart()
      .comment("The highest a mineshaft can spawn.\nDefault: 30".indent(1))
      .define("Maximum y-coordinate", 30);
   public static final ConfigOresNeoForge ores = new ConfigOresNeoForge(BUILDER);
   public static final ConfigSpawnRatesNeoForge spawnRates = new ConfigSpawnRatesNeoForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Mineshafts");
      BUILDER.pop();
   }
}

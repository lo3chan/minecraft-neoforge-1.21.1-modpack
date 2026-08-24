package com.yungnickyoung.minecraft.betterdungeons.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigZombieDungeonNeoForge {
   public final ConfigValue<Integer> zombieDungeonMaxSurfaceStaircaseLength;

   public ConfigZombieDungeonNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# Zombie Dungeon settings.\n##########################################################################################################"
         )
         .push("Zombie Dungeons");
      this.zombieDungeonMaxSurfaceStaircaseLength = BUILDER.comment(
            "The longest distance that can be checked when attempting to generate a surface entrance staircase.\nMaking this too large may cause problems.\nDefault: 20"
               .indent(1)
         )
         .worldRestart()
         .define("Zombie Dungeon Surface Entrance Staircase Max Length", 20);
      BUILDER.pop();
   }
}

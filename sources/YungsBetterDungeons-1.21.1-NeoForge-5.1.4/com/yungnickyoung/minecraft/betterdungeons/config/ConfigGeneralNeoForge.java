package com.yungnickyoung.minecraft.betterdungeons.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigGeneralNeoForge {
   public final ConfigValue<Boolean> enableHeads;
   public final ConfigValue<Boolean> enableNetherBlocks;

   public ConfigGeneralNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# General settings.\n##########################################################################################################"
         )
         .push("General");
      this.enableHeads = BUILDER.comment(
            "Whether or not dungeons should be allowed to place skeleton skulls and other mob heads.\nThis option may be useful for some modpack creators.\nDefault: true"
               .indent(1)
         )
         .worldRestart()
         .define("Enable Skulls & Heads", true);
      this.enableNetherBlocks = BUILDER.comment(
            "Some dungeons can rarely spawn Nether-related blocks such as soul sand, soul campfires, and soul lanterns.\nNote that the blocks will be purely decorative - nothing progression-breaking like Ancient Debris.\nSet this to false to prevent any Nether-related blocks from spawning in dungeons.\nThis option may be useful for some modpack creators.\nDefault: true"
               .indent(1)
         )
         .worldRestart()
         .define("Enable Nether Blocks in Dungeons", true);
      BUILDER.pop();
   }
}

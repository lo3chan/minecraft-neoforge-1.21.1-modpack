package com.yungnickyoung.minecraft.betterdungeons.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigSmallNetherDungeonsNeoForge {
   public final ConfigValue<Boolean> enabled;
   public final ConfigValue<Boolean> witherSkeletonsDropWitherSkulls;
   public final ConfigValue<Boolean> blazesDropBlazeRods;
   public final ConfigValue<Integer> bannerMaxCount;

   public ConfigSmallNetherDungeonsNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# Small Nether Dungeon settings.\n# These are disabled by default.\n##########################################################################################################"
         )
         .push("Small Nether Dungeons");
      this.enabled = BUILDER.comment(" Whether or not small Nether dungeons should spawn.\n Default: false")
         .worldRestart()
         .define("Enable Small Nether Dungeons", false);
      this.witherSkeletonsDropWitherSkulls = BUILDER.comment(
            " Whether or not Wither skeletons spawned from small Nether dungeons have a chance to drop Wither skeleton skulls.\n Default: true"
         )
         .worldRestart()
         .define("Wither Skeletons From Spawners Drop Wither Skeleton Skulls", true);
      this.blazesDropBlazeRods = BUILDER.comment(" Whether or not blazes spawned from small Nether dungeons have a chance to drop blaze rods.\n Default: true")
         .worldRestart()
         .define("Blazes From Spawners Drop Blaze Rods", true);
      this.bannerMaxCount = BUILDER.comment(" The maximum number of banners that can spawn in a single small Nether dungeon.\n Default: 2")
         .worldRestart()
         .defineInRange("Small Nether Dungeon Max Banner Count", 2, 0, 8);
      BUILDER.pop();
   }
}

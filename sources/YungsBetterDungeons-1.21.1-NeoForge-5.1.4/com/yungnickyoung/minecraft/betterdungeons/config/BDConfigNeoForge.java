package com.yungnickyoung.minecraft.betterdungeons.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class BDConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigGeneralNeoForge general = new ConfigGeneralNeoForge(BUILDER);
   public static final ConfigZombieDungeonNeoForge zombieDungeons = new ConfigZombieDungeonNeoForge(BUILDER);
   public static final ConfigSmallDungeonsNeoForge smallDungeons = new ConfigSmallDungeonsNeoForge(BUILDER);
   public static final ConfigSmallNetherDungeonsNeoForge smallNetherDungeons = new ConfigSmallNetherDungeonsNeoForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Dungeons");
      BUILDER.pop();
   }
}

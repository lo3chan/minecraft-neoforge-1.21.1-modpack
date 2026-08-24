package com.yungnickyoung.minecraft.betterstrongholds.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class BSConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigGeneralNeoForge general = new ConfigGeneralNeoForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Strongholds");
      BUILDER.pop();
   }
}

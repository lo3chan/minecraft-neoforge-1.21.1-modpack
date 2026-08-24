package com.yungnickyoung.minecraft.betterfortresses.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class BNFConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigGeneralNeoForge general = new ConfigGeneralNeoForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Nether Fortresses");
      BUILDER.pop();
   }
}

package com.yungnickyoung.minecraft.betterjungletemples.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class BJTConfigNeoForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigGeneralNeoForge general = new ConfigGeneralNeoForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Jungle Temples");
      BUILDER.pop();
   }
}

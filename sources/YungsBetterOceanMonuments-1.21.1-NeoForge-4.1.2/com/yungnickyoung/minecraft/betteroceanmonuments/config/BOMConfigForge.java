package com.yungnickyoung.minecraft.betteroceanmonuments.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class BOMConfigForge {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigGeneralForge general = new ConfigGeneralForge(BUILDER);

   static {
      BUILDER.push("YUNG's Better Ocean Monuments");
      BUILDER.pop();
   }
}

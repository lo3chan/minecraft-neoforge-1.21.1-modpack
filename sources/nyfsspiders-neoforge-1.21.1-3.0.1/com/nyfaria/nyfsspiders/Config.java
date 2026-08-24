package com.nyfaria.nyfsspiders;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class Config {
   public static final ModConfigSpec COMMON;
   public static final BooleanValue PATH_FINDER_DEBUG_PREVIEW;
   public static final BooleanValue PREVENT_CLIMBING_IN_RAIN;

   static {
      Builder builder = new Builder();
      PATH_FINDER_DEBUG_PREVIEW = builder.worldRestart()
         .comment("Whether the path finder debug preview should be enabled.")
         .define("path_finder_debug_preview", false);
      PREVENT_CLIMBING_IN_RAIN = builder.worldRestart()
         .comment("Whether spiders should be prevented from climbing in rain.")
         .define("prevent_climbing_in_rain", false);
      COMMON = builder.build();
   }
}

package io.github.razordevs.aeroblender;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class AeroBlenderConfig {
   public static final ModConfigSpec COMMON_SPEC;
   public static final AeroBlenderConfig.Common COMMON;

   static {
      Pair<AeroBlenderConfig.Common, ModConfigSpec> commonSpecPair = new Builder().configure(AeroBlenderConfig.Common::new);
      COMMON_SPEC = (ModConfigSpec)commonSpecPair.getRight();
      COMMON = (AeroBlenderConfig.Common)commonSpecPair.getLeft();
   }

   public static class Common {
      public final ConfigValue<Integer> aetherRegionSize;
      public final ConfigValue<Integer> vanillaAetherRegionWeight;
      public static AeroBlenderConfig CONFIG;

      public Common(Builder builder) {
         builder.push("general");
         this.aetherRegionSize = builder.comment("The size of aether biome regions from each mod that uses AeroBlender.")
            .translation("aether_region_size")
            .define("Aether Region Size", 3);
         builder.pop();
         builder.push("general");
         this.vanillaAetherRegionWeight = builder.comment("The weighting of vanilla biome regions in the aether.")
            .translation("vanilla_aether_region_weight")
            .define("Aether Region Weight", 10);
         builder.pop();
      }

      public static void setConfig(AeroBlenderConfig config) {
         CONFIG = config;
      }
   }
}

package com.seibel.distanthorizons.core.api.external.methods.config.common;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.both.IDhApiWorldGenerationConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiWorldGenerationConfig implements IDhApiWorldGenerationConfig {
   public static DhApiWorldGenerationConfig INSTANCE = new DhApiWorldGenerationConfig();

   private DhApiWorldGenerationConfig() {
   }

   @Override
   public IDhApiConfigValue<Boolean> enableDistantWorldGeneration() {
      return new DhApiConfigValue<>(Config.Common.WorldGenerator.enableDistantGeneration);
   }

   @Override
   public IDhApiConfigValue<EDhApiDistantGeneratorMode> distantGeneratorMode() {
      return new DhApiConfigValue<>(Config.Common.WorldGenerator.distantGeneratorMode);
   }
}

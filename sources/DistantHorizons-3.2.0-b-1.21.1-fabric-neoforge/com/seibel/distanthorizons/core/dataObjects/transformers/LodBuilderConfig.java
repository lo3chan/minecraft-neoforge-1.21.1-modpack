package com.seibel.distanthorizons.core.dataObjects.transformers;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;

public class LodBuilderConfig {
   public boolean useHeightmap = false;
   public boolean useBiomeColors = false;
   public boolean useSolidBlocksInColorGen = true;
   public boolean quickFillWithVoid = false;
   public EDhApiDistantGeneratorMode distantGeneratorDetailLevel;

   public LodBuilderConfig(EDhApiDistantGeneratorMode distantGeneratorDetailLevel) {
      this.distantGeneratorDetailLevel = distantGeneratorDetailLevel;
   }

   public static LodBuilderConfig getFillVoidConfig() {
      LodBuilderConfig config = new LodBuilderConfig(EDhApiDistantGeneratorMode.PRE_EXISTING_ONLY);
      config.quickFillWithVoid = true;
      return config;
   }
}

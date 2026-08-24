package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiNoiseTextureConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiNoiseTextureConfig implements IDhApiNoiseTextureConfig {
   public static DhApiNoiseTextureConfig INSTANCE = new DhApiNoiseTextureConfig();

   private DhApiNoiseTextureConfig() {
   }

   @Override
   public IDhApiConfigValue<Boolean> noiseEnabled() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.NoiseTexture.enableNoiseTexture);
   }

   @Override
   public IDhApiConfigValue<Integer> noiseSteps() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.NoiseTexture.noiseSteps);
   }

   @Override
   public IDhApiConfigValue<Float> noiseIntensity() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.NoiseTexture.noiseIntensity);
   }

   @Override
   public IDhApiConfigValue<Integer> noiseDropoff() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.NoiseTexture.noiseDropoff);
   }
}

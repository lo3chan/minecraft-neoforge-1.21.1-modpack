package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiNoiseTextureConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<Boolean> noiseEnabled();

   IDhApiConfigValue<Integer> noiseSteps();

   IDhApiConfigValue<Float> noiseIntensity();

   IDhApiConfigValue<Integer> noiseDropoff();
}

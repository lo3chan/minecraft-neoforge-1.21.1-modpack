package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiFarFogConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiFarFogConfig implements IDhApiFarFogConfig {
   public static DhApiFarFogConfig INSTANCE = new DhApiFarFogConfig();

   private DhApiFarFogConfig() {
   }

   @Override
   public IDhApiConfigValue<Float> farFogStartDistance() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogStart);
   }

   @Override
   public IDhApiConfigValue<Float> farFogEndDistance() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogEnd);
   }

   @Override
   public IDhApiConfigValue<Float> farFogMinThickness() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogMin);
   }

   @Override
   public IDhApiConfigValue<Float> farFogMaxThickness() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogMax);
   }

   @Override
   public IDhApiConfigValue<EDhApiFogFalloff> farFogFalloff() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogFalloff);
   }

   @Override
   public IDhApiConfigValue<Float> farFogDensity() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.farFogDensity);
   }
}

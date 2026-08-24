package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiHeightFogConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiHeightFogConfig implements IDhApiHeightFogConfig {
   public static DhApiHeightFogConfig INSTANCE = new DhApiHeightFogConfig();

   private DhApiHeightFogConfig() {
   }

   @Override
   public IDhApiConfigValue<EDhApiHeightFogMixMode> heightFogMixMode() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogMixMode);
   }

   @Override
   public IDhApiConfigValue<EDhApiHeightFogDirection> heightFogDirection() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogDirection);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogBaseHeight() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogBaseHeight);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogStartingHeightPercent() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogStart);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogEndingHeightPercent() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogEnd);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogMinThickness() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogMin);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogMaxThickness() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogMax);
   }

   @Override
   public IDhApiConfigValue<EDhApiFogFalloff> heightFogFalloff() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogFalloff);
   }

   @Override
   public IDhApiConfigValue<Float> heightFogDensity() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.Fog.HeightFog.heightFogDensity);
   }
}

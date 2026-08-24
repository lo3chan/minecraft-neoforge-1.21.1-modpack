package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiHeightFogConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<EDhApiHeightFogMixMode> heightFogMixMode();

   IDhApiConfigValue<EDhApiHeightFogDirection> heightFogDirection();

   IDhApiConfigValue<Float> heightFogBaseHeight();

   IDhApiConfigValue<Float> heightFogStartingHeightPercent();

   IDhApiConfigValue<Float> heightFogEndingHeightPercent();

   IDhApiConfigValue<Float> heightFogMinThickness();

   IDhApiConfigValue<Float> heightFogMaxThickness();

   IDhApiConfigValue<EDhApiFogFalloff> heightFogFalloff();

   IDhApiConfigValue<Float> heightFogDensity();
}

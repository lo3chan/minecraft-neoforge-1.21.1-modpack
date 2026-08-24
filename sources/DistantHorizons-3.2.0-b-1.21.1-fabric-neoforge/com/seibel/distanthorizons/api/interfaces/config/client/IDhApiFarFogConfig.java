package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiFarFogConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<Float> farFogStartDistance();

   IDhApiConfigValue<Float> farFogEndDistance();

   IDhApiConfigValue<Float> farFogMinThickness();

   IDhApiConfigValue<Float> farFogMaxThickness();

   IDhApiConfigValue<EDhApiFogFalloff> farFogFalloff();

   IDhApiConfigValue<Float> farFogDensity();
}

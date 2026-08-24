package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogColorMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogDrawMode;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiFogConfig extends IDhApiConfigGroup {
   IDhApiFarFogConfig farFog();

   IDhApiHeightFogConfig heightFog();

   @Deprecated
   IDhApiConfigValue<EDhApiFogDrawMode> drawMode();

   IDhApiConfigValue<Boolean> enableDhFog();

   IDhApiConfigValue<EDhApiFogColorMode> color();

   @Deprecated
   IDhApiConfigValue<Boolean> disableVanillaFog();

   IDhApiConfigValue<Boolean> enableVanillaFog();
}

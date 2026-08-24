package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiGenericRenderingConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<Boolean> renderingEnabled();

   IDhApiConfigValue<Boolean> beaconRenderingEnabled();

   IDhApiConfigValue<Boolean> cloudRenderingEnabled();
}

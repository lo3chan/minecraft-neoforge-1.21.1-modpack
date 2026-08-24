package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiMultiThreadingConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<Integer> threadCount();

   IDhApiConfigValue<Double> threadRuntimeRatio();
}

package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiThreadingConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiMultiThreadingConfig implements IDhApiMultiThreadingConfig {
   public static DhApiMultiThreadingConfig INSTANCE = new DhApiMultiThreadingConfig();

   private DhApiMultiThreadingConfig() {
   }

   @Override
   public IDhApiConfigValue<Integer> threadCount() {
      return new DhApiConfigValue<>(Config.Common.MultiThreading.numberOfThreads);
   }

   @Override
   public IDhApiConfigValue<Double> threadRuntimeRatio() {
      return new DhApiConfigValue<>(Config.Common.MultiThreading.threadRunTimeRatio);
   }
}

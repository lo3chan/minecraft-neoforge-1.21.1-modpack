package com.mrcrayfish.configured;

import com.mrcrayfish.configured.platform.Services;

public class Bootstrap {
   public static void init() {
      Config.load(Services.PLATFORM.getConfigPath());
   }
}

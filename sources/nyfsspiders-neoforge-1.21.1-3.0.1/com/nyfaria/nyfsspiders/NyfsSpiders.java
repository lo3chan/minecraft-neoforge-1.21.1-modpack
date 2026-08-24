package com.nyfaria.nyfsspiders;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;

@Mod("nyfsspiders")
public class NyfsSpiders {
   public NyfsSpiders(ModContainer container, IEventBus bus) {
      container.registerConfig(Type.COMMON, Config.COMMON, "nyfsspiders.toml");
      CommonClass.init();
   }
}

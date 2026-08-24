package com.yungnickyoung.minecraft.betteroceanmonuments;

import com.yungnickyoung.minecraft.betteroceanmonuments.module.ConfigModuleNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("betteroceanmonuments")
public class BetterOceanMonumentsNeoForge {
   public static IEventBus loadingContextEventBus;

   public BetterOceanMonumentsNeoForge(IEventBus eventBus, ModContainer container) {
      loadingContextEventBus = eventBus;
      BetterOceanMonumentsCommon.init();
      ConfigModuleNeoForge.init(container);
   }
}

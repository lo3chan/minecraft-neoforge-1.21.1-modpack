package com.yungnickyoung.minecraft.betterendisland;

import com.yungnickyoung.minecraft.betterendisland.module.ConfigModuleNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("betterendisland")
public class BetterEndIslandNeoForge {
   public static IEventBus loadingContextEventBus;

   public BetterEndIslandNeoForge(IEventBus eventBus, ModContainer container) {
      loadingContextEventBus = eventBus;
      BetterEndIslandCommon.init();
      ConfigModuleNeoForge.init(container);
   }
}

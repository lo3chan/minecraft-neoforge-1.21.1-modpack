package com.yungnickyoung.minecraft.betterdeserttemples;

import com.yungnickyoung.minecraft.betterdeserttemples.module.ConfigModuleNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("betterdeserttemples")
public class BetterDesertTemplesNeoForge {
   public static IEventBus loadingContextEventBus;

   public BetterDesertTemplesNeoForge(IEventBus eventBus, ModContainer container) {
      loadingContextEventBus = eventBus;
      BetterDesertTemplesCommon.init();
      ConfigModuleNeoForge.init(container);
   }
}

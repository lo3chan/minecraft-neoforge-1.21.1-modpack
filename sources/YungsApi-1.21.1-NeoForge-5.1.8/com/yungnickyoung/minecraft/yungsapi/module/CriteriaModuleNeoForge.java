package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.registries.Registries;

public class CriteriaModuleNeoForge {
   public static void processEntries() {
      YungsApiNeoForge.loadingContextEventBus
         .addListener(YungsApiNeoForge.buildSimpleRegistrar(Registries.TRIGGER_TYPE, AutoRegistrationManager.CRITERION_TRIGGERS));
   }
}

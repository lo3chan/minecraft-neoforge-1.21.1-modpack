package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class EntityDataSerializerModuleNeoForge {
   public static void processEntries() {
      YungsApiNeoForge.loadingContextEventBus
         .addListener(YungsApiNeoForge.buildSimpleRegistrar(Keys.ENTITY_DATA_SERIALIZERS, AutoRegistrationManager.ENTITY_DATA_SERIALIZERS));
   }
}

package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterMobEffect;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper;

public class MobEffectModuleNeoForge {
   public static void processEntries() {
      YungsApiNeoForge.loadingContextEventBus
         .addListener(
            YungsApiNeoForge.buildAutoRegistrar(
               Registries.MOB_EFFECT, AutoRegistrationManager.MOB_EFFECTS, MobEffectModuleNeoForge::buildMobEffect, MobEffectModuleNeoForge::registerMobEffect
            )
         );
   }

   private static MobEffect buildMobEffect(AutoRegisterField data) {
      return ((AutoRegisterMobEffect)data.object()).get();
   }

   private static void registerMobEffect(AutoRegisterField data, MobEffect mobEffect, RegisterHelper<MobEffect> helper) {
      Holder<MobEffect> holder = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, data.name(), mobEffect);
      ((AutoRegisterMobEffect)data.object()).setHolder(holder);
   }
}

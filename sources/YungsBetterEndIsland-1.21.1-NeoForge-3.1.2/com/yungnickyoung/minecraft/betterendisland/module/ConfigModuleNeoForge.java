package com.yungnickyoung.minecraft.betterendisland.module;

import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandNeoForge;
import com.yungnickyoung.minecraft.betterendisland.config.BEIConfigNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

public class ConfigModuleNeoForge {
   public static void init(ModContainer container) {
      container.registerConfig(Type.COMMON, BEIConfigNeoForge.SPEC, "betterendisland-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterEndIslandNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BEIConfigNeoForge.SPEC) {
         bakeConfig();
      }
   }

   private static void bakeConfig() {
      BetterEndIslandCommon.CONFIG.resummonedDragonDropsEgg = (Boolean)BEIConfigNeoForge.resummonedDragonDropsEgg.get();
      BetterEndIslandCommon.CONFIG.useVanillaSpawnPlatform = (Boolean)BEIConfigNeoForge.useVanillaSpawnPlatform.get();
      BetterEndIslandCommon.CONFIG.useVanillaEndGateways = (Boolean)BEIConfigNeoForge.useVanillaEndGateways.get();
      BetterEndIslandCommon.CONFIG.playBellSound = (Boolean)BEIConfigNeoForge.playBellSound.get();
      BetterEndIslandCommon.CONFIG.spawnCentralTowerInitially = (Boolean)BEIConfigNeoForge.spawnCentralTowerInitially.get();
      BetterEndIslandCommon.CONFIG.spawnCentralTowerOnResummon = (Boolean)BEIConfigNeoForge.spawnCentralTowerOnResummon.get();
   }
}

package com.yungnickyoung.minecraft.betterjungletemples.module;

import com.yungnickyoung.minecraft.betterjungletemples.BetterJungleTemplesCommon;
import com.yungnickyoung.minecraft.betterjungletemples.BetterJungleTemplesNeoForge;
import com.yungnickyoung.minecraft.betterjungletemples.config.BJTConfigNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

public class ConfigModuleNeoForge {
   public static void init(ModContainer container) {
      container.registerConfig(Type.COMMON, BJTConfigNeoForge.SPEC, "betterjungletemples-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterJungleTemplesNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BJTConfigNeoForge.SPEC) {
         bakeConfig();
      }
   }

   private static void bakeConfig() {
      BetterJungleTemplesCommon.CONFIG.general.disableVanillaJungleTemples = (Boolean)BJTConfigNeoForge.general.disableVanillaJungleTemples.get();
   }
}

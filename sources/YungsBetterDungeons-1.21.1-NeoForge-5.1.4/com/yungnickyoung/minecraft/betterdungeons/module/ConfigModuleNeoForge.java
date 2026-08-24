package com.yungnickyoung.minecraft.betterdungeons.module;

import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsNeoForge;
import com.yungnickyoung.minecraft.betterdungeons.config.BDConfigNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

public class ConfigModuleNeoForge {
   public static void init(ModContainer container) {
      container.registerConfig(Type.COMMON, BDConfigNeoForge.SPEC, "betterdungeons-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterDungeonsNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BDConfigNeoForge.SPEC) {
         bakeConfig();
      }
   }

   private static void bakeConfig() {
      BetterDungeonsCommon.CONFIG.general.enableHeads = (Boolean)BDConfigNeoForge.general.enableHeads.get();
      BetterDungeonsCommon.CONFIG.general.enableNetherBlocks = (Boolean)BDConfigNeoForge.general.enableNetherBlocks.get();
      BetterDungeonsCommon.CONFIG.zombieDungeons.zombieDungeonMaxSurfaceStaircaseLength = (Integer)BDConfigNeoForge.zombieDungeons
         .zombieDungeonMaxSurfaceStaircaseLength
         .get();
      BetterDungeonsCommon.CONFIG.smallDungeons.bannerMaxCount = (Integer)BDConfigNeoForge.smallDungeons.bannerMaxCount.get();
      BetterDungeonsCommon.CONFIG.smallDungeons.chestMinCount = (Integer)BDConfigNeoForge.smallDungeons.chestMinCount.get();
      BetterDungeonsCommon.CONFIG.smallDungeons.chestMaxCount = (Integer)BDConfigNeoForge.smallDungeons.chestMaxCount.get();
      BetterDungeonsCommon.CONFIG.smallDungeons.enableOreProps = (Boolean)BDConfigNeoForge.smallDungeons.enableOreProps.get();
      BetterDungeonsCommon.CONFIG.smallNetherDungeons.enabled = (Boolean)BDConfigNeoForge.smallNetherDungeons.enabled.get();
      BetterDungeonsCommon.CONFIG.smallNetherDungeons.witherSkeletonsDropWitherSkulls = (Boolean)BDConfigNeoForge.smallNetherDungeons
         .witherSkeletonsDropWitherSkulls
         .get();
      BetterDungeonsCommon.CONFIG.smallNetherDungeons.blazesDropBlazeRods = (Boolean)BDConfigNeoForge.smallNetherDungeons.blazesDropBlazeRods.get();
      BetterDungeonsCommon.CONFIG.smallNetherDungeons.bannerMaxCount = (Integer)BDConfigNeoForge.smallNetherDungeons.bannerMaxCount.get();
   }
}

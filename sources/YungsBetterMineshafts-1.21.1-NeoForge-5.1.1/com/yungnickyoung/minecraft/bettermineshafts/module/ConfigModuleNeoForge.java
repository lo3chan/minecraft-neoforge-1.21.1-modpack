package com.yungnickyoung.minecraft.bettermineshafts.module;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsNeoForge;
import com.yungnickyoung.minecraft.bettermineshafts.config.BMConfigNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

public class ConfigModuleNeoForge {
   public static void init(ModContainer container) {
      container.registerConfig(Type.COMMON, BMConfigNeoForge.SPEC, "bettermineshafts-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterMineshaftsNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BMConfigNeoForge.SPEC) {
         bakeConfig();
      }
   }

   private static void bakeConfig() {
      BetterMineshaftsCommon.CONFIG.disableVanillaMineshafts = (Boolean)BMConfigNeoForge.disableVanillaMineshafts.get();
      BetterMineshaftsCommon.CONFIG.minY = (Integer)BMConfigNeoForge.minY.get();
      BetterMineshaftsCommon.CONFIG.maxY = (Integer)BMConfigNeoForge.maxY.get();
      BetterMineshaftsCommon.CONFIG.ores.enabled = (Boolean)BMConfigNeoForge.ores.enabled.get();
      BetterMineshaftsCommon.CONFIG.ores.cobble = (Integer)BMConfigNeoForge.ores.cobble.get();
      BetterMineshaftsCommon.CONFIG.ores.coal = (Integer)BMConfigNeoForge.ores.coal.get();
      BetterMineshaftsCommon.CONFIG.ores.iron = (Integer)BMConfigNeoForge.ores.iron.get();
      BetterMineshaftsCommon.CONFIG.ores.redstone = (Integer)BMConfigNeoForge.ores.redstone.get();
      BetterMineshaftsCommon.CONFIG.ores.gold = (Integer)BMConfigNeoForge.ores.gold.get();
      BetterMineshaftsCommon.CONFIG.ores.lapis = (Integer)BMConfigNeoForge.ores.lapis.get();
      BetterMineshaftsCommon.CONFIG.ores.emerald = (Integer)BMConfigNeoForge.ores.emerald.get();
      BetterMineshaftsCommon.CONFIG.ores.diamond = (Integer)BMConfigNeoForge.ores.diamond.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.lanternSpawnRate = (Double)BMConfigNeoForge.spawnRates.lanternSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.torchSpawnRate = (Double)BMConfigNeoForge.spawnRates.torchSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.workstationSpawnRate = (Double)BMConfigNeoForge.spawnRates.workstationSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.workstationDungeonSpawnRate = (Double)BMConfigNeoForge.spawnRates.workstationDungeonSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftSpawnRate = (Double)BMConfigNeoForge.spawnRates.smallShaftSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.cobwebSpawnRate = (Double)BMConfigNeoForge.spawnRates.cobwebSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftChestMinecartSpawnRate = (Double)BMConfigNeoForge.spawnRates.smallShaftChestMinecartSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftTntMinecartSpawnRate = (Double)BMConfigNeoForge.spawnRates.smallShaftTntMinecartSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.mainShaftChestMinecartSpawnRate = (Double)BMConfigNeoForge.spawnRates.mainShaftChestMinecartSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.mainShaftTntMinecartSpawnRate = (Double)BMConfigNeoForge.spawnRates.mainShaftTntMinecartSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.zombieVillagerRoomSpawnRate = (Integer)BMConfigNeoForge.spawnRates.zombieVillagerRoomSpawnRate.get();
      BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftPieceChainLength = (Integer)BMConfigNeoForge.spawnRates.smallShaftPieceChainLength.get();
   }
}

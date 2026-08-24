package com.yungnickyoung.minecraft.betterstrongholds.module;

import com.yungnickyoung.minecraft.betterstrongholds.BetterStrongholdsCommon;
import com.yungnickyoung.minecraft.betterstrongholds.BetterStrongholdsNeoForge;
import com.yungnickyoung.minecraft.betterstrongholds.config.BSConfigNeoForge;
import com.yungnickyoung.minecraft.betterstrongholds.world.ArmorStandChances;
import com.yungnickyoung.minecraft.betterstrongholds.world.ItemFrameChances;
import com.yungnickyoung.minecraft.betterstrongholds.world.OreChances;
import com.yungnickyoung.minecraft.betterstrongholds.world.RareBlockChances;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

public class ConfigModuleNeoForge {
   public static final String CUSTOM_CONFIG_PATH = "betterstrongholds";
   public static final String VERSION_PATH = "neoforge-1_21";

   public static void init(ModContainer container) {
      initCustomFiles();
      container.registerConfig(Type.COMMON, BSConfigNeoForge.SPEC, "betterstrongholds-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterStrongholdsNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
      loadJSON();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BSConfigNeoForge.SPEC) {
         bakeConfig();
         loadJSON();
      }
   }

   private static void initCustomFiles() {
      createDirectory();
      createBaseReadMe();
      createJsonReadMe();
      loadJSON();
   }

   private static void loadJSON() {
      loadOresJSON();
      loadRareBlocksJSON();
      loadArmorStandsJSON();
      loadItemFramesJSON();
   }

   private static void createDirectory() {
      File parentDir = new File(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds");
      File customConfigDir = new File(parentDir, "neoforge-1_21");

      try {
         String filePath = customConfigDir.getCanonicalPath();
         if (customConfigDir.mkdirs()) {
            BetterStrongholdsCommon.LOGGER.info("Creating directory for additional Better Strongholds configuration at {}", filePath);
         }
      } catch (IOException var3) {
         BetterStrongholdsCommon.LOGGER.error("ERROR creating Better Strongholds config directory: {}", var3.toString());
      }
   }

   private static void createBaseReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "This directory is for a few additional options for YUNG's Better Strongholds.\nOptions provided may vary by version.\nThis directory contains subdirectories for supported versions. The first time you run Better Strongholds, a version subdirectory will be created if that version supports advanced options.\nFor example, the first time you use Better Strongholds for MC 1.21 on NeoForge, the 'neoforge-1_21' subdirectory will be created in this folder.\nIf no subdirectory for your version is created, then that version probably does not support the additional options.\n\nNOTE -- MOST OPTIONS CAN BE FOUND IN A CONFIG FILE OUTSIDE THIS FOLDER!\nFor example, on NeoForge 1.21 the file is 'betterstrongholds-neoforge-1_21.toml'.";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create README file!");
         }
      }
   }

   private static void createJsonReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "neoforge-1_21", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "######################################\n#             ores.json              #\n######################################\n\n  This file contains a BlockSetSelector (see below) describing the probability of a given ore being chosen.\nThese probabilities are used in treasure rooms in the stronghold, in which\npiles of ore have a chance of spawning.\nFor information on BlockSetSelectors, see the bottom of this README.\n\n######################################\n#          rareblocks.json           #\n######################################\n\n  This file contains a BlockSetSelector describing the probability of a given block being chosen.\nThese probabilities are used in grand libraries, in which\ntwo rare blocks will spawn.\nFor information on BlockSetSelectors, see the bottom of this README.\n\n######################################\n#          armorstands.json          #\n######################################\n\n  This file contains ItemSetSelectors describing the probability distribution of armor on armor stands.\nCommon armor stands spawn in Armoury rooms, while Rare ones are only available in the rare Commander rooms.\nFor information on ItemSetSelectors, see the bottom of this README.\n\n######################################\n#          itemframes.json          #\n######################################\n\n  This file contains ItemSetSelectors describing the probability distribution of items in item frames.\nItem frames only spawn in storage rooms and armoury rooms.\nFor information on ItemSetSelectors, see the bottom of this README.\n\n######################################\n#         BlockSetSelectors          #\n######################################\n\nDescribes a set of blockstates and the probability of each blockstate being chosen.\n - entries: An object where each entry's key is a blockstate, and each value is that blockstate's probability of being chosen.\n      The total sum of all probabilities SHOULD NOT exceed 1.0!\n - defaultBlock: The blockstate used for any leftover probability ranges.\n      For example, if the total sum of all the probabilities of the entries is 0.6, then\n      there is a 0.4 chance of the defaultBlock being selected.\n\nHere's an example block selector:\n\"entries\": {\n  \"minecraft:cobblestone\": 0.25,\n  \"minecraft:air\": 0.2,\n  \"minecraft:stone_bricks\": 0.1\n},\n\"defaultBlock\": \"minecraft:oak_planks\"\n\nFor each block, this selector has a 25% chance of returning cobblestone, 20% chance of choosing air,\n10% chance of choosing stone bricks, and a 100 - (25 + 20 + 10) = 45% chance of choosing oak planks (since it's the default block).\n\n######################################\n#         ItemSetSelectors           #\n######################################\n\nDescribes a set of items and the probability of each item being chosen.\nWorks the same as BlockSetSelectors, but with items instead of blockstates.\n";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create ores and rare blocks README file!");
         }
      }
   }

   private static void loadOresJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "neoforge-1_21", "ores.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, OreChances.get());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create ores.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterStrongholdsCommon.LOGGER.error("Better Strongholds ores.json file not readable! Using default configuration...");
            return;
         }

         try {
            OreChances.instance = (OreChances)JSON.loadObjectFromJsonFile(jsonPath, OreChances.class);
         } catch (IOException var3) {
            BetterStrongholdsCommon.LOGGER.error("Error loading Better Strongholds ores.json file: {}", var3.toString());
            BetterStrongholdsCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void loadRareBlocksJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "neoforge-1_21", "rareblocks.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, RareBlockChances.get());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create rareblocks.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterStrongholdsCommon.LOGGER.error("Better Strongholds rareblocks.json file not readable! Using default configuration...");
            return;
         }

         try {
            RareBlockChances.instance = (RareBlockChances)JSON.loadObjectFromJsonFile(jsonPath, RareBlockChances.class);
         } catch (IOException var3) {
            BetterStrongholdsCommon.LOGGER.error("Error loading Better Strongholds rareblocks.json file: {}", var3.toString());
            BetterStrongholdsCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void loadArmorStandsJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "neoforge-1_21", "armorstands.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, ArmorStandChances.get());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create armorstands.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterStrongholdsCommon.LOGGER.error("Better Strongholds armorstands.json file not readable! Using default configuration...");
            return;
         }

         try {
            ArmorStandChances.instance = (ArmorStandChances)JSON.loadObjectFromJsonFile(jsonPath, ArmorStandChances.class);
         } catch (IOException var3) {
            BetterStrongholdsCommon.LOGGER.error("Error loading Better Strongholds armorstands.json file: {}", var3.toString());
            BetterStrongholdsCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void loadItemFramesJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterstrongholds", "neoforge-1_21", "itemframes.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, ItemFrameChances.get());
         } catch (IOException var4) {
            BetterStrongholdsCommon.LOGGER.error("Unable to create itemframes.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterStrongholdsCommon.LOGGER.error("Better Strongholds itemframes.json file not readable! Using default configuration...");
            return;
         }

         try {
            ItemFrameChances.instance = (ItemFrameChances)JSON.loadObjectFromJsonFile(jsonPath, ItemFrameChances.class);
         } catch (IOException var3) {
            BetterStrongholdsCommon.LOGGER.error("Error loading Better Strongholds itemframes.json file: {}", var3.toString());
            BetterStrongholdsCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void bakeConfig() {
      BetterStrongholdsCommon.CONFIG.general.enableStructureRuin = (Boolean)BSConfigNeoForge.general.enableStructureRuin.get();
      BetterStrongholdsCommon.CONFIG.general.filledPortalFrameChance = ((Double)BSConfigNeoForge.general.filledPortalFrameChance.get()).floatValue();
   }
}

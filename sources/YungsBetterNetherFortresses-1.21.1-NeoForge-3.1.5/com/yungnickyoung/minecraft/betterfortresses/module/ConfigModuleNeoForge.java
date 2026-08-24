package com.yungnickyoung.minecraft.betterfortresses.module;

import com.yungnickyoung.minecraft.betterfortresses.BetterFortressesCommon;
import com.yungnickyoung.minecraft.betterfortresses.BetterFortressesNeoForge;
import com.yungnickyoung.minecraft.betterfortresses.config.BNFConfigNeoForge;
import com.yungnickyoung.minecraft.betterfortresses.world.ItemFrameChances;
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
   public static final String CUSTOM_CONFIG_PATH = "betterfortresses";
   public static final String VERSION_PATH = "neoforge-1_21";

   public static void init(ModContainer container) {
      initCustomFiles();
      container.registerConfig(Type.COMMON, BNFConfigNeoForge.SPEC, "betterfortresses-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterFortressesNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
      loadItemFramesJSON();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BNFConfigNeoForge.SPEC) {
         bakeConfig();
         loadItemFramesJSON();
      }
   }

   private static void initCustomFiles() {
      createDirectory();
      createBaseReadMe();
      createJsonReadMe();
      loadItemFramesJSON();
   }

   private static void createDirectory() {
      File parentDir = new File(FMLPaths.CONFIGDIR.get().toString(), "betterfortresses");
      File customConfigDir = new File(parentDir, "neoforge-1_21");

      try {
         String filePath = customConfigDir.getCanonicalPath();
         if (customConfigDir.mkdirs()) {
            BetterFortressesCommon.LOGGER.info("Creating directory for additional Better Nether Fortresses configuration at {}", filePath);
         }
      } catch (IOException var3) {
         BetterFortressesCommon.LOGGER.error("ERROR creating Better Nether Fortresses config directory: {}", var3.toString());
      }
   }

   private static void createBaseReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterfortresses", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "This directory is for a few additional options for YUNG's Better Nether Fortresses.\nOptions provided may vary by version.\nThis directory contains subdirectories for supported versions. The first time you run Better Nether Fortresses, a version subdirectory will be created if that version supports advanced options.\nFor example, the first time you use Better Nether Fortresses for MC 1.21 on Forge, the 'forge-1_21' subdirectory will be created in this folder.\nIf no subdirectory for your version is created, then that version probably does not support the additional options.\nNOTE -- Most of this mod's config settings can be found in a config file outside this folder!\nFor example, on Forge 1.21 the file is 'betterfortresses-forge-1_21.toml'.\nAlso note that many of the structure's settings such as spawn rate & spawn conditions can only be modified via data pack.";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterFortressesCommon.LOGGER.error("Unable to create README file!");
         }
      }
   }

   private static void createJsonReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterfortresses", "neoforge-1_21", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "######################################\n#          itemframes.json          #\n######################################\n  This file contains ItemRandomizers describing the probability distribution of items in item frames.\nItem frames only spawn in certain rooms and hallway pieces.\nFor information on ItemRandomizers, see the bottom of this README.\n######################################\n#         ItemRandomizers           #\n######################################\nDescribes a set of items and the probability of each item being chosen.\n - entries: An object where each entry's key is an item, and each value is that item's probability of being chosen.\n      The total sum of all probabilities SHOULD NOT exceed 1.0!\n - defaultItem: The item used for any leftover probability ranges.\n      For example, if the total sum of all the probabilities of the entries is 0.6, then\n      there is a 0.4 chance of the defaultItem being selected.\nHere's an example ItemRandomizer:\n{\n  \"entries\": {\n    \"minecraft:cobblestone\": 0.25,\n    \"minecraft:air\": 0.2,\n    \"minecraft:stone_sword\": 0.1\n  },\n  \"defaultItem\": \"minecraft:iron_axe\"\n}\nThis randomizer has a 25% chance of returning cobblestone, 20% chance of choosing air,\n10% chance of choosing a stone sword, and a 100 - (25 + 20 + 10) = 45% chance of choosing iron axe (since it's the default item).\n";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterFortressesCommon.LOGGER.error("Unable to create item frames README file!");
         }
      }
   }

   private static void loadItemFramesJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterfortresses", "neoforge-1_21", "itemframes.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, ItemFrameChances.get());
         } catch (IOException var4) {
            BetterFortressesCommon.LOGGER.error("Unable to create itemframes.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterFortressesCommon.LOGGER.error("Better Nether Fortresses itemframes.json file not readable! Using default configuration...");
            return;
         }

         try {
            ItemFrameChances.instance = (ItemFrameChances)JSON.loadObjectFromJsonFile(jsonPath, ItemFrameChances.class);
         } catch (IOException var3) {
            BetterFortressesCommon.LOGGER.error("Error loading Better Nether Fortresses itemframes.json file: {}", var3.toString());
            BetterFortressesCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void bakeConfig() {
      BetterFortressesCommon.CONFIG.general.disableVanillaFortresses = (Boolean)BNFConfigNeoForge.general.disableVanillaFortresses.get();
   }
}

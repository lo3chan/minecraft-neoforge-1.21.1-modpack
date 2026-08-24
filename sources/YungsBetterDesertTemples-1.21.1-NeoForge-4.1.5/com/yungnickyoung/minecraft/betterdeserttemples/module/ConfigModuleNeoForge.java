package com.yungnickyoung.minecraft.betterdeserttemples.module;

import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesNeoForge;
import com.yungnickyoung.minecraft.betterdeserttemples.config.BDTConfigNeoForge;
import com.yungnickyoung.minecraft.betterdeserttemples.world.ArmorStandChances;
import com.yungnickyoung.minecraft.betterdeserttemples.world.ItemFrameChances;
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
   public static final String CUSTOM_CONFIG_PATH = "betterdeserttemples";
   public static final String VERSION_PATH = "neoforge-1_21";

   public static void init(ModContainer container) {
      initCustomFiles();
      container.registerConfig(Type.COMMON, BDTConfigNeoForge.SPEC, "betterdeserttemples-neoforge-1_21.toml");
      NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
      BetterDesertTemplesNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
   }

   private static void onWorldLoad(Load event) {
      bakeConfig();
      loadJSON();
   }

   private static void onConfigChange(ModConfigEvent event) {
      if (event.getConfig().getSpec() == BDTConfigNeoForge.SPEC) {
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
      loadArmorStandsJSON();
      loadItemFramesJSON();
   }

   private static void createDirectory() {
      File parentDir = new File(FMLPaths.CONFIGDIR.get().toString(), "betterdeserttemples");
      File customConfigDir = new File(parentDir, "neoforge-1_21");

      try {
         String filePath = customConfigDir.getCanonicalPath();
         if (customConfigDir.mkdirs()) {
            BetterDesertTemplesCommon.LOGGER.info("Creating directory for additional Better Desert Temples configuration at {}", filePath);
         }
      } catch (IOException var3) {
         BetterDesertTemplesCommon.LOGGER.error("ERROR creating Better Desert Temples config directory: {}", var3.toString());
      }
   }

   private static void createBaseReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterdeserttemples", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "This directory is for a few additional options for YUNG's Better Desert Temples.\nOptions provided may vary by version.\nThis directory contains subdirectories for supported versions. The first time you run Better Desert Temples, a version subdirectory will be created if that version supports advanced options.\nFor example, the first time you use Better Desert Temples for 1.21 on NeoForge, the 'neoforge-1_21' subdirectory will be created in this folder.\nIf no subdirectory for your version is created, then that version probably does not support the additional options.\nNOTE -- MOST OPTIONS CAN BE FOUND IN A CONFIG FILE OUTSIDE THIS FOLDER!\nFor example, on NeoForge 1.21 the file is 'betterdeserttemples-neoforge-1_21.toml'.";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterDesertTemplesCommon.LOGGER.error("Unable to create README file!");
         }
      }
   }

   private static void createJsonReadMe() {
      Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterdeserttemples", "neoforge-1_21", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "######################################\n#          armorstands.json          #\n######################################\n  This file contains ItemRandomizers describing the probability distribution of armor on armor stands.\nArmor stands spawn in armory rooms and wardrobe rooms.\nFor information on ItemRandomizers, see the bottom of this README.\n######################################\n#          itemframes.json          #\n######################################\n  This file contains ItemRandomizers describing the probability distribution of items in item frames.\nItem frames only spawn in food storage rooms and armoury rooms.\nFor information on ItemRandomizers, see the bottom of this README.\n######################################\n#         ItemRandomizers          #\n######################################\nDescribes a set of items and the probability of each item being chosen.\n - entries: An object where each entry's key is a item, and each value is that item's probability of being chosen.\n      The total sum of all probabilities SHOULD NOT exceed 1.0!\n - defaultItem: The item used for any leftover probability ranges.\n      For example, if the total sum of all the probabilities of the entries is 0.6, then\n      there is a 0.4 chance of the defaultItem being selected.\nHere's an example ItemRandomizer:\n\"entries\": {\n  \"minecraft:stone_axe\": 0.25,\n  \"minecraft:shield\": 0.2,\n  \"minecraft:air\": 0.1\n},\n\"defaultItem\": \"minecraft:iron_axe\"\nFor each item, this randomizer has a 25% chance of returning a stone axe, 20% chance of choosing a shield,\n10% chance of choosing air (nothing), and a 100 - (25 + 20 + 10) = 45% chance of choosing an iron axe (since it's the default item).\n";

         try {
            Files.write(path, readmeText.getBytes());
         } catch (IOException var4) {
            BetterDesertTemplesCommon.LOGGER.error("Unable to create armorstands and itemframes README file!");
         }
      }
   }

   private static void loadArmorStandsJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterdeserttemples", "neoforge-1_21", "armorstands.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, ArmorStandChances.get());
         } catch (IOException var4) {
            BetterDesertTemplesCommon.LOGGER.error("Unable to create armorstands.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterDesertTemplesCommon.LOGGER.error("Better Desert Temples armorstands.json file not readable! Using default configuration...");
            return;
         }

         try {
            ArmorStandChances.instance = (ArmorStandChances)JSON.loadObjectFromJsonFile(jsonPath, ArmorStandChances.class);
         } catch (IOException var3) {
            BetterDesertTemplesCommon.LOGGER.error("Error loading Better Desert Temples armorstands.json file: {}", var3.toString());
            BetterDesertTemplesCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void loadItemFramesJSON() {
      Path jsonPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), "betterdeserttemples", "neoforge-1_21", "itemframes.json");
      File jsonFile = new File(jsonPath.toString());
      if (!jsonFile.exists()) {
         try {
            JSON.createJsonFileFromObject(jsonPath, ItemFrameChances.get());
         } catch (IOException var4) {
            BetterDesertTemplesCommon.LOGGER.error("Unable to create itemframes.json file: {}", var4.toString());
         }
      } else {
         if (!jsonFile.canRead()) {
            BetterDesertTemplesCommon.LOGGER.error("Better Desert Temples itemframes.json file not readable! Using default configuration...");
            return;
         }

         try {
            ItemFrameChances.instance = (ItemFrameChances)JSON.loadObjectFromJsonFile(jsonPath, ItemFrameChances.class);
         } catch (IOException var3) {
            BetterDesertTemplesCommon.LOGGER.error("Error loading Better Desert Temples itemframes.json file: {}", var3.toString());
            BetterDesertTemplesCommon.LOGGER.error("Using default configuration...");
         }
      }
   }

   private static void bakeConfig() {
      BetterDesertTemplesCommon.CONFIG.general.disableVanillaPyramids = (Boolean)BDTConfigNeoForge.general.disableVanillaPyramids.get();
      BetterDesertTemplesCommon.CONFIG.general.applyMiningFatigue = (Boolean)BDTConfigNeoForge.general.applyMiningFatigue.get();
   }
}

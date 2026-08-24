package com.seibel.distanthorizons.common.wrappers.worldGeneration.params;

import com.mojang.datafixers.DataFixer;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import net.minecraft.class_1959;
import net.minecraft.class_2378;
import net.minecraft.class_2794;
import net.minecraft.class_3218;
import net.minecraft.class_3485;
import net.minecraft.class_4543;
import net.minecraft.class_5219;
import net.minecraft.class_5285;
import net.minecraft.class_5455;
import net.minecraft.class_6830;
import net.minecraft.class_7138;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

public final class GlobalWorldGenParams_fabric {
   public final IDhServerLevel dhServerLevel;
   public final class_2794 generator;
   public final class_3218 mcServerLevel;
   public final class_2378<class_1959> biomes;
   public final class_5455 registry;
   public final long worldSeed;
   public final DataFixer dataFixer;
   public final class_3485 structures;
   public final class_7138 randomState;
   public final class_5285 worldOptions;
   public final class_4543 biomeManager;
   public final class_6830 chunkScanner;

   public GlobalWorldGenParams_fabric(IDhServerLevel dhServerLevel) {
      this.dhServerLevel = dhServerLevel;
      this.mcServerLevel = ((ServerLevelWrapper_fabric)dhServerLevel.getServerLevelWrapper()).getWrappedMcObject();
      MinecraftServer server = this.mcServerLevel.method_8503();
      class_5219 worldData = server.method_27728();
      this.registry = server.method_30611();
      this.worldOptions = worldData.method_28057();
      this.biomes = this.registry.method_30530(class_7924.field_41236);
      this.worldSeed = this.worldOptions.method_28028();
      this.biomeManager = new class_4543(this.mcServerLevel, class_4543.method_27984(this.worldSeed));
      this.chunkScanner = this.mcServerLevel.method_14178().method_39777();
      this.structures = server.method_27727();
      this.generator = this.mcServerLevel.method_14178().method_12129();
      this.dataFixer = server.method_3855();
      this.randomState = this.mcServerLevel.method_14178().method_41248();
   }
}

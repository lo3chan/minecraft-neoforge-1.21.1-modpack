package com.seibel.distanthorizons.common.wrappers.worldGeneration.params;

import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.WorldGenStructFeatManager_neoforge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.StructureCheck;

public final class ThreadWorldGenParams_neoforge {
   private static final ThreadLocal<ThreadWorldGenParams_neoforge> LOCAL_PARAM_REF = new ThreadLocal<>();
   final ServerLevel level;
   public WorldGenStructFeatManager_neoforge structFeatManager = null;
   public StructureCheck structCheck;
   private static GlobalWorldGenParams_neoforge previousGlobalWorldGenParams = null;

   public static ThreadWorldGenParams_neoforge getOrMake(GlobalWorldGenParams_neoforge globalParams) {
      ThreadWorldGenParams_neoforge threadParam = LOCAL_PARAM_REF.get();
      if (threadParam != null && threadParam.level == globalParams.mcServerLevel) {
         return threadParam;
      } else {
         threadParam = new ThreadWorldGenParams_neoforge(globalParams);
         LOCAL_PARAM_REF.set(threadParam);
         return threadParam;
      }
   }

   private ThreadWorldGenParams_neoforge(GlobalWorldGenParams_neoforge param) {
      previousGlobalWorldGenParams = param;
      this.level = param.mcServerLevel;
      this.structCheck = new StructureCheck(
         param.chunkScanner,
         param.registry,
         param.structures,
         param.mcServerLevel.dimension(),
         param.generator,
         param.randomState,
         this.level,
         param.generator.getBiomeSource(),
         param.worldSeed,
         param.dataFixer
      );
   }

   public void makeStructFeatManager(WorldGenLevel genLevel, GlobalWorldGenParams_neoforge param) {
      this.structFeatManager = new WorldGenStructFeatManager_neoforge(param.worldOptions, genLevel, this.structCheck);
   }

   public void recreateStructureCheck() {
   }
}

package com.seibel.distanthorizons.common.wrappers.worldGeneration.params;

import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.WorldGenStructFeatManager_fabric;
import net.minecraft.class_3218;
import net.minecraft.class_5281;
import net.minecraft.class_6832;

public final class ThreadWorldGenParams_fabric {
   private static final ThreadLocal<ThreadWorldGenParams_fabric> LOCAL_PARAM_REF = new ThreadLocal<>();
   final class_3218 level;
   public WorldGenStructFeatManager_fabric structFeatManager = null;
   public class_6832 structCheck;
   private static GlobalWorldGenParams_fabric previousGlobalWorldGenParams = null;

   public static ThreadWorldGenParams_fabric getOrMake(GlobalWorldGenParams_fabric globalParams) {
      ThreadWorldGenParams_fabric threadParam = LOCAL_PARAM_REF.get();
      if (threadParam != null && threadParam.level == globalParams.mcServerLevel) {
         return threadParam;
      } else {
         threadParam = new ThreadWorldGenParams_fabric(globalParams);
         LOCAL_PARAM_REF.set(threadParam);
         return threadParam;
      }
   }

   private ThreadWorldGenParams_fabric(GlobalWorldGenParams_fabric param) {
      previousGlobalWorldGenParams = param;
      this.level = param.mcServerLevel;
      this.structCheck = new class_6832(
         param.chunkScanner,
         param.registry,
         param.structures,
         param.mcServerLevel.method_27983(),
         param.generator,
         param.randomState,
         this.level,
         param.generator.method_12098(),
         param.worldSeed,
         param.dataFixer
      );
   }

   public void makeStructFeatManager(class_5281 genLevel, GlobalWorldGenParams_fabric param) {
      this.structFeatManager = new WorldGenStructFeatManager_fabric(param.worldOptions, genLevel, this.structCheck);
   }

   public void recreateStructureCheck() {
   }
}

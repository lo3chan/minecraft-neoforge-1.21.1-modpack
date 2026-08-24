package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.AbstractDhApiChunkWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import java.util.ArrayList;
import net.minecraft.class_2338;
import net.minecraft.class_2791;
import net.minecraft.class_3218;
import net.minecraft.class_5742;

public class TestChunkWorldGenerator extends AbstractDhApiChunkWorldGenerator {
   private final class_3218 level;
   private final IDhApiLevelWrapper levelWrapper;

   public TestChunkWorldGenerator(class_3218 level) {
      this.level = level;
      this.levelWrapper = ServerLevelWrapper_fabric.getWrapper(level);
   }

   @Override
   public EDhApiWorldGeneratorReturnType getReturnType() {
      return EDhApiWorldGeneratorReturnType.API_CHUNKS;
   }

   @Override
   public boolean runApiValidation() {
      return true;
   }

   @Override
   public Object[] generateChunk(int chunkX, int chunkZ, EDhApiDistantGeneratorMode eDhApiDistantGeneratorMode) {
      class_2791 chunk = this.level.method_8497(chunkX, chunkZ);
      return new Object[]{chunk, this.level};
   }

   @Override
   public DhApiChunk generateApiChunk(int chunkPosX, int chunkPosZ, EDhApiDistantGeneratorMode generatorMode) {
      class_2791 chunk = this.level.method_8497(chunkPosX, chunkPosZ);
      int minBuildHeight = this.levelWrapper.getMinHeight();
      int maxBuildHeight = this.levelWrapper.getMaxHeight();
      DhApiChunk apiChunk = DhApiChunk.create(chunkPosX, chunkPosZ, minBuildHeight, maxBuildHeight);

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            ArrayList<DhApiTerrainDataPoint> dataPoints = new ArrayList<>();
            IDhApiBlockStateWrapper block = null;
            IDhApiBiomeWrapper biome = null;

            for (int y = minBuildHeight; y < maxBuildHeight; y++) {
               block = DhApi.Delayed.wrapperFactory.getBlockStateWrapper(new Object[]{chunk.method_8320(new class_2338(x, y, z))}, this.levelWrapper);
               biome = DhApi.Delayed.wrapperFactory
                  .getBiomeWrapper(
                     new Object[]{chunk.method_16359(class_5742.method_33100(x), class_5742.method_33100(y), class_5742.method_33100(z))}, this.levelWrapper
                  );
               dataPoints.add(DhApiTerrainDataPoint.create((byte)0, 0, 15, y, y + 1, block, biome));
            }

            apiChunk.setDataPoints(x, z, dataPoints);
         }
      }

      return apiChunk;
   }

   @Override
   public void preGeneratorTaskStart() {
   }

   @Override
   public void close() {
   }
}

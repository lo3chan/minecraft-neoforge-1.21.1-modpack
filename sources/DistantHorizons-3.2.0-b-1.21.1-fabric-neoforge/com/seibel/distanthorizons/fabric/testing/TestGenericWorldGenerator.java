package com.seibel.distanthorizons.fabric.testing;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.EDhApiDetailLevel;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class TestGenericWorldGenerator implements IDhApiWorldGenerator {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final IDhApiLevelWrapper levelWrapper;

   public TestGenericWorldGenerator(IDhApiLevelWrapper levelWrapper) {
      this.levelWrapper = levelWrapper;
   }

   @Override
   public byte getSmallestDataDetailLevel() {
      return EDhApiDetailLevel.BLOCK.detailLevel;
   }

   @Override
   public byte getLargestDataDetailLevel() {
      return (byte)(EDhApiDetailLevel.BLOCK.detailLevel + 12);
   }

   @Override
   public EDhApiWorldGeneratorReturnType getReturnType() {
      return EDhApiWorldGeneratorReturnType.API_DATA_SOURCES;
   }

   @Override
   public boolean runApiValidation() {
      return true;
   }

   @Override
   public CompletableFuture<Void> generateLod(
      int chunkPosMinX,
      int chunkPosMinZ,
      int posX,
      int posZ,
      byte detailLevel,
      IDhApiFullDataSource pooledFullDataSource,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<IDhApiFullDataSource> resultConsumer
   ) {
      return CompletableFuture.runAsync(
         () -> this.generateInternal(chunkPosMinX, chunkPosMinZ, posX, posZ, detailLevel, pooledFullDataSource, generatorMode, resultConsumer),
         worldGeneratorThreadPool
      );
   }

   public void generateInternal(
      int chunkPosMinX,
      int chunkPosMinZ,
      int posX,
      int posZ,
      byte detailLevel,
      IDhApiFullDataSource pooledFullDataSource,
      EDhApiDistantGeneratorMode generatorMode,
      Consumer<IDhApiFullDataSource> resultConsumer
   ) {
      IDhApiBiomeWrapper biome;
      IDhApiBlockStateWrapper colorBlock;
      IDhApiBlockStateWrapper borderBlock;
      IDhApiBlockStateWrapper airBlock;
      int maxHeight;
      try {
         biome = DhApi.Delayed.wrapperFactory.getBiomeWrapper("minecraft:plains", this.levelWrapper);
         airBlock = DhApi.Delayed.wrapperFactory.getAirBlockStateWrapper();
         borderBlock = DhApi.Delayed.wrapperFactory.getDefaultBlockStateWrapper("minecraft:stone", this.levelWrapper);
         String blockResourceLocation;
         switch (detailLevel) {
            case 0:
               blockResourceLocation = "minecraft:red_wool";
               maxHeight = 20;
               break;
            case 1:
               blockResourceLocation = "minecraft:orange_wool";
               maxHeight = 30;
               break;
            case 2:
               blockResourceLocation = "minecraft:yellow_wool";
               maxHeight = 40;
               break;
            case 3:
               blockResourceLocation = "minecraft:lime_wool";
               maxHeight = 50;
               break;
            case 4:
               blockResourceLocation = "minecraft:cyan_wool";
               maxHeight = 60;
               break;
            case 5:
               blockResourceLocation = "minecraft:blue_wool";
               maxHeight = 70;
               break;
            case 6:
               blockResourceLocation = "minecraft:magenta_wool";
               maxHeight = 80;
               break;
            case 7:
               blockResourceLocation = "minecraft:white_wool";
               maxHeight = 90;
               break;
            case 8:
               blockResourceLocation = "minecraft:gray_wool";
               maxHeight = 100;
               break;
            default:
               blockResourceLocation = "minecraft:black_wool";
               maxHeight = 110;
         }

         colorBlock = DhApi.Delayed.wrapperFactory.getDefaultBlockStateWrapper(blockResourceLocation, this.levelWrapper);
      } catch (IOException var19) {
         LOGGER.error("Failed to get biome/block: " + var19.getMessage(), var19);
         return;
      }

      ArrayList<DhApiTerrainDataPoint> dataPoints = new ArrayList<>();
      int width = pooledFullDataSource.getWidthInDataColumns();

      for (int x = 0; x < width; x++) {
         for (int z = 0; z < width; z++) {
            dataPoints.clear();
            IDhApiBlockStateWrapper block = colorBlock;
            if (x == 0 || x == width - 1 || z == 0 || z == width - 1) {
               block = borderBlock;
            }

            dataPoints.add(DhApiTerrainDataPoint.create((byte)0, 0, 15, 0, maxHeight, block, biome));
            dataPoints.add(DhApiTerrainDataPoint.create((byte)0, 0, 15, maxHeight, 256, airBlock, biome));
            pooledFullDataSource.setApiDataPointColumn(x, z, dataPoints);
         }
      }

      resultConsumer.accept(pooledFullDataSource);
   }

   @Override
   public void preGeneratorTaskStart() {
   }

   @Override
   public void close() {
   }
}

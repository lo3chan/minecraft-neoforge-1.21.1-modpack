package com.seibel.distanthorizons.core.util.threading;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import java.util.concurrent.ThreadPoolExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadPoolUtil {
   private static PriorityTaskPicker taskPicker;
   private static PriorityTaskPicker.Executor fileHandlerThreadPool;
   private static PriorityTaskPicker.Executor renderSectionLoadThreadPool;
   private static PriorityTaskPicker.Executor updatePropagatorThreadPool;
   private static PriorityTaskPicker.Executor worldGenThreadPool;
   public static final String CLEANUP_THREAD_NAME = "Cleanup";
   private static final ThreadPoolExecutor cleanupThreadPool = ThreadUtil.makeSingleDaemonThreadPool("Cleanup");
   public static final String BEACON_CULLING_THREAD_NAME = "Beacon Culling";
   private static ThreadPoolExecutor beaconCullingThreadPool;
   private static PriorityTaskPicker.Executor networkCompressionThreadPool;
   private static ThreadPoolExecutor networkClientHandlerThreadPool;
   public static final String FULL_DATA_MIGRATION_THREAD_NAME = "Full Data Migration";
   private static ThreadPoolExecutor fullDataMigrationThreadPool;
   private static PriorityTaskPicker.Executor chunkToLodBuilderThreadPool;

   @Nullable
   public static PriorityTaskPicker.Executor getFileHandlerExecutor() {
      return fileHandlerThreadPool;
   }

   @Nullable
   public static PriorityTaskPicker.Executor getRenderLoadingExecutor() {
      return renderSectionLoadThreadPool;
   }

   @Nullable
   public static PriorityTaskPicker.Executor getUpdatePropagatorExecutor() {
      return updatePropagatorThreadPool;
   }

   @Nullable
   public static PriorityTaskPicker.Executor getWorldGenExecutor() {
      return worldGenThreadPool;
   }

   @NotNull
   public static ThreadPoolExecutor getCleanupExecutor() {
      return cleanupThreadPool;
   }

   @Nullable
   public static ThreadPoolExecutor getBeaconCullingExecutor() {
      return beaconCullingThreadPool;
   }

   @Nullable
   public static PriorityTaskPicker.Executor getNetworkCompressionExecutor() {
      return networkCompressionThreadPool;
   }

   @Nullable
   public static ThreadPoolExecutor networkClientHandlerExecutor() {
      return networkClientHandlerThreadPool;
   }

   @Nullable
   public static ThreadPoolExecutor getFullDataMigrationExecutor() {
      return fullDataMigrationThreadPool;
   }

   @Nullable
   public static PriorityTaskPicker.Executor getChunkToLodBuilderExecutor() {
      return chunkToLodBuilderThreadPool;
   }

   public static void setupThreadPools() {
      if (taskPicker != null) {
         taskPicker.shutdownNow();
      }

      taskPicker = new PriorityTaskPicker();
      networkCompressionThreadPool = taskPicker.createExecutor("Network Compression");
      networkClientHandlerThreadPool = ThreadUtil.makeSingleThreadPool("Network Client Handler");
      fileHandlerThreadPool = taskPicker.createExecutor("IO");
      renderSectionLoadThreadPool = taskPicker.createExecutor("Render Loader");
      chunkToLodBuilderThreadPool = taskPicker.createExecutor("LOD Builder");
      updatePropagatorThreadPool = taskPicker.createExecutor("Update Propagator", ThreadPoolUtil::worldGenThreadsCanRun);
      worldGenThreadPool = taskPicker.createExecutor("World Gen", ThreadPoolUtil::worldGenThreadsCanRun);
      if (beaconCullingThreadPool != null) {
         beaconCullingThreadPool.shutdown();
      }

      beaconCullingThreadPool = ThreadUtil.makeSingleThreadPool("Beacon Culling");
      if (fullDataMigrationThreadPool != null) {
         fullDataMigrationThreadPool.shutdown();
      }

      fullDataMigrationThreadPool = ThreadUtil.makeSingleThreadPool("Full Data Migration");
   }

   public static void shutdownThreadPools() {
      networkClientHandlerThreadPool.shutdownNow();
      taskPicker.shutdownNow();
      beaconCullingThreadPool.shutdown();
      fullDataMigrationThreadPool.shutdown();
   }

   public static boolean worldGenThreadsCanRun() {
      double cameraSpeed = ClientApi.INSTANCE.getAvgCameraSpeed();
      double maxAllowedSpeed = 20.0;
      return !(cameraSpeed > maxAllowedSpeed);
   }
}

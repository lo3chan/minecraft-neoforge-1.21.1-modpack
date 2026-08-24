package com.seibel.distanthorizons.core.util.delayedSaveCache;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelayedBeaconSaveCache extends AbstractDelayedSaveCache<BeaconBeamDTO, DelayedBeaconSaveCache.BeaconSaveObjContainer> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().name(DelayedBeaconSaveCache.class.getSimpleName()).build();
   @NotNull
   private final DelayedBeaconSaveCache.ISaveBeaconsFunc saveBeaconsFunc;

   public DelayedBeaconSaveCache(@NotNull DelayedBeaconSaveCache.ISaveBeaconsFunc saveBeaconsFunc, int saveDelayInMs) {
      super(saveDelayInMs);
      this.saveBeaconsFunc = saveBeaconsFunc;
   }

   public void queueBeaconBeamUpdatesForChunkPos(@NotNull DhChunkPos chunkPos, @NotNull List<BeaconBeamDTO> activeBeamList) {
      long pos = DhSectionPos.encodeContaining((byte)6, chunkPos);
      DelayedBeaconSaveCache.BeaconSaveObjContainer container = (DelayedBeaconSaveCache.BeaconSaveObjContainer)super.writeToMemoryAndQueueSave(pos, null);
      ReentrantLock lockForPos = this.saveLockContainer.getLockForPos(pos);

      try {
         lockForPos.lock();
         container.addBeaconsAtChunkPos(chunkPos, activeBeamList);
      } finally {
         lockForPos.unlock();
      }
   }

   @Deprecated
   public DelayedBeaconSaveCache.BeaconSaveObjContainer writeToMemoryAndQueueSave(long inputPos, BeaconBeamDTO inputObj) {
      throw new UnsupportedOperationException("Use queueBeaconBeamUpdatesForChunkPos instead");
   }

   protected DelayedBeaconSaveCache.BeaconSaveObjContainer createEmptySaveObjContainer(long inputPos) {
      return new DelayedBeaconSaveCache.BeaconSaveObjContainer(inputPos);
   }

   protected void handleDataSourceRemoval(@NotNull DelayedBeaconSaveCache.BeaconSaveObjContainer saveContainer) {
      for (DhChunkPos chunkPos : saveContainer.beaconsByBlockPosByChunkPos.keySet()) {
         if (chunkPos != null) {
            HashMap<DhBlockPos, BeaconBeamDTO> beaconsByBlockPos = saveContainer.beaconsByBlockPosByChunkPos.get(chunkPos);
            ArrayList<BeaconBeamDTO> beaconList = new ArrayList<>(beaconsByBlockPos.values());
            int minBlockX = chunkPos.getMinBlockX();
            int minBlockZ = chunkPos.getMinBlockZ();
            int maxBlockX = chunkPos.getMaxBlockX();
            int maxBlockZ = chunkPos.getMaxBlockZ();
            this.saveBeaconsFunc.updateBeaconBeamsBetweenBlockPos(saveContainer.pos, minBlockX, maxBlockX, minBlockZ, maxBlockZ, beaconList);
         }
      }
   }

   public static class BeaconSaveObjContainer extends AbstractSaveObjContainer<BeaconBeamDTO> {
      public final long pos;
      public final HashMap<DhChunkPos, HashMap<DhBlockPos, BeaconBeamDTO>> beaconsByBlockPosByChunkPos = new HashMap<>();

      public BeaconSaveObjContainer(long pos) {
         this.pos = pos;
      }

      public void addBeaconsAtChunkPos(DhChunkPos chunkPos, List<BeaconBeamDTO> activeBeamList) {
         HashMap<DhBlockPos, BeaconBeamDTO> beaconsByBlockPos = this.beaconsByBlockPosByChunkPos.get(chunkPos);
         if (!this.beaconsByBlockPosByChunkPos.containsKey(chunkPos)) {
            beaconsByBlockPos = new HashMap<>();
            this.beaconsByBlockPosByChunkPos.put(chunkPos, beaconsByBlockPos);
         }

         for (BeaconBeamDTO beacon : activeBeamList) {
            beaconsByBlockPos.put(beacon.blockPos, beacon);
         }
      }

      public void update(@Nullable BeaconBeamDTO newObj) {
      }
   }

   @FunctionalInterface
   public interface ISaveBeaconsFunc {
      void updateBeaconBeamsBetweenBlockPos(long l, int i, int j, int k, int m, List<BeaconBeamDTO> list);
   }
}

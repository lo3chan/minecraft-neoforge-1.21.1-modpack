package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.fullDatafile.GeneratedFullDataSourceProvider;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.sql.repo.BeaconBeamRepo;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDhLevel extends AutoCloseable, GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener {
   @NotNull
   ILevelWrapper getLevelWrapper();

   int getChunkHash(DhChunkPos dhChunkPos);

   void updateChunkAsync(IChunkWrapper iChunkWrapper, int i);

   default void updateBeaconBeamsForChunk(IChunkWrapper chunkToUpdate, ArrayList<IChunkWrapper> nearbyChunkList) {
      List<BeaconBeamDTO> activeBeamList = chunkToUpdate.getAllActiveBeacons(nearbyChunkList);
      this.updateBeaconBeamsForChunkPos(chunkToUpdate.getChunkPos(), activeBeamList);
   }

   void updateBeaconBeamsForChunkPos(DhChunkPos dhChunkPos, List<BeaconBeamDTO> list);

   void updateBeaconBeamsForSectionPos(long l, List<BeaconBeamDTO> list);

   @Nullable
   BeaconBeamRepo getBeaconBeamRepo();

   FullDataSourceProviderV2 getFullDataProvider();

   ISaveStructure getSaveStructure();

   CompletableFuture<Void> updateDataSourcesAsync(FullDataSourceV2 fullDataSourceV2);

   void addDebugMenuStringsToList(List<String> list);

   @Nullable
   IDhGenericRenderer getGenericRenderer();

   @Nullable
   RenderBufferHandler getRenderBufferHandler();
}

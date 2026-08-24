package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerStateManager;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DhServerLevel extends AbstractDhServerLevel {
   public DhServerLevel(ISaveStructure saveStructure, IServerLevelWrapper serverLevelWrapper, ServerPlayerStateManager serverPlayerStateManager) throws SQLException, IOException {
      super(saveStructure, serverLevelWrapper, serverPlayerStateManager);
   }

   @Override
   public boolean shouldDoWorldGen() {
      return super.shouldDoWorldGen();
   }

   @Override
   public DhBlockPos2D getTargetPosForGeneration() {
      DhBlockPos2D targetPos = super.getTargetPosForGeneration();
      return targetPos == null ? DhBlockPos2D.ZERO : targetPos;
   }

   @Override
   public IDhGenericRenderer getGenericRenderer() {
      return null;
   }

   @Override
   public RenderBufferHandler getRenderBufferHandler() {
      return null;
   }

   @Override
   public void addDebugMenuStringsToList(List<String> messageList) {
      messageList.add("[" + this.serverLevelWrapper.getDhIdentifier() + "]");
      super.addDebugMenuStringsToList(messageList);
   }

   @Override
   public String toString() {
      return "DhServerLevel{" + this.serverLevelWrapper.getKeyedLevelDimensionName() + "}";
   }

   @Override
   public void close() {
      super.close();
      this.serverside.close();
   }
}

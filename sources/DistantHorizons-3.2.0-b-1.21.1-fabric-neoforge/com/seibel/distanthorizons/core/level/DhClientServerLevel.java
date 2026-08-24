package com.seibel.distanthorizons.core.level;

import com.seibel.distanthorizons.core.config.eventHandlers.IgnoredDimensionCsvHandler;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerStateManager;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DhClientServerLevel extends AbstractDhServerLevel implements IDhClientLevel {
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   public final ClientLevelModule clientside;

   public DhClientServerLevel(ISaveStructure saveStructure, IServerLevelWrapper serverLevelWrapper, ServerPlayerStateManager serverPlayerStateManager) throws SQLException, IOException {
      super(saveStructure, serverLevelWrapper, serverPlayerStateManager, false);
      this.serverLevelWrapper.setDhLevel(this);
      this.clientside = new ClientLevelModule(this);
      this.runRepoReliantSetup();
   }

   @Override
   public void clientTick() {
      IClientLevelWrapper clientLevelWrapper = MC_CLIENT.getWrappedClientLevel();
      if (clientLevelWrapper != null && clientLevelWrapper.getDhLevel() == this) {
         this.clientside.clientTick();
      }
   }

   public void startRenderer() {
      this.clientside.startRenderer();
   }

   public void stopRenderer() {
      this.clientside.stopRenderer();
   }

   @Override
   public boolean isRendering() {
      return this.clientside.isRendering();
   }

   @Nullable
   @Override
   public IClientLevelWrapper getClientLevelWrapper() {
      return MC_CLIENT.getWrappedClientLevel();
   }

   @Override
   public void clearRenderCache() {
      this.clientside.clearRenderCache();
   }

   @Override
   public void addDebugMenuStringsToList(List<String> messageList) {
      String o = "§6";
      String y = "§e";
      String g = "§a";
      String r = "§c";
      String cf = "§r";
      String dimName = this.serverLevelWrapper.getDhIdentifier();
      boolean rendering = this.clientside.isRendering() && !IgnoredDimensionCsvHandler.INSTANCE.dimensionNameShouldBeIgnored(dimName);
      String renderingString = rendering ? g + "yes" + cf : r + "no" + cf;
      messageList.add("[" + y + dimName + cf + "] rendering: " + renderingString);
      super.addDebugMenuStringsToList(messageList);
   }

   @Override
   public IDhGenericRenderer getGenericRenderer() {
      return this.clientside.genericRenderer;
   }

   @Override
   public RenderBufferHandler getRenderBufferHandler() {
      ClientLevelModule.ClientRenderState renderState = this.clientside.ClientRenderStateRef.get();
      return renderState != null ? renderState.renderBufferHandler : null;
   }

   @Override
   public void onWorldGenTaskComplete(long pos) {
      super.onWorldGenTaskComplete(pos);
      this.clientside.reloadPos(pos);
   }

   @Override
   public String toString() {
      return "DhClientServerLevel{" + this.serverLevelWrapper.getKeyedLevelDimensionName() + "}";
   }

   @Override
   public void close() {
      this.clientside.close();
      super.close();
      this.serverside.close();
      LOGGER.info("Closed " + this.getClass().getSimpleName() + " for " + this.getServerLevelWrapper());
   }
}

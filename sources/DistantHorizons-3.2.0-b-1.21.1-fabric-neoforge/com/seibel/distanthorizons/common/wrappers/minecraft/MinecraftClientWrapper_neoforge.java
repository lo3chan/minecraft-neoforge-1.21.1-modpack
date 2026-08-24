package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.common.wrappers.gui.NativeDialogUtil;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.File;
import net.minecraft.CrashReport;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MinecraftClientWrapper_neoforge extends AbstractMinecraftSharedWrapper_neoforge implements IMinecraftClientWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Minecraft MINECRAFT = Minecraft.getInstance();
   public static final MinecraftClientWrapper_neoforge INSTANCE = new MinecraftClientWrapper_neoforge();
   private ProfilerWrapper_neoforge profilerWrapper;

   @Override
   public boolean hasSinglePlayerServer() {
      return MINECRAFT.hasSingleplayerServer();
   }

   @Override
   public boolean clientConnectedToDedicatedServer() {
      return this.hasServerConnection() && !this.hasSinglePlayerServer();
   }

   @Override
   public boolean connectedToReplay() {
      return !this.hasServerConnection() && !this.hasSinglePlayerServer();
   }

   private boolean hasServerConnection() {
      return MINECRAFT.getCurrentServer() != null;
   }

   @Override
   public String getCurrentServerName() {
      if (this.connectedToReplay()) {
         return "REPLAY";
      } else {
         ServerData server = MINECRAFT.getCurrentServer();
         return server != null ? server.name : "NULL";
      }
   }

   @Override
   public String getCurrentServerIp() {
      if (this.connectedToReplay()) {
         return "";
      } else {
         ServerData server = this.getCurrentServerData();
         return this.getServerIp(server);
      }
   }

   @Override
   public String getCurrentServerVersion() {
      ServerData server = this.getCurrentServerData();
      return this.getServerVersion(server);
   }

   private ServerData getCurrentServerData() {
      return MINECRAFT.getCurrentServer();
   }

   private String getServerIp(ServerData server) {
      return server == null ? "NA" : server.ip;
   }

   private String getServerVersion(ServerData server) {
      return server == null ? "UNKOWN" : server.version.getString();
   }

   public LocalPlayer getPlayer() {
      return MINECRAFT.player;
   }

   @Override
   public boolean playerExists() {
      return MINECRAFT.player != null;
   }

   @Override
   public DhBlockPos getPlayerBlockPos() {
      LocalPlayer player = this.getPlayer();
      if (player == null) {
         return new DhBlockPos(0, 0, 0);
      } else {
         if (MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS != null) {
            DhBlockPos pos = MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS.getActualPlayerBlockPos();
            if (pos != null) {
               return pos;
            }
         }

         BlockPos playerPos = player.blockPosition();
         return new DhBlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
      }
   }

   @Override
   public DhChunkPos getPlayerChunkPos() {
      LocalPlayer player = this.getPlayer();
      if (player == null) {
         return new DhChunkPos(0, 0);
      } else {
         if (MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS != null) {
            DhChunkPos pos = MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS.getActualPlayerChunkPos();
            if (pos != null) {
               return pos;
            }
         }

         ChunkPos playerPos = player.chunkPosition();
         return new DhChunkPos(playerPos.x, playerPos.z);
      }
   }

   @Nullable
   @Override
   public IClientLevelWrapper getWrappedClientLevel() {
      return this.getWrappedClientLevel(false);
   }

   @Nullable
   @Override
   public IClientLevelWrapper getWrappedClientLevel(boolean bypassLevelKeyManager) {
      if (!bypassLevelKeyManager && MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS != null) {
         IClientLevelWrapper level = MinecraftClientWrapper$DelayedAccessors_neoforge.IMMERSIVE_PORTALS.getActualClientLevelWrapper();
         if (level != null) {
            return level;
         }
      }

      ClientLevel level = MINECRAFT.level;
      return level == null ? null : ClientLevelWrapper_neoforge.getWrapper(level, bypassLevelKeyManager);
   }

   @Override
   public void sendChatMessage(String string) {
      LocalPlayer player = this.getPlayer();
      if (player != null) {
         player.displayClientMessage(Component.translatable(string), false);
      }
   }

   @Override
   public void sendOverlayMessage(String string) {
      LocalPlayer player = this.getPlayer();
      if (player != null) {
         player.displayClientMessage(Component.translatable(string), true);
      }
   }

   @Override
   public void disableVanillaClouds() {
      LOGGER.info("Disabling vanilla clouds... This is done to prevent vanilla clouds from rendering on top of Distant Horizons LODs.");
      MINECRAFT.options.cloudStatus().set(CloudStatus.OFF);
   }

   @Override
   public void disableVanillaChunkFadeIn() {
      String message = "Disabling vanilla chunk fade in... This is done to prevent vanilla chunks from flashing on the Distant Horizons boarder when moving (which is distracting).";
   }

   @Override
   public void disableFabulousTransparency() {
      String reasoning = "This is done to fix vanilla chunks (specifically water blocks) not fading into Distant Horizons LODs when DH's 'Vanilla Fade' option is enabled.";
      LOGGER.info("Disabling fabulous graphics... " + reasoning);
      GraphicsStatus oldGraphicsStatus = (GraphicsStatus)MINECRAFT.options.graphicsMode().get();
      if (oldGraphicsStatus == GraphicsStatus.FABULOUS) {
         MINECRAFT.options.graphicsMode().set(GraphicsStatus.FANCY);
      }
   }

   public long getGlfwWindowId() {
      return MINECRAFT.getWindow().getWindow();
   }

   @Override
   public IProfilerWrapper getProfiler() {
      ProfilerFiller profiler = MINECRAFT.getProfiler();
      if (this.profilerWrapper == null) {
         this.profilerWrapper = new ProfilerWrapper_neoforge(profiler);
      } else if (profiler != this.profilerWrapper.profiler) {
         this.profilerWrapper.profiler = profiler;
      }

      return this.profilerWrapper;
   }

   @Override
   public void crashMinecraft(String errorMessage, Throwable exception) {
      LOGGER.fatal("Distant Horizons had the following error: [" + errorMessage + "]. Crashing Minecraft...", exception);
      this.executeOnRenderThread(() -> {
         CrashReport report = new CrashReport(errorMessage, exception);
         MINECRAFT.delayCrash(report);
      });
   }

   @Override
   public void executeOnRenderThread(Runnable runnable) {
      MINECRAFT.execute(runnable);
   }

   @Override
   public void showDialog(String title, String message, String dialogType, String iconType) {
      NativeDialogUtil.showDialog(title, message, dialogType, iconType);
   }

   @Override
   public Object getOptionsObject() {
      return MINECRAFT.options;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public File getInstallationDirectory() {
      return MINECRAFT.gameDirectory;
   }

   @Override
   public int getPlayerCount() {
      return MINECRAFT.getSingleplayerServer() == null ? 1 : MINECRAFT.getSingleplayerServer().getPlayerCount();
   }

   @Nullable
   @Override
   public IServerLevelWrapper getLevelWrapper(String dimensionResourceLocation) {
      if (!this.hasSinglePlayerServer()) {
         return null;
      } else {
         ResourceKey<Level> dimensionKey = this.deserializeDimensionResourceKey(dimensionResourceLocation);
         ServerLevel mcLevel = MINECRAFT.getSingleplayerServer().getLevel(dimensionKey);
         return ServerLevelWrapper_neoforge.getWrapper(mcLevel);
      }
   }
}

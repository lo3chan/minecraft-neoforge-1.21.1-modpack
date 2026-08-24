package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.common.wrappers.gui.NativeDialogUtil;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.File;
import net.minecraft.class_128;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_3695;
import net.minecraft.class_4063;
import net.minecraft.class_5321;
import net.minecraft.class_5365;
import net.minecraft.class_638;
import net.minecraft.class_642;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;

public class MinecraftClientWrapper_fabric extends AbstractMinecraftSharedWrapper_fabric implements IMinecraftClientWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_310 MINECRAFT = class_310.method_1551();
   public static final MinecraftClientWrapper_fabric INSTANCE = new MinecraftClientWrapper_fabric();
   private ProfilerWrapper_fabric profilerWrapper;

   @Override
   public boolean hasSinglePlayerServer() {
      return MINECRAFT.method_1496();
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
      return MINECRAFT.method_1558() != null;
   }

   @Override
   public String getCurrentServerName() {
      if (this.connectedToReplay()) {
         return "REPLAY";
      } else {
         class_642 server = MINECRAFT.method_1558();
         return server != null ? server.field_3752 : "NULL";
      }
   }

   @Override
   public String getCurrentServerIp() {
      if (this.connectedToReplay()) {
         return "";
      } else {
         class_642 server = this.getCurrentServerData();
         return this.getServerIp(server);
      }
   }

   @Override
   public String getCurrentServerVersion() {
      class_642 server = this.getCurrentServerData();
      return this.getServerVersion(server);
   }

   private class_642 getCurrentServerData() {
      return MINECRAFT.method_1558();
   }

   private String getServerIp(class_642 server) {
      return server == null ? "NA" : server.field_3761;
   }

   private String getServerVersion(class_642 server) {
      return server == null ? "UNKOWN" : server.field_3760.getString();
   }

   public class_746 getPlayer() {
      return MINECRAFT.field_1724;
   }

   @Override
   public boolean playerExists() {
      return MINECRAFT.field_1724 != null;
   }

   @Override
   public DhBlockPos getPlayerBlockPos() {
      class_746 player = this.getPlayer();
      if (player == null) {
         return new DhBlockPos(0, 0, 0);
      } else {
         if (MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS != null) {
            DhBlockPos pos = MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS.getActualPlayerBlockPos();
            if (pos != null) {
               return pos;
            }
         }

         class_2338 playerPos = player.method_24515();
         return new DhBlockPos(playerPos.method_10263(), playerPos.method_10264(), playerPos.method_10260());
      }
   }

   @Override
   public DhChunkPos getPlayerChunkPos() {
      class_746 player = this.getPlayer();
      if (player == null) {
         return new DhChunkPos(0, 0);
      } else {
         if (MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS != null) {
            DhChunkPos pos = MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS.getActualPlayerChunkPos();
            if (pos != null) {
               return pos;
            }
         }

         class_1923 playerPos = player.method_31476();
         return new DhChunkPos(playerPos.field_9181, playerPos.field_9180);
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
      if (!bypassLevelKeyManager && MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS != null) {
         IClientLevelWrapper level = MinecraftClientWrapper$DelayedAccessors_fabric.IMMERSIVE_PORTALS.getActualClientLevelWrapper();
         if (level != null) {
            return level;
         }
      }

      class_638 level = MINECRAFT.field_1687;
      return level == null ? null : ClientLevelWrapper_fabric.getWrapper(level, bypassLevelKeyManager);
   }

   @Override
   public void sendChatMessage(String string) {
      class_746 player = this.getPlayer();
      if (player != null) {
         player.method_7353(class_2561.method_43471(string), false);
      }
   }

   @Override
   public void sendOverlayMessage(String string) {
      class_746 player = this.getPlayer();
      if (player != null) {
         player.method_7353(class_2561.method_43471(string), true);
      }
   }

   @Override
   public void disableVanillaClouds() {
      LOGGER.info("Disabling vanilla clouds... This is done to prevent vanilla clouds from rendering on top of Distant Horizons LODs.");
      MINECRAFT.field_1690.method_42528().method_41748(class_4063.field_18162);
   }

   @Override
   public void disableVanillaChunkFadeIn() {
      String message = "Disabling vanilla chunk fade in... This is done to prevent vanilla chunks from flashing on the Distant Horizons boarder when moving (which is distracting).";
   }

   @Override
   public void disableFabulousTransparency() {
      String reasoning = "This is done to fix vanilla chunks (specifically water blocks) not fading into Distant Horizons LODs when DH's 'Vanilla Fade' option is enabled.";
      LOGGER.info("Disabling fabulous graphics... " + reasoning);
      class_5365 oldGraphicsStatus = (class_5365)MINECRAFT.field_1690.method_42534().method_41753();
      if (oldGraphicsStatus == class_5365.field_25429) {
         MINECRAFT.field_1690.method_42534().method_41748(class_5365.field_25428);
      }
   }

   public long getGlfwWindowId() {
      return MINECRAFT.method_22683().method_4490();
   }

   @Override
   public IProfilerWrapper getProfiler() {
      class_3695 profiler = MINECRAFT.method_16011();
      if (this.profilerWrapper == null) {
         this.profilerWrapper = new ProfilerWrapper_fabric(profiler);
      } else if (profiler != this.profilerWrapper.profiler) {
         this.profilerWrapper.profiler = profiler;
      }

      return this.profilerWrapper;
   }

   @Override
   public void crashMinecraft(String errorMessage, Throwable exception) {
      LOGGER.fatal("Distant Horizons had the following error: [" + errorMessage + "]. Crashing Minecraft...", exception);
      this.executeOnRenderThread(() -> {
         class_128 report = new class_128(errorMessage, exception);
         MINECRAFT.method_1494(report);
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
      return MINECRAFT.field_1690;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public File getInstallationDirectory() {
      return MINECRAFT.field_1697;
   }

   @Override
   public int getPlayerCount() {
      return MINECRAFT.method_1576() == null ? 1 : MINECRAFT.method_1576().method_3788();
   }

   @Nullable
   @Override
   public IServerLevelWrapper getLevelWrapper(String dimensionResourceLocation) {
      if (!this.hasSinglePlayerServer()) {
         return null;
      } else {
         class_5321<class_1937> dimensionKey = this.deserializeDimensionResourceKey(dimensionResourceLocation);
         class_3218 mcLevel = MINECRAFT.method_1576().method_3847(dimensionKey);
         return ServerLevelWrapper_fabric.getWrapper(mcLevel);
      }
   }
}

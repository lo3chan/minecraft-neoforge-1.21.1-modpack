package com.seibel.distanthorizons.core.wrapperInterfaces.minecraft;

import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import org.jetbrains.annotations.Nullable;

public interface IMinecraftClientWrapper extends IBindable {
   boolean hasSinglePlayerServer();

   boolean clientConnectedToDedicatedServer();

   boolean connectedToReplay();

   String getCurrentServerName();

   String getCurrentServerIp();

   String getCurrentServerVersion();

   boolean playerExists();

   DhBlockPos getPlayerBlockPos();

   DhChunkPos getPlayerChunkPos();

   @Nullable
   IClientLevelWrapper getWrappedClientLevel();

   @Nullable
   IClientLevelWrapper getWrappedClientLevel(boolean bl);

   void sendChatMessage(String string);

   void sendOverlayMessage(String string);

   void disableVanillaClouds();

   void disableVanillaChunkFadeIn();

   void disableFabulousTransparency();

   IProfilerWrapper getProfiler();

   void crashMinecraft(String string, Throwable throwable);

   void executeOnRenderThread(Runnable runnable);

   void showDialog(String string, String string2, String string3, String string4);

   Object getOptionsObject();
}

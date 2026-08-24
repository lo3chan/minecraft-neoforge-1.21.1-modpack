package com.seibel.distanthorizons.core.logging.f3;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.ModJarInfo;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class F3Screen {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   public static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();

   public static void addStringToDisplay(List<String> messageList) {
      String r = "§c";
      String y = "§e";
      String a = "§b";
      String cf = "§r";
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world != null) {
         Iterable<? extends IDhLevel> levelIterator = world.getAllLoadedLevels();
         messageList.add("");
         messageList.add("Distant Horizons: 3.2.0-b");
         if (ModInfo.IS_DEV_BUILD) {
            messageList.add("Build: " + StringUtil.shortenString(ModJarInfo.Git_Commit, 8) + " (" + ModJarInfo.Git_Branch + ")");
         }

         if (ClientApi.INSTANCE.lastRenderParamValidationMessage != null) {
            messageList.add("Render Validation Err: " + r + ClientApi.INSTANCE.lastRenderParamValidationMessage + cf);
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showPlayerPos.get()) {
            if (MC_CLIENT != null) {
               byte requestedDetailLevel = Config.Client.Advanced.Debugging.F3Screen.playerPosSectionDetailLevel.get().byteValue();
               long sectionPos = DhSectionPos.encodeContaining(requestedDetailLevel, MC_CLIENT.getPlayerChunkPos());
               int detailLevel = DhSectionPos.getDetailLevel(sectionPos);
               int posX = DhSectionPos.getX(sectionPos);
               int posZ = DhSectionPos.getZ(sectionPos);
               messageList.add("LOD Pos: " + y + detailLevel + "*" + posX + "," + posZ + cf);
               AbstractDhRenderApiDefinition renderApiDef = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
               messageList.add("Rendering API: " + a + renderApiDef.getEngineName() + cf);
            }

            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showThreadPools.get()) {
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("World Gen/Import", ThreadPoolUtil.getWorldGenExecutor()));
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("Render Load", ThreadPoolUtil.getFileHandlerExecutor()));
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("File Handler", ThreadPoolUtil.getRenderLoadingExecutor()));
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("Update Propagator", ThreadPoolUtil.getUpdatePropagatorExecutor()));
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("LOD Builder", ThreadPoolUtil.getChunkToLodBuilderExecutor()));
            messageList.add(PriorityTaskPicker.Executor.getThreadPoolStatString("Networking", ThreadPoolUtil.getNetworkCompressionExecutor()));
            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showRenderThreadTasks.get()) {
            RenderThreadTaskHandler.INSTANCE.addDebugMenuStringsToList(messageList);
            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showCombinedObjectPools.get()) {
            PhantomArrayListPool.addDebugMenuStringsToListForCombinedPools(messageList);
            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showSeparatedObjectPools.get()) {
            PhantomArrayListPool.addDebugMenuStringsToListForSeparatePools(messageList);
            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showQueuedChunkUpdateCount.get()) {
            ArrayList<String> chunkQueueList = SharedApi.INSTANCE.getDebugMenuString();
            messageList.addAll(chunkQueueList);
            messageList.add("");
         }

         if (Config.Client.Advanced.Debugging.F3Screen.showLevelStatus.get()) {
            world.addDebugMenuStringsToList(messageList);
            messageList.add("");

            for (IDhLevel level : levelIterator) {
               if (Config.Client.Advanced.Debugging.F3Screen.onlyShowRenderingLevels.get() && level instanceof IDhClientLevel) {
                  IDhClientLevel clientLevel = (IDhClientLevel)level;
                  if (!clientLevel.isRendering()) {
                     continue;
                  }
               }

               level.addDebugMenuStringsToList(messageList);
               RenderBufferHandler renderBufferHandler = level.getRenderBufferHandler();
               if (renderBufferHandler != null) {
                  messageList.add(renderBufferHandler.getVboRenderDebugMenuString());
                  String showPassString = renderBufferHandler.getShadowPassRenderDebugMenuString();
                  if (showPassString != null) {
                     messageList.add(showPassString);
                  }
               }

               IDhGenericRenderer genericRenderer = level.getGenericRenderer();
               if (genericRenderer != null) {
                  messageList.add(genericRenderer.getVboRenderDebugMenuString());
               }

               messageList.add("");
            }
         }
      }
   }
}

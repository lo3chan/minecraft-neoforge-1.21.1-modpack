package com.seibel.distanthorizons.neoforge;

import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_neoforge;
import com.seibel.distanthorizons.common.util.ProxyUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_neoforge;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.concurrent.AbstractExecutorService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import org.lwjgl.opengl.GL33;

public class NeoforgeClientProxy implements AbstractModInitializer$IEventProxy_neoforge {
   private static final IMinecraftClientWrapper MC = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   @Override
   public void registerEvents() {
      NeoForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void rightClickBlockEvent(RightClickBlock event) {
      if (MC.clientConnectedToDedicatedServer()) {
         LevelAccessor level = event.getLevel();
         ILevelWrapper wrappedLevel = ProxyUtil_neoforge.getLevelWrapper(level);
         if (SharedApi.isChunkAtBlockPosAlreadyUpdating(wrappedLevel, event.getPos().getX(), event.getPos().getZ())) {
            return;
         }

         AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
         if (executor != null) {
            executor.execute(() -> {
               ChunkAccess chunk = level.getChunk(event.getPos());
               SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_neoforge(chunk, wrappedLevel), wrappedLevel, true);
            });
         }
      }
   }

   @SubscribeEvent
   public void leftClickBlockEvent(LeftClickBlock event) {
      if (MC.clientConnectedToDedicatedServer()) {
         LevelAccessor level = event.getLevel();
         ILevelWrapper wrappedLevel = ProxyUtil_neoforge.getLevelWrapper(level);
         if (SharedApi.isChunkAtBlockPosAlreadyUpdating(wrappedLevel, event.getPos().getX(), event.getPos().getZ())) {
            return;
         }

         AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
         if (executor != null) {
            executor.execute(() -> {
               ChunkAccess chunk = level.getChunk(event.getPos());
               this.onBlockChangeEvent(level, chunk);
            });
         }
      }
   }

   private void onBlockChangeEvent(LevelAccessor level, ChunkAccess chunk) {
      ILevelWrapper wrappedLevel = ProxyUtil_neoforge.getLevelWrapper(level);
      SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_neoforge(chunk, wrappedLevel), wrappedLevel, true);
   }

   @SubscribeEvent
   public void registerKeyBindings(Key event) {
      if (Minecraft.getInstance().player != null) {
         if (event.getAction() == 1) {
            ClientApi.INSTANCE.keyPressedEvent(event.getKey());
         }
      }
   }

   @SubscribeEvent
   public void afterLevelRenderEvent(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_LEVEL) {
         try {
            MinecraftRenderWrapper_neoforge.INSTANCE.finalLevelFrameBufferId = GL33.glGetInteger(36006);
         } catch (Error | Exception var3) {
            LOGGER.error("Unexpected error in afterLevelRenderEvent: " + var3.getMessage(), var3);
         }
      }
   }
}

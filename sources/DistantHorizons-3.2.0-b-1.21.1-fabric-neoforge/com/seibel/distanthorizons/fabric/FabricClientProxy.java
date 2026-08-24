package com.seibel.distanthorizons.fabric;

import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_fabric;
import com.seibel.distanthorizons.common.AbstractPluginPacketSender_fabric;
import com.seibel.distanthorizons.common.CommonPacketPayload$Codec_fabric;
import com.seibel.distanthorizons.common.CommonPacketPayload_fabric;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftClientWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.util.HashSet;
import java.util.concurrent.AbstractExecutorService;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterSetup;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterTranslucent;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_1269;
import net.minecraft.class_2791;
import net.minecraft.class_3675;
import net.minecraft.class_638;
import net.minecraft.class_239.class_240;

@Environment(EnvType.CLIENT)
public class FabricClientProxy implements AbstractModInitializer$IEventProxy_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final MinecraftClientWrapper_fabric MC = MinecraftClientWrapper_fabric.INSTANCE;
   private static final AbstractPluginPacketSender_fabric PACKET_SENDER = (AbstractPluginPacketSender_fabric)SingletonInjector.INSTANCE
      .get(IPluginPacketSender.class);
   HashSet<Integer> previouslyPressKeyCodes = new HashSet<>();

   @Override
   public void registerEvents() {
      LOGGER.info("Registering Fabric Client Events");
      ClientChunkEvents.CHUNK_LOAD.register((Load)(level, chunk) -> {
         if (MC.clientConnectedToDedicatedServer()) {
            AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
            if (executor != null) {
               executor.execute(() -> {
                  IClientLevelWrapper wrappedLevel = ClientLevelWrapper_fabric.getWrapper(level);
                  SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_fabric(chunk, wrappedLevel), wrappedLevel, true);
               });
            }
         }
      });
      AttackBlockCallback.EVENT.register((AttackBlockCallback)(player, level, interactionHand, blockPos, direction) -> {
         if (MC.clientConnectedToDedicatedServer()) {
            IClientLevelWrapper wrappedLevel = ClientLevelWrapper_fabric.getWrapper((class_638)level);
            if (SharedApi.isChunkAtBlockPosAlreadyUpdating(wrappedLevel, blockPos.method_10263(), blockPos.method_10260())) {
               AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
               if (executor != null) {
                  executor.execute(() -> {
                     class_2791 chunk = level.method_22350(blockPos);
                     if (chunk != null) {
                        SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_fabric(chunk, wrappedLevel), wrappedLevel, true);
                     }
                  });
               }
            }
         }

         return class_1269.field_5811;
      });
      UseBlockCallback.EVENT.register((UseBlockCallback)(player, level, hand, hitResult) -> {
         if (MC.clientConnectedToDedicatedServer() && hitResult.method_17783() == class_240.field_1332 && !hitResult.method_17781()) {
            IClientLevelWrapper wrappedLevel = ClientLevelWrapper_fabric.getWrapper((class_638)level);
            if (SharedApi.isChunkAtBlockPosAlreadyUpdating(wrappedLevel, hitResult.method_17777().method_10263(), hitResult.method_17777().method_10260())) {
               AbstractExecutorService executor = ThreadPoolUtil.getFileHandlerExecutor();
               if (executor != null) {
                  executor.execute(() -> {
                     class_2791 chunk = level.method_22350(hitResult.method_17777());
                     if (chunk != null) {
                        SharedApi.INSTANCE.applyChunkUpdate(new ChunkWrapper_fabric(chunk, wrappedLevel), wrappedLevel, true);
                     }
                  });
               }
            }
         }

         return class_1269.field_5811;
      });
      WorldRenderEvents.AFTER_SETUP
         .register(
            (AfterSetup)renderContext -> {
               ClientApi.RENDER_STATE.mcProjectionMatrix = McObjectConverter_fabric.convert(renderContext.projectionMatrix());
               ClientApi.RENDER_STATE.mcModelViewMatrix = McObjectConverter_fabric.convert(renderContext.positionMatrix());
               ClientApi.RENDER_STATE.partialTickTime = renderContext.tickCounter().method_60636();
               ClientApi.RENDER_STATE.clientLevelWrapper = ClientLevelWrapper_fabric.getWrapperIfDifferent(
                  ClientApi.RENDER_STATE.clientLevelWrapper, renderContext.world()
               );
               ClientApi.INSTANCE.renderLods();
            }
         );
      WorldRenderEvents.AFTER_ENTITIES
         .register(
            (AfterEntities)renderContext -> {
               ClientApi.RENDER_STATE.mcProjectionMatrix = McObjectConverter_fabric.convert(renderContext.projectionMatrix());
               ClientApi.RENDER_STATE.mcModelViewMatrix = McObjectConverter_fabric.convert(renderContext.positionMatrix());
               ClientApi.RENDER_STATE.partialTickTime = renderContext.tickCounter().method_60636();
               ClientApi.RENDER_STATE.clientLevelWrapper = ClientLevelWrapper_fabric.getWrapperIfDifferent(
                  ClientApi.RENDER_STATE.clientLevelWrapper, renderContext.world()
               );
               ClientApi.INSTANCE.renderFadeOpaque();
            }
         );
      WorldRenderEvents.AFTER_TRANSLUCENT
         .register(
            (AfterTranslucent)renderContext -> {
               ClientApi.RENDER_STATE.mcProjectionMatrix = McObjectConverter_fabric.convert(renderContext.projectionMatrix());
               ClientApi.RENDER_STATE.mcModelViewMatrix = McObjectConverter_fabric.convert(renderContext.positionMatrix());
               ClientApi.RENDER_STATE.partialTickTime = renderContext.tickCounter().method_60636();
               ClientApi.RENDER_STATE.clientLevelWrapper = ClientLevelWrapper_fabric.getWrapperIfDifferent(
                  ClientApi.RENDER_STATE.clientLevelWrapper, renderContext.world()
               );
               ClientApi.INSTANCE.renderFadeTransparent();
            }
         );
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {});
      PayloadTypeRegistry.playS2C().register(CommonPacketPayload_fabric.TYPE, new CommonPacketPayload$Codec_fabric());
      ClientPlayNetworking.registerGlobalReceiver(CommonPacketPayload_fabric.TYPE, (payload, context) -> {
         if (payload.message() != null) {
            ClientApi.INSTANCE.pluginMessageReceived(payload.message());
         }
      });
   }

   public void onKeyInput() {
      HashSet<Integer> currentKeyDown = new HashSet<>();

      for (int keyCode = 48; keyCode <= 348; keyCode++) {
         if (class_3675.method_15987(MC.getGlfwWindowId(), keyCode)) {
            currentKeyDown.add(keyCode);
         }
      }

      for (int keyCodex : currentKeyDown) {
         if (!this.previouslyPressKeyCodes.contains(keyCodex)) {
            ClientApi.INSTANCE.keyPressedEvent(keyCodex);
         }
      }

      this.previouslyPressKeyCodes = currentKeyDown;
   }
}

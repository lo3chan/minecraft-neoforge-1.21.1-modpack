package com.seibel.distanthorizons.core.api.internal;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.config.EDhApiMcRenderingFadeMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeDeferredRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.core.api.internal.rendering.DhRenderState;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.network.session.NetworkSession;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.render.DhApiRenderProxy;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.render.RenderThreadTaskHandler;
import com.seibel.distanthorizons.core.render.renderer.LodRenderer;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.objects.Pair;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.world.DhClientWorld;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IImmersivePortalsAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhMetaRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTestTriangleRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhVanillaFadeRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientApi {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder().maxCountPerSecond(1).build();
   public static final ClientApi INSTANCE = new ClientApi();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IVersionConstants VERSION_CONSTANTS = SingletonInjector.INSTANCE.get(IVersionConstants.class);
   private static final int MS_BETWEEN_STATIC_STARTUP_MESSAGES = 4000;
   public static final DhRenderState RENDER_STATE = new DhRenderState();
   private static final RenderParams RENDER_PARAMS = new RenderParams();
   private static final long MIN_MS_BETWEEN_SPEED_CHECKS = 50L;
   private boolean isDevBuildMessagePrinted = false;
   private boolean lowMemoryWarningPrinted = false;
   private boolean highVanillaRenderDistanceWarningPrinted = false;
   private boolean deprecatedRendererWarningPrinted = false;
   private long lastStaticWarningMessageSentMsTime = 0L;
   private final Queue<String> chatMessageQueueForNextFrame = new LinkedBlockingQueue<>();
   private final Queue<String> overlayMessageQueueForNextFrame = new LinkedBlockingQueue<>();
   public boolean rendererDisabledBecauseOfExceptions = false;
   public final HashSet<IClientLevelWrapper> waitingClientLevels = new HashSet<>();
   public final Map<Pair<IClientLevelWrapper, DhChunkPos>, IChunkWrapper> waitingChunkByClientLevelAndPos = new ConcurrentHashMap<>();
   @Nullable
   public String lastRenderParamValidationMessage = null;
   private final RollingAverage cameraSpeedRollingAverage = new RollingAverage(40);
   private DhVec3d lastCameraPosForSpeedCheck = new DhVec3d();
   private long msSinceLastSpeedCheck = 0L;
   private boolean irisShadersEnabledLastFrame = false;

   public double getAvgCameraSpeed() {
      return this.cameraSpeedRollingAverage.getAverage();
   }

   private ClientApi() {
   }

   public synchronized void onClientOnlyConnected() {
      boolean connectedToServer = MC_CLIENT.clientConnectedToDedicatedServer();
      boolean connectedToReplay = MC_CLIENT.connectedToReplay();
      if (connectedToServer || connectedToReplay) {
         if (connectedToServer) {
            LOGGER.info("Client on ClientOnly mode connecting.");
         } else {
            LOGGER.info("Replay on ClientServer mode connecting.");
            if (Config.Common.Logging.Warning.showReplayWarningOnStartup.get()) {
               MC_CLIENT.sendChatMessage("§6Distant Horizons: Replay detected.§r");
               MC_CLIENT.sendChatMessage("DH may behave strangely or have missing functionality.");
               MC_CLIENT.sendChatMessage("In order to use pre-generated LODs, put your DH database(s) in:");
               MC_CLIENT.sendChatMessage(
                  "§7.Minecraft" + File.separator + "Distant_Horizons_server_data" + File.separator + "REPLAY" + File.separator + "DIMENSION_NAME" + "§r"
               );
               MC_CLIENT.sendChatMessage("This message can be disabled in DH's config under Advanced -> Logging.");
               MC_CLIENT.sendChatMessage("");
            }
         }

         DhClientWorld world = new DhClientWorld();
         SharedApi.setDhWorld(world);
      }
   }

   public synchronized void onClientOnlyDisconnected() {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world != null) {
         LOGGER.info("Client on ClientOnly mode disconnecting.");
         world.close();
         SharedApi.setDhWorld(null);
      }

      this.waitingChunkByClientLevelAndPos.clear();
   }

   public void loadWaitingChunksForLevel(IClientLevelWrapper level) {
      HashSet<Pair<IClientLevelWrapper, DhChunkPos>> keysToRemove = new HashSet<>();
      String levelDimensionName = level.getDimensionName();

      for (Pair<IClientLevelWrapper, DhChunkPos> levelChunkPair : this.waitingChunkByClientLevelAndPos.keySet()) {
         IClientLevelWrapper levelWrapper = levelChunkPair.first;
         if (levelWrapper.equals(level) || levelWrapper.getDimensionName().equals(levelDimensionName)) {
            IChunkWrapper chunkWrapper = this.waitingChunkByClientLevelAndPos.get(levelChunkPair);
            SharedApi.INSTANCE.applyChunkUpdate(chunkWrapper.copyWithLevel(level), level, false);
            keysToRemove.add(levelChunkPair);
         }
      }

      LOGGER.info("Loaded [" + keysToRemove.size() + "] waiting chunk wrappers.");

      for (Pair<IClientLevelWrapper, DhChunkPos> keyToRemove : keysToRemove) {
         this.waitingChunkByClientLevelAndPos.remove(keyToRemove);
      }
   }

   public void pluginMessageReceived(@NotNull AbstractNetworkMessage message) {
      ThreadPoolExecutor executor = ThreadPoolUtil.networkClientHandlerExecutor();
      if (executor == null) {
         LOGGER.warn("warn");
      } else {
         try {
            executor.execute(() -> {
               try {
                  IDhClientWorld clientWorld = SharedApi.tryGetDhClientWorld();
                  if (!(clientWorld instanceof DhClientWorld)) {
                     return;
                  }

                  DhClientWorld world = (DhClientWorld)clientWorld;
                  NetworkSession networkSession = world.pluginChannelApi.networkSession;
                  if (networkSession != null) {
                     networkSession.tryHandleMessage(message);
                  }
               } catch (Exception var4x) {
                  LOGGER.warn("pluginMessageReceived unexpected error: [" + var4x.getMessage() + "]", var4x);
               }
            });
         } catch (RejectedExecutionException var4) {
            LOGGER.warn("Plugin message executor rejected");
         }
      }
   }

   public void renderLods() {
      this.renderLodLayer(false);
   }

   public void renderDeferredLodsForShaders() {
      this.renderLodLayer(true);
   }

   private void renderLodLayer(boolean renderingDeferredLayer) {
      IProfilerWrapper profiler = MC_CLIENT.getProfiler();
      IProfilerWrapper.IProfileBlock dhRender_profile = profiler.push("DH-RenderLevel");

      label161: {
         label162: {
            label163: {
               try {
                  if (!renderingDeferredLayer) {
                  }

                  if (!renderingDeferredLayer) {
                     IProfilerWrapper.IProfileBlock renderTask_profile = profiler.push("DH render thread tasks");

                     try {
                        this.sendQueuedChatMessages();

                        try {
                           RenderThreadTaskHandler.INSTANCE.runRenderThreadTasks();
                        } catch (Exception var16) {
                           LOGGER.error("Unexpected issue running render thread tasks, error: [" + var16.getMessage() + "].", var16);
                        }

                        long nowMs = System.currentTimeMillis();
                        if (this.msSinceLastSpeedCheck + 50L < nowMs
                           && (ClientApi.DelayedAccessors.IMMERSIVE_PORTALS == null || !ClientApi.DelayedAccessors.IMMERSIVE_PORTALS.isRenderingPortal())) {
                           double secSinceLastCheck = (nowMs - this.msSinceLastSpeedCheck) / 1000.0;
                           this.msSinceLastSpeedCheck = nowMs;
                           DhVec3d camPos = MC_RENDER.getCameraExactPosition();
                           double distanceInBlocks = camPos.getDistance(this.lastCameraPosForSpeedCheck);
                           double speed = distanceInBlocks / secSinceLastCheck;
                           this.cameraSpeedRollingAverage.add(speed);
                           this.lastCameraPosForSpeedCheck = camPos;
                        }

                        IIrisAccessor irisAccessor = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
                        if (irisAccessor != null) {
                           boolean shadersActive = irisAccessor.isShaderPackInUse();
                           if (this.irisShadersEnabledLastFrame != shadersActive) {
                              this.irisShadersEnabledLastFrame = shadersActive;
                              DhApi.Delayed.renderProxy.clearRenderDataCache();
                           }
                        }
                     } catch (Throwable var18) {
                        if (renderTask_profile != null) {
                           try {
                              renderTask_profile.close();
                           } catch (Throwable var15) {
                              var18.addSuppressed(var15);
                           }
                        }

                        throw var18;
                     }

                     if (renderTask_profile != null) {
                        renderTask_profile.close();
                     }
                  }

                  EDhApiRenderPass renderPass;
                  if (DhApiRenderProxy.INSTANCE.getDeferTransparentRendering()) {
                     if (renderingDeferredLayer) {
                        renderPass = EDhApiRenderPass.TRANSPARENT;
                     } else {
                        renderPass = EDhApiRenderPass.OPAQUE;
                     }
                  } else {
                     renderPass = EDhApiRenderPass.OPAQUE_AND_TRANSPARENT;
                  }

                  RENDER_PARAMS.update(renderPass, RENDER_STATE);
                  String validationMessage = RENDER_PARAMS.getValidationErrorMessage();
                  if (validationMessage != null) {
                     this.lastRenderParamValidationMessage = validationMessage;
                     break label161;
                  }

                  this.lastRenderParamValidationMessage = null;
                  if (this.rendererDisabledBecauseOfExceptions) {
                     if (!Config.Client.quickEnableRendering.get()) {
                        LOGGER.info(
                           "DH Renderer re-enabled after exception. Some rendering issues may occur. Please reboot Minecraft if you see any rendering issues."
                        );
                        this.rendererDisabledBecauseOfExceptions = false;
                        Config.Client.quickEnableRendering.set(true);
                     }
                     break label162;
                  }

                  if (Config.Client.Advanced.Debugging.rendererMode.get() == EDhApiRendererMode.DISABLED) {
                     break label163;
                  }

                  try {
                     if (Config.Client.Advanced.Debugging.rendererMode.get() == EDhApiRendererMode.DEFAULT) {
                        if (!renderingDeferredLayer) {
                           boolean renderingCancelled = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeRenderEvent.class, RENDER_PARAMS);
                           if (!renderingCancelled) {
                              LodRenderer.INSTANCE.render(RENDER_PARAMS, profiler);
                           }

                           if (!DhApi.Delayed.renderProxy.getDeferTransparentRendering()) {
                              ApiEventInjector.INSTANCE.fireAllEvents(DhApiAfterRenderEvent.class, null);
                           }
                        } else {
                           boolean renderingCancelledx = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeDeferredRenderEvent.class, RENDER_PARAMS);
                           if (!renderingCancelledx) {
                              LodRenderer.INSTANCE.renderDeferred(RENDER_PARAMS, profiler);
                           }

                           if (DhApi.Delayed.renderProxy.getDeferTransparentRendering()) {
                              ApiEventInjector.INSTANCE.fireAllEvents(DhApiAfterRenderEvent.class, null);
                           }
                        }
                     } else if (!renderingDeferredLayer) {
                        IDhMetaRenderer metaRenderer = SingletonInjector.INSTANCE.get(IDhMetaRenderer.class);
                        IDhTestTriangleRenderer testRenderer = SingletonInjector.INSTANCE.get(IDhTestTriangleRenderer.class);
                        if (testRenderer != null && metaRenderer != null) {
                           metaRenderer.runRenderPassSetup(RENDER_PARAMS);
                           testRenderer.render(RENDER_PARAMS);
                           metaRenderer.runRenderPassCleanup(RENDER_PARAMS);
                        } else {
                           RATE_LIMITED_LOGGER.warn("Unable to find singleton [" + IDhTestTriangleRenderer.class.getSimpleName() + "]");
                        }
                     }
                  } catch (Exception var17) {
                     this.rendererDisabledBecauseOfExceptions = true;
                     LOGGER.error("Unexpected Renderer error in render pass [" + renderPass + "]. Error: " + var17.getMessage(), var17);
                     MC_CLIENT.sendChatMessage("§4§lERROR: Distant Horizons renderer has encountered an exception!§r");
                     MC_CLIENT.sendChatMessage("§4Renderer disabled to try preventing GL state corruption.§r");
                     MC_CLIENT.sendChatMessage("§4Toggle DH rendering via the config UI to re-activate DH rendering.§r");
                     MC_CLIENT.sendChatMessage("§4Error: §r" + var17);
                  }
               } catch (Throwable var19) {
                  if (dhRender_profile != null) {
                     try {
                        dhRender_profile.close();
                     } catch (Throwable var14) {
                        var19.addSuppressed(var14);
                     }
                  }

                  throw var19;
               }

               if (dhRender_profile != null) {
                  dhRender_profile.close();
               }

               return;
            }

            if (dhRender_profile != null) {
               dhRender_profile.close();
            }

            return;
         }

         if (dhRender_profile != null) {
            dhRender_profile.close();
         }

         return;
      }

      if (dhRender_profile != null) {
         dhRender_profile.close();
      }
   }

   public void renderFadeOpaque() {
      IDhVanillaFadeRenderer fadeRenderer = SingletonInjector.INSTANCE.get(IDhVanillaFadeRenderer.class);
      if (fadeRenderer != null) {
         if (Config.Client.Advanced.Debugging.rendererMode.get() != EDhApiRendererMode.DISABLED
            && (
               Config.Client.Advanced.Graphics.Quality.vanillaFadeMode.get() == EDhApiMcRenderingFadeMode.DOUBLE_PASS
                  || Config.Client.Advanced.Debugging.lodOnlyMode.get()
            )
            && shouldRenderFade()) {
            RENDER_PARAMS.update(EDhApiRenderPass.OPAQUE, RENDER_STATE);
            fadeRenderer.render(RENDER_PARAMS);
         }
      }
   }

   public void renderFadeTransparent() {
      IDhVanillaFadeRenderer fadeRenderer = SingletonInjector.INSTANCE.get(IDhVanillaFadeRenderer.class);
      if (fadeRenderer != null) {
         if (Config.Client.Advanced.Debugging.rendererMode.get() != EDhApiRendererMode.DISABLED) {
            boolean renderFade = (
                  Config.Client.Advanced.Graphics.Quality.vanillaFadeMode.get() != EDhApiMcRenderingFadeMode.NONE
                     || Config.Client.Advanced.Debugging.lodOnlyMode.get()
               )
               && shouldRenderFade();
            if (renderFade) {
               RENDER_PARAMS.update(EDhApiRenderPass.TRANSPARENT, RENDER_STATE);
               fadeRenderer.render(RENDER_PARAMS);
            }
         }
      }
   }

   private static boolean shouldRenderFade() {
      if (DhApiRenderProxy.INSTANCE.getDeferTransparentRendering()) {
         return false;
      } else {
         IImmersivePortalsAccessor immersivePortals = ModAccessorInjector.INSTANCE.get(IImmersivePortalsAccessor.class);
         return immersivePortals == null || !immersivePortals.isRenderingPortal();
      }
   }

   public void keyPressedEvent(int glfwKey) {
      if (Config.Client.Advanced.Debugging.enableDebugKeybindings.get()) {
         if (glfwKey == 295) {
            Config.Client.Advanced.Debugging.rendererMode.set(EDhApiRendererMode.next(Config.Client.Advanced.Debugging.rendererMode.get()));
            MC_CLIENT.sendChatMessage("F6: Set rendering to " + Config.Client.Advanced.Debugging.rendererMode.get());
         } else if (glfwKey == 296) {
            Config.Client.Advanced.Debugging.lodOnlyMode.set(!Config.Client.Advanced.Debugging.lodOnlyMode.get());
            MC_CLIENT.sendChatMessage("F7: Set LOD only mode to " + Config.Client.Advanced.Debugging.lodOnlyMode.get());
         } else if (glfwKey == 297) {
            Config.Client.Advanced.Debugging.debugRenderingColors.set(EDhApiDebugRendering.next(Config.Client.Advanced.Debugging.debugRenderingColors.get()));
            MC_CLIENT.sendChatMessage("F8: Set debug mode to " + Config.Client.Advanced.Debugging.debugRenderingColors.get());
         }
      }
   }

   private void sendQueuedChatMessages() {
      this.detectAndSendBootTimeWarnings();
      if (!this.staticStartupMessageSentRecently()) {
         while (!this.chatMessageQueueForNextFrame.isEmpty()) {
            String message = this.chatMessageQueueForNextFrame.poll();
            if (message == null) {
               message = "";
            }

            MC_CLIENT.sendChatMessage(message);
         }

         while (!this.overlayMessageQueueForNextFrame.isEmpty()) {
            String message = this.overlayMessageQueueForNextFrame.poll();
            if (message == null) {
               message = "";
            }

            MC_CLIENT.sendOverlayMessage(message);
         }
      }
   }

   private void detectAndSendBootTimeWarnings() {
      if (ModInfo.IS_DEV_BUILD && !this.isDevBuildMessagePrinted && MC_CLIENT.playerExists()) {
         this.isDevBuildMessagePrinted = true;
         this.lastStaticWarningMessageSentMsTime = System.currentTimeMillis();
         String message = "§2Distant Horizons: nightly/unstable build, version: [3.2.0-b].§r\nIssues may occur with this version.\nHere be dragons!\n";
         MC_CLIENT.sendChatMessage(message);
      }

      if (!this.staticStartupMessageSentRecently()) {
         if (!this.lowMemoryWarningPrinted && Config.Common.Logging.Warning.showLowMemoryWarningOnStartup.get()) {
            this.lowMemoryWarningPrinted = true;
            this.lastStaticWarningMessageSentMsTime = System.currentTimeMillis();
            long minimumRecommendedMemoryInBytes = 4000000000L;
            long maxMemoryInBytes = Runtime.getRuntime().maxMemory();
            if (maxMemoryInBytes < minimumRecommendedMemoryInBytes) {
               String message = "§6Distant Horizons: Low memory detected.§r\nStuttering or low FPS may occur. \nPlease increase Minecraft's available memory to 4 GB or more. \nThis warning can be disabled in DH's config under Advanced -> Logging. \n";
               MC_CLIENT.sendChatMessage(message);
            }
         }

         if (!this.staticStartupMessageSentRecently()) {
            if (!this.highVanillaRenderDistanceWarningPrinted && Config.Common.Logging.Warning.showHighVanillaRenderDistanceWarning.get()) {
               this.highVanillaRenderDistanceWarningPrinted = true;
               if (MC_RENDER.getRenderDistance() > 12) {
                  this.lastStaticWarningMessageSentMsTime = System.currentTimeMillis();
                  String message = "§eDistant Horizons: High vanilla render distance detected.§r\nUsing a high vanilla render distance uses a lot of CPU power \nand doesn't improve graphics much after about 12.\nLowering your vanilla render distance will give you better FPS\nand reduce stuttering at a similar visual quality.\n§7A vanilla render distance of 8 is recommended.§r\nThis message can be disabled in DH's config under Advanced -> Logging.\n";
                  MC_CLIENT.sendChatMessage(message);
               }
            }

            if (!this.staticStartupMessageSentRecently()) {
               if (!this.deprecatedRendererWarningPrinted) {
                  this.deprecatedRendererWarningPrinted = true;
                  EDhApiRenderingEngine activeRenderingEngine = Config.Client.Advanced.Graphics.Experimental.renderingEngine.get();
                  EDhApiRenderingEngine recommendedEngine = VERSION_CONSTANTS.getDefaultRenderingEngine();
                  if (activeRenderingEngine == EDhApiRenderingEngine.OPEN_GL
                     && recommendedEngine != EDhApiRenderingEngine.OPEN_GL
                     && Config.Common.Logging.Warning.showDeprecatedRendererWarningOnStartup.get()) {
                     IMinecraftClientWrapper mc = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
                     String message = "§6Distant Horizons: Deprecated Rendering Engine.§r\nDH is currently rendering via raw OpenGL. \nRaw OpenGL is deprecated for this Minecraft version, \nmeaning there may be visual issues. \nThis warning can be disabled in DH's config under Advanced -> Logging. \n";
                     mc.sendChatMessage(message);
                  }
               }
            }
         }
      }
   }

   private boolean staticStartupMessageSentRecently() {
      if (this.lastStaticWarningMessageSentMsTime == 0L) {
         return false;
      } else {
         long timeSinceLastMessage = System.currentTimeMillis() - this.lastStaticWarningMessageSentMsTime;
         return timeSinceLastMessage <= 4000L;
      }
   }

   public void showChatMessageNextFrame(String chatMessage) {
      this.chatMessageQueueForNextFrame.add(chatMessage);
   }

   public void showOverlayMessageNextFrame(String message) {
      this.overlayMessageQueueForNextFrame.add(message);
   }

   private static class DelayedAccessors {
      public static final IImmersivePortalsAccessor IMMERSIVE_PORTALS = ModAccessorInjector.INSTANCE.get(IImmersivePortalsAccessor.class);
   }
}

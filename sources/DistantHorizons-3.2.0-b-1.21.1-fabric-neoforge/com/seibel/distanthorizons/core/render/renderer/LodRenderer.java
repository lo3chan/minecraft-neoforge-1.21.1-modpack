package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeFogRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderCleanupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderSetupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeTextureClearEvent;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.DhApiRenderProxy;
import com.seibel.distanthorizons.core.render.RenderBufferHandler;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFarFadeRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFogRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhMetaRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhSsaoRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTerrainRenderer;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.awt.Color;

public class LodRenderer {
   public static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logRendererEventToFile).build();
   public static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererEventToFile)
      .maxCountPerSecond(4)
      .build();
   private static final IMinecraftClientWrapper MC = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   public static final LodRenderer INSTANCE = new LodRenderer();
   private boolean vanillaSettingsOverridden = false;
   private boolean renderersBound = false;
   private IDhMetaRenderer metaRenderer;
   private IDhTerrainRenderer terrainRenderer;
   private IDhSsaoRenderer ssaoRenderer;
   private IDhFogRenderer fogRenderer;
   private IDhFarFadeRenderer farFadeRenderer;
   private AbstractDebugWireframeRenderer debugWireframeRenderer;

   private LodRenderer() {
   }

   private void bindRenderers() {
      this.metaRenderer = SingletonInjector.INSTANCE.get(IDhMetaRenderer.class);
      this.terrainRenderer = SingletonInjector.INSTANCE.get(IDhTerrainRenderer.class);
      this.ssaoRenderer = SingletonInjector.INSTANCE.get(IDhSsaoRenderer.class);
      this.fogRenderer = SingletonInjector.INSTANCE.get(IDhFogRenderer.class);
      this.farFadeRenderer = SingletonInjector.INSTANCE.get(IDhFarFadeRenderer.class);
      this.debugWireframeRenderer = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   }

   public void render(RenderParams renderParams, IProfilerWrapper profiler) {
      this.renderTerrain(renderParams, profiler, false);
   }

   public void renderDeferred(RenderParams renderParams, IProfilerWrapper profiler) {
      this.renderTerrain(renderParams, profiler, true);
   }

   private void renderTerrain(RenderParams renderParams, IProfilerWrapper profiler, boolean runningDeferredPass) {
      boolean deferTransparentRendering = DhApiRenderProxy.INSTANCE.getDeferTransparentRendering();
      if (!runningDeferredPass || deferTransparentRendering) {
         boolean firstPass = !runningDeferredPass;
         if (!renderParams.hasBeenValidated) {
            throw new IllegalArgumentException("Render parameters validation");
         } else {
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeRenderSetupEvent.class, renderParams.apiCopy);
            IProfilerWrapper.IProfileBlock terrainRender_profile = profiler.push("LOD GL setup");

            try {
               if (!this.renderersBound) {
                  this.bindRenderers();
                  this.renderersBound = true;
               }

               RenderBufferHandler renderBufferHandler = renderParams.renderBufferHandler;
               IDhGenericRenderer genericRenderer = renderParams.genericRenderer;
               this.metaRenderer.runRenderPassSetup(renderParams);
               if (!this.vanillaSettingsOverridden) {
                  if (Config.Client.Advanced.Graphics.overrideVanillaGraphicsSettings.get()) {
                     LOGGER.info(
                        "Overriding vanilla MC settings to better fit Distant Horizons... This behavior can be disabled in the Distant Horizons config."
                     );
                     MC.disableVanillaClouds();
                     MC.disableVanillaChunkFadeIn();
                     MC.disableFabulousTransparency();
                  }

                  this.vanillaSettingsOverridden = true;
               }

               if (firstPass) {
                  profiler.popPush("LOD build render list");
                  renderBufferHandler.buildRenderList(renderParams);
               }

               Boolean apiFogOverride = Config.Client.Advanced.Graphics.Fog.enableDhFog.getApiValue();
               boolean renderFog;
               if (apiFogOverride != null) {
                  renderFog = apiFogOverride;
               } else {
                  renderFog = Config.Client.Advanced.Graphics.Fog.enableDhFog.get();
                  renderFog |= renderParams.vanillaFogEnabled;
               }

               DhApiBeforeFogRenderEvent.EventParam fogRenderEventParam = FogRenderParamFactory.getRenderParam(renderParams);
               if (!runningDeferredPass) {
                  boolean clearTextures = !ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeTextureClearEvent.class, renderParams.apiCopy);
                  if (clearTextures) {
                     this.metaRenderer.clearDhDepthAndColorTextures(renderParams);
                  }

                  profiler.popPush("LOD Opaque");
                  this.renderTerrain(this.terrainRenderer, renderBufferHandler, renderParams, true, profiler);
                  if (Config.Client.Advanced.Graphics.GenericRendering.enableGenericRendering.get()) {
                     profiler.popPush("Custom Objects");
                     genericRenderer.render(renderParams, profiler, true);
                  }

                  if (Config.Client.Advanced.Graphics.enableSsao.get()) {
                     profiler.popPush("LOD SSAO");
                     this.ssaoRenderer.render(renderParams);
                  }

                  if (Config.Client.Advanced.Graphics.GenericRendering.enableGenericRendering.get()) {
                     profiler.popPush("Custom Objects");
                     genericRenderer.render(renderParams, profiler, false);
                  }

                  if (!deferTransparentRendering && Config.Client.Advanced.Graphics.Quality.transparency.get() == EDhApiTransparency.COMPLETE) {
                     profiler.popPush("LOD Transparent");
                     this.renderTerrain(this.terrainRenderer, renderBufferHandler, renderParams, false, profiler);
                  }

                  boolean cancelFogEvent = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeFogRenderEvent.class, fogRenderEventParam);
                  if (renderFog && !cancelFogEvent) {
                     profiler.popPush("LOD Fog");
                     this.fogRenderer.render(renderParams, fogRenderEventParam.getFogRenderParam());
                  }

                  if (Config.Client.Advanced.Graphics.Quality.dhFadeFarClipPlane.get() && IRIS_ACCESSOR == null) {
                     profiler.popPush("Fade Far Clip Fade");
                     this.farFadeRenderer.render(renderParams);
                  }

                  if (Config.Client.Advanced.Debugging.DebugWireframe.enableRendering.get()) {
                     profiler.popPush("Debug wireframes");
                     this.debugWireframeRenderer.render(renderParams);
                  }

                  if (Config.Client.Advanced.Debugging.PositionFinder.positionFinderEnable.get()) {
                     this.debugWireframeRenderer
                        .renderBox(
                           new AbstractDebugWireframeRenderer.Box(
                              DhSectionPos.encode(
                                 Config.Client.Advanced.Debugging.PositionFinder.positionFinderDetailLevel.get().byteValue(),
                                 Config.Client.Advanced.Debugging.PositionFinder.positionFinderXPos.get(),
                                 Config.Client.Advanced.Debugging.PositionFinder.positionFinderZPos.get()
                              ),
                              Config.Client.Advanced.Debugging.PositionFinder.positionFinderMinBlockY.get().intValue(),
                              Config.Client.Advanced.Debugging.PositionFinder.positionFinderMaxBlockY.get().intValue(),
                              Config.Client.Advanced.Debugging.PositionFinder.positionFinderMarginPercent.get(),
                              Color.GREEN
                           )
                        );
                  }

                  boolean cancelApplyShader = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeApplyShaderRenderEvent.class, renderParams.apiCopy);
                  if (!cancelApplyShader) {
                     profiler.popPush("Apply to MC");
                     this.metaRenderer.applyToMcTexture(renderParams);
                  }
               } else if (Config.Client.Advanced.Graphics.Quality.transparency.get() == EDhApiTransparency.COMPLETE) {
                  profiler.popPush("LOD Transparent");
                  this.renderTerrain(this.terrainRenderer, renderBufferHandler, renderParams, false, profiler);
                  boolean cancelFogEventx = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeFogRenderEvent.class, fogRenderEventParam);
                  if (renderFog && !cancelFogEventx) {
                     profiler.popPush("LOD Fog");
                     this.fogRenderer.render(renderParams, fogRenderEventParam.getFogRenderParam());
                  }
               }

               profiler.popPush("LOD cleanup");
               ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeRenderCleanupEvent.class, renderParams.apiCopy);
               this.metaRenderer.runRenderPassCleanup(renderParams);
            } catch (Throwable var16) {
               if (terrainRender_profile != null) {
                  try {
                     terrainRender_profile.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }
               }

               throw var16;
            }

            if (terrainRender_profile != null) {
               terrainRender_profile.close();
            }
         }
      }
   }

   private void renderTerrain(
      IDhTerrainRenderer terrainRenderer,
      RenderBufferHandler lodBufferHandler,
      RenderParams renderEventParam,
      boolean opaquePass,
      IProfilerWrapper profilerWrapper
   ) {
      SortedArraySet<LodBufferContainer> lodBufferContainer = lodBufferHandler.getColumnRenderBuffers();
      if (lodBufferContainer != null) {
         terrainRenderer.render(renderEventParam, opaquePass, lodBufferContainer, profilerWrapper);
      }
   }
}

package com.seibel.distanthorizons.common.render.openGl;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShaderProgram;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterColorDepthTextureCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeColorDepthTextureCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeTextureClearEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiColorDepthTextureCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiTextureCreatedParam;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import com.seibel.distanthorizons.common.render.openGl.glObject.GlDhFramebuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.EGlDhDepthBufferFormat;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.EGlDhInternalTextureFormat;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.EGlDhPixelFormat;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.EGlDhPixelType;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.GlDhColorTexture;
import com.seibel.distanthorizons.common.render.openGl.glObject.texture.GlDhDepthTexture;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.apply.GlDhApplyShader_neoforge;
import com.seibel.distanthorizons.common.render.openGl.terrain.GlBlockTextureAtlas;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.common.wrappers.misc.LightMapWrapper_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.DhApiRenderProxy;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.ILightMapWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IOptifineAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhMetaRenderer;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL33;

public class GlDhMetaRenderer_neoforge implements IDhMetaRenderer {
   public static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logRendererEventToFile).build();
   public static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererEventToFile)
      .maxCountPerSecond(4)
      .build();
   public static final GlDhMetaRenderer_neoforge INSTANCE = new GlDhMetaRenderer_neoforge();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private static final IOptifineAccessor OPTIFINE_ACCESSOR = ModAccessorInjector.INSTANCE.get(IOptifineAccessor.class);
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   private int activeFramebufferId = -1;
   private int activeColorTextureId = -1;
   private int activeDepthTextureId = -1;
   private int textureWidth;
   private int textureHeight;
   private IDhApiFramebuffer framebuffer;
   @Nullable
   private GlDhColorTexture nullableColorTexture;
   private GlDhDepthTexture depthTexture;
   private boolean usingMcFramebuffer = false;
   private boolean renderObjectsCreated = false;
   public IDhApiShaderProgram shaderProgramForThisFrame;

   @Override
   public void runRenderPassSetup(RenderParams renderParams) {
      boolean firstPass = renderParams.renderPass == EDhApiRenderPass.OPAQUE || renderParams.renderPass == EDhApiRenderPass.OPAQUE_AND_TRANSPARENT;
      if (!this.renderObjectsCreated) {
         boolean setupSuccess = this.createRenderObjects();
         if (!setupSuccess) {
            return;
         }

         this.renderObjectsCreated = true;
      }

      this.shaderProgramForThisFrame = GlDhTerrainRenderer_neoforge.INSTANCE.getTerrainShaderProgram();
      IDhApiShaderProgram lodShaderProgramOverride = OverrideInjector.INSTANCE.get(IDhApiShaderProgram.class);
      if (lodShaderProgramOverride != null && this.shaderProgramForThisFrame.overrideThisFrame()) {
         this.shaderProgramForThisFrame = lodShaderProgramOverride;
      }

      this.setGLState(renderParams, firstPass);
      this.bindLightmap(renderParams.lightmap);
      if (Config.Client.Advanced.Graphics.Texture.enableTexturedLods.get() && this.irisShadersInactive()) {
         GlBlockTextureAtlas.INSTANCE.uploadPendingTiles();
         GlBlockTextureAtlas.INSTANCE.bind();
      }
   }

   private void setGLState(DhApiRenderParam renderEventParam, boolean firstPass) {
      int viewportWidth = MC_RENDER.getTargetFramebufferViewportWidth();
      int viewportHeight = MC_RENDER.getTargetFramebufferViewportHeight();
      IDhApiFramebuffer framebuffer = this.framebuffer;
      IDhApiFramebuffer framebufferOverride = OverrideInjector.INSTANCE.get(IDhApiFramebuffer.class);
      if (framebufferOverride != null && framebufferOverride.overrideThisFrame()) {
         framebuffer = framebufferOverride;
      }

      this.setActiveFramebufferId(framebuffer.getId());
      framebuffer.bind();
      GL33.glPolygonMode(1032, 6914);
      GLMC.enableFaceCulling();
      GLMC.glBlendFunc(770, 771);
      GLMC.glBlendFuncSeparate(770, 771, 1, 0);
      GL33.glDisable(3089);
      GLMC.enableDepthTest();
      GLMC.glDepthFunc(513);
      GLMC.enableDepthMask();
      GL33.glViewport(0, 0, viewportWidth, viewportHeight);
      this.shaderProgramForThisFrame.bind();
      IDhApiShaderProgram shaderProgramOverride = OverrideInjector.INSTANCE.get(IDhApiShaderProgram.class);
      if (shaderProgramOverride != null) {
         shaderProgramOverride.fillUniformData(renderEventParam);
      }

      this.shaderProgramForThisFrame.fillUniformData(renderEventParam);
      if (viewportWidth != this.textureWidth || viewportHeight != this.textureHeight) {
         this.createAndBindTextures();
      }

      int depthTextureId = this.depthTexture.getTextureId();
      this.setActiveDepthTextureId(depthTextureId);
      if (this.nullableColorTexture != null) {
         int colorTextureId = this.nullableColorTexture.getTextureId();
         this.setActiveColorTextureId(colorTextureId);
      } else {
         int colorTextureId = GL33.glGetFramebufferAttachmentParameteri(36160, 36064, 36049);
         this.setActiveColorTextureId(colorTextureId);
      }

      boolean clearTextures = !ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeTextureClearEvent.class, renderEventParam);
      if (clearTextures) {
         GL33.glClearDepth(1.0);
         float[] clearColorValues = new float[4];
         GL33.glGetFloatv(3106, clearColorValues);
         GL33.glClearColor(clearColorValues[0], clearColorValues[1], clearColorValues[2], 1.0F);
         if (this.usingMcFramebuffer && framebufferOverride == null) {
            framebuffer.addDepthAttachment(this.depthTexture.getTextureId(), EGlDhDepthBufferFormat.DEPTH32F.isCombinedStencil());
            GL33.glClear(256);
         } else if (firstPass) {
            GL33.glClear(16640);
         }
      }
   }

   private boolean createRenderObjects() {
      if (this.renderObjectsCreated) {
         LOGGER.warn("Renderer setup called but it has already completed setup!");
         return false;
      } else {
         GLProxy.getInstance();
         LOGGER.info("Setting up renderer");
         if (OPTIFINE_ACCESSOR != null) {
            int currentFramebufferId = MC_RENDER.getTargetFramebuffer();
            this.framebuffer = new GlDhFramebuffer(currentFramebufferId);
            this.usingMcFramebuffer = true;
         } else {
            this.framebuffer = new GlDhFramebuffer();
            this.usingMcFramebuffer = false;
         }

         this.createAndBindTextures();
         if (this.framebuffer.getStatus() != 36053) {
            LOGGER.warn("Framebuffer [" + this.framebuffer.getId() + "] isn't complete.");
            return false;
         } else {
            LOGGER.info("Renderer setup complete");
            return true;
         }
      }
   }

   private void createAndBindTextures() {
      int oldWidth = this.textureWidth;
      int oldHeight = this.textureHeight;
      this.textureWidth = MC_RENDER.getTargetFramebufferViewportWidth();
      this.textureHeight = MC_RENDER.getTargetFramebufferViewportHeight();
      DhApiTextureCreatedParam textureCreatedParam = new DhApiTextureCreatedParam(oldWidth, oldHeight, this.textureWidth, this.textureHeight);
      ApiEventInjector.INSTANCE.fireAllEvents(DhApiColorDepthTextureCreatedEvent.class, new DhApiColorDepthTextureCreatedEvent.EventParam(textureCreatedParam));
      ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeColorDepthTextureCreatedEvent.class, textureCreatedParam);
      IDhApiFramebuffer framebufferOverride = OverrideInjector.INSTANCE.get(IDhApiFramebuffer.class);
      if (this.depthTexture != null) {
         this.depthTexture.destroy();
      }

      this.depthTexture = new GlDhDepthTexture(this.textureWidth, this.textureHeight, EGlDhDepthBufferFormat.DEPTH32F);
      this.framebuffer.addDepthAttachment(this.depthTexture.getTextureId(), EGlDhDepthBufferFormat.DEPTH32F.isCombinedStencil());
      if (framebufferOverride != null) {
         framebufferOverride.addDepthAttachment(this.depthTexture.getTextureId(), EGlDhDepthBufferFormat.DEPTH32F.isCombinedStencil());
      }

      if (!this.usingMcFramebuffer) {
         if (this.nullableColorTexture != null) {
            this.nullableColorTexture.destroy();
         }

         this.nullableColorTexture = GlDhColorTexture.builder()
            .setDimensions(this.textureWidth, this.textureHeight)
            .setInternalFormat(EGlDhInternalTextureFormat.RGBA8)
            .setPixelType(EGlDhPixelType.UNSIGNED_BYTE)
            .setPixelFormat(EGlDhPixelFormat.RGBA)
            .build();
         this.framebuffer.addColorAttachment(0, this.nullableColorTexture.getTextureId());
         if (framebufferOverride != null) {
            framebufferOverride.addColorAttachment(0, this.nullableColorTexture.getTextureId());
         }
      } else {
         this.nullableColorTexture = null;
      }

      ApiEventInjector.INSTANCE.fireAllEvents(DhApiAfterColorDepthTextureCreatedEvent.class, textureCreatedParam);
   }

   @Override
   public void runRenderPassCleanup(RenderParams renderParams) {
      boolean runningDeferredPass = renderParams.renderPass == EDhApiRenderPass.TRANSPARENT;
      if (!runningDeferredPass && this.usingMcFramebuffer) {
         GL33.glClear(256);
      }

      this.unbindLightmap();
      if (Config.Client.Advanced.Graphics.Texture.enableTexturedLods.get() && this.irisShadersInactive()) {
         GlBlockTextureAtlas.INSTANCE.unbind();
      }

      this.shaderProgramForThisFrame.unbind();
   }

   @Override
   public void applyToMcTexture(RenderParams renderParams) {
      GlDhApplyShader_neoforge.INSTANCE.render(renderParams);
   }

   @Override
   public void clearDhDepthAndColorTextures(RenderParams renderParams) {
      IDhApiFramebuffer framebufferOverride = OverrideInjector.INSTANCE.get(IDhApiFramebuffer.class);
      boolean firstPass = renderParams.renderPass == EDhApiRenderPass.OPAQUE || renderParams.renderPass == EDhApiRenderPass.OPAQUE_AND_TRANSPARENT;
      GL33.glClearDepth(1.0);
      float[] clearColorValues = new float[4];
      GL33.glGetFloatv(3106, clearColorValues);
      GL33.glClearColor(clearColorValues[0], clearColorValues[1], clearColorValues[2], 0.0F);
      if (this.usingMcFramebuffer && framebufferOverride == null) {
         GL33.glClear(256);
      } else if (firstPass) {
         GL33.glClear(16640);
      }
   }

   public void setActiveFramebufferId(int id) {
      this.activeFramebufferId = id;
   }

   public int getActiveFramebufferId() {
      return this.activeFramebufferId;
   }

   public void setActiveColorTextureId(int id) {
      this.activeColorTextureId = id;
      DhApiRenderProxy.activeOpenGlDhColorTextureId = id;
   }

   public int getActiveColorTextureId() {
      return this.activeColorTextureId;
   }

   public void setActiveDepthTextureId(int id) {
      this.activeDepthTextureId = id;
      DhApiRenderProxy.activeOpenGlDhDepthTextureId = id;
   }

   public int getActiveDepthTextureId() {
      return this.activeDepthTextureId;
   }

   public void bindLightmap(ILightMapWrapper lightMapWrapper) {
      LightMapWrapper_neoforge lightMap = (LightMapWrapper_neoforge)lightMapWrapper;
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(lightMap.getOpenGlId());
   }

   public void unbindLightmap() {
      GLMC.glBindTexture(0);
   }

   private boolean irisShadersInactive() {
      return IRIS_ACCESSOR == null || !IRIS_ACCESSOR.isShaderPackInUse();
   }
}

package com.seibel.distanthorizons.common.render.openGl.terrain;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShaderProgram;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeBufferRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;
import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlAbstractVertexAttribute;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexAttributePostGL43;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexAttributePreGL43;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexPointer;
import com.seibel.distanthorizons.common.render.openGl.util.vertexFormat.GlLodVertexFormat;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import org.lwjgl.opengl.GL33;

public class GlDhTerrainShaderProgram_fabric extends GlShaderProgram implements IDhApiShaderProgram {
   public static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logRendererEventToFile).build();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   private static final DhVec3f MODEL_POS = new DhVec3f();
   private static final DhApiBeforeBufferRenderEvent.EventParam BEFORE_BUFFER_RENDER_EVENT_PARAM = new DhApiBeforeBufferRenderEvent.EventParam();
   private boolean init = false;
   public GlAbstractVertexAttribute vao;
   public int uCombinedMatrix = -1;
   public int uModelOffset = -1;
   public int uWorldYOffset = -1;
   public int uMircoOffset = -1;
   public int uEarthRadius = -1;
   public int uLightMap = -1;
   public int uBlockAtlas = -1;
   public int uClipDistance = -1;
   public int uDitherDhRendering = -1;
   public int uNoiseEnabled = -1;
   public int uNoiseSteps = -1;
   public int uNoiseIntensity = -1;
   public int uNoiseDropoff = -1;
   public int uIsWhiteWorld = -1;

   public GlDhTerrainShaderProgram_fabric() {
      super(
         "assets/distanthorizons/shaders/terrain/gl/vert.vert",
         "assets/distanthorizons/shaders/terrain/gl/frag.frag",
         new String[]{"vPosition", "color", "irisData"}
      );
   }

   public void tryInit() {
      if (!this.init) {
         this.uCombinedMatrix = this.getUniformLocation("uCombinedMatrix");
         this.uModelOffset = this.getUniformLocation("uModelOffset");
         this.uWorldYOffset = this.getUniformLocation("uWorldYOffset");
         this.uDitherDhRendering = this.getUniformLocation("uDitherDhRendering");
         this.uMircoOffset = this.getUniformLocation("uMircoOffset");
         this.uEarthRadius = this.getUniformLocation("uEarthRadius");
         this.uLightMap = this.getUniformLocation("uLightMap");
         this.uBlockAtlas = this.getUniformLocation("uBlockAtlas");
         this.uClipDistance = this.getUniformLocation("uClipDistance");
         this.uNoiseEnabled = this.getUniformLocation("uNoiseEnabled");
         this.uNoiseSteps = this.getUniformLocation("uNoiseSteps");
         this.uNoiseIntensity = this.getUniformLocation("uNoiseIntensity");
         this.uNoiseDropoff = this.getUniformLocation("uNoiseDropoff");
         this.uIsWhiteWorld = this.getUniformLocation("uIsWhiteWorld");
         if (GLProxy.getInstance().vertexAttributeBufferBindingSupported) {
            this.vao = new GlVertexAttributePostGL43();
         } else {
            this.vao = new GlVertexAttributePreGL43();
         }

         this.vao.bind();
         this.vao.setVertexAttribute(0, 0, GlVertexPointer.addUnsignedShortsPointer(4, false, true));
         this.vao.setVertexAttribute(0, 1, GlVertexPointer.addUnsignedBytesPointer(4, true, false));
         this.vao.setVertexAttribute(0, 2, GlVertexPointer.addUnsignedBytesPointer(4, true, true));

         try {
            int vertexByteCount = GlLodVertexFormat.DH_VERTEX_FORMAT.getByteSize();
            this.vao.completeAndCheck(vertexByteCount);
         } catch (RuntimeException var2) {
            System.out.println(GlLodVertexFormat.DH_VERTEX_FORMAT);
            throw var2;
         }

         this.vao.unbind();
         this.init = true;
      }
   }

   @Override
   public void bind() {
      this.tryInit();
      super.bind();
      this.vao.bind();
   }

   @Override
   public void unbind() {
      super.unbind();
      this.vao.unbind();
   }

   @Override
   public void free() {
      this.vao.free();
      super.free();
   }

   @Override
   public void bindVertexBuffer(int vbo) {
      this.vao.bindBufferToAllBindingPoints(vbo);
   }

   @Override
   public void fillUniformData(DhApiRenderParam renderParameters) {
      DhMat4f combinedMatrix = new DhMat4f(renderParameters.dhProjectionMatrix);
      combinedMatrix.multiply(renderParameters.dhModelViewMatrix);
      super.bind();
      this.setUniform(this.uCombinedMatrix, combinedMatrix);
      this.setUniform(this.uMircoOffset, 0.01F);
      this.setUniform(this.uLightMap, 0);
      boolean texturedLodsEnabled = Config.Client.Advanced.Graphics.Texture.enableTexturedLods.get();
      if (texturedLodsEnabled) {
         this.setUniform(this.uBlockAtlas, 1);
      }

      this.setUniform(this.uWorldYOffset, renderParameters.worldYOffset);
      this.setUniform(this.uDitherDhRendering, Config.Client.Advanced.Graphics.Quality.ditherDhFade.get());
      float curveRatio = Config.Client.Advanced.Graphics.Experimental.earthCurveRatio.get().intValue();
      if (!(curveRatio < -1.0F) && !(curveRatio > 1.0F)) {
         curveRatio = 0.0F;
      } else {
         curveRatio = 6371000.0F / curveRatio;
      }

      this.setUniform(this.uEarthRadius, curveRatio);
      this.setUniform(this.uNoiseEnabled, Config.Client.Advanced.Graphics.NoiseTexture.enableNoiseTexture.get());
      this.setUniform(this.uNoiseSteps, Config.Client.Advanced.Graphics.NoiseTexture.noiseSteps.get());
      this.setUniform(this.uNoiseIntensity, Config.Client.Advanced.Graphics.NoiseTexture.noiseIntensity.get());
      this.setUniform(this.uNoiseDropoff, Config.Client.Advanced.Graphics.NoiseTexture.noiseDropoff.get());
      this.setUniform(this.uIsWhiteWorld, Config.Client.Advanced.Debugging.enableWhiteWorld.get());
      float dhNearClipDistance = RenderUtil.getNearClipPlaneInBlocks();
      if (!Config.Client.Advanced.Debugging.lodOnlyMode.get()) {
         dhNearClipDistance += 16.0F;
      }

      this.setUniform(this.uClipDistance, dhNearClipDistance);
   }

   @Override
   public void setModelOffsetPos(DhApiVec3f modelOffsetPos) {
      this.setUniform(this.uModelOffset, new DhVec3f(modelOffsetPos));
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public boolean overrideThisFrame() {
      return true;
   }

   public void render(RenderParams renderEventParam, boolean opaquePass, SortedArraySet<LodBufferContainer> bufferContainers, IProfilerWrapper profiler) {
      boolean renderWireframe = Config.Client.Advanced.Debugging.renderWireframe.get();
      if (renderWireframe) {
         GL33.glPolygonMode(1032, 6913);
         GLMC.disableFaceCulling();
      } else {
         GL33.glPolygonMode(1032, 6914);
         GLMC.enableFaceCulling();
      }

      if (!opaquePass) {
         GLMC.enableBlend();
         GLMC.enableDepthTest();
         GL33.glBlendEquation(32774);
         GLMC.glBlendFuncSeparate(770, 771, 1, 771);
      } else {
         GLMC.disableBlend();
      }

      GL33.glColorMask(true, true, true, true);
      ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeRenderPassEvent.class, renderEventParam.apiCopy);
      if (IRIS_ACCESSOR != null) {
         GLMC.enableFaceCulling();
      }

      if (bufferContainers != null) {
         for (int lodIndex = 0; lodIndex < bufferContainers.size(); lodIndex++) {
            LodBufferContainer bufferContainer = bufferContainers.get(lodIndex);
            if (bufferContainer.buffersUploaded) {
               DhVec3d camPos = renderEventParam.exactCameraPosition;
               MODEL_POS.set(
                  (float)(bufferContainer.minCornerBlockPos.getX() - camPos.x),
                  (float)(bufferContainer.minCornerBlockPos.getY() - camPos.y),
                  (float)(bufferContainer.minCornerBlockPos.getZ() - camPos.z)
               );
               BEFORE_BUFFER_RENDER_EVENT_PARAM.update(renderEventParam, MODEL_POS);
               GlDhMetaRenderer_fabric.INSTANCE.shaderProgramForThisFrame.bind();
               GlDhMetaRenderer_fabric.INSTANCE.shaderProgramForThisFrame.setModelOffsetPos(MODEL_POS);
               ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeBufferRenderEvent.class, BEFORE_BUFFER_RENDER_EVENT_PARAM);
               IVertexBufferWrapper[] vertexBuffers = opaquePass ? bufferContainer.vboOpaqueWrappers : bufferContainer.vboTransparentWrappers;

               for (int vboIndex = 0; vboIndex < vertexBuffers.length; vboIndex++) {
                  GLVertexBuffer vbo = (GLVertexBuffer)vertexBuffers[vboIndex];
                  if (vbo != null) {
                     long vboReadStamp = vbo.renderStampLock.readLock();
                     long iboReadStamp = vbo.getQuadIBO().renderStampLock.readLock();

                     try {
                        if (vbo.getVertexCount() != 0 && vbo.getId() != 0 && vbo.getQuadIBO().getId() != 0) {
                           int indexCount = (int)(vbo.getVertexCount() * 1.5);
                           vbo.bind();
                           vbo.getQuadIBO().bind();
                           GlDhMetaRenderer_fabric.INSTANCE.shaderProgramForThisFrame.bindVertexBuffer(vbo.getId());
                           GL33.glDrawElements(4, indexCount, vbo.getQuadIBO().getGlType(), 0L);
                           vbo.unbind();
                           vbo.getQuadIBO().unbind();
                        }
                     } finally {
                        vbo.renderStampLock.unlock(vboReadStamp);
                        vbo.getQuadIBO().renderStampLock.unlock(iboReadStamp);
                     }
                  }
               }
            }
         }
      }

      if (renderWireframe) {
         GL33.glPolygonMode(1032, 6914);
         GLMC.enableFaceCulling();
      }
   }
}

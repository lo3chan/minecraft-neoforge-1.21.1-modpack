package com.seibel.distanthorizons.common.render.openGl.generic;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiGenericObjectShaderProgram;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericObjectRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericRenderCleanupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericRenderSetupEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLProxy;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLIndexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.EPlatform;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.render.renderer.GenericRenderObjectFactory;
import com.seibel.distanthorizons.core.render.renderer.RenderableBoxGroup;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.GL33;

public class GlGenericObjectRenderer implements IDhGenericRenderer {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private static final DhApiRenderableBoxGroupShading DEFAULT_SHADING = DhApiRenderableBoxGroupShading.getUnshaded();
   private static final DhApiBeforeGenericObjectRenderEvent.EventParam EVENT_PARAM = new DhApiBeforeGenericObjectRenderEvent.EventParam();
   public static final boolean RENDER_DEBUG_OBJECTS = false;
   private boolean init = false;
   private IDhApiGenericObjectShaderProgram instancedShaderProgram;
   private IDhApiGenericObjectShaderProgram directShaderProgram;
   private GLVertexBuffer boxVertexBuffer;
   private GLIndexBuffer boxIndexBuffer;
   private boolean instancedRenderingAvailable;
   private boolean vertexAttribDivisorSupported;
   private boolean instancedArraysSupported;
   private final ConcurrentHashMap<Long, RenderableBoxGroup> boxGroupById = new ConcurrentHashMap<>();
   private static final float[] BOX_VERTICES = new float[]{
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F
   };
   private static final int[] BOX_INDICES = new int[]{
      2, 1, 0, 0, 3, 2, 6, 5, 4, 4, 7, 6, 10, 9, 8, 8, 11, 10, 14, 13, 12, 12, 15, 14, 18, 17, 16, 16, 19, 18, 20, 21, 22, 22, 23, 20
   };

   public void init() {
      if (!this.init) {
         this.init = true;
         this.vertexAttribDivisorSupported = GLProxy.getInstance().vertexAttribDivisorSupported;
         this.instancedArraysSupported = GLProxy.getInstance().instancedArraysSupported;
         boolean isMac = EPlatform.get() == EPlatform.MACOS;
         if (isMac) {
            LOGGER.warn("Generic rendering not supported by Mac. Clouds, beacons, and some other effects will be disabled.");
            Config.Client.Advanced.Graphics.GenericRendering.enableGenericRendering.setApiValue(false);
         } else {
            this.instancedRenderingAvailable = (this.vertexAttribDivisorSupported || this.instancedArraysSupported) && !isMac;
            if (!this.instancedRenderingAvailable) {
               LOGGER.warn(
                  "Instanced rendering not supported by this GPU, falling back to direct rendering. Generic object rendering will be slow and some effects may be disabled."
               );
            }

            this.instancedShaderProgram = new GlGenericObjectShaderProgram(true);
            this.directShaderProgram = new GlGenericObjectShaderProgram(false);
            this.createBuffers();
         }
      }
   }

   private void createBuffers() {
      ByteBuffer boxVerticesBuffer = ByteBuffer.allocateDirect(BOX_VERTICES.length * 4);
      boxVerticesBuffer.order(ByteOrder.nativeOrder());
      boxVerticesBuffer.asFloatBuffer().put(BOX_VERTICES);
      boxVerticesBuffer.rewind();
      this.boxVertexBuffer = new GLVertexBuffer(false);
      this.boxVertexBuffer.bind();
      this.boxVertexBuffer.uploadBuffer(boxVerticesBuffer, 8, EDhApiGpuUploadMethod.DATA, BOX_VERTICES.length * 4);
      ByteBuffer solidIndexBuffer = ByteBuffer.allocateDirect(BOX_INDICES.length * 4);
      solidIndexBuffer.order(ByteOrder.nativeOrder());
      solidIndexBuffer.asIntBuffer().put(BOX_INDICES);
      solidIndexBuffer.rewind();
      this.boxIndexBuffer = new GLIndexBuffer(false);
      this.boxIndexBuffer.uploadBuffer(solidIndexBuffer, EDhApiGpuUploadMethod.DATA, BOX_INDICES.length * 4, 35044);
      this.boxIndexBuffer.bind();
   }

   private void addGenericDebugObjects() {
      GenericRenderObjectFactory factory = GenericRenderObjectFactory.INSTANCE;
      IDhApiRenderableBoxGroup singleGiantBoxGroup = factory.createForSingleBox(
         "DistantHorizons:CyanChunkBox",
         new DhApiRenderableBox(
            new DhApiVec3d(0.0, 0.0, 0.0),
            new DhApiVec3d(16.0, 190.0, 16.0),
            new Color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), 125),
            EDhApiBlockMaterial.WATER
         )
      );
      singleGiantBoxGroup.setSkyLight(15);
      singleGiantBoxGroup.setBlockLight(15);
      this.add(singleGiantBoxGroup);
      IDhApiRenderableBoxGroup singleTallBoxGroup = factory.createForSingleBox(
         "DistantHorizons:GreenBeacon",
         new DhApiRenderableBox(
            new DhApiVec3d(16.0, 0.0, 31.0),
            new DhApiVec3d(17.0, 2000.0, 32.0),
            new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 125),
            EDhApiBlockMaterial.ILLUMINATED
         )
      );
      singleTallBoxGroup.setSkyLight(15);
      singleTallBoxGroup.setBlockLight(15);
      this.add(singleTallBoxGroup);
      ArrayList<DhApiRenderableBox> absBoxList = new ArrayList<>();

      for (int i = 0; i < 18; i++) {
         absBoxList.add(
            new DhApiRenderableBox(
               new DhApiVec3d(i, 150 + i, 24.0),
               new DhApiVec3d(1 + i, 151 + i, 25.0),
               new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue()),
               EDhApiBlockMaterial.LAVA
            )
         );
      }

      IDhApiRenderableBoxGroup absolutePosBoxGroup = factory.createAbsolutePositionedGroup("DistantHorizons:OrangeStairs", absBoxList);
      this.add(absolutePosBoxGroup);
      ArrayList<DhApiRenderableBox> relBoxList = new ArrayList<>();

      for (int i = 0; i < 8; i += 2) {
         relBoxList.add(
            new DhApiRenderableBox(
               new DhApiVec3d(0.0, i, 0.0),
               new DhApiVec3d(1.0, 1 + i, 1.0),
               new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue()),
               EDhApiBlockMaterial.METAL
            )
         );
      }

      IDhApiRenderableBoxGroup relativePosBoxGroup = factory.createRelativePositionedGroup(
         "DistantHorizons:MovingMagentaGroup", new DhApiVec3d(24.0, 140.0, 24.0), relBoxList
      );
      relativePosBoxGroup.setPreRenderFunc(event -> {
         DhApiVec3d pos = relativePosBoxGroup.getOriginBlockPos();
         pos.x = pos.x + event.partialTicks / 2.0F;
         pos.x %= 32.0;
         relativePosBoxGroup.setOriginBlockPos(pos);
      });
      this.add(relativePosBoxGroup);
      ArrayList<DhApiRenderableBox> massRelBoxList = new ArrayList<>();

      for (int x = 0; x < 100; x += 2) {
         for (int z = 0; z < 100; z += 2) {
            massRelBoxList.add(
               new DhApiRenderableBox(
                  new DhApiVec3d(-x, 0.0, -z),
                  new DhApiVec3d(1 - x, 1.0, 1 - z),
                  new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue()),
                  EDhApiBlockMaterial.TERRACOTTA
               )
            );
         }
      }

      IDhApiRenderableBoxGroup massRelativePosBoxGroup = factory.createRelativePositionedGroup(
         "DistantHorizons:MassRedGroup", new DhApiVec3d(-25.0, 140.0, 0.0), massRelBoxList
      );
      massRelativePosBoxGroup.setPreRenderFunc(event -> {
         DhApiVec3d blockPos = massRelativePosBoxGroup.getOriginBlockPos();
         blockPos.y = blockPos.y + event.partialTicks / 4.0F;
         if (blockPos.y > 150.0) {
            blockPos.y = 140.0;
            Color newColor = massRelativePosBoxGroup.get(0).color == Color.RED ? Color.RED.darker() : Color.RED;
            massRelativePosBoxGroup.forEach(box -> box.color = newColor);
            massRelativePosBoxGroup.triggerBoxChange();
         }

         massRelativePosBoxGroup.setOriginBlockPos(blockPos);
      });
      this.add(massRelativePosBoxGroup);
   }

   @Override
   public void add(IDhApiRenderableBoxGroup iBoxGroup) throws IllegalArgumentException {
      if (!(iBoxGroup instanceof RenderableBoxGroup boxGroup)) {
         throw new IllegalArgumentException(
            "Box group must be of type ["
               + RenderableBoxGroup.class.getSimpleName()
               + "], type received: ["
               + (iBoxGroup != null ? iBoxGroup.getClass() : "NULL")
               + "]."
         );
      } else {
         if (boxGroup.size() != 0) {
            boxGroup.triggerBoxChange();
         }

         long id = boxGroup.getId();
         if (this.boxGroupById.containsKey(id)) {
            throw new IllegalArgumentException("A box group with the ID [" + id + "] is already present.");
         } else {
            this.boxGroupById.put(id, boxGroup);
         }
      }
   }

   @Override
   public IDhApiRenderableBoxGroup remove(long id) {
      return this.boxGroupById.remove(id);
   }

   public void clear() {
      this.boxGroupById.clear();
   }

   @Override
   public void render(RenderParams renderEventParam, IProfilerWrapper profiler, boolean renderingWithSsao) {
      if (EPlatform.get() != EPlatform.MACOS) {
         try (IProfilerWrapper.IProfileBlock setup_profile = profiler.push("setup")) {
            this.init();
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeGenericRenderSetupEvent.class, renderEventParam.apiCopy);
            boolean renderWireframe = Config.Client.Advanced.Debugging.renderWireframe.get();
            if (renderWireframe) {
               GL33.glPolygonMode(1032, 6913);
               GLMC.disableFaceCulling();
            } else {
               GL33.glPolygonMode(1032, 6914);
               GLMC.enableFaceCulling();
            }

            GLMC.enableBlend();
            GL33.glBlendEquation(32774);
            GLMC.glBlendFuncSeparate(770, 771, 1, 771);
            IDhApiGenericObjectShaderProgram shaderProgram = this.instancedRenderingAvailable ? this.instancedShaderProgram : this.directShaderProgram;
            IDhApiGenericObjectShaderProgram shaderProgramOverride = OverrideInjector.INSTANCE.get(IDhApiGenericObjectShaderProgram.class);
            if (shaderProgramOverride != null && shaderProgram.overrideThisFrame()) {
               shaderProgram = shaderProgramOverride;
            }

            shaderProgram.bind(renderEventParam);
            shaderProgram.bindVertexBuffer(this.boxVertexBuffer.getId());
            this.boxIndexBuffer.bind();

            for (RenderableBoxGroup boxGroup : this.boxGroupById.values()) {
               if (boxGroup != null && boxGroup.ssaoEnabled == renderingWithSsao) {
                  profiler.popPush("render prep");
                  boxGroup.preRender(renderEventParam);
                  if (boxGroup.active) {
                     EVENT_PARAM.update(renderEventParam, boxGroup);
                     boolean cancelRendering = ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeGenericObjectRenderEvent.class, EVENT_PARAM);
                     if (!cancelRendering) {
                        if (this.instancedRenderingAvailable) {
                           boxGroup.tryUpdateInstancedDataAsync();
                           if (boxGroup.vertexBufferContainer.getState() != IDhGenericObjectVertexBufferContainer.EState.RENDER) {
                              continue;
                           }
                        }

                        profiler.popPush("rendering");

                        try (
                           IProfilerWrapper.IProfileBlock namespace_profile = profiler.push(boxGroup.getResourceLocationNamespace());
                           IProfilerWrapper.IProfileBlock location_profile = profiler.push(boxGroup.getResourceLocationPath());
                        ) {
                           if (this.instancedRenderingAvailable) {
                              this.renderBoxGroupInstanced(shaderProgram, renderEventParam, boxGroup, renderEventParam.exactCameraPosition, profiler);
                           } else {
                              this.renderBoxGroupDirect(shaderProgram, renderEventParam, boxGroup, renderEventParam.exactCameraPosition, profiler);
                           }
                        }

                        boxGroup.postRender(renderEventParam);
                     }
                  }
               }
            }

            profiler.popPush("cleanup");
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiBeforeGenericRenderCleanupEvent.class, renderEventParam.apiCopy);
            if (renderWireframe) {
               GL33.glPolygonMode(1032, 6914);
               GLMC.enableFaceCulling();
            }

            shaderProgram.unbind();
            this.boxVertexBuffer.unbind();
            this.boxIndexBuffer.unbind();
         }
      }
   }

   private void renderBoxGroupInstanced(
      IDhApiGenericObjectShaderProgram shaderProgram, DhApiRenderParam renderEventParam, RenderableBoxGroup boxGroup, DhVec3d camPos, IProfilerWrapper profiler
   ) {
      try (IProfilerWrapper.IProfileBlock render_profile = profiler.push("vertex setup")) {
         DhApiRenderableBoxGroupShading shading = boxGroup.shading;
         if (shading == null) {
            shading = DEFAULT_SHADING;
         }

         shaderProgram.fillIndirectUniformData(renderEventParam, shading, boxGroup, camPos);
         profiler.popPush("binding");
         GlGenericObjectVertexContainer container = (GlGenericObjectVertexContainer)boxGroup.vertexBufferContainer;
         GL33.glBindBuffer(34962, container.color);
         GL33.glEnableVertexAttribArray(1);
         GL33.glVertexAttribPointer(1, 4, 5126, false, 16, 0L);
         this.vertexAttribDivisor(1, 1);
         GL33.glBindBuffer(34962, container.scale);
         GL33.glEnableVertexAttribArray(2);
         this.vertexAttribDivisor(2, 1);
         GL33.glVertexAttribPointer(2, 3, 5126, false, 12, 0L);
         GL33.glBindBuffer(34962, container.chunkPos);
         GL33.glEnableVertexAttribArray(3);
         this.vertexAttribDivisor(3, 1);
         GL33.glVertexAttribIPointer(3, 3, 5124, 12, 0L);
         GL33.glBindBuffer(34962, container.subChunkPos);
         GL33.glEnableVertexAttribArray(4);
         this.vertexAttribDivisor(4, 1);
         GL33.glVertexAttribPointer(4, 3, 5126, false, 12, 0L);
         GL33.glBindBuffer(34962, container.material);
         GL33.glEnableVertexAttribArray(5);
         this.vertexAttribDivisor(5, 1);
         GL33.glVertexAttribIPointer(5, 1, 5120, 1, 0L);
         profiler.popPush("render");
         if (container.uploadedBoxCount > 0) {
            GL33.glDrawElementsInstanced(4, BOX_INDICES.length, 5125, 0L, container.uploadedBoxCount);
         }

         profiler.popPush("cleanup");
         GL33.glDisableVertexAttribArray(1);
         GL33.glDisableVertexAttribArray(2);
         GL33.glDisableVertexAttribArray(3);
         GL33.glDisableVertexAttribArray(4);
         GL33.glDisableVertexAttribArray(5);
      }
   }

   private void vertexAttribDivisor(int index, int divisor) {
      if (this.vertexAttribDivisorSupported) {
         GL33.glVertexAttribDivisor(index, divisor);
      } else {
         if (!this.instancedArraysSupported) {
            throw new IllegalStateException("Instanced rendering isn't supported by this machine. Direct rendering should have been used instead.");
         }

         ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
      }
   }

   private void renderBoxGroupDirect(
      IDhApiGenericObjectShaderProgram shaderProgram, DhApiRenderParam renderEventParam, RenderableBoxGroup boxGroup, DhVec3d camPos, IProfilerWrapper profiler
   ) {
      profiler.popPush("shared uniforms");
      DhApiRenderableBoxGroupShading shading = boxGroup.shading;
      if (shading == null) {
         shading = DhApiRenderableBoxGroupShading.getUnshaded();
      }

      shaderProgram.fillSharedDirectUniformData(renderEventParam, shading, boxGroup, camPos);

      for (int i = 0; i < boxGroup.size(); i++) {
         try {
            DhApiRenderableBox box = boxGroup.get(i);
            if (box != null) {
               profiler.popPush("direct uniforms");
               shaderProgram.fillDirectUniformData(renderEventParam, boxGroup, box, camPos);
               profiler.popPush("render");
               GL33.glDrawElements(4, BOX_INDICES.length, 5125, 0L);
            }
         } catch (IndexOutOfBoundsException var9) {
            break;
         }
      }
   }

   public boolean getInstancedRenderingAvailable() throws IllegalStateException {
      if (!this.init) {
         throw new IllegalStateException("GL initialization hasn't been completed.");
      } else {
         return this.instancedRenderingAvailable;
      }
   }

   @Override
   public String getVboRenderDebugMenuString() {
      int totalGroupCount = this.boxGroupById.size();
      int totalBoxCount = 0;
      int activeGroupCount = 0;
      int activeBoxCount = 0;

      for (long key : this.boxGroupById.keySet()) {
         RenderableBoxGroup renderGroup = this.boxGroupById.get(key);
         if (renderGroup.active) {
            activeGroupCount++;
            activeBoxCount += renderGroup.size();
         }

         totalBoxCount += renderGroup.size();
      }

      return "Generic Obj #: "
         + F3Screen.NUMBER_FORMAT.format((long)activeGroupCount)
         + "/"
         + F3Screen.NUMBER_FORMAT.format((long)totalGroupCount)
         + ", Cube #: "
         + F3Screen.NUMBER_FORMAT.format((long)activeBoxCount)
         + "/"
         + F3Screen.NUMBER_FORMAT.format((long)totalBoxCount);
   }

   @Override
   public void close() {
      if (this.boxVertexBuffer != null) {
         this.boxVertexBuffer.close();
      }

      if (this.boxIndexBuffer != null) {
         this.boxIndexBuffer.close();
      }
   }
}

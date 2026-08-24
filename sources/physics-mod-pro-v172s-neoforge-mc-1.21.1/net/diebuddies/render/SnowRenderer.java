package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.IntBuffer;
import java.util.Map.Entry;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ClientChunkCacheAccessor;
import net.diebuddies.mixins.vines.StorageInvoker;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowBatch;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;

public class SnowRenderer {
   public static final int SNOW_ENTITY_ID = 829925;
   private MainRenderer mainRenderer;
   private SnowRenderer.MultiDrawElementsBaseVertexCommand drawElementsCommand;
   private SnowRenderer.MultiDrawArraysCommand drawAraysCommands;
   private Matrix4f transformation = new Matrix4f();
   private Matrix4f currentPose = new Matrix4f();
   private Matrix3f tmp = new Matrix3f();

   public SnowRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.drawElementsCommand = new SnowRenderer.MultiDrawElementsBaseVertexCommand();
      this.drawAraysCommands = new SnowRenderer.MultiDrawArraysCommand();
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view) {
      if (ConfigClient.areSnowPhysicsEnabled()) {
         PerformanceTracker.startNoFlush("snow_rendering");
         if (level.effects().constantAmbientLight()) {
            RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
         } else {
            RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
         }

         RenderSystem.setShaderTexture(0, PhysicsMod.whiteTexture.getID());
         RenderSystem.activeTexture(33984);
         RenderSystem.bindTexture(PhysicsMod.whiteTexture.getID());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableCull();
         this.mainRenderer.setupPBRTextures();
         RenderSystem.activeTexture(33984);
         ShaderInstance shader = RenderSystem.getShader();
         RenderSystem.setupShaderLights(shader);
         if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setColorModulator(RenderSystem.getShaderColor());
         } else if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
            shader.COLOR_MODULATOR.upload();
         }

         if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
            if (shader.LIGHT0_DIRECTION != null) {
               shader.LIGHT0_DIRECTION.upload();
            }

            if (shader.LIGHT1_DIRECTION != null) {
               shader.LIGHT1_DIRECTION.upload();
            }
         }

         GL32C.glVertexAttrib4f(Data.COLOR.getAttribute(), 1.0F, 1.0F, 1.0F, 1.0F);
         GL32C.glVertexAttrib2f(Data.TEX_COORD_SHADER.getAttribute(), 0.0F, 0.0F);
         if (StarterClient.optifabric) {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), 0.0F, 0.0F);
         } else if (StarterClient.iris) {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_SHADER.getAttribute(), 0.0F, 0.0F);
         }

         GL32C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         int entityIdLocation = GL32C.glGetUniformLocation(RenderSystem.getShader().getId(), "entityId");
         int lastEntityId = 0;
         if (entityIdLocation != -1) {
            lastEntityId = GL32C.glGetUniformi(RenderSystem.getShader().getId(), entityIdLocation);
            GL32C.glUniform1i(entityIdLocation, 829925);
         } else if (StarterClient.iris && Data.ENTITY_ID_SHADER.getAttribute() != -1) {
            GL32C.glVertexAttribI3ui(Data.ENTITY_ID_SHADER.getAttribute(), 829925, 0, 0);
         }

         SnowWorld snowWorld = physics.getSnowWorld();
         Matrix4f cameraRotation = matrixStackIn;
         Matrix3f normal = matrixStackIn.normal(this.tmp);
         snowWorld.bindForRendering();
         if (level.getChunkSource() != null && level.getChunkSource() instanceof ClientChunkCacheAccessor cacheAccessor) {
            StorageInvoker storageInvoker = (StorageInvoker)cacheAccessor.getStorage();

            for (Entry<Vector3i, SnowBatch.SnowChunkBucket> entry : snowWorld.getSnowBatch().getBuckets().entrySet()) {
               Vector3i position = entry.getKey();
               SnowBatch.SnowChunkBucket bucket = entry.getValue();
               AABB3D modelBoundingBox = bucket.getAABB();
               Vector3d start = modelBoundingBox.start;
               Vector3d end = modelBoundingBox.end;
               if (this.mainRenderer
                  .frustumInt
                  .testAab(
                     (float)(start.x - view.x),
                     (float)(start.y - view.y),
                     (float)(start.z - view.z),
                     (float)(end.x - view.x),
                     (float)(end.y - view.y),
                     (float)(end.z - view.z)
                  )) {
                  for (ChunkEntity snowChunk : bucket.getEntities()) {
                     if (storageInvoker.invokeInRange(snowChunk.position.x, snowChunk.position.z)) {
                        this.renderSnow(snowWorld, level, cameraRotation, normal, view, snowChunk);
                     }
                  }

                  if (this.drawElementsCommand.size() > 0 || this.drawAraysCommands.size() > 0) {
                     this.setupMatrices(shader, position, view, cameraRotation, normal);
                     this.executeDrawCommands();
                  }
               }
            }
         }

         if (entityIdLocation != -1) {
            GL32C.glUniform1i(entityIdLocation, lastEntityId);
         } else if (Data.ENTITY_ID_SHADER.getAttribute() != -1) {
            GL32C.glVertexAttribI3ui(Data.ENTITY_ID_SHADER.getAttribute(), 0, 0, 0);
         }

         StateTracker.unbindVertexArray();
         PerformanceTracker.end("snow_rendering");
      }
   }

   private void executeDrawCommands() {
      int size = this.drawElementsCommand.size();
      if (size > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer counts = stack.mallocInt(size);
            PointerBuffer pointers = stack.mallocPointer(size);
            IntBuffer baseVertices = stack.mallocInt(size);

            for (int i = 0; i < size; i++) {
               counts.put(i, this.drawElementsCommand.getCount(i));
               pointers.put(i, this.drawElementsCommand.getPointer(i));
               baseVertices.put(i, this.drawElementsCommand.getBaseVertex(i));
            }

            GL32C.glMultiDrawElementsBaseVertex(4, counts, 5125, pointers, baseVertices);
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }

         this.drawElementsCommand.clear();
      }

      size = this.drawAraysCommands.size();
      if (size > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer firsts = stack.mallocInt(size);
            IntBuffer counts = stack.mallocInt(size);

            for (int i = 0; i < size; i++) {
               firsts.put(i, this.drawAraysCommands.getFirst(i));
               counts.put(i, this.drawAraysCommands.getCount(i));
            }

            GL32C.glMultiDrawArrays(4, firsts, counts);
         } catch (Throwable var9) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (stack != null) {
            stack.close();
         }

         this.drawAraysCommands.clear();
      }
   }

   private void setupMatrices(ShaderInstance shader, Vector3i position, Vec3 view, Matrix4f cameraRotation, Matrix3f normal) {
      float scale = 1.0F / IChunk.CHUNK_MULTIPLE;
      this.transformation
         .setTranslation(
            (float)((position.x * IChunk.CHUNK_SIZE + 0.5) * scale - view.x),
            (float)((position.y * IChunk.CHUNK_SIZE + 0.5) * scale - view.y),
            (float)((position.z * IChunk.CHUNK_SIZE + 0.5) * scale - view.z)
         );
      this.transformation.m00(scale);
      this.transformation.m11(scale);
      this.transformation.m22(scale);
      cameraRotation.mul(this.transformation, this.currentPose);
      if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         Optifine.setModelViewMatrix(this.currentPose);
      } else {
         int location = shader.MODEL_VIEW_MATRIX.getLocation();
         if (location != -1) {
            GL32C.glUniformMatrix4fv(location, false, this.currentPose.get(MainRenderer.matrixBuffer));
         }

         if (StarterClient.iris) {
            Iris.setNormalMatrix(shader, this.currentPose, normal);
         }
      }
   }

   private void renderSnow(SnowWorld snowWorld, Level level, Matrix4f cameraRotationMatrix, Matrix3f normalMatrix, Vec3 view, ChunkEntity snow) {
      AABB3D modelBoundingBox = snow.aabb;
      Vector3d start = modelBoundingBox.start;
      Vector3d end = modelBoundingBox.end;
      if (this.mainRenderer
         .frustumInt
         .testAab(
            (float)(start.x - view.x),
            (float)(start.y - view.y),
            (float)(start.z - view.z),
            (float)(end.x - view.x),
            (float)(end.y - view.y),
            (float)(end.z - view.z)
         )) {
         if (snowWorld.getSnowIndexData() == null) {
            ArenaBuffer.MemorySegment segment = snow.vertexSegment;
            this.drawAraysCommands.add(segment.offset / snowWorld.format.getStride(), segment.size / snowWorld.format.getStride());
         } else {
            ArenaBuffer.MemorySegment vertexSegment = snow.vertexSegment;
            ArenaBuffer.MemorySegment indexSegment = snow.indexSegment;
            int baseVertex = vertexSegment.offset / snowWorld.format.getStride();
            this.drawElementsCommand.add(indexSegment.size / 4, indexSegment.offset, baseVertex);
         }
      }
   }

   private static class MultiDrawArraysCommand {
      public IntList first = new IntArrayList();
      public IntList count = new IntArrayList();

      public MultiDrawArraysCommand() {
      }

      public void add(int first, int count) {
         this.first.add(first);
         this.count.add(count);
      }

      public int getFirst(int index) {
         return this.first.getInt(index);
      }

      public int getCount(int index) {
         return this.count.getInt(index);
      }

      public void clear() {
         this.first.clear();
         this.count.clear();
      }

      public int size() {
         return this.count.size();
      }
   }

   private static class MultiDrawElementsBaseVertexCommand {
      public IntList count = new IntArrayList();
      public LongList pointer = new LongArrayList();
      public IntList baseVertex = new IntArrayList();

      public MultiDrawElementsBaseVertexCommand() {
      }

      public void add(int count, long pointer, int baseVertex) {
         this.count.add(count);
         this.pointer.add(pointer);
         this.baseVertex.add(baseVertex);
      }

      public int getCount(int index) {
         return this.count.getInt(index);
      }

      public long getPointer(int index) {
         return this.pointer.getLong(index);
      }

      public int getBaseVertex(int index) {
         return this.baseVertex.getInt(index);
      }

      public void clear() {
         this.count.clear();
         this.pointer.clear();
         this.baseVertex.clear();
      }

      public int size() {
         return this.count.size();
      }
   }
}

package org.dimdev.limlib.client.specialmodels.compat.sodium;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.SectionPos;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public final class SodiumSpecialModelMeshRegistry {
   private static final ConcurrentLinkedQueue<SodiumSpecialModelMeshRegistry.PendingSectionMeshes> pendingSections = new ConcurrentLinkedQueue<>();
   private static final Map<Long, SodiumSpecialModelMeshRegistry.UploadedSectionMeshes> uploadedSections = new LinkedHashMap<>();

   public static SodiumSpecialModelMeshRegistry.PendingMeshBuilder createBuilder(RenderType renderType) {
      ByteBufferBuilder byteBuffer = new ByteBufferBuilder(renderType.bufferSize());
      return new SodiumSpecialModelMeshRegistry.PendingMeshBuilder(byteBuffer, new BufferBuilder(byteBuffer, renderType.mode(), renderType.format()));
   }

   public static void submit(SectionPos sectionPos, Map<RenderType, SodiumSpecialModelMeshRegistry.PendingMeshBuilder> builders) {
      Map<RenderType, SodiumSpecialModelMeshRegistry.PendingMesh> meshes = new LinkedHashMap<>();

      for (Entry<RenderType, SodiumSpecialModelMeshRegistry.PendingMeshBuilder> entry : builders.entrySet()) {
         SodiumSpecialModelMeshRegistry.PendingMesh mesh = entry.getValue().build();
         if (mesh != null) {
            meshes.put(entry.getKey(), mesh);
         }
      }

      pendingSections.add(new SodiumSpecialModelMeshRegistry.PendingSectionMeshes(sectionPos.asLong(), meshes));
   }

   public static void remove(SectionPos sectionPos) {
      pendingSections.add(new SodiumSpecialModelMeshRegistry.PendingSectionMeshes(sectionPos.asLong(), Map.of()));
   }

   public static void renderAll(double cameraX, double cameraY, double cameraZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
      uploadPending();
      if (!uploadedSections.isEmpty()) {
         for (RenderType renderType : SpecialModelRenderTypes.chunkBufferLayers()) {
            boolean drawing = false;

            for (SodiumSpecialModelMeshRegistry.UploadedSectionMeshes sectionMeshes : uploadedSections.values()) {
               VertexBuffer vertexBuffer = sectionMeshes.meshes().get(renderType);
               if (vertexBuffer != null) {
                  if (!drawing) {
                     renderType.setupRenderState();
                     drawing = true;
                  }

                  ShaderInstance shader = RenderSystem.getShader();
                  if (shader != null) {
                     vertexBuffer.bind();
                     shader.setDefaultUniforms(renderType.mode(), modelViewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
                     if (shader.CHUNK_OFFSET != null) {
                        shader.CHUNK_OFFSET.set((float)(-cameraX), (float)(-cameraY), (float)(-cameraZ));
                     }

                     shader.apply();
                     vertexBuffer.draw();
                     if (shader.CHUNK_OFFSET != null) {
                        shader.CHUNK_OFFSET.set(0.0F, 0.0F, 0.0F);
                        shader.CHUNK_OFFSET.upload();
                     }

                     shader.clear();
                  }
               }
            }

            if (drawing) {
               VertexBuffer.unbind();
               renderType.clearRenderState();
            }
         }
      }
   }

   public static void clear() {
      SodiumSpecialModelMeshRegistry.PendingSectionMeshes pending;
      while ((pending = pendingSections.poll()) != null) {
         pending.close();
      }

      uploadedSections.values().forEach(SodiumSpecialModelMeshRegistry.UploadedSectionMeshes::close);
      uploadedSections.clear();
   }

   private static void uploadPending() {
      SodiumSpecialModelMeshRegistry.PendingSectionMeshes pending;
      while ((pending = pendingSections.poll()) != null) {
         SodiumSpecialModelMeshRegistry.UploadedSectionMeshes previous = uploadedSections.remove(pending.sectionKey());
         if (previous != null) {
            previous.close();
         }

         if (!pending.meshes().isEmpty()) {
            uploadedSections.put(pending.sectionKey(), pending.upload());
         }
      }
   }

   private SodiumSpecialModelMeshRegistry() {
   }

   private record PendingMesh(MeshData meshData, ByteBufferBuilder byteBuffer) {
      private VertexBuffer upload() {
         VertexBuffer vertexBuffer = new VertexBuffer(Usage.STATIC);
         vertexBuffer.bind();

         try {
            vertexBuffer.upload(this.meshData);
         } finally {
            VertexBuffer.unbind();
            this.byteBuffer.close();
         }

         return vertexBuffer;
      }

      private void close() {
         try {
            this.meshData.close();
         } finally {
            this.byteBuffer.close();
         }
      }
   }

   public record PendingMeshBuilder(ByteBufferBuilder byteBuffer, BufferBuilder buffer) {
      @Nullable
      private SodiumSpecialModelMeshRegistry.PendingMesh build() {
         MeshData meshData = this.buffer.build();
         if (meshData == null) {
            this.byteBuffer.close();
            return null;
         } else {
            return new SodiumSpecialModelMeshRegistry.PendingMesh(meshData, this.byteBuffer);
         }
      }
   }

   private record PendingSectionMeshes(long sectionKey, Map<RenderType, SodiumSpecialModelMeshRegistry.PendingMesh> meshes) {
      private SodiumSpecialModelMeshRegistry.UploadedSectionMeshes upload() {
         Map<RenderType, VertexBuffer> uploaded = new LinkedHashMap<>();

         try {
            for (Entry<RenderType, SodiumSpecialModelMeshRegistry.PendingMesh> entry : this.meshes.entrySet()) {
               uploaded.put(entry.getKey(), entry.getValue().upload());
            }
         } catch (RuntimeException var4) {
            uploaded.values().forEach(VertexBuffer::close);
            throw var4;
         }

         return new SodiumSpecialModelMeshRegistry.UploadedSectionMeshes(uploaded);
      }

      private void close() {
         this.meshes.values().forEach(SodiumSpecialModelMeshRegistry.PendingMesh::close);
      }
   }

   private record UploadedSectionMeshes(Map<RenderType, VertexBuffer> meshes) {
      private void close() {
         this.meshes.values().forEach(VertexBuffer::close);
      }
   }
}

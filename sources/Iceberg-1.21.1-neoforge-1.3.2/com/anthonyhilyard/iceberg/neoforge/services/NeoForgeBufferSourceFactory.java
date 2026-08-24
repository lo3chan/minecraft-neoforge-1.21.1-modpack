package com.anthonyhilyard.iceberg.neoforge.services;

import com.anthonyhilyard.iceberg.renderer.CheckedBufferSource;
import com.anthonyhilyard.iceberg.renderer.VertexCollector;
import com.anthonyhilyard.iceberg.services.IBufferSourceFactory;
import com.anthonyhilyard.iceberg.util.UnsafeUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class NeoForgeBufferSourceFactory implements IBufferSourceFactory {
   @Override
   public CheckedBufferSource createCheckedBufferSource(Object bufferSource) {
      return new CheckedBufferSource((MultiBufferSource)bufferSource) {
         @Override
         public VertexConsumer getBuffer(RenderType renderType) {
            final VertexConsumer vertexConsumer = this.bufferSource.getBuffer(renderType);
            VertexConsumer vertexConsumerWrap = new VertexConsumerSodium() {
               public VertexConsumer addVertex(float x, float y, float z) {
                  hasRendered = true;
                  return vertexConsumer.addVertex(x, y, z);
               }

               public VertexConsumer setColor(int r, int g, int b, int a) {
                  return vertexConsumer.setColor(r, g, b, a);
               }

               public VertexConsumer setUv(float u, float v) {
                  return vertexConsumer.setUv(u, v);
               }

               public VertexConsumer setUv1(int u, int v) {
                  return vertexConsumer.setUv1(u, v);
               }

               public VertexConsumer setUv2(int u, int v) {
                  return vertexConsumer.setUv2(u, v);
               }

               public VertexConsumer setNormal(float x, float y, float z) {
                  return vertexConsumer.setNormal(x, y, z);
               }

               public void push(MemoryStack memoryStack, long pointer, int count, VertexFormat format) {
                  hasRendered = true;
                  ((VertexBufferWriter)vertexConsumer).push(memoryStack, pointer, count, format);
               }
            };
            return vertexConsumerWrap;
         }
      };
   }

   @Override
   public VertexCollector createVertexCollector() {
      return new VertexCollector() {
         @Override
         public VertexConsumer getBuffer(RenderType renderType) {
            return new VertexConsumerSodium() {
               public VertexConsumer addVertex(float x, float y, float z) {
                  if (currentAlpha >= 25) {
                     vertices.add(new Vector3f(currentVertex.set(x, y, z)));
                  }

                  currentAlpha = 255;
                  return this;
               }

               public VertexConsumer setColor(int r, int g, int b, int a) {
                  currentAlpha = a;
                  return this;
               }

               public VertexConsumer setUv(float u, float v) {
                  return this;
               }

               public VertexConsumer setUv1(int u, int v) {
                  return this;
               }

               public VertexConsumer setUv2(int u, int v) {
                  return this;
               }

               public VertexConsumer setNormal(float x, float y, float z) {
                  return this;
               }

               public void push(MemoryStack memoryStack, long pointer, int count, VertexFormat format) {
                  for (int i = 0; i < count; i++) {
                     float x = UnsafeUtil.readFloat(pointer + i * format.getVertexSize() + format.getOffset(VertexFormatElement.POSITION));
                     float y = UnsafeUtil.readFloat(pointer + i * format.getVertexSize() + format.getOffset(VertexFormatElement.POSITION) + 4L);
                     float z = UnsafeUtil.readFloat(pointer + i * format.getVertexSize() + format.getOffset(VertexFormatElement.POSITION) + 8L);
                     int a = UnsafeUtil.readByte(pointer + i * format.getVertexSize() + format.getOffset(VertexFormatElement.COLOR) + 3L) & 255;
                     if (a >= 25) {
                        vertices.add(new Vector3f(x, y, z));
                     }
                  }
               }
            };
         }
      };
   }
}

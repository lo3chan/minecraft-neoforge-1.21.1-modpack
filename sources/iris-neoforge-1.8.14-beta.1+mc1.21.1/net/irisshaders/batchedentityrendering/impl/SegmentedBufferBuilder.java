package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.irisshaders.batchedentityrendering.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderType;

public class SegmentedBufferBuilder implements MemoryTrackingBuffer {
   private final Map<RenderType, ByteBufferBuilderHolder> buffers;
   private final Map<RenderType, BufferBuilder> builders;
   private final List<BufferSegment> segments;
   private final FullyBufferedMultiBufferSource parent;

   public SegmentedBufferBuilder(FullyBufferedMultiBufferSource parent) {
      this.parent = parent;
      this.buffers = new Object2ObjectOpenHashMap();
      this.builders = new Object2ObjectOpenHashMap();
      this.segments = new ArrayList<>();
   }

   private static boolean shouldSortOnUpload(RenderType type) {
      return ((RenderTypeAccessor)type).shouldSortOnUpload();
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      try {
         ByteBufferBuilderHolder buffer = this.buffers
            .computeIfAbsent(renderType, r -> new ByteBufferBuilderHolder(new ByteBufferBuilder(renderType.bufferSize())));
         buffer.wasUsed();
         BufferBuilder builder = this.builders.computeIfAbsent(renderType, t -> new BufferBuilder(buffer.getBuffer(), renderType.mode(), renderType.format()));
         if (RenderTypeUtil.isTriangleStripDrawMode(renderType)) {
            ((BufferBuilderExt)builder).splitStrip();
         }

         return builder;
      } catch (OutOfMemoryError var4) {
         this.weAreOutOfMemory();
         return this.getBuffer(renderType);
      }
   }

   private void weAreOutOfMemory() {
      this.parent.weAreOutOfMemory();
   }

   public List<BufferSegment> getSegments() {
      this.builders.forEach((renderType, bufferBuilder) -> {
         try {
            MeshData meshData = bufferBuilder.build();
            if (meshData == null) {
               return;
            }

            if (shouldSortOnUpload(renderType)) {
               meshData.sortQuads(this.buffers.get(renderType).getBuffer(), RenderSystem.getVertexSorting());
            }

            this.segments.add(new BufferSegment(meshData, renderType));
         } catch (OutOfMemoryError var4) {
            this.weAreOutOfMemory();
         }
      });
      this.builders.clear();
      List<BufferSegment> finalSegments = new ArrayList<>(this.segments);
      this.segments.clear();
      return finalSegments;
   }

   @Override
   public long getAllocatedSize() {
      long usedSize = 0L;

      for (ByteBufferBuilderHolder buffer : this.buffers.values()) {
         usedSize += buffer.getAllocatedSize();
      }

      return usedSize;
   }

   @Override
   public long getUsedSize() {
      long usedSize = 0L;

      for (ByteBufferBuilderHolder buffer : this.buffers.values()) {
         usedSize += buffer.getUsedSize();
      }

      return usedSize;
   }

   @Override
   public void freeAndDeleteBuffer() {
      for (ByteBufferBuilderHolder buffer : this.buffers.values()) {
         buffer.forceDelete();
      }

      this.buffers.clear();
   }

   public void clearBuffers(int clearTime) {
      this.buffers.values().removeIf(b -> b.deleteOrClear(clearTime));
   }

   public void lastDitchAttempt() {
      this.buffers.values().removeIf(b -> b.delete(500));
   }
}

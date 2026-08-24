package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class OldFullyBufferedMultiBufferSource extends BufferSource {
   private final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
   private final Object2IntMap<RenderType> unused = new Object2IntOpenHashMap();
   private final Set<BufferBuilder> activeBuffers = new HashSet<>();
   private final Set<RenderType> typesThisFrame;
   private final List<RenderType> typesInOrder;
   private boolean flushed = false;

   public OldFullyBufferedMultiBufferSource() {
      super(new ByteBufferBuilder(0), Object2ObjectSortedMaps.emptyMap());
      this.typesThisFrame = new HashSet<>();
      this.typesInOrder = new ArrayList<>();
   }

   private TransparencyType getTransparencyType(RenderType type) {
      while (type instanceof WrappableRenderType) {
         type = ((WrappableRenderType)type).unwrap();
      }

      return type instanceof BlendingStateHolder ? ((BlendingStateHolder)type).getTransparencyType() : TransparencyType.GENERAL_TRANSPARENT;
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      this.flushed = false;
      BufferBuilder buffer = this.bufferBuilders
         .computeIfAbsent(renderType, type -> new BufferBuilder(new ByteBufferBuilder(type.bufferSize()), renderType.mode(), renderType.format()));
      if (this.activeBuffers.add(buffer)) {
      }

      if (this.typesThisFrame.add(renderType)) {
         this.typesInOrder.add(renderType);
      }

      this.unused.removeInt(renderType);
      return buffer;
   }

   public void endBatch() {
      if (!this.flushed) {
         List<RenderType> removedTypes = new ArrayList<>();
         this.unused.forEach((unusedType, unusedCount) -> {
            if (unusedCount >= 10) {
               BufferBuilder buffer = this.bufferBuilders.remove(unusedType);
               removedTypes.add(unusedType);
               if (this.activeBuffers.contains(buffer)) {
                  throw new IllegalStateException("A buffer was simultaneously marked as inactive and as active, something is very wrong...");
               }
            }
         });

         for (RenderType removed : removedTypes) {
            this.unused.removeInt(removed);
         }

         this.typesInOrder.sort(Comparator.comparing(this::getTransparencyType));

         for (RenderType type : this.typesInOrder) {
            this.drawInternal(type);
         }

         this.typesInOrder.clear();
         this.typesThisFrame.clear();
         this.flushed = true;
      }
   }

   public void endBatch(RenderType type) {
   }

   private void drawInternal(RenderType type) {
      BufferBuilder buffer = this.bufferBuilders.get(type);
      if (buffer != null) {
         if (this.activeBuffers.remove(buffer)) {
            type.draw(buffer.build());
         } else {
            int unusedCount = this.unused.getOrDefault(type, 0);
            this.unused.put(type, ++unusedCount);
         }
      }
   }
}

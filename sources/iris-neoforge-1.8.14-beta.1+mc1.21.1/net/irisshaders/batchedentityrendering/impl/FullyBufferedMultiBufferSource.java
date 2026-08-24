package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.irisshaders.batchedentityrendering.impl.ordering.GraphTranslucencyRenderOrderManager;
import net.irisshaders.batchedentityrendering.impl.ordering.RenderOrderManager;
import net.irisshaders.iris.layer.WrappingMultiBufferSource;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.profiling.ProfilerFiller;

public class FullyBufferedMultiBufferSource extends BufferSource implements MemoryTrackingBuffer, Groupable, WrappingMultiBufferSource {
   private static final int NUM_BUFFERS = 32;
   private final RenderOrderManager renderOrderManager;
   private final SegmentedBufferBuilder[] builders;
   private final LinkedHashMap<RenderType, Integer> affinities;
   private final BufferSegmentRenderer segmentRenderer;
   private final FullyBufferedMultiBufferSource.UnflushableWrapper unflushableWrapper;
   private final List<Function<RenderType, RenderType>> wrappingFunctionStack;
   private final Map<RenderType, List<BufferSegment>> typeToSegment = new HashMap<>();
   private int drawCalls;
   private int renderTypes;
   private Function<RenderType, RenderType> wrappingFunction = null;
   private boolean isReady;
   private List<RenderType> renderOrder = new ArrayList<>();

   public FullyBufferedMultiBufferSource() {
      super(new ByteBufferBuilder(0), Object2ObjectSortedMaps.emptyMap());
      this.renderOrderManager = new GraphTranslucencyRenderOrderManager();
      this.builders = new SegmentedBufferBuilder[32];

      for (int i = 0; i < this.builders.length; i++) {
         this.builders[i] = new SegmentedBufferBuilder(this);
      }

      this.affinities = new LinkedHashMap<>(32, 0.75F, true);
      this.drawCalls = 0;
      this.segmentRenderer = new BufferSegmentRenderer();
      this.unflushableWrapper = new FullyBufferedMultiBufferSource.UnflushableWrapper(this);
      this.wrappingFunctionStack = new ArrayList<>();
   }

   private static long toMib(long x) {
      return x / 1024L / 1024L;
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      this.removeReady();
      if (this.wrappingFunction != null) {
         renderType = this.wrappingFunction.apply(renderType);
      }

      this.renderOrderManager.begin(renderType);
      Integer affinity = this.affinities.get(renderType);
      if (affinity == null) {
         if (this.affinities.size() < this.builders.length) {
            affinity = this.affinities.size();
         } else {
            Iterator<Entry<RenderType, Integer>> iterator = this.affinities.entrySet().iterator();
            Entry<RenderType, Integer> evicted = iterator.next();
            iterator.remove();
            this.affinities.remove(evicted.getKey());
            affinity = evicted.getValue();
         }

         this.affinities.put(renderType, affinity);
      }

      return this.builders[affinity].getBuffer(renderType);
   }

   private void removeReady() {
      this.isReady = false;
      this.typeToSegment.clear();
      this.renderOrder.clear();
   }

   public void readyUp() {
      this.isReady = true;
      ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
      profiler.push("collect");

      for (SegmentedBufferBuilder builder : this.builders) {
         for (BufferSegment segment : builder.getSegments()) {
            this.typeToSegment.computeIfAbsent(segment.type(), type -> new ArrayList<>()).add(segment);
         }
      }

      profiler.popPush("resolve ordering");
      this.renderOrder = this.renderOrderManager.getRenderOrder();
      this.renderOrderManager.reset();
      this.affinities.clear();
      profiler.pop();
   }

   public void endBatch() {
      ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
      if (!this.isReady) {
         this.readyUp();
      }

      profiler.push("draw buffers");

      for (RenderType type : this.renderOrder) {
         if (this.typeToSegment.containsKey(type)) {
            List<BufferSegment> segments = this.typeToSegment.getOrDefault(type, Collections.emptyList());
            this.renderTypes++;
            type.setupRenderState();
            ImmediateState.mergeRendering = true;

            for (BufferSegment segment : segments) {
               segment.type().draw(segment.meshData());
               this.drawCalls++;
            }

            type.clearRenderState();
         }
      }

      ImmediateState.mergeRendering = false;
      int targetClearTime = this.getTargetClearTime();

      for (SegmentedBufferBuilder builder : this.builders) {
         builder.clearBuffers(targetClearTime);
      }

      profiler.popPush("reset");
      this.removeReady();
      profiler.pop();
   }

   public void endBatchWithType(TransparencyType transparencyType) {
      ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
      if (!this.isReady) {
         this.readyUp();
      }

      profiler.push("draw buffers");
      List<RenderType> types = new ArrayList<>();

      for (RenderType type : this.renderOrder) {
         if (((BlendingStateHolder)type).getTransparencyType() == transparencyType) {
            types.add(type);
            if (this.typeToSegment.containsKey(type)) {
               List<BufferSegment> segments = this.typeToSegment.getOrDefault(type, Collections.emptyList());
               this.renderTypes++;
               type.setupRenderState();
               ImmediateState.mergeRendering = true;

               for (BufferSegment segment : segments) {
                  segment.type().draw(segment.meshData());
                  this.drawCalls++;
               }

               this.typeToSegment.remove(type);
               type.clearRenderState();
            }
         }
      }

      ImmediateState.mergeRendering = false;
      profiler.popPush("reset type " + transparencyType);
      this.renderOrder.removeAll(types);
      profiler.pop();
   }

   private int getTargetClearTime() {
      long sizeInMiB = toMib(this.getAllocatedSize());
      if (sizeInMiB > 5000L) {
         return 1000;
      } else {
         return sizeInMiB > 1000L ? 5000 : 10000;
      }
   }

   public int getDrawCalls() {
      return this.drawCalls;
   }

   public int getRenderTypes() {
      return this.renderTypes;
   }

   public void resetDrawCalls() {
      this.drawCalls = 0;
      this.renderTypes = 0;
   }

   public void endBatch(RenderType type) {
   }

   public BufferSource getUnflushableWrapper() {
      return this.unflushableWrapper;
   }

   @Override
   public long getAllocatedSize() {
      long size = 0L;

      for (SegmentedBufferBuilder builder : this.builders) {
         size += builder.getAllocatedSize();
      }

      return size;
   }

   @Override
   public long getUsedSize() {
      long size = 0L;

      for (SegmentedBufferBuilder builder : this.builders) {
         size += builder.getUsedSize();
      }

      return size;
   }

   @Override
   public void freeAndDeleteBuffer() {
      for (SegmentedBufferBuilder builder : this.builders) {
         builder.freeAndDeleteBuffer();
      }
   }

   @Override
   public void startGroup() {
      this.renderOrderManager.startGroup();
   }

   @Override
   public boolean maybeStartGroup() {
      return this.renderOrderManager.maybeStartGroup();
   }

   @Override
   public void endGroup() {
      this.renderOrderManager.endGroup();
   }

   @Override
   public void pushWrappingFunction(Function<RenderType, RenderType> wrappingFunction) {
      if (this.wrappingFunction != null) {
         this.wrappingFunctionStack.add(this.wrappingFunction);
      }

      this.wrappingFunction = wrappingFunction;
   }

   @Override
   public void popWrappingFunction() {
      if (this.wrappingFunctionStack.isEmpty()) {
         this.wrappingFunction = null;
      } else {
         this.wrappingFunction = (Function<RenderType, RenderType>)this.wrappingFunctionStack.removeLast();
      }
   }

   @Override
   public void assertWrapStackEmpty() {
      if (!this.wrappingFunctionStack.isEmpty() || this.wrappingFunction != null) {
         throw new IllegalStateException("Wrapping function stack not empty!");
      }
   }

   public void weAreOutOfMemory() {
      for (SegmentedBufferBuilder builder : this.builders) {
         builder.lastDitchAttempt();
      }
   }

   private static class UnflushableWrapper extends BufferSource implements Groupable {
      private final FullyBufferedMultiBufferSource wrapped;

      UnflushableWrapper(FullyBufferedMultiBufferSource wrapped) {
         super(new ByteBufferBuilder(0), Object2ObjectSortedMaps.emptyMap());
         this.wrapped = wrapped;
      }

      public VertexConsumer getBuffer(RenderType renderType) {
         return this.wrapped.getBuffer(renderType);
      }

      public void endBatch() {
      }

      public void endBatch(RenderType type) {
      }

      @Override
      public void startGroup() {
         this.wrapped.startGroup();
      }

      @Override
      public boolean maybeStartGroup() {
         return this.wrapped.maybeStartGroup();
      }

      @Override
      public void endGroup() {
         this.wrapped.endGroup();
      }
   }
}

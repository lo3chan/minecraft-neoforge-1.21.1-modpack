package net.raphimc.immediatelyfast.feature.core;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.Arrays;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.compat.IrisCompat;

public class BatchableBufferSource extends BufferSource implements AutoCloseable {
   private static final ByteBufferBuilder FALLBACK_BUFFER = new ByteBufferBuilder(0);
   protected final Map<RenderType, ReferenceSet<BufferBuilder>> pendingBuffers = (Map<RenderType, ReferenceSet<BufferBuilder>>)(IrisCompat.IRIS_LOADED
      ? new Object2ObjectLinkedOpenHashMap()
      : new Reference2ObjectLinkedOpenHashMap());
   protected final Set<RenderType> activeLayers = (Set<RenderType>)(IrisCompat.IRIS_LOADED ? new ObjectLinkedOpenHashSet() : new ReferenceLinkedOpenHashSet());
   protected boolean drawFallbackLayersFirst = false;

   public BatchableBufferSource() {
      this(Object2ObjectSortedMaps.emptyMap());
   }

   public BatchableBufferSource(SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
      this(FALLBACK_BUFFER, layerBuffers);
   }

   public BatchableBufferSource(ByteBufferBuilder fallbackBuffer, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
      super(fallbackBuffer, layerBuffers);
   }

   public VertexConsumer getBuffer(RenderType layer) {
      if (!this.drawFallbackLayersFirst && this.lastSharedType != null && this.lastSharedType != layer && !this.fixedBuffers.containsKey(this.lastSharedType)) {
         this.drawFallbackLayersFirst = true;
      }

      if (IrisCompat.IRIS_LOADED) {
         IrisCompat.skipExtension.set(!IrisCompat.isRenderingLevel.getAsBoolean());
      }

      boolean hasBufferForRenderLayer = layer.canConsolidateConsecutiveGeometry() && this.pendingBuffers.containsKey(layer);
      BufferBuilder bufferBuilder;
      if (!layer.canConsolidateConsecutiveGeometry()) {
         bufferBuilder = new BufferBuilder(this.getNextBufferAllocator(), layer.mode(), layer.format());
         this.lastSharedType = layer;
      } else if (hasBufferForRenderLayer) {
         bufferBuilder = (BufferBuilder)this.pendingBuffers.get(layer).iterator().next();
      } else if (this.fixedBuffers.containsKey(layer)) {
         bufferBuilder = new BufferBuilder((ByteBufferBuilder)this.fixedBuffers.get(layer), layer.mode(), layer.format());
      } else {
         bufferBuilder = new BufferBuilder(this.getNextBufferAllocator(), layer.mode(), layer.format());
         this.lastSharedType = layer;
      }

      if (IrisCompat.IRIS_LOADED) {
         IrisCompat.skipExtension.set(false);
      }

      if (hasBufferForRenderLayer) {
         if ((ImmediatelyFast.config.debug_only_use_last_usage_for_batch_ordering || layer.name.contains("immediatelyfast:renderlast"))
            && this.activeLayers.contains(layer)) {
            this.activeLayers.remove(layer);
            this.activeLayers.add(layer);
         }
      } else {
         this.pendingBuffers.computeIfAbsent(layer, k -> new ReferenceLinkedOpenHashSet()).add(bufferBuilder);
         this.activeLayers.add(layer);
      }

      return bufferBuilder;
   }

   public void endLastBatch() {
      this.lastSharedType = null;
      this.drawFallbackLayersFirst = false;
      int sortedLayersLength = 0;
      RenderType[] sortedLayers = new RenderType[this.activeLayers.size()];

      for (RenderType layer : this.activeLayers) {
         if (!this.fixedBuffers.containsKey(layer)) {
            sortedLayers[sortedLayersLength++] = layer;
         }
      }

      if (sortedLayersLength != 0) {
         Arrays.sort(sortedLayers, (l1, l2) -> Integer.compare(this.getLayerOrder(l1), this.getLayerOrder(l2)));

         for (int i = 0; i < sortedLayersLength; i++) {
            this.endBatch(sortedLayers[i]);
         }
      }
   }

   public void endBatch() {
      if (this.activeLayers.isEmpty()) {
         this.close();
      } else {
         this.endLastBatch();

         for (RenderType layer : this.fixedBuffers.keySet()) {
            this.endBatch(layer);
         }
      }
   }

   public void endBatch(RenderType layer) {
      if (this.drawFallbackLayersFirst) {
         this.endLastBatch();
      }

      this.drawDirect(layer);
   }

   @Override
   public void close() {
      this.lastSharedType = null;
      this.drawFallbackLayersFirst = false;

      for (Set<BufferBuilder> buffers : this.pendingBuffers.values()) {
         for (BufferBuilder bufferBuilder : buffers) {
            bufferBuilder.build().close();
            BufferAllocatorPool.returnBufferAllocatorSafe(bufferBuilder.buffer);
         }
      }

      this.activeLayers.clear();
      this.pendingBuffers.clear();
   }

   public void drawDirect(RenderType layer) {
      if (IrisCompat.IRIS_LOADED && !IrisCompat.isRenderingLevel.getAsBoolean()) {
         IrisCompat.renderWithExtendedVertexFormat.accept(false);
      }

      this.activeLayers.remove(layer);
      Set<BufferBuilder> buffers = (Set<BufferBuilder>)this.pendingBuffers.remove(layer);
      if (buffers != null) {
         for (BufferBuilder bufferBuilder : buffers) {
            ByteBufferBuilder prevBufferAllocator = this.sharedBuffer;
            this.sharedBuffer = bufferBuilder.buffer;
            this.endBatch(layer, bufferBuilder);
            this.sharedBuffer = prevBufferAllocator;
            BufferAllocatorPool.returnBufferAllocatorSafe(bufferBuilder.buffer);
         }
      }

      if (this.lastSharedType == layer) {
         this.lastSharedType = null;
      }

      if (IrisCompat.IRIS_LOADED && !IrisCompat.isRenderingLevel.getAsBoolean()) {
         IrisCompat.renderWithExtendedVertexFormat.accept(true);
      }
   }

   public boolean hasActiveLayers() {
      return !this.activeLayers.isEmpty();
   }

   protected int getLayerOrder(RenderType layer) {
      if (layer == null) {
         return 2147483647;
      } else {
         int order = 0;
         if (layer instanceof CompositeRenderType multiPhase) {
            ResourceLocation textureId = (ResourceLocation)multiPhase.state().textureState.cutoutTexture().orElse(null);
            if (textureId != null) {
               if (textureId.getPath().startsWith("textures/entity/horse/")) {
                  String horseTexturePath = textureId.getPath().substring("textures/entity/horse/".length());
                  if (horseTexturePath.startsWith("horse_markings")) {
                     return 2;
                  }

                  if (horseTexturePath.startsWith("armor/")) {
                     return 3;
                  }

                  return 1;
               }

               if (textureId.toString().startsWith("minecraft:textures/entity/wolf/")) {
                  if (textureId.equals(WolfCollarLayer.WOLF_COLLAR_LOCATION)) {
                     return 2;
                  }

                  return 1;
               }

               if (textureId.getPath().startsWith("textures/entity/villager/")) {
                  String villagerTexturePath = textureId.getPath().substring("textures/entity/villager/".length());
                  if (villagerTexturePath.startsWith("type/")) {
                     return 2;
                  }

                  if (villagerTexturePath.startsWith("profession/")) {
                     return 3;
                  }

                  if (villagerTexturePath.startsWith("profession_level/")) {
                     return 4;
                  }

                  return 1;
               }

               if (textureId.equals(Sheets.ARMOR_TRIMS_SHEET)) {
                  order = 1;
               } else if (!layer.name.startsWith("text") && !layer.name.startsWith("neoforge_text") && !layer.name.startsWith("forge_text")) {
                  if (textureId.getNamespace().equals("cataclysm")) {
                     if (textureId.getPath().equals("textures/entity/maledictus/phantom_halberd.png")) {
                        return 2;
                     }

                     if (textureId.getPath().equals("textures/entity/maledictus/phantom_halberd_discard.png")) {
                        return 1;
                     }
                  }
               } else if (textureId.getNamespace().equals("minecraft")) {
                  order = 2;
               } else {
                  order = 1;
               }
            }
         }

         return !layer.sortOnUpload() ? order : 100000000 + order;
      }
   }

   private ByteBufferBuilder getNextBufferAllocator() {
      return this.sharedBuffer != FALLBACK_BUFFER && this.lastSharedType == null && this.sharedBuffer.pointer != 0L
         ? this.sharedBuffer
         : BufferAllocatorPool.borrowBufferAllocator();
   }
}

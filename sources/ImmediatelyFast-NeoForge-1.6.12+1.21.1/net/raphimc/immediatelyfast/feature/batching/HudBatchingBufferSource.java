package net.raphimc.immediatelyfast.feature.batching;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceObjectImmutablePair;
import it.unimi.dsi.fastutil.objects.ReferenceObjectPair;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.SequencedMap;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;

public class HudBatchingBufferSource extends BatchableBufferSource {
   private final Object2ObjectMap<ReferenceObjectPair<RenderType, LightingState>, RenderType> lightingRenderLayers = new Object2ObjectOpenHashMap();
   private final Reference2ObjectMap<RenderType, ReferenceSet<RenderType>> renderLayerMap = new Reference2ObjectOpenHashMap();
   private boolean renderingItem = false;
   private boolean renderingItemDecorations = false;
   private boolean currentlyDrawing = false;

   public HudBatchingBufferSource(ByteBufferBuilder fallbackBuffer, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
      super(fallbackBuffer, layerBuffers);
   }

   public void setRenderingItem(boolean renderingItem) {
      this.renderingItem = renderingItem;
   }

   public void setRenderingItemDecorations(boolean renderingItemDecorations) {
      this.renderingItemDecorations = renderingItemDecorations;
   }

   public boolean isCurrentlyDrawing() {
      return this.currentlyDrawing;
   }

   @Override
   public VertexConsumer getBuffer(RenderType layer) {
      if (layer.name.contains("glint")) {
         return super.getBuffer(layer);
      } else if (this.renderingItem) {
         LightingState lightingState = LightingState.current();
         RenderType newLayer = (RenderType)this.lightingRenderLayers
            .computeIfAbsent(
               new ReferenceObjectImmutablePair(layer, lightingState),
               key -> new BatchingBuffers.WrappedRenderLayer(layer, lightingState::saveAndApply, lightingState::revert)
            );
         ((ReferenceSet)this.renderLayerMap.computeIfAbsent(layer, key -> new ReferenceOpenHashSet())).add(newLayer);
         return super.getBuffer(newLayer);
      } else {
         return this.renderingItemDecorations && layer == RenderType.guiOverlay() ? super.getBuffer(RenderType.gui()) : super.getBuffer(layer);
      }
   }

   @Override
   public void drawDirect(RenderType layer) {
      this.currentlyDrawing = true;

      try {
         Set<RenderType> renderLayers = (Set<RenderType>)this.renderLayerMap.remove(layer);
         if (renderLayers != null) {
            for (RenderType renderLayer : renderLayers) {
               super.drawDirect(renderLayer);
            }
         } else {
            super.drawDirect(layer);
         }
      } finally {
         this.currentlyDrawing = false;
      }
   }

   @Override
   public void endBatch() {
      super.endBatch();
      this.lightingRenderLayers.clear();
      this.renderLayerMap.clear();
   }

   @Override
   public void close() {
      super.close();
      this.lightingRenderLayers.clear();
      this.renderLayerMap.clear();
   }
}

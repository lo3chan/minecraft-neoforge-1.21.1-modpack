package net.raphimc.immediatelyfast.feature.batching;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class BatchingBuffers {
   private static BufferSource nonBatchingEntityVertexConsumers;
   private static HudBatchingBufferSource hudBatchingVertexConsumers;
   private static boolean isHudBatching;

   public static BufferSource getNonBatchingEntityVertexConsumers() {
      if (nonBatchingEntityVertexConsumers == null) {
         SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = createLayerBuffers(
            Minecraft.getInstance().renderBuffers().bufferSource().fixedBuffers.keySet()
         );
         nonBatchingEntityVertexConsumers = new BufferSource(new ByteBufferBuilder(786432), layerBuffers);
      }

      return nonBatchingEntityVertexConsumers;
   }

   public static BufferSource getHudBatchingVertexConsumers() {
      if (hudBatchingVertexConsumers == null) {
         SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = createLayerBuffers(
            Minecraft.getInstance().renderBuffers().bufferSource().fixedBuffers.keySet()
         );
         hudBatchingVertexConsumers = new HudBatchingBufferSource(new ByteBufferBuilder(786432), layerBuffers);
      }

      return hudBatchingVertexConsumers;
   }

   public static void runBatched(GuiGraphics drawContext, Runnable runnable) {
      drawContext.flush();
      BufferSource prev = drawContext.bufferSource;
      drawContext.bufferSource = getHudBatchingVertexConsumers();
      isHudBatching = true;

      try {
         runnable.run();
         drawContext.flush();
      } finally {
         drawContext.bufferSource = prev;
         isHudBatching = false;
      }
   }

   public static BufferSource beginHudBatching(GuiGraphics drawContext) {
      drawContext.flush();
      BufferSource prev = drawContext.bufferSource;
      drawContext.bufferSource = getHudBatchingVertexConsumers();
      isHudBatching = true;
      return prev;
   }

   public static void endHudBatching(GuiGraphics drawContext, BufferSource prev) {
      drawContext.flush();
      drawContext.bufferSource = prev;
      isHudBatching = false;
   }

   public static boolean isHudBatching() {
      return isHudBatching;
   }

   public static void tryForceDrawHudBuffers() {
      if (!hudBatchingVertexConsumers.isCurrentlyDrawing() && hudBatchingVertexConsumers.hasActiveLayers()) {
         RenderSystemState renderSystemState = RenderSystemState.current();

         try {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            hudBatchingVertexConsumers.endBatch();
         } finally {
            renderSystemState.apply();
         }
      }
   }

   private static SequencedMap<RenderType, ByteBufferBuilder> createLayerBuffers(Set<RenderType> layers) {
      SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = new Object2ObjectLinkedOpenHashMap(layers.size());

      for (RenderType layer : layers) {
         layerBuffers.put(layer, new ByteBufferBuilder(layer.bufferSize()));
      }

      return layerBuffers;
   }

   public static class WrappedRenderLayer extends RenderType {
      public WrappedRenderLayer(RenderType renderLayer, Runnable additionalStartAction, Runnable additionalEndAction) {
         super(
            renderLayer.name,
            renderLayer.format(),
            renderLayer.mode(),
            renderLayer.bufferSize(),
            renderLayer.affectsCrumbling(),
            renderLayer.sortOnUpload(),
            () -> {
               renderLayer.setupRenderState();
               additionalStartAction.run();
            },
            () -> {
               renderLayer.clearRenderState();
               additionalEndAction.run();
            }
         );
      }
   }
}

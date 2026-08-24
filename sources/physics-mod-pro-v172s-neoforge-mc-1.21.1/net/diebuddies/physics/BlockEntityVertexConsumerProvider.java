package net.diebuddies.physics;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.compat.Sodium;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BlockEntityVertexConsumerProvider implements MultiBufferSource {
   public static BlockEntityVertexConsumer currentConsumer;
   private Map<RenderType, BlockEntityVertexConsumer> renderTypes = new Object2ObjectOpenHashMap();
   private RenderType lastLayer;
   private boolean destruction;

   public BlockEntityVertexConsumerProvider(boolean destruction) {
      this.destruction = destruction;
   }

   public VertexConsumer getBuffer(RenderType layer) {
      if (this.lastLayer != null) {
         this.lastLayer.clearRenderState();
      }

      this.lastLayer = layer;
      layer.setupRenderState();
      return currentConsumer = this.renderTypes
         .computeIfAbsent(layer, key -> StarterClient.sodium ? Sodium.getNewBlockConsumer() : new BlockEntityVertexConsumer());
   }

   public RenderType getLastLayer() {
      return this.lastLayer;
   }

   public Map<RenderType, BlockEntityVertexConsumer> getBakedRenderTypeModels() {
      return this.renderTypes;
   }

   public boolean isDestruction() {
      return this.destruction;
   }
}

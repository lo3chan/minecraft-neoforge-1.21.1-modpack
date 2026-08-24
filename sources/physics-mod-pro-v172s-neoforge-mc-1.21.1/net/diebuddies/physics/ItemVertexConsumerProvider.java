package net.diebuddies.physics;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.compat.Sodium;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class ItemVertexConsumerProvider implements MultiBufferSource {
   public Map<RenderType, DummyVertexConsumer> dummy = new Object2ObjectOpenHashMap();
   public RenderType lastLayer;

   public VertexConsumer getBuffer(RenderType layer) {
      return this.dummy.computeIfAbsent(layer, key -> StarterClient.sodium ? Sodium.getNewDummyConsumer() : new DummyVertexConsumer());
   }
}

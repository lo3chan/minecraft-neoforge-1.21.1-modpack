package net.irisshaders.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.Arrays;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {BufferBuilder.class},
   priority = 999
)
public abstract class MixinBufferBuilder_SeparateAo implements VertexConsumer {
   public void putBulkData(
      Pose matrixEntry,
      BakedQuad quad,
      float[] brightnesses,
      float red,
      float green,
      float blue,
      float alpha,
      int[] lights,
      int overlay,
      boolean useQuadColorData
   ) {
      if (WorldRenderingSettings.INSTANCE.shouldUseSeparateAo()) {
         int brightnessIndex = 0;
         brightnesses = new float[brightnesses.length];
         Arrays.fill(brightnesses, 1.0F);
      }

      super.putBulkData(matrixEntry, quad, brightnesses, red, green, blue, alpha, lights, overlay, useQuadColorData);
   }
}

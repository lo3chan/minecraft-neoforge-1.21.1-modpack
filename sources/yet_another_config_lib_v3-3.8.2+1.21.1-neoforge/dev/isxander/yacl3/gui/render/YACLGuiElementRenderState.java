package dev.isxander.yacl3.gui.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;

public interface YACLGuiElementRenderState {
   BaseRenderState baseState();

   void buildVertices(VertexConsumer var1, float var2);

   default VertexConsumer add2DVertex(VertexConsumer vertexConsumer, float x, float y, float z) {
      return vertexConsumer.addVertex(this.baseState().pose(), x, y, z);
   }

   default void submit(GuiGraphics graphics) {
      VertexConsumer vertexConsumer = GuiRenderStateSink.bufferSource(graphics).getBuffer(this.baseState().renderType());
      this.buildVertices(vertexConsumer, 0.0F);
   }
}

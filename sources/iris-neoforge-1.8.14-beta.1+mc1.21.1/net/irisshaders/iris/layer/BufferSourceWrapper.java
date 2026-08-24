package net.irisshaders.iris.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BufferSourceWrapper implements MultiBufferSource, Groupable {
   private final MultiBufferSource bufferSource;
   private final Function<RenderType, RenderType> typeChanger;

   public BufferSourceWrapper(MultiBufferSource bufferSource, Function<RenderType, RenderType> typeChanger) {
      this.bufferSource = bufferSource;
      this.typeChanger = typeChanger;
   }

   public MultiBufferSource getOriginal() {
      return this.bufferSource;
   }

   @Override
   public void startGroup() {
      if (this.bufferSource instanceof Groupable groupable) {
         groupable.startGroup();
      }
   }

   @Override
   public boolean maybeStartGroup() {
      return this.bufferSource instanceof Groupable groupable ? groupable.maybeStartGroup() : false;
   }

   @Override
   public void endGroup() {
      if (this.bufferSource instanceof Groupable groupable) {
         groupable.endGroup();
      }
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      return this.bufferSource.getBuffer(this.typeChanger.apply(renderType));
   }
}

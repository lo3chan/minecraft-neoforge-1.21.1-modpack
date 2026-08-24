package net.raphimc.immediatelyfast.feature.batching;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;

public class BatchingRenderLayers {
   public static final BiFunction<Integer, BlendFuncDepthFuncState, RenderType> TEXTURE = Util.memoize(
      (id, blendFuncDepthFunc) -> new BatchingRenderLayers.ImmediatelyFastRenderLayer("texture", Mode.QUADS, DefaultVertexFormat.POSITION_TEX, false, () -> {
         blendFuncDepthFunc.saveAndApply();
         RenderSystem.setShaderTexture(0, id);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
      }, blendFuncDepthFunc::revert)
   );
   public static final BiFunction<Integer, BlendFuncDepthFuncState, RenderType> COLORED_TEXTURE = Util.memoize(
      (id, blendFuncDepthFunc) -> new BatchingRenderLayers.ImmediatelyFastRenderLayer(
         "colored_texture", Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, false, () -> {
            blendFuncDepthFunc.saveAndApply();
            RenderSystem.setShaderTexture(0, id);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         }, blendFuncDepthFunc::revert
      )
   );

   private static class ImmediatelyFastRenderLayer extends RenderType {
      private ImmediatelyFastRenderLayer(String name, Mode drawMode, VertexFormat vertexFormat, boolean translucent, Runnable startAction, Runnable endAction) {
         super("immediatelyfast_" + name, vertexFormat, drawMode, 2048, false, translucent, startAction, endAction);
      }
   }
}

package com.anthonyhilyard.legendarytooltips.mixin;

import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.iceberg.util.Tooltips.TooltipRenderContext;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipScroll;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BakedGlyph.class})
public class BakedGlyphMixin {
   @Shadow
   @Final
   private float down;
   @Unique
   private Vector3f currentTop = new Vector3f();
   @Unique
   private Vector3f currentBottom = new Vector3f();
   private final float fadeThickness = 10.0F;

   @Inject(
      method = {"render(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"},
      at = {@At("HEAD")}
   )
   private void grabLocals(
      boolean bl, float x, float y, Matrix4f matrix4f, VertexConsumer vertexConsumer, float h, float i, float j, float k, int l, CallbackInfo info
   ) {
      matrix4f.transformPosition(x, y, 0.0F, this.currentTop);
      matrix4f.transformPosition(x, y + this.down, 0.0F, this.currentBottom);
   }

   @Unique
   private float calculateFadeAlpha(float vertexY) {
      TooltipRenderContext context = Tooltips.getCurrentRenderContext();
      float scrollTop = TooltipScroll.getScrollTop(context.index());
      float scrollBottom = TooltipScroll.getScrollBottom(context.index());
      float topFadeEnd = scrollTop + 10.0F;
      float bottomFadeStart = scrollBottom - 10.0F;
      if (vertexY < topFadeEnd) {
         return Math.clamp((vertexY - scrollTop) / 10.0F, 0.0F, 1.0F);
      } else {
         return vertexY > bottomFadeStart ? Math.clamp((scrollBottom - vertexY) / 10.0F, 0.0F, 1.0F) : 1.0F;
      }
   }

   @ModifyArg(
      method = {"render(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
         ordinal = 0
      ),
      index = 3
   )
   private float fadeGlyphs0(float r, float g, float b, float a) {
      if (TooltipScroll.isTooltipVisible(Tooltips.getCurrentRenderContext().index())) {
         a = this.calculateFadeAlpha(this.currentTop.y);
      }

      return a;
   }

   @ModifyArg(
      method = {"render(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
         ordinal = 1
      ),
      index = 3
   )
   private float fadeGlyphs1(float r, float g, float b, float a) {
      if (TooltipScroll.isTooltipVisible(Tooltips.getCurrentRenderContext().index())) {
         a = this.calculateFadeAlpha(this.currentBottom.y);
      }

      return a;
   }

   @ModifyArg(
      method = {"render(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
         ordinal = 2
      ),
      index = 3
   )
   private float fadeGlyphs2(float r, float g, float b, float a) {
      if (TooltipScroll.isTooltipVisible(Tooltips.getCurrentRenderContext().index())) {
         a = this.calculateFadeAlpha(this.currentBottom.y);
      }

      return a;
   }

   @ModifyArg(
      method = {"render(ZFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
         ordinal = 3
      ),
      index = 3
   )
   private float fadeGlyphs3(float r, float g, float b, float a) {
      if (TooltipScroll.isTooltipVisible(Tooltips.getCurrentRenderContext().index())) {
         a = this.calculateFadeAlpha(this.currentTop.y);
      }

      return a;
   }
}

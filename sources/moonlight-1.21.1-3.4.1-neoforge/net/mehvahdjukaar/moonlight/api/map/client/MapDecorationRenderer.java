package net.mehvahdjukaar.moonlight.api.map.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.integration.MapAtlasCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class MapDecorationRenderer<T extends MLMapDecoration> {
   protected final ResourceLocation textureId;

   public MapDecorationRenderer(ResourceLocation texture) {
      this.textureId = texture;
   }

   protected int getColor(T decoration) {
      return -1;
   }

   protected int getAlpha(T decoration) {
      return 255;
   }

   protected boolean hasOutline(T decoration) {
      return false;
   }

   protected boolean rendersOnFrame(T decoration) {
      return true;
   }

   public boolean render(
      T decoration,
      PoseStack matrixStack,
      VertexConsumer vertexBuilder,
      MultiBufferSource buffer,
      @Nullable MapItemSavedData mapData,
      boolean isOnFrame,
      int light,
      int index
   ) {
      return this.render(decoration, matrixStack, vertexBuilder, buffer, mapData, isOnFrame, light, index, true);
   }

   public boolean render(
      T decoration,
      PoseStack matrixStack,
      VertexConsumer vertexBuilder,
      MultiBufferSource buffer,
      @Nullable MapItemSavedData mapData,
      boolean isOnFrame,
      int light,
      int index,
      boolean rendersText
   ) {
      if (isOnFrame && !this.rendersOnFrame(decoration)) {
         return false;
      } else {
         matrixStack.pushPose();
         matrixStack.translate(0.0F + decoration.getX() / 2.0F + 64.0F, 0.0F + decoration.getY() / 2.0F + 64.0F, -0.02F);
         matrixStack.mulPose(Axis.ZP.rotationDegrees(decoration.getRot() * 360 / 16.0F));
         matrixStack.scale(4.0F, 4.0F, 3.0F);
         if (CompatHandler.MAP_ATLASES) {
            MapAtlasCompat.scaleDecoration(matrixStack);
         }

         this.renderDecorationSprite(
            matrixStack, buffer, vertexBuilder, light, index, this.getColor(decoration), this.getAlpha(decoration), this.hasOutline(decoration)
         );
         matrixStack.popPose();
         if (decoration.getDisplayName() != null && rendersText) {
            this.renderName(decoration, matrixStack, buffer, light);
         }

         return true;
      }
   }

   public void renderDecorationSprite(
      PoseStack matrixStack, MultiBufferSource buffer, VertexConsumer vertexBuilder, int light, int index, int color, int alpha, boolean outline
   ) {
      int b = ARGB32.blue(color);
      int g = ARGB32.green(color);
      int r = ARGB32.red(color);
      RenderSystem.enableDepthTest();
      TextureAtlasSprite sprite = Minecraft.getInstance().getMapDecorationTextures().getSprite(this.textureId);
      if (alpha != 0) {
         matrixStack.pushPose();
         matrixStack.translate(0.0F, 0.0F, index * -0.001F);
         RenderUtil.renderSprite(matrixStack, vertexBuilder, light, b, g, r, alpha, sprite);
         if (outline) {
            int tint = 255;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer vb2 = buffer.getBuffer(RenderUtil.getColoredTextureRenderType(MapDecorationClientManager.LOCATION_MAP_MARKERS));

            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 1; k++) {
                  if (j != 0 || k != 0) {
                     matrixStack.pushPose();
                     matrixStack.translate(j * 0.125, k * 0.125, 0.001);
                     RenderUtil.renderSprite(matrixStack, vb2, 15728880, tint, tint, tint, alpha, sprite);
                     matrixStack.popPose();
                  }
               }
            }
         }

         matrixStack.popPose();
      }
   }

   protected void renderName(T decoration, PoseStack matrixStack, MultiBufferSource buffer, int light) {
      Font font = Minecraft.getInstance().font;
      Component displayName = decoration.getDisplayName();
      float width = font.width(displayName);
      float scale = Mth.clamp(25.0F / width, 0.0F, 0.6666667F);
      matrixStack.pushPose();
      matrixStack.translate(
         0.0F + decoration.getX() / 2.0F + 64.0F - width * scale / 2.0F, 0.0F + decoration.getY() / 2.0F + 64.0F + 4.0F, -0.02500000037252903
      );
      if (CompatHandler.MAP_ATLASES) {
         MapAtlasCompat.scaleDecorationText(matrixStack, width, scale);
      }

      matrixStack.scale(scale, scale, 1.0F);
      matrixStack.translate(0.0, 0.0, -0.10000000149011612);
      font.drawInBatch(displayName, 0.0F, 0.0F, -1, false, matrixStack.last().pose(), buffer, DisplayMode.NORMAL, -2147483648, light);
      matrixStack.popPose();
   }
}

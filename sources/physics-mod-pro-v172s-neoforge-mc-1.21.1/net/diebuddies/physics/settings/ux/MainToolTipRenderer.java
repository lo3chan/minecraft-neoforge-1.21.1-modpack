package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class MainToolTipRenderer {
   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      List<FormattedCharSequence> list,
      GuiGraphics guiGraphics,
      float x,
      float width,
      float y,
      float barSize,
      int backgroundColor
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      int heightPerRow = 10;
      int height = list.size() * heightPerRow + padding * 2;
      PoseStack matrices = guiGraphics.pose();
      matrices.pushPose();
      matrices.translate(0.0F, 0.0F, 200.0F);
      Matrix4f pose = matrices.last().pose();
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      float depth = -120.0F;
      int yOffset = height;
      if (alignment == MainToolTipRenderer.TooltipAlignment.TOP) {
         yOffset = 0;
      }

      Animator.drawRect(bufferBuilder, pose, x, y - yOffset, width, height, depth, backgroundColor);
      BufferUploader.drawWithShader(bufferBuilder.build());
      RenderSystem.disableBlend();
      int color = BaseColors.HIGHLIGHT_COLOR;
      if (animatable != null) {
         BarRenderer bar = animatable.getAnimator(BarRenderer.class);
         if (bar != null) {
            color = bar.getActiveColor();
         }
      }

      BarRenderer.renderHighlightBar(BarRenderer.BarAlignment.BOTTOM, pose, x, y - yOffset, width, height, depth + 1.0F, barSize, color);
      float xText = x + padding;

      for (int i = 0; i < list.size(); i++) {
         float yText = y + i * heightPerRow + padding - yOffset;
         Animator.drawText(guiGraphics, font, list.get(i), xText, yText);
      }

      matrices.popPose();
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color
   ) {
      renderToolTip(MainToolTipRenderer.TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize
   ) {
      renderToolTip(MainToolTipRenderer.TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      Component component,
      GuiGraphics guiGraphics,
      float x,
      float width,
      float y,
      float barSize,
      int color
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      List<FormattedCharSequence> list = font.split(component, (int)width - padding * 2);
      renderToolTip(alignment, animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      Component component,
      GuiGraphics guiGraphics,
      float x,
      float width,
      float y,
      float barSize
   ) {
      renderToolTip(alignment, animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      List<FormattedCharSequence> list = font.split(component, (int)width - padding * 2);
      renderToolTip(animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(@Nullable Animatable animatable, Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
      renderToolTip(animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize, int color) {
      renderToolTip(null, component, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(Component component, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
      renderToolTip(null, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(List<FormattedCharSequence> list, GuiGraphics guiGraphics, float x, float width, float y, float barSize) {
      renderToolTip(null, list, guiGraphics, x, width, y, barSize);
   }

   public static enum TooltipAlignment {
      TOP,
      BOTTOM;
   }
}

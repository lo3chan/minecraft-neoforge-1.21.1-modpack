package com.anthonyhilyard.legendarytooltips.tooltip;

import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.iceberg.util.Easing.EasingType;
import com.anthonyhilyard.iceberg.util.Tooltips.TooltipColors;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class TooltipDecor {
   public static final ResourceLocation DEFAULT_BORDERS = ResourceLocation.fromNamespaceAndPath("legendarytooltips", "textures/gui/tooltip_borders.png");
   private static float shineTimer = 1.5F;

   public static void setCurrentTooltipBorderStart(int color) {
      Tooltips.currentColors = new TooltipColors(
         Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd(), color, Tooltips.currentColors.borderColorEnd()
      );
   }

   public static void setCurrentTooltipBorderEnd(int color) {
      Tooltips.currentColors = new TooltipColors(
         Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd(), Tooltips.currentColors.borderColorStart(), color
      );
   }

   public static void setCurrentTooltipBackgroundStart(int color) {
      Tooltips.currentColors = new TooltipColors(
         color, Tooltips.currentColors.backgroundColorEnd(), Tooltips.currentColors.borderColorStart(), Tooltips.currentColors.borderColorEnd()
      );
   }

   public static void setCurrentTooltipBackgroundEnd(int color) {
      Tooltips.currentColors = new TooltipColors(
         Tooltips.currentColors.backgroundColorStart(), color, Tooltips.currentColors.borderColorStart(), Tooltips.currentColors.borderColorEnd()
      );
   }

   public static void updateTimer(float deltaTime) {
      if (shineTimer > 0.0F) {
         shineTimer -= deltaTime;
      }
   }

   public static void resetTimer() {
      shineTimer = 1.5F;
   }

   public static void drawShadow(PoseStack poseStack, int x, int y, int width, int height) {
      int shadowColor = 1140850688;
      poseStack.pushPose();
      Matrix4f matrix = poseStack.last().pose();
      GuiHelper.drawGradientRect(matrix, 390, x - 1, y + height + 4, x + width + 4, y + height + 5, shadowColor, shadowColor);
      GuiHelper.drawGradientRect(matrix, 390, x + width + 4, y - 1, x + width + 5, y + height + 5, shadowColor, shadowColor);
      GuiHelper.drawGradientRect(matrix, 390, x + width + 3, y + height + 3, x + width + 4, y + height + 4, shadowColor, shadowColor);
      GuiHelper.drawGradientRect(matrix, 390, x, y + height + 5, x + width + 5, y + height + 6, shadowColor, shadowColor);
      GuiHelper.drawGradientRect(matrix, 390, x + width + 5, y, x + width + 6, y + height + 5, shadowColor, shadowColor);
      poseStack.popPose();
   }

   public static void drawSeparator(PoseStack poseStack, int x, int y, int width, int color) {
      poseStack.pushPose();
      Matrix4f matrix = poseStack.last().pose();
      GuiHelper.drawGradientRectHorizontal(matrix, 400, x, y, x + width / 2, y + 1, color & 16777215, color);
      GuiHelper.drawGradientRectHorizontal(matrix, 400, x + width / 2, y, x + width, y + 1, color, color & 16777215);
      poseStack.popPose();
   }

   public static void drawBorder(
      PoseStack poseStack,
      int x,
      int y,
      int width,
      int height,
      ItemStack item,
      List<ClientTooltipComponent> components,
      Font font,
      LegendaryTooltipsConfig.FrameDefinition frameDefinition,
      boolean comparison,
      int index
   ) {
      if (comparison) {
         drawSeparator(poseStack, x - 3 + 1, y - 3 + 1 + 12, width, Tooltips.currentColors.borderColorStart());
         height++;
      }

      if (LegendaryTooltipsConfig.getInstance().nameSeparator.get() && (LegendaryTooltipsConfig.getInstance().showSeparatorForEmpty.get() || !item.isEmpty())) {
         int titleLines = Tooltips.calculateTitleLines(components);
         int numComponents = components.size();

         for (int i = 0; i < components.size(); i++) {
            if (!(components.get(i) instanceof ClientTextTooltip) && !(components.get(i) instanceof ItemModelComponent)) {
               if (--numComponents == titleLines) {
                  break;
               }
            }
         }

         if (titleLines < numComponents) {
            int offset = 0;
            int titleStart = Tooltips.calculateTitleStart(components);
            if (components.stream().anyMatch(c -> c instanceof ItemModelComponent)) {
               offset -= 2;
            }

            for (int ix = titleStart + titleLines; ix < components.size() && components.get(ix) instanceof TitleBreakComponent; ix++) {
               titleLines++;
            }

            for (int ix = 0; ix < titleStart + titleLines && ix < components.size(); ix++) {
               ClientTooltipComponent component = components.get(ix);
               if (component instanceof ClientTextTooltip) {
                  offset += Math.max(component.getHeight(), 9);
               } else {
                  offset += component.getHeight();
                  if (ix <= titleStart) {
                     offset += 2;
                  }
               }
            }

            drawSeparator(poseStack, x - 3 + 1, y - 3 + 2 + offset, width, Tooltips.currentColors.borderColorStart());
         }
      }

      if (frameDefinition.index() != LegendaryTooltipsConfig.STANDARD_BORDER.index() && frameDefinition.index() != LegendaryTooltipsConfig.NO_BORDER.index()) {
         if (LegendaryTooltipsConfig.getInstance().shineEffect.get()) {
            poseStack.pushPose();
            Matrix4f matrix = poseStack.last().pose();
            if (shineTimer >= 0.5F && shineTimer <= 1.5F) {
               float interval = 1.0F - Mth.clamp((shineTimer - 0.5F) * 2.0F - 0.5F, -0.5F, 1.5F);
               int alpha = 1996488704;
               int horizontalMin = x - 3;
               int horizontalMax = x + width + 3;
               int left = (int)Easing.Ease(horizontalMin, horizontalMax, Math.clamp(interval - 0.35F, 0.0F, 1.0F), EasingType.Quad);
               int middle = (int)Easing.Ease(horizontalMin, horizontalMax, Math.clamp(interval, 0.0F, 1.0F), EasingType.Quad);
               int right = (int)Easing.Ease(horizontalMin, horizontalMax, Math.clamp(interval + 0.35F, 0.0F, 1.0F), EasingType.Quad);
               GuiHelper.drawGradientRectHorizontal(matrix, 400, left, y - 3, middle, y - 3 + 1, 16777215, 16777215 | alpha);
               GuiHelper.drawGradientRectHorizontal(matrix, 400, middle, y - 3, right, y - 3 + 1, 16777215 | alpha, 16777215);
            }

            if (shineTimer <= 1.0F) {
               float interval = Mth.clamp(shineTimer, 0.0F, 1.0F);
               int alpha = (int)(85.0F * interval) << 24;
               int verticalMin = y - 3 + 1;
               int verticalMax = y + height + 3 - 1;
               int verticalInterval = (int)Mth.lerp(interval * interval, verticalMax, verticalMin);
               GuiHelper.drawGradientRect(
                  matrix,
                  400,
                  x - 3,
                  Math.max(verticalInterval - 12, verticalMin),
                  x - 3 + 1,
                  Math.min(verticalInterval, verticalMax),
                  16777215,
                  16777215 | alpha
               );
               GuiHelper.drawGradientRect(
                  matrix,
                  400,
                  x - 3,
                  Math.max(verticalInterval, verticalMin),
                  x - 3 + 1,
                  Math.min(verticalInterval + 12, verticalMax),
                  16777215 | alpha,
                  16777215
               );
            }

            poseStack.popPose();
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, frameDefinition.resource());
         Minecraft minecraft = Minecraft.getInstance();
         AbstractTexture borderTexture = minecraft.getTextureManager().getTexture(frameDefinition.resource());
         borderTexture.bind();
         int textureWidth = GlStateManager._getTexLevelParameter(3553, 0, 4096);
         int textureHeight = GlStateManager._getTexLevelParameter(3553, 0, 4097);
         int frameIndex = frameDefinition.index();
         int frameWidth = frameDefinition.frameWidth();
         int partSize = frameDefinition.partSize();
         int partOffset = frameDefinition.partOffset();
         int cornerOffset = frameDefinition.cornerOffset();
         int frameHeight = partSize * 2;
         int partWidth = frameWidth - partSize * 2;
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, 400.0);
         GuiHelper.blit(
            poseStack,
            x - partSize + cornerOffset,
            y - partSize + cornerOffset,
            partSize,
            partSize,
            frameIndex / 8 * frameWidth,
            frameIndex * frameHeight % textureHeight,
            partSize,
            partSize,
            textureWidth,
            textureHeight
         );
         GuiHelper.blit(
            poseStack,
            x + width - cornerOffset,
            y - partSize + cornerOffset,
            partSize,
            partSize,
            frameWidth - partSize + frameIndex / 8 * frameWidth,
            frameIndex * frameHeight % textureHeight,
            partSize,
            partSize,
            textureWidth,
            textureHeight
         );
         GuiHelper.blit(
            poseStack,
            x - partSize + cornerOffset,
            y + height - cornerOffset,
            partSize,
            partSize,
            frameIndex / 8 * frameWidth,
            frameIndex * frameHeight % textureHeight + partSize,
            partSize,
            partSize,
            textureWidth,
            textureHeight
         );
         GuiHelper.blit(
            poseStack,
            x + width - cornerOffset,
            y + height - cornerOffset,
            partSize,
            partSize,
            frameWidth - partSize + frameIndex / 8 * frameWidth,
            frameIndex * frameHeight % textureHeight + partSize,
            partSize,
            partSize,
            textureWidth,
            textureHeight
         );
         if (width >= partWidth) {
            GuiHelper.blit(
               poseStack,
               x + width / 2 - partWidth / 2,
               y - partSize + partOffset,
               partWidth,
               partSize,
               partSize + frameIndex / 8 * frameWidth,
               frameIndex * frameHeight % textureHeight,
               partWidth,
               partSize,
               textureWidth,
               textureHeight
            );
            GuiHelper.blit(
               poseStack,
               x + width / 2 - partWidth / 2,
               y + height - partOffset,
               partWidth,
               partSize,
               partSize + frameIndex / 8 * frameWidth,
               frameIndex * frameHeight % textureHeight + partSize,
               partWidth,
               partSize,
               textureWidth,
               textureHeight
            );
         }

         poseStack.popPose();
      }
   }
}

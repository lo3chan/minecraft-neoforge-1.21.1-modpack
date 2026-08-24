package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.diebuddies.math.Math;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class ButtonRenderer extends Animator {
   private TextAlignment alignment;
   private boolean renderTooltips = true;
   private ChatFormatting chatFormatting;
   private ResourceLocation image;

   public ButtonRenderer(TextAlignment alignment, ChatFormatting chatFormatting) {
      this.chatFormatting = chatFormatting;
      this.alignment = alignment;
   }

   public ButtonRenderer(TextAlignment alignment) {
      this(alignment, null);
   }

   public ButtonRenderer() {
      this(TextAlignment.CENTER);
   }

   @Override
   public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      boolean hovered = animatable.isInside(mouseX, mouseY);
      if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
         boolean wasHovered = accessor.getIsHovered();
         accessor.setIsHovered(hovered);
         if (!wasHovered && hovered) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, Math.random() * 0.2F + 0.9F));
         }
      }

      int color = BaseColors.BACKGROUND_COLOR;
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth();
      float height = animatable.getAnimHeight();
      float depth = animatable.getAnimDepth();
      PoseStack matrices = guiGraphics.pose();
      matrices.pushPose();
      matrices.translate(0.0F, 0.0F, depth);
      depth = -2.0F;
      AbstractButton button = (AbstractButton)animatable;
      if (!button.active) {
         color = BaseColors.INACTIVE_COLOR;
      }

      Matrix4f pose = matrices.last().pose();
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      drawRect(bufferBuilder, pose, x, y, width, height, depth, color);
      BufferUploader.drawWithShader(bufferBuilder.build());
      if (this.image != null) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, this.image);
         RenderSystem.setShaderColor(animatable.getAnimRed(), animatable.getAnimGreen(), animatable.getAnimBlue(), animatable.getAnimAlpha());
         bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         drawRect(bufferBuilder, pose, x, y, width, height, depth, 0.0F, 1.0F, 0.0F, 1.0F);
         BufferUploader.drawWithShader(bufferBuilder.build());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RenderSystem.disableBlend();
      FormattedCharSequence formattedCharSequence = (this.chatFormatting == null
            ? button.getMessage()
            : button.getMessage().copy().withStyle(this.chatFormatting))
         .getVisualOrderText();
      Font font = Minecraft.getInstance().font;
      float xText = x + 7.0F;
      if (this.alignment == TextAlignment.CENTER) {
         xText = x + width * 0.5F - font.width(formattedCharSequence) * 0.5F;
      } else if (this.alignment == TextAlignment.RIGHT) {
         xText = x + width - font.width(formattedCharSequence) - 7.0F;
      }

      drawText(guiGraphics, font, formattedCharSequence, Math.fastRound(xText), Math.fastRound(y + (height - 8.0F) / 2.0F));
      matrices.popPose();
      if (this.renderTooltips
         && animatable instanceof MixinAbstractWidgetAccessor invoker
         && animatable instanceof AbstractWidget widget
         && widget.isHoveredOrFocused()
         && widget.getTooltip() != null) {
         ((MixinAbstractWidgetAccessor)widget)
            .getTooltipHolder()
            .refreshTooltipForNextRenderPass(widget.isHovered(), widget.isFocused(), widget.getRectangle());
      }

      return true;
   }

   public ButtonRenderer setRenderTooltips(boolean renderTooltips) {
      this.renderTooltips = renderTooltips;
      return this;
   }

   public boolean isRenderingTooltips() {
      return this.renderTooltips;
   }

   public ResourceLocation getImage() {
      return this.image;
   }

   public ButtonRenderer setImage(ResourceLocation image) {
      this.image = image;
      return this;
   }
}

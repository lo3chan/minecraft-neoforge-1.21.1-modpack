package mezz.jei.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.DrawableBlank;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

class InternalIconButton extends Button implements IButtonState {
   private IDrawable icon = DrawableBlank.EMPTY;
   private boolean pressed = false;
   private boolean forcePressed = false;

   public InternalIconButton() {
      super(0, 0, 0, 0, CommonComponents.EMPTY, b -> {}, Button.DEFAULT_NARRATION);
   }

   public void updateBounds(ImmutableRect2i area) {
      this.setX(area.getX());
      this.setY(area.getY());
      this.width = area.getWidth();
      this.height = area.getHeight();
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      Textures textures = Internal.getTextures();
      boolean isPressed = this.pressed || this.forcePressed;
      ScalableDrawable texture = textures.getButtonForState(isPressed, this.active, hovered);
      texture.draw(guiGraphics, this.getX(), this.getY(), this.width, this.height);
      int color = -2039584;
      if (!this.active) {
         color = -6250336;
      } else if (hovered) {
         color = -1;
      }

      float red = (color >> 16 & 0xFF) / 255.0F;
      float blue = (color >> 8 & 0xFF) / 255.0F;
      float green = (color & 0xFF) / 255.0F;
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(red, blue, green, alpha);
      double xOffset = this.getX() + (this.width - this.icon.getWidth()) / 2.0;
      double yOffset = this.getY() + (this.height - this.icon.getHeight()) / 2.0;
      if (isPressed) {
         xOffset += 0.5;
         yOffset += 0.5;
      }

      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(xOffset, yOffset, 0.0);
      this.icon.draw(guiGraphics);
      poseStack.popPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void setPressed(boolean pressed) {
      this.pressed = pressed;
   }

   @Override
   public void setForcePressed(boolean forcePressed) {
      this.forcePressed = forcePressed;
   }

   public boolean clicked(double x, double y) {
      return super.clicked(x, y);
   }

   public boolean isValidClickButton(int mouseButton) {
      return super.isValidClickButton(mouseButton);
   }

   @Override
   public void setIcon(IDrawable icon) {
      this.icon = icon;
   }

   @Override
   public void setActive(boolean value) {
      this.active = value;
   }

   @Override
   public void setVisible(boolean value) {
      this.visible = value;
   }
}

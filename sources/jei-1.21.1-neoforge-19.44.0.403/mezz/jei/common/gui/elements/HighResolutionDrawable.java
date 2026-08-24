package mezz.jei.common.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class HighResolutionDrawable implements IDrawable {
   private final IDrawable drawable;
   private final int scale;

   public HighResolutionDrawable(IDrawable drawable, int scale) {
      this.drawable = drawable;
      this.scale = scale;
   }

   @Override
   public int getWidth() {
      int width = this.drawable.getWidth();
      return width / this.scale;
   }

   @Override
   public int getHeight() {
      int height = this.drawable.getHeight();
      return height / this.scale;
   }

   @Override
   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(xOffset, yOffset, 0.0F);
      poseStack.scale(1.0F / this.scale, 1.0F / this.scale, 1.0F);
      this.drawable.draw(guiGraphics);
      poseStack.popPose();
   }

   @Override
   public void draw(GuiGraphics guiGraphics) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.scale(1.0F / this.scale, 1.0F / this.scale, 1.0F);
      this.drawable.draw(guiGraphics);
      poseStack.popPose();
   }
}

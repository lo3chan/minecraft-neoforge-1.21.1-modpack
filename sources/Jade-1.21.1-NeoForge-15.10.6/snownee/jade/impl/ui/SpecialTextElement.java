package snownee.jade.impl.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.ITextElement;
import snownee.jade.overlay.DisplayHelper;

public class SpecialTextElement extends TextElement {
   private float scale = 1.0F;
   private int zOffset;

   public SpecialTextElement(FormattedText text) {
      super(text);
   }

   @Override
   public Vec2 getSize() {
      return new Vec2(DisplayHelper.font().width(this.text) * this.scale, 9.0F * this.scale + 1.0F);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      PoseStack matrixStack = guiGraphics.pose();
      matrixStack.pushPose();
      matrixStack.translate(x, y + this.scale, this.zOffset);
      matrixStack.scale(this.scale, this.scale, 1.0F);
      DisplayHelper.INSTANCE.drawText(guiGraphics, this.text, 0.0F, 0.0F, IThemeHelper.get().getNormalColor());
      matrixStack.popPose();
   }

   @Override
   public SpecialTextElement toSpecial() {
      return this;
   }

   @Override
   public ITextElement scale(float scale) {
      this.scale = scale;
      return this;
   }

   @Override
   public ITextElement zOffset(int zOffset) {
      this.zOffset = zOffset;
      return this;
   }
}

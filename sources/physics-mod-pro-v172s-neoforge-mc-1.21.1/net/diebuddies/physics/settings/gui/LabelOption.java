package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class LabelOption extends LegacyOption {
   private String value;
   private int inactiveColor = 10526880;
   public LabelOption.LabelComponent label;

   public LabelOption(String value) {
      super(value);
      this.value = value;
   }

   @Override
   public AbstractWidget createButton(Options options, int x, int y, int width) {
      return this.label = new LabelOption.LabelComponent(x, y, width, 20, Component.literal(this.value));
   }

   public void setInactiveColor(int inactiveColor) {
      this.inactiveColor = inactiveColor;
   }

   public class LabelComponent extends AbstractWidget {
      public LabelComponent(int x, int y, int width, int height, Component component) {
         super(x, y, width, height, component);
      }

      public void updateWidgetNarration(NarrationElementOutput narration) {
      }

      public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
         PoseStack matrices = guiGraphics.pose();
         matrices.pushPose();
         matrices.translate(0.0F, 0.0F, -100.0F);
         Minecraft minecraft = Minecraft.getInstance();
         Font font = minecraft.font;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         int color = this.active ? 16777215 : LabelOption.this.inactiveColor;
         guiGraphics.drawCenteredString(
            font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | Mth.ceil(this.alpha * 255.0F) << 24
         );
         matrices.popPose();
      }
   }
}

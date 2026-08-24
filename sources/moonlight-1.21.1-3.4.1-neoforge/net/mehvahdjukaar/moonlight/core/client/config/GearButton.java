package net.mehvahdjukaar.moonlight.core.client.config;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class GearButton extends Button {
   private static final int SPRITE_SIZE = 16;
   private static final float SECONDS_PER_TURN = 32.0F;
   private static final float HOVER_SCALE = 1.25F;
   private static final float SCALE_APPROACH = 10.0F;
   private float scale = 1.0F;
   private long lastMs = -1L;

   public GearButton(int x, int y, int size, OnPress onPress) {
      super(x, y, size, size, Component.empty(), onPress, DEFAULT_NARRATION);
      this.setTooltip(Tooltip.create(Component.translatable("gui.moonlight.config.mods_button")));
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      long now = Util.getMillis();
      float dt = this.lastMs < 0L ? 0.0F : Math.min((float)(now - this.lastMs) / 1000.0F, 0.1F);
      this.lastMs = now;
      float target = this.isHoveredOrFocused() ? 1.25F : 1.0F;
      this.scale = this.scale + (target - this.scale) * Math.min(1.0F, 10.0F * dt);
      float angle = (float)(now % 32000L) / 32000.0F * 360.0F;
      PoseStack pose = graphics.pose();
      pose.pushPose();
      pose.translate(this.getX() + this.getWidth() / 2.0F, this.getY() + this.getHeight() / 2.0F, 0.0F);
      pose.mulPose(Axis.ZP.rotationDegrees(angle));
      pose.scale(this.scale, this.scale, 1.0F);
      pose.translate(-8.0F, -8.0F, 0.0F);
      if (!this.active) {
         graphics.setColor(0.5F, 0.5F, 0.5F, 1.0F);
      }

      graphics.blitSprite(MoonlightIcons.CONFIG, 0, 0, 16, 16);
      if (!this.active) {
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      pose.popPose();
   }
}

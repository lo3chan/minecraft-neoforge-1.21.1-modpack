package net.diebuddies.physics.settings.ux;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public class TooltipRenderer extends Animator {
   private TooltipRenderer.Renderable renderable;

   public TooltipRenderer(TooltipRenderer.Renderable renderable) {
      this.renderable = renderable;
   }

   @Override
   public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      if (animatable instanceof AbstractWidget widget && widget.isHoveredOrFocused()) {
         this.renderable.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
      }

      return false;
   }

   public interface Renderable {
      void render(Animatable var1, GuiGraphics var2, int var3, int var4, float var5, float var6);
   }
}

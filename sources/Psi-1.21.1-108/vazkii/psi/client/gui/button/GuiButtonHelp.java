package vazkii.psi.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonHelp extends Button {
   final GuiProgrammer gui;

   public GuiButtonHelp(int x, int y, GuiProgrammer gui) {
      super(x, y, 12, 12, Component.empty(), button -> {}, DEFAULT_NARRATION);
      this.gui = gui;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      if (!this.gui.takingScreenshot) {
         boolean overHelp = mouseX > this.getX() && mouseY > this.getY() && mouseX < this.getX() + 12 && mouseY < this.getY() + 12;
         graphics.blit(GuiProgrammer.texture, this.getX(), this.getY(), this.gui.xSize + (overHelp ? 12 : 0), this.gui.ySize + 9, 12, 12);
         if (overHelp && !Screen.hasAltDown()) {
            this.gui.tooltip.add(Component.translatable("psimisc.programmer_help"));
            String ctrl = I18n.get(Minecraft.ON_OSX ? "psimisc.ctrl_mac" : "psimisc.ctrl_windows", new Object[0]);
            TooltipHelper.tooltipIfShift(this.gui.tooltip, () -> {
               int i = 0;

               while (I18n.exists("psi.programmer_reference" + i)) {
                  this.gui.tooltip.add(Component.translatable("psi.programmer_reference" + i++, new Object[]{ctrl}));
               }
            });
         }
      }
   }
}

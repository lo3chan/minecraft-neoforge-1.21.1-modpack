package vazkii.psi.client.gui.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonPage extends Button {
   public final boolean right;
   final GuiProgrammer gui;

   public GuiButtonPage(int x, int y, boolean right, GuiProgrammer gui, OnPress pressable) {
      super(x, y, 18, 10, Component.empty(), pressable, DEFAULT_NARRATION);
      this.gui = gui;
      this.right = right;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int par2, int par3, float pTicks) {
      if (this.active) {
         boolean hover = par2 >= this.getX() && par3 >= this.getY() && par2 < this.getX() + this.width && par3 < this.getY() + this.height;
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(GuiProgrammer.texture, this.getX(), this.getY(), hover ? 216 : 198, this.right ? 145 : 155, this.width, this.height);
         if (hover) {
            this.gui.tooltip.add(Component.translatable(this.right ? "psimisc.next_page" : "psimisc.prev_page"));
         }
      }
   }

   public boolean isRight() {
      return this.right;
   }
}

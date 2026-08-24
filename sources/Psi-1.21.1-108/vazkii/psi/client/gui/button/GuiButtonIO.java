package vazkii.psi.client.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonIO extends Button {
   public final boolean out;
   final GuiProgrammer gui;

   public GuiButtonIO(int x, int y, boolean out, GuiProgrammer gui, OnPress pressable) {
      super(x, y, 12, 12, Component.empty(), pressable, DEFAULT_NARRATION);
      this.out = out;
      this.gui = gui;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int par2, int par3, float pticks) {
      if (this.active && !this.gui.takingScreenshot) {
         boolean hover = par2 >= this.getX() && par3 >= this.getY() && par2 < this.getX() + this.width && par3 < this.getY() + this.height;
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(GuiProgrammer.texture, this.getX(), this.getY(), hover ? 186 : 174, this.out ? 169 : 181, this.width, this.height);
         if (hover) {
            String key = this.out ? "psimisc.export_to_clipboard" : "psimisc.import_from_clipboard";
            ChatFormatting color = this.out ? ChatFormatting.RED : ChatFormatting.BLUE;
            Component tip = Component.translatable(key).withStyle(color);
            this.gui.tooltip.add(tip);
            this.gui.tooltip.add(Component.translatable("psimisc.must_hold_shift").withStyle(ChatFormatting.GRAY));
         }
      }
   }
}

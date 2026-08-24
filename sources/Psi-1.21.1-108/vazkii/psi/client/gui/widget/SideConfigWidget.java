package vazkii.psi.client.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.gui.GuiProgrammer;

public class SideConfigWidget extends AbstractWidget {
   public final List<Button> configButtons = new ArrayList<>();
   public final GuiProgrammer parent;
   public boolean configEnabled = false;

   public SideConfigWidget(int x, int y, int width, int height, GuiProgrammer programmer) {
      super(x, y, width, height, Component.empty());
      this.parent = programmer;
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return false;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      SpellPiece piece = null;
      if (SpellGrid.exists(GuiProgrammer.selectedX, GuiProgrammer.selectedY)) {
         piece = this.parent.spell.grid.gridData[GuiProgrammer.selectedX][GuiProgrammer.selectedY];
      }

      if (this.configEnabled && !this.parent.takingScreenshot) {
         graphics.blit(GuiProgrammer.texture, this.parent.left - 81, this.parent.top + 55, this.parent.xSize, 30, 81, 115);
         String configStr = I18n.get("psimisc.config", new Object[0]);
         graphics.drawString(
            this.parent.getMinecraft().font, configStr, this.parent.left - this.parent.getMinecraft().font.width(configStr) - 2, this.parent.top + 45, 16777215
         );
         int i = 0;
         if (piece != null) {
            int param = -1;

            for (int j = 0; j < 4; j++) {
               if (InputConstants.isKeyDown(this.parent.getMinecraft().getWindow().getWindow(), 49 + j)) {
                  param = j;
               }
            }

            for (String s : piece.params.keySet()) {
               int x = this.parent.left - 75;
               int y = this.parent.top + 70 + i * 26;
               graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
               graphics.blit(GuiProgrammer.texture, x + 50, y - 8, this.parent.xSize, 145, 24, 24);
               String localized = I18n.get(s, new Object[0]);
               if (i == param) {
                  localized = ChatFormatting.UNDERLINE + localized;
               }

               graphics.drawString(this.parent.getMinecraft().font, localized, x, y, 16777215);
               i++;
            }
         }
      }
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
      this.defaultButtonNarrationText(pNarrationElementOutput);
   }
}

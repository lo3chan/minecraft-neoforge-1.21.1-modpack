package vazkii.psi.client.gui.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.ItemCAD;

public class SpellCostsWidget extends AbstractWidget {
   private final GuiProgrammer parent;

   public SpellCostsWidget(int x, int y, int width, int height, String message, GuiProgrammer programmer) {
      super(x, y, width, height, Component.nullToEmpty(message));
      this.parent = programmer;
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return false;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      this.parent
         .compileResult
         .left()
         .ifPresent(
            compiledSpell -> {
               int i = 0;
               int statX = this.parent.left + this.parent.xSize + 3;
               SpellMetadata meta = compiledSpell.metadata;
               ItemStack cad = PsiAPI.getPlayerCAD(this.parent.getMinecraft().player);

               for (EnumSpellStat stat : meta.getStatSet()) {
                  int val = meta.getStat(stat);
                  int statY = this.parent.top + (this.parent.takingScreenshot ? 40 : 20) + i * 20;
                  EnumCADStat cadStat = stat.getTarget();
                  int cadVal = 0;
                  if (cadStat == null) {
                     cadVal = -1;
                  } else if (!cad.isEmpty()) {
                     ICAD cadItem = (ICAD)cad.getItem();
                     cadVal = cadItem.getStatValue(cad, cadStat);
                  }

                  String s = val + "";
                  if (stat == EnumSpellStat.COST) {
                     s = s + " (" + Math.max(0, ItemCAD.getRealCost(cad, ItemStack.EMPTY, val)) + ")";
                  } else {
                     s = s + "/" + (cadVal == -1 ? "∞" : cadVal);
                  }

                  graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                  graphics.blit(GuiProgrammer.texture, statX, statY, (stat.ordinal() + 1) * 12, this.parent.ySize + 16, 12, 12);
                  graphics.drawString(
                     this.parent.getMinecraft().font, s, statX + 16, statY + 2, cadStat != null && cadVal < val && cadVal != -1 ? 16737894 : 16777215, false
                  );
                  if (mouseX > statX && mouseY > statY && mouseX < statX + 12 && mouseY < statY + 12 && !this.parent.panelWidget.panelEnabled) {
                     this.parent.tooltip.add(Component.translatable(stat.getName()).withStyle(Psi.magical ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.AQUA));
                     this.parent.tooltip.add(Component.translatable(stat.getDesc()).withStyle(ChatFormatting.GRAY));
                  }

                  i++;
               }
            }
         );
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
      this.defaultButtonNarrationText(pNarrationElementOutput);
   }
}

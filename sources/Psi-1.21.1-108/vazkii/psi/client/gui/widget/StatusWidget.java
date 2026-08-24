package vazkii.psi.client.gui.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.client.gui.GuiProgrammer;

public class StatusWidget extends AbstractWidget {
   private final GuiProgrammer parent;

   public StatusWidget(int x, int y, int width, int height, String message, GuiProgrammer programmer) {
      super(x, y, width, height, Component.nullToEmpty(message));
      this.parent = programmer;
   }

   protected boolean isValidClickButton(int p_isValidClickButton_1_) {
      return false;
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      graphics.blit(GuiProgrammer.texture, this.parent.left - 48, this.parent.top + 5, this.parent.xSize, 0, 48, 30);
      graphics.blit(
         GuiProgrammer.texture,
         this.parent.left - 16,
         this.parent.top + 13,
         this.parent.compileResult.right().isPresent() ? 12 : 0,
         this.parent.ySize + 28,
         12,
         12
      );
      if (mouseX > this.parent.left - 16 - 1 && mouseY > this.parent.top + 13 - 1 && mouseX < this.parent.left - 16 + 13 && mouseY < this.parent.top + 13 + 13) {
         if (this.parent.compileResult.right().isPresent()) {
            SpellCompilationException ex = (SpellCompilationException)this.parent.compileResult.right().get();
            this.parent.tooltip.add(Component.translatable("psimisc.errored").withStyle(ChatFormatting.RED));
            this.parent.tooltip.add(Component.translatable(ex.getMessage()).withStyle(ChatFormatting.GRAY));
            Pair<Integer, Integer> errorPos = ex.location;
            if (errorPos != null && (Integer)errorPos.getRight() != -1 && (Integer)errorPos.getLeft() != -1) {
               this.parent
                  .tooltip
                  .add(
                     Component.literal(
                           "[" + GuiProgrammer.convertIntToLetter((Integer)errorPos.getLeft() + 1) + ", " + ((Integer)errorPos.getRight() + 1) + "]"
                        )
                        .withStyle(ChatFormatting.GRAY)
                  );
            }
         } else {
            this.parent.tooltip.add(Component.translatable("psimisc.compiled").withStyle(ChatFormatting.GREEN));
         }
      }

      ItemStack cad = PsiAPI.getPlayerCAD(this.parent.getMinecraft().player);
      if (!cad.isEmpty()) {
         int cadX = this.parent.left - 42;
         int cadY = this.parent.top + 12;
         graphics.renderFakeItem(cad, cadX, cadY);
         if (mouseX > cadX && mouseY > cadY && mouseX < cadX + 16 && mouseY < cadY + 16) {
            this.parent
               .tooltip
               .addAll(cad.getTooltipLines(TooltipContext.of(this.parent.getMinecraft().level), this.parent.getMinecraft().player, this.parent.tooltipFlag));
         }
      }
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
      this.defaultButtonNarrationText(pNarrationElementOutput);
   }
}

package vazkii.psi.client.gui.button;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiButtonSpellPiece extends Button {
   public final SpellPiece piece;
   final GuiProgrammer gui;

   public GuiButtonSpellPiece(GuiProgrammer gui, SpellPiece piece, int x, int y, OnPress pressable) {
      super(x, y, 16, 16, Component.empty(), pressable, DEFAULT_NARRATION);
      this.gui = gui;
      this.piece = piece;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pTicks) {
      if (this.active && this.visible) {
         boolean hover = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
         BufferSource buffers = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
         graphics.pose().pushPose();
         graphics.pose().translate(this.getX(), this.getY(), 0.0F);
         this.piece.draw(graphics.pose(), buffers, 15728880);
         buffers.endBatch();
         graphics.pose().popPose();
         if (hover) {
            this.piece.getTooltip(this.gui.tooltip);
            graphics.blit(GuiProgrammer.texture, this.getX(), this.getY(), 16, this.gui.ySize, 16, 16);
         }
      }
   }

   public SpellPiece getPiece() {
      return this.piece;
   }

   public String getPieceSortingName() {
      return this.piece.getSortingName();
   }
}

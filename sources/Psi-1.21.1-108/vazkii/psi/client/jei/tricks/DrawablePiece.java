package vazkii.psi.client.jei.tricks;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.SpellPiece;

public record DrawablePiece(SpellPiece piece) implements IDrawableStatic {
   public void draw(GuiGraphics graphics, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
      graphics.pose().pushPose();
      graphics.pose().translate(xOffset, yOffset, 0.0F);
      BufferSource buffers = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
      this.piece.drawBackground(graphics.pose(), buffers, 15728880);
      buffers.endBatch();
      graphics.pose().popPose();
   }

   public int getWidth() {
      return 16;
   }

   public int getHeight() {
      return 16;
   }

   public void draw(@NotNull GuiGraphics graphics, int xOff, int yOff) {
      this.draw(graphics, xOff, yOff, 0, 0, 0, 0);
   }
}

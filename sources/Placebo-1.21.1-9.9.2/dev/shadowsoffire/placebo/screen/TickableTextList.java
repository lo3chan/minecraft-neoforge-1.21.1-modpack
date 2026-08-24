package dev.shadowsoffire.placebo.screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.joml.Matrix4f;

public class TickableTextList {
   protected final Font font;
   protected final List<TickableTextList.TickableText> texts;
   protected int ticks;
   protected int maxWidth;
   protected int lineSpacing;
   protected int width = 0;

   public TickableTextList(Font font, int maxWidth) {
      this.font = font;
      this.texts = new ArrayList<>();
      this.ticks = 0;
      this.maxWidth = maxWidth;
      this.lineSpacing = 9 + 3;
   }

   public void addLine(FormattedText text, float tickRate) {
      this.texts.add(new TickableTextList.TickableText(text, Math.max(0.01F, tickRate)));
      this.width = this.computeWidth();
   }

   public void addLine(FormattedText text) {
      this.addLine(text, 1.0F);
   }

   public void continueLine(FormattedText text, float tickRate) {
      if (this.texts.isEmpty()) {
         this.addLine(text, tickRate);
      } else {
         TickableTextList.TickableText last = (TickableTextList.TickableText)this.texts.removeLast();
         this.addLine(FormattedText.composite(new FormattedText[]{last.text, text}), tickRate);
      }
   }

   public void setLine(int index, FormattedText text, float tickRate) {
      this.texts.set(index, new TickableTextList.TickableText(text, Math.max(0.01F, tickRate)));
      this.width = this.computeWidth();
   }

   public void render(
      float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource buffer, DisplayMode mode, int bgColor, int packedLight
   ) {
      int line = 0;
      MutableFloat timeLeft = new MutableFloat(this.ticks);

      for (TickableTextList.TickableText tickable : this.texts) {
         for (FormattedCharSequence seq : this.font.split(tickable.text, this.maxWidth)) {
            seq = this.wrap(seq, tickable.tickRate, timeLeft);
            this.font.drawInBatch(seq, x, y + this.lineSpacing * line, color, dropShadow, matrix, buffer, mode, bgColor, packedLight);
            line++;
         }
      }
   }

   public void render(GuiGraphics gfx, float x, float y, int color, boolean dropShadow) {
      this.render(x, y, color, dropShadow, gfx.pose().last().pose(), gfx.bufferSource(), DisplayMode.NORMAL, 0, 15728880);
   }

   public void render(GuiGraphics gfx, float x, float y) {
      this.render(gfx, x, y, -1, false);
   }

   public void clear() {
      this.texts.clear();
      this.ticks = 0;
   }

   public int getTicks() {
      return this.ticks;
   }

   public void setTicks(int ticks) {
      this.ticks = ticks;
   }

   public int getMaxWidth() {
      return this.maxWidth;
   }

   public void setMaxWidth(int maxWidth) {
      this.maxWidth = maxWidth;
   }

   public int getLineSpacing() {
      return this.lineSpacing;
   }

   public void setLineSpacing(int lineSpacing) {
      this.lineSpacing = lineSpacing;
   }

   public void tick() {
      this.ticks++;
   }

   public int getWidth() {
      return this.width;
   }

   private FormattedCharSequence wrap(FormattedCharSequence text, float tickRate, MutableFloat timeLeft) {
      return sink -> text.accept(new TickableTextList.TimeLimitedCharSink(sink, tickRate, timeLeft));
   }

   private int computeWidth() {
      int width = 0;

      for (TickableTextList.TickableText text : this.texts) {
         width = Math.clamp(this.font.width(text.text), width, this.maxWidth);
      }

      return width;
   }

   private record TickableText(FormattedText text, float tickRate) {
   }

   private class TimeLimitedCharSink implements FormattedCharSink {
      private final FormattedCharSink wrapped;
      private final float tickRate;
      private final MutableFloat timeLeft;

      public TimeLimitedCharSink(FormattedCharSink wrapped, float tickRate, MutableFloat timeLeft) {
         this.wrapped = wrapped;
         this.tickRate = tickRate;
         this.timeLeft = timeLeft;
      }

      public boolean accept(int positionInCurrentSequence, Style style, int codePoint) {
         this.timeLeft.subtract(1.0F / this.tickRate);
         if (this.timeLeft.getValue() >= 0.0F) {
            this.wrapped.accept(positionInCurrentSequence, style, codePoint);
            return true;
         } else {
            return false;
         }
      }
   }
}

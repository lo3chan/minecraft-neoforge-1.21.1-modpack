package codx.codxlib.api.ui;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CodxColorScreen extends CodxConfigScreen {
   private final IntSupplier getColor;
   private final IntConsumer setColor;

   public CodxColorScreen(Screen parent, Component title, IntSupplier getColor, IntConsumer setColor) {
      super(parent, title);
      this.getColor = getColor;
      this.setColor = setColor;
   }

   @Override
   protected void addContents() {
      int left = this.width / 2 - 110;
      this.addRenderableWidget(new CodxColorScreen.ChannelSlider(left, 56, Component.literal("Red"), 16));
      this.addRenderableWidget(new CodxColorScreen.ChannelSlider(left, 88, Component.literal("Green"), 8));
      this.addRenderableWidget(new CodxColorScreen.ChannelSlider(left, 120, Component.literal("Blue"), 0));
   }

   @Override
   protected void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int color = this.getColor.getAsInt();
      graphics.fill(this.width / 2 - 40, 152, this.width / 2 + 40, 176, 0xFF000000 | color);
      graphics.renderOutline(this.width / 2 - 40, 152, 80, 24, -1);
      graphics.drawCenteredString(this.font, Component.literal(String.format("#%06X", color & 16777215)), this.width / 2, 182, 16777215);
   }

   private final class ChannelSlider extends AbstractSliderButton {
      private final Component channelLabel;
      private final int shift;

      private ChannelSlider(int x, int y, Component channelLabel, int shift) {
         super(x, y, 220, 20, Component.empty(), (CodxColorScreen.this.getColor.getAsInt() >> shift & 0xFF) / 255.0);
         this.channelLabel = channelLabel;
         this.shift = shift;
         this.updateMessage();
      }

      private int channelValue() {
         return (int)Math.round(this.value * 255.0);
      }

      protected void updateMessage() {
         this.setMessage(Component.literal(this.channelLabel.getString() + ": " + this.channelValue()));
      }

      protected void applyValue() {
         int mask = 255 << this.shift;
         int updated = CodxColorScreen.this.getColor.getAsInt() & ~mask | this.channelValue() << this.shift;
         CodxColorScreen.this.setColor.accept(updated & 16777215);
         this.updateMessage();
      }
   }
}

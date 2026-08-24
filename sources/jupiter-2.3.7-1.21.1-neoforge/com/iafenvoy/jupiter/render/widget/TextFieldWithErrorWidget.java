package com.iafenvoy.jupiter.render.widget;

import com.iafenvoy.jupiter.util.TextUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.NotNull;

public class TextFieldWithErrorWidget extends EditBox {
   private boolean hasError = false;

   public TextFieldWithErrorWidget(Font font, int x, int y, int width, int height) {
      super(font, x, y, width, height, TextUtil.empty());
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.hasError) {
         this.setTextColorUneditable(-65536);
         this.setEditable(false);
      }

      super.renderWidget(graphics, mouseX, mouseY, partialTick);
      this.setEditable(true);
   }

   public void setHasError(boolean hasError) {
      this.hasError = hasError;
   }
}

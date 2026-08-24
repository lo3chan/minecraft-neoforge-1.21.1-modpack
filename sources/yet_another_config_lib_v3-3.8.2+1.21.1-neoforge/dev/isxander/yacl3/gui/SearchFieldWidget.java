package dev.isxander.yacl3.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchFieldWidget extends EditBox {
   private Component emptyText;
   private final YACLScreen yaclScreen;
   private final Font font;
   private final Consumer<String> updateConsumer;
   private boolean isEmpty = true;

   public SearchFieldWidget(
      YACLScreen yaclScreen, Font font, int x, int y, int width, int height, Component text, Component emptyText, Consumer<String> updateConsumer
   ) {
      super(font, x, y, width, height, text);
      this.setResponder(this::update);
      this.yaclScreen = yaclScreen;
      this.font = font;
      this.emptyText = emptyText;
      this.updateConsumer = updateConsumer;
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.renderWidget(graphics, mouseX, mouseY, delta);
      if (this.isVisible() && this.isEmpty()) {
         graphics.drawString(this.font, this.emptyText, this.getX() + 4, this.getY() + (this.height - 8) / 2, 7368816, true);
      }
   }

   private void update(String query) {
      boolean wasEmpty = this.isEmpty;
      this.isEmpty = query.isEmpty();
      if (!this.isEmpty || !wasEmpty) {
         this.updateConsumer.accept(query);
      }
   }

   public String getQuery() {
      return this.getValue().toLowerCase();
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public Component getEmptyText() {
      return this.emptyText;
   }

   public void setEmptyText(Component emptyText) {
      this.emptyText = emptyText;
   }
}

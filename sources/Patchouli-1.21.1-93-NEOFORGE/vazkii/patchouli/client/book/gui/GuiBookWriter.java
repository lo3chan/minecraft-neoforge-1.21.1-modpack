package vazkii.patchouli.client.book.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {
   private BookTextRenderer text;
   private BookTextRenderer editableText;
   private EditBox textfield;
   private static String savedText = "";
   private static boolean drawHeader;

   public GuiBookWriter(Book book) {
      super(book, Component.empty());
   }

   @Override
   public void init() {
      super.init();
      this.text = new BookTextRenderer(this, Component.translatable("patchouli.gui.lexicon.editor.info"), 15, 38);
      this.textfield = new EditBox(this.font, 15, 140, 116, 20, this.textfield, Component.empty());
      this.textfield.setMaxLength(2147483647);
      if (this.textfield.getValue().isEmpty()) {
         this.textfield.setValue(savedText);
      }

      this.editableText = new BookTextRenderer(this, Component.literal(""), 141, 18 + (drawHeader ? 22 : -4));
      this.addRenderableWidget(
         new GuiButtonBook(
            this,
            this.bookLeft + 115,
            this.bookTop + 156 - 36,
            330,
            9,
            11,
            11,
            this::handleToggleHeaderButton,
            Component.translatable("patchouli.gui.lexicon.button.toggle_mock_header")
         )
      );
      this.refreshText();
   }

   @Override
   void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
      this.drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor", new Object[0]), 73, 18, this.book.headerColor);
      drawSeparator(graphics, this.book, 15, 30);
      if (drawHeader) {
         this.drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor.mock_header", new Object[0]), 199, 18, this.book.headerColor);
         drawSeparator(graphics, this.book, 141, 30);
      }

      this.textfield.render(graphics, mouseX, mouseY, partialTicks);
      this.text.render(graphics, mouseX, mouseY, partialTicks);
      this.editableText.render(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
      if (this.textfield.mouseClicked(this.getRelativeX(mouseX), this.getRelativeY(mouseY), mouseButton)) {
         this.textfield.setFocused(true);
         return true;
      } else if (this.text.click(mouseX, mouseY, mouseButton)) {
         return true;
      } else {
         return this.editableText.click(mouseX, mouseY, mouseButton) ? true : super.mouseClickedScaled(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public boolean keyPressed(int key, int scanCode, int modifiers) {
      if (this.textfield.keyPressed(key, scanCode, modifiers)) {
         this.refreshText();
         return true;
      } else {
         return super.keyPressed(key, scanCode, modifiers);
      }
   }

   public boolean charTyped(char c, int i) {
      if (this.textfield.charTyped(c, i)) {
         this.refreshText();
         return true;
      } else {
         return super.charTyped(c, i);
      }
   }

   private void handleToggleHeaderButton(Button button) {
      drawHeader = !drawHeader;
      this.init();
   }

   private void refreshText() {
      savedText = this.textfield.getValue();

      try {
         this.editableText.setText(Component.literal(savedText));
      } catch (Throwable var2) {
         this.editableText.setText(Component.literal("[ERROR]"));
         PatchouliAPI.LOGGER.catching(var2);
      }
   }
}

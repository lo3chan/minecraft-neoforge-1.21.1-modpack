package net.mehvahdjukaar.moonlight.api.client.gui.screen;

import com.google.gson.JsonParser;
import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.JsonHighlighter;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.SyntaxEditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class JsonEditScreen extends Screen {
   private static final int HEADER = 44;
   private static final int SIDE_MARGIN = 20;
   private static final int DESC_PAD_TOP = 6;
   private static final int DESC_PAD_BOTTOM = 8;
   private final Screen parent;
   private final Consumer<String> onApply;
   private final String initial;
   @Nullable
   private final Component description;
   private SyntaxEditBox editor;
   private Button done;
   private List<FormattedCharSequence> descriptionLines = List.of();
   private int descriptionBlockHeight;

   public JsonEditScreen(Component title, @Nullable Component description, String initial, Screen parent, Consumer<String> onApply) {
      super(title);
      this.description = description;
      this.initial = initial;
      this.parent = parent;
      this.onApply = onApply;
   }

   protected void init() {
      this.layoutDescription();
      int top = 44 + this.descriptionBlockHeight + 6;
      int bottom = this.height - 36;
      this.editor = new SyntaxEditBox(
         this.font, 20, top, this.width - 40, bottom - top, Component.translatable("gui.moonlight.config.json_hint"), JsonHighlighter.INSTANCE
      );
      this.editor.setValue(this.initial);
      this.editor.setValueListener(s -> this.refreshValid());
      this.addRenderableWidget(this.editor);
      int cx = this.width / 2;
      this.done = Button.builder(CommonComponents.GUI_DONE, b -> {
         this.onApply.accept(this.editor.getValue());
         this.onClose();
      }).bounds(cx - 100, this.height - 28, 96, 20).build();
      this.addRenderableWidget(this.done);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(cx + 4, this.height - 28, 96, 20).build());
      this.setInitialFocus(this.editor);
      this.refreshValid();
   }

   private void layoutDescription() {
      this.descriptionLines = List.of();
      this.descriptionBlockHeight = 0;
      if (this.description != null && !this.description.getString().isBlank()) {
         this.descriptionLines = this.font.split(this.description, this.width - 40);
         if (!this.descriptionLines.isEmpty()) {
            this.descriptionBlockHeight = 6 + this.descriptionLines.size() * 9 + 8;
         }
      }
   }

   private void refreshValid() {
      this.done.active = isValidJson(this.editor.getValue());
   }

   private static boolean isValidJson(String s) {
      try {
         JsonParser.parseString(s);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.width, 44);
      this.renderDescription(graphics);
      if (!this.done.active) {
         graphics.drawCenteredString(
            this.font, Component.translatable("gui.moonlight.config.json_invalid"), this.width / 2, this.height - 42, ConfigGuiColors.ERROR
         );
      }
   }

   private void renderDescription(GuiGraphics graphics) {
      if (!this.descriptionLines.isEmpty()) {
         int bandBottom = 44 + this.descriptionBlockHeight;
         graphics.fill(0, 44, this.width, bandBottom, -15592936);
         graphics.fill(0, bandBottom - 1, this.width, bandBottom, -15724526);
         int y = 50;

         for (FormattedCharSequence line : this.descriptionLines) {
            graphics.drawString(this.font, line, 20, y, ConfigGuiColors.DESCRIPTION);
            y += 9;
         }
      }
   }
}

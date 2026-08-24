package codx.codxlib.api.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class CodxConfigScreen extends Screen {
   public static final int ACCENT_AMBER = -2056144;
   private static final int HEADER_H = 33;
   private static final int FOOTER_H = 36;
   private static final int SIDE_PAD = 8;
   private static final int BAR_TOP = -1072689134;
   private static final int BAR_FADE = 1611665426;
   private static final int LINE_DARK = -16777216;
   private static final int LINE_LIGHT = 587202559;
   private static final int TITLE_COLOR = -1;
   private static final int SUBTITLE_COLOR = -5197648;
   private static final int TAG_COLOR = -7697782;
   protected final Screen parent;
   private final Component subtitle;
   private int accent = -2056144;

   protected CodxConfigScreen(Screen parent, Component title) {
      this(parent, title, null);
   }

   protected CodxConfigScreen(Screen parent, Component title, Component subtitle) {
      super(title);
      this.parent = parent;
      this.subtitle = subtitle;
   }

   protected final void setAccent(int argb) {
      this.accent = argb;
   }

   protected final int accent() {
      return this.accent;
   }

   protected final int contentLeft() {
      return 8;
   }

   protected final int contentRight() {
      return this.width - 8;
   }

   protected final int contentTop() {
      return 34;
   }

   protected final int contentBottom() {
      return this.height - 36 - 1;
   }

   protected void init() {
      this.clearWidgets();
      this.addContents();
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), button -> this.onDone()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build()
      );
   }

   protected abstract void addContents();

   protected void onSave() {
   }

   protected Component headerTag() {
      return null;
   }

   protected boolean pausesGame() {
      return true;
   }

   public boolean isPauseScreen() {
      return this.pausesGame();
   }

   protected void drawExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
   }

   private void onDone() {
      this.onSave();
      this.minecraft.setScreen(this.parent);
   }

   public void onClose() {
      this.onDone();
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      this.drawChrome(graphics, mouseX, mouseY, partialTick);
   }

   private void drawChrome(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int headerLine = 33;
      int footerLine = this.height - 36;
      graphics.fillGradient(0, 0, this.width, headerLine, -1072689134, 1611665426);
      graphics.fillGradient(0, footerLine, this.width, this.height, 1611665426, -1072689134);
      graphics.fill(0, headerLine, this.width, headerLine + 1, -16777216);
      graphics.fill(0, headerLine + 1, this.width, headerLine + 2, 587202559);
      graphics.fill(0, footerLine - 1, this.width, footerLine, 587202559);
      graphics.fill(0, footerLine, this.width, footerLine + 1, -16777216);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, this.subtitle != null ? 7 : 12, -1);
      int underlineY = this.subtitle != null ? 18 : 23;
      graphics.fill(this.width / 2 - 20, underlineY, this.width / 2 + 20, underlineY + 1, this.accent);
      if (this.subtitle != null) {
         graphics.drawCenteredString(this.font, this.subtitle, this.width / 2, 21, -5197648);
      }

      Component tag = this.headerTag();
      if (tag != null) {
         graphics.drawString(this.font, tag, this.width - 8 - this.font.width(tag), 12, -7697782);
      }

      this.drawExtras(graphics, mouseX, mouseY, partialTick);
   }
}

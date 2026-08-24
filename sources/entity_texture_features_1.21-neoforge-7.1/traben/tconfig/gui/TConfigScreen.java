package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;

public class TConfigScreen extends Screen {
   private final boolean showBackButton;
   protected Screen parent;
   protected Runnable resetDefaultValuesRunnable = null;
   protected Runnable undoChangesRunnable = null;

   protected TConfigScreen(@Translatable String title, Screen parent, boolean showBackButton) {
      super(Component.translatable(title));
      this.parent = parent;
      this.showBackButton = showBackButton;
   }

   protected Component getBackButtonText() {
      return CommonComponents.GUI_BACK;
   }

   protected void init() {
      if (this.showBackButton) {
         this.addRenderableWidget(
            Button.builder(this.getBackButtonText(), button -> this.onClose())
               .bounds((int)(this.width * 0.7), (int)(this.height * 0.9), (int)(this.width * 0.2), 20)
               .build()
         );
      }

      if (this.resetDefaultValuesRunnable != null) {
         this.addRenderableWidget(Button.builder(ETF.getTextFromTranslation("dataPack.validation.reset"), button -> {
            this.resetDefaultValuesRunnable.run();
            this.rebuildWidgets();
         }).bounds((int)(this.width * 0.4), (int)(this.height * 0.9), (int)(this.width * 0.22), 20).build());
      }

      if (this.undoChangesRunnable != null) {
         this.addRenderableWidget(Button.builder(ETF.getTextFromTranslation("config.entity_features.undo"), button -> {
            this.undoChangesRunnable.run();
            this.rebuildWidgets();
         }).bounds((int)(this.width * 0.1), (int)(this.height * 0.9), (int)(this.width * 0.2), 20).build());
      }
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredString(this.font, this.title, this.width / 2, 15, -1);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }
}

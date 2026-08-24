package net.mehvahdjukaar.moonlight.core.integration.platform;

import net.mehvahdjukaar.moonlight.api.client.gui.widget.MediaButton;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigSelectScreen;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.mehvahdjukaar.moonlight.core.CommonConfigs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Items;

public class ModConfigSelectScreen extends CustomConfigSelectScreen {
   public ModConfigSelectScreen(Screen parent) {
      super("moonlight", Items.AIR.getDefaultInstance(), "§3Moonlight Configured", parent, ModConfigScreen::new, ClientConfigs.CONFIG, CommonConfigs.CONFIG);
   }

   protected void init() {
      super.init();
      Button found = null;

      for (GuiEventListener c : this.children()) {
         if (c instanceof Button button && button.getWidth() == 150) {
            found = button;
         }
      }

      if (found != null) {
         this.removeWidget(found);
      }

      int y = this.height - 29;
      int centerX = this.width / 2;
      MediaButton.addAuthorMediaButtons(
         this,
         x$0 -> {
            Button var10000 = (Button)this.addRenderableWidget(x$0);
         },
         centerX,
         y,
         22,
         "moonlight",
         "https://www.curseforge.com/minecraft/mc-mods/moonlight-lib",
         "https://modrinth.com/mod/moonlight",
         () -> this.minecraft.setScreen(this.parent)
      );
   }
}
